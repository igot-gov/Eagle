/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
// Libraries
//--------------------------------------------------------------------------------
const path = require('path');
// Request
const request = require('request');
const fse = require('fs-extra');
let {
  getVideoDurationInSeconds
} = require('get-video-duration');
const archiver = require('archiver');
// const unzip = require('unzip');
const unzip = require('unzip-stream');
const fs = require('fs');

// File imports
//--------------------------------------------------------------------------------
// helper to interact with aws-sdk
const appConfig = require('../ConfigReader/loader');
const log = require('../Logger/log');
const {
  errors,
  success
} = require('./status-codes');
const helper = require('./helper');
const {
  getBucketsFromKey,
  getCDNsFromKey
} = require('./org-util');
// Image extensions
const imageExtensions = require('../Content/util').validImageExtensions;

const cassandraUtil = require('../CassandraUtil/cassandra');
const esUtil = require('../ElasticsearchUtil/es-util');

// Constants
const {
  contentRoot,
  imagesBehaviourRoute
} = require('./constants');

/*
S3 & CDN Config
*/
//--------------------------------------------------------------------------------
/**
 * These need to be turned into envs
 */
// The bucket used to store keys for hls decryption
const keysBucket = appConfig.getProperty('keys_bucket');


// Supported extensions by TS (Transcoding service), was previously called ETS(Amazon Elastic Transcoder Service)
const supportedETSExtensions = [
  '.avi', '.webm', '.ogv', // video extensions
  // Audio extensions not supported for inhouse transcoding
  //'mp4' '.wav', '.mp3', '.flac', '.oga', '.ogg' // audio extensions
];

const audioExtensions = [
  '.wav', '.mp3', '.flac', '.oga', '.ogg'
];

//--------------------------------------------------------------------------------

/**
 * DB services and variables
 */

// Elasticsearch config

const esUrl = esUtil.esUrl;
const esAuthHeaders = esUtil.authHeaders;

const contentIndex = appConfig.getProperty('es_index_name');
// Temporarily use contentindex, after sunbird upgrade use search index
searchIndex = contentIndex;
// const searchIndex = appConfig.getProperty('es_sunbird_index_name');
const accessControlIndex = appConfig.getProperty('es_access_control_index');

// Cassandra config
const accessPathTable = appConfig.getProperty('cassandra_user_access_paths');

/**
 * Add replace all for strings
 */
String.prototype.replaceAll = function (search, replacement) {
  let target = this;
  return target.replace(new RegExp(search, 'g'), replacement);
};

String.prototype.replaceSlashes = function () {
  let target = this;
  return target.replaceAll('%2f', '%2F').replaceAll('%2F', '/');
}

String.prototype.countSlash = function () {
  let count = 0;
  let str = this;
  for (let i = 0; i < str.length; i++) {
    if (str[i] === '/') count++;
  }
  return count;
}

Array.prototype.replaceWhiteSpacesWithUnderscore = function () {
  let array = this;

  for (let i = 0; i < array.length; i++) {
    if (array[i].includes(' ')) {
      array[i] = array[i].replaceAll(' ', '_');
    }
  }
  return array;
}

function extractHostname(url) {
  var hostname;
  //find & remove protocol (http, ftp, etc.) and get hostname

  if (url.indexOf("//") > -1) {
    hostname = url.split('/')[2];
  } else {
    hostname = url.split('/')[0];
  }

  //find & remove port number
  hostname = hostname.split(':')[0];
  //find & remove "?"
  hostname = hostname.split('?')[0];

  return hostname;
}

function findLexIdFromUrl(url) {
  const hostName = extractHostname(url);
  const stringToCheck = url.split(hostName)[1];
  let urlSplitArr = stringToCheck.split('/');

  for (let i = 0; i < urlSplitArr.length; i++) {
    let currentVal = urlSplitArr[i];
    if (currentVal.startsWith('lex') || currentVal.startsWith('do_')) {
      return currentVal;
    }
  }
  const errorMsg = 'Could not find LEX ID in the url';
  console.error(errorMsg);
  throw new Error(errorMsg);
}

function extractDomainName(url) {
  return url.split("://")[1].split('/')[0];
}

/**
 * Create directory
 */
async function createPath(key) {
  try {
    // If the key contains %2F, replace it with /
    key = key.replaceSlashes();

    const {
      authoringBucket
    } = getBucketsFromKey(key);

    let exists = await checkPathExists(key, authoringBucket);
    if (exists) {
      throw errors.AlreadyExists(`${key} already exits in bucket ${authoringBucket}`);
    } else {
      await helper.createFolder(authoringBucket, key);
      return success.ResourceCreated(`${key} created successfully in bucket ${authoringBucket}`)
    }
  } catch (error) {
    if (error.code) throw error;
    else {
      log.error(error.toString());
      throw errors.InternalServerError();
    }
  }
}

async function checkPathExists(path, bucket) {
  if (path && typeof path === 'string' || path instanceof String) {
    try {
      // Check if the path exists
      let exists = await helper.exists(bucket, path);
      if (exists) {
        return true;
      }
    } catch (error) {
      if (error.code) throw error;
      else {
        log.error(error.toString());
        throw errors.InternalServerError();
      }
    }
    return false;
  } else {
    throw new Error('Invalid input provided for checking path');
  }
}

//--------------------------------------------------------------------------------

/**
 * Delete a path and all the contents within that path
 */
async function deletePath(key, force) {
  try {
    // If the key contains %2F, replace it with /
    key = key.replaceSlashes();

    if (key.split('/').filter(item => item && item.length > 0).length <= 4) {
      return errors.BadRequest('Content at this level cannot be deleted');
    }

    /*if ((key.countSlash() <= 2 && !force)) {
      return errors.BadRequest(`It is dangerous to delete folders at this level. If necessary try using ?force=true`);
    } else {
      // Check if the key exists
      let exists = await helper.exists(authoringBucket, key);

      // If the key exists, delete it
      if (exists) {
        await helper.deletePath(authoringBucket, key);
        return success.ResourceDeleted(`${key} successfully deleted from bucket ${authoringBucket}`);
      } else { // else, throw a 404
        throw errors.NotFound(`${key} does not exists in ${authoringBucket}`);
      }
    }*/
  } catch (error) {
    if (error.code) throw error;
    else {
      log.error(error.toString());
      throw errors.InternalServerError();
    }
  }
}

//--------------------------------------------------------------------------------

/**
 * Upload a file at a specific path
 */
async function uploadContent(key, fileName, file) {

  try {

    // If the key contains %2F, replace it with /
    key = key.replaceSlashes();

    const {
      authoringBucket,
      imageAuthoringBucket
    } = getBucketsFromKey(key);
    const {
      CONTENT_CDN,
      IMAGES_CDN
    } = getCDNsFromKey(key);

    // Check if filename carries any special character
    if (!/^[-A-Za-z0-9_.]+$/.test(fileName)) {
      throw errors.BadRequest(`Make sure your file name does not contain spaces or any special characters other than - & _`);
    }

    let basename = path.basename(key);

    if (!['assets', 'artifacts', 'ecar_files', 'web-hosted'].includes(basename)) {
      throw errors.BadRequest(`Please specify one of the following directories to upload => assets,artifacts,ecar_files, web-hosted`);
    }

    // Checking if the request is for meta assets
    const isUploadForMetaAssets = key.includes('/meta-assets/');
    // Check if the key exists,
    // Check 1 level above because we will create the directory type folder
    // let exists = await helper.exists(authoringBucket, path.dirname(key));
    // OLD LOGIC REMOVED: Removeed the logic to check if the location where the key should exist in the bucket and creating the directory if not present.
    // Upload the file
    key += fileName;

    let extension = path.extname(fileName).toLowerCase();
    let artifactURL = CONTENT_CDN + '/' + key;
    console.log('Artifact URL: ', artifactURL);

    // Generate download URL
    let dKey = key;
    let downloadKey = dKey.split('/');
    downloadKey = downloadKey.splice(1, downloadKey.length - 1).join('/');
    downloadKey = downloadKey.replaceAll('/', '%2F');
    let downloadURL = `http://${appConfig.getProperty('NETWORK_ALIAS')}/contentv3/download/${downloadKey}`;

    // Cheking if the request is for uploading the content as body of description.
    // This check is performed, if the request URL is <ROOT_ORG>/meta-assets/lex-id
    if (isUploadForMetaAssets) {
      // Push the content into the meta-assets
      await helper.uploadContent(imageAuthoringBucket, key, file);

      artifactURL = `${IMAGES_CDN}/${key}`;
      // Adding the artifact url and auth artifact url
      return success.ResourceCreated(`${key} created in ${imageAuthoringBucket}`, {
        artifactURL,
        authArtifactUrl: downloadURL,
        downloadURL: downloadURL + '?type=main',
      });
    }

    if (audioExtensions.includes(extension)) {
      await helper.uploadContent(authoringBucket, key, file);

      let videoLengthResult = await getVideoLength(key, authoringBucket);

      let videoLength = {
        success: false
      };
      if (videoLengthResult.duration) {
        videoLength = {
          success: true,
          fileName: videoLengthResult.fileName,
          duration: videoLengthResult.duration,
          unit: videoLengthResult.unit,
        };
      }
      return success.ResourceCreated(`${key} created in ${authoringBucket}`, {
        artifactURL,
        authArtifactUrl: downloadURL,
        downloadUrl: downloadURL + '?type=main',
        videoLength
      });
    } else if (imageExtensions.includes(extension.split('.')[1]) && !(key.includes('web-hosted'))) {
      // else if it is an image and not from a web-hosted folder upload it to the images authoring bucket
      //REMOVE LATER:  Hard coding the key for testing
      await helper.uploadContent(imageAuthoringBucket, key, file);
      // artifactURL = `${CDN}/${imagesBehaviourRoute}/${key.replace('content-store/', '')}`;
      artifactURL = `${IMAGES_CDN}/${key}`;
      console.log('Artifact URL after bug Fix: ', artifactURL);
      return success.ResourceCreated(`${key} created in ${imageAuthoringBucket}`, {
        artifactURL,
        authArtifactUrl: downloadURL,
        downloadURL: downloadURL + '?type=main',
      });
    } else { //else upload it to the authoring bucket
      await helper.uploadContent(authoringBucket, key, file);
      return success.ResourceCreated(`${key} created in ${authoringBucket}`, {
        artifactURL,
        authArtifactUrl: downloadURL,
        downloadURL: downloadURL + '?type=main',
      });
    }
  } catch (error) {
    if (error.code) {
      throw error;
    } else {
      log.error(error.toString());
      console.error(error);
      throw errors.InternalServerError();
    }
  }
}
//--------------------------------------------------------------------------------

function downloadAssessmentContent(key, res) {
  // Replacing the %2F with /
  key = key.replaceSlashes();
  const {
    mainBucket
  } = getBucketsFromKey(key);
  let url = helper.generateSignedURL(mainBucket, key);
  request(url).pipe(res);
}

/**
 * Download the content using the URL of that path
 */
async function downloadContent(key, req, res) {
  // If the key contains %2F, replace it with /
  key = key.replaceSlashes();

  const {
    downloadBucket,
    imageAuthoringBucket,
    authoringBucket
  } = getBucketsFromKey(key);
  let extension = path.extname(key).toLowerCase();
  let type = req.query.type;

  if (type === 'main') {
    let url = helper.generateSignedURL(downloadBucket, key);
    request(url).pipe(res);
    // helper.download(downloadBucket, key, req, res, true);
  } else {
    // Check if the file extension is supported by ETS
    // if (supportedETSExtensions.includes(extension)) {
    // 	// let url = helper.generateSignedURL(downloadBucket, key);
    // 	// request(url).pipe(res);
    // 	// If it is a supported extension,download the file from the transcoding bucket
    // 	helper.download(transcodingBucket, key, req, res, true);
    // }
    if (imageExtensions.includes(extension.split('.')[1]) && !(key.includes('web-hosted'))) {
      // else, download from images bucket if it is a bucket
      helper.download(imageAuthoringBucket, key, req, res, false);
    } else {
      // else, download from authoring bucket
      helper.download(authoringBucket, key, req, res, false);
    }
  }
}
//--------------------------------------------------------------------------------

/**
 * Copy the content from source to destination in authoring or main bucket
 */
async function copyContent(source, destination, type, includeFolder) {
  try {

    // If the source or dest contains %2F, replace it with /
    source = source.replaceSlashes();
    destination = destination.replaceSlashes();

    const {
      authoringBucket,
      mainBucket
    } = getBucketsFromKey(source);
    const bucket = type === 'authoring' ? authoringBucket : mainBucket

    // Copy the content from source to destination
    let data = await helper.copy(bucket, bucket, source, destination, includeFolder);

    if (data.FailedCount === 0) {
      return success.Success(`All files were copied successfully from ${source} to ${destination}`);
    } else {
      return success.MultiStatus(`Not all files were copied successfully. Copy failed.`, data);
    }
  } catch (error) {
    if (error.code) throw error;
    else {
      log.error(error.toString());
      throw errors.InternalServerError();
    }
  }
}
//--------------------------------------------------------------------------------

/**
 * Copy the content from source to destination in authoring or main bucket
 */
async function moveContent(source, destination, type, includeFolder) {
  try {
    const {
      authoringBucket,
      mainBucket
    } = getBucketsFromKey(source);
    const bucket = type === 'authoring' ? authoringBucket : mainBucket

    // If the source or dest contains %2F, replace it with /
    source = source.replaceSlashes();
    destination = destination.replaceSlashes();

    // Copy the content from source to destination
    let data = await helper.copy(bucket, bucket, source, destination, includeFolder);

    if (data.FailedCount === 0) {
      // Delete the source path
      await helper.deletePath(bucket, source);

      // After the file has been deleted send the success status code
      return success.Success(`All files were moved successfully from ${source} to ${destination}`);
    } else {
      return success.MultiStatus(`Not all files were copied successfully. Move failed.`, data);
    }
  } catch (error) {
    if (error.code) throw error;
    else {
      log.error(error.toString());
      throw errors.InternalServerError();
    }
  }
}
//--------------------------------------------------------------------------------

/**
 * Publish the content from authoring to main bucket
 *
 * Future development
 * Publish API currently copies the content from one bucket to another
 * For moving the content just use the move function
 * Instead of maintaining a .old to move the content for publish
 *
 * It is safe to do so because the move function is managed by us,
 * it is basically copy first, if copy succeeds, then delete
 * In case of failure, it wont move half the content like traditional move functionality
 * Instead it willtry to copy, if it is successfull in copying everything, then
 * only it will delete
 */
async function publish(key) {
  try {

    // If the source or dest contains %2F, replace it with /
    key = key.replaceAll('%2F', '/');

    // Forming the Key for checking if the file is present in the meta-assets
    const keySplitArr = key.replace(`${contentRoot}/`, '').split('/');
    const rootOrgName = keySplitArr[0];
    const lexId = keySplitArr[keySplitArr.length - 1];

    const metaAssetsKey = `${contentRoot}/${rootOrgName}/meta-assets/${lexId}`;

    const {
      authoringBucket,
      mainBucket,
      imageAuthoringBucket,
      imageBucket,
      downloadBucket
    } = getBucketsFromKey(key);

    // Check if the content exists on any of the two buckets
    let existsInBuckets = await Promise.all([
      // helper.exists(transcodingBucket, key),
      helper.exists(imageAuthoringBucket, key),
      helper.exists(authoringBucket, key),
      helper.exists(imageAuthoringBucket, metaAssetsKey)
    ]);

    // let existsInTranscodingBucket = existsInBuckets[0];
    let existsInImagesBucket = existsInBuckets[0];
    let existsInAuthoringBucket = existsInBuckets[1];
    let existsInMetaLocation = existsInBuckets[2];

    let contentDoesExists = existsInAuthoringBucket || existsInImagesBucket || existsInMetaLocation;

    if (!contentDoesExists) {
      throw errors.NotFound(`Content not found`);
    } else {
      let operations = [];

      // New Logic the mp4 file will be in the S3 authoring bucket after being transcoded

      // Old Logic
      // If mp4 file is present, copy it to the downloads bucket
      // if (existsInTranscodingBucket) {
      // 	helper.copy(transcodingBucket, downloadBucket, key, path.dirname(key), true)
      // }

      if (existsInAuthoringBucket) {
        // Copy the content from authoring bucket to main bucket and download bucket appropriately
        operations.push(helper.copy(authoringBucket, mainBucket, key, path.dirname(key), true));
        operations.push(helper.copy(authoringBucket, downloadBucket, key, path.dirname(key), true));
      }

      if (existsInImagesBucket) {
        // Copy the content from images auth/pre-publish to images live
        // helper.copy(imageAuthoringBucket, imageBucket, key, path.dirname(key), true);

        // Moving the images as well in the operations
        operations.push(helper.copy(imageAuthoringBucket, imageBucket, key, path.dirname(key), true));
      }

      if (existsInMetaLocation) {
        // Moving the images as well in the operations
        operations.push(helper.copy(imageAuthoringBucket, imageBucket, metaAssetsKey, path.dirname(metaAssetsKey), true));
      } else {
        console.log('Does not exist in meta loacation');
      }

      // Copy from authoring bucket to download bucket and main bucket
      let data = await Promise.all(operations);

      let published = true;
      for (let i = 0; i < data.length; i++) {
        let failedCount = data[i].FailedCount;
        if (failedCount != 0) published = false;
      }

      if (published) {
        // Asynchronously deleting the cache for the content on publish.
        try {
          const {
            IMAGES_CDN,
            CONTENT_CDN
          } = getCDNsFromKey(key);
          const contentInvalidationURL = `${CONTENT_CDN}/content-store/${key.endsWith('/') ? key : key + '/'}`;
          const imageInvalidationURL = `${IMAGES_CDN}/content-store/${key.endsWith('/') ? key : key + '/'}`;

          Promise.all([
              createMultiplePathInvalidations([contentInvalidationURL]),
              createMultiplePathInvalidations([imageInvalidationURL]),
            ])
            .then((values) => console.log('Invalidation completed: ', values))
            .catch((ex) => {
              console.error('Exception while trying to clear the cache after publish');
              console.error(ex);
            });
        } catch (ex) {
          console.error('Exception while trying to clear the cache after publish');
          console.error(ex);
        }
        return success.Success(`The content was published successfully`);
      } else {
        throw success.MultiStatus(`Not all files were copied successfully. Publish failed.`, data);
      }
    }
  } catch (error) {
    if (error.code) throw error;
    else {
      log.error(error.toString());
      throw errors.InternalServerError();
    }
  }
}

async function copyDirInSameBucket(sourceDirectory, destDirectory) {
  try {
    const {
      authoringBucket
    } = getBucketsFromKey(sourceDirectory);
    let data = await helper.copyInSameBucket(sourceDirectory, destDirectory, authoringBucket);
    return data;
  } catch (error) {
    console.log(error);
    if (error.code) throw error;
    else {
      log.error(error.toString());
      throw errors.InternalServerError();
    }
  }
}
/**
 *	Pullback content from live
 *  Copy the content from authoring bucket to live bucket
 *  Ditto copy of publish API , just the buckets are reversed in function
 */
async function pullback(key) {
  try {

    const {
      authoringBucket,
      mainBucket,
      imageAuthoringBucket,
      imageBucket,
      downloadBucket
    } = getBucketsFromKey(key);

    // If the source or dest contains %2F, replace it with /
    key = key.replaceAll('%2F', '/');

    // Check if the content exists on any of the two buckets
    let existsInBuckets = await Promise.all([
      // helper.exists(transcodingBucket, key),
      helper.exists(imageAuthoringBucket, key),
      helper.exists(authoringBucket, key)
    ]);

    // let existsInTranscodingBucket = existsInBuckets[0];
    let existsInImagesBucket = existsInBuckets[0];
    let existsInAuthoringBucket = existsInBuckets[1];

    let contentDoesExists = existsInAuthoringBucket || existsInImagesBucket;

    if (!contentDoesExists) {
      throw errors.NotFound(`Content not found`);
    } else {
      let operations = [];

      // New Logic the mp4 file will be in the S3 authoring bucket after being transcoded

      // Old Logic
      // If mp4 file is present, get it from the downloads bucket
      // if (existsInTranscodingBucket) {
      // 	helper.copy(downloadBucket, transcodingBucket, key, path.dirname(key), true)
      // }

      if (existsInAuthoringBucket) {
        // Copy the content from main bucket and download bucket to authoring bucket appropriately
        operations.push(helper.copy(mainBucket, authoringBucket, key, path.dirname(key), true));
        operations.push(helper.copy(downloadBucket, authoringBucket, key, path.dirname(key), true));
      }

      if (existsInImagesBucket) {
        // Copy the content from images live to images auth/pre-publish
        helper.copy(imageBucket, imageAuthoringBucket, key, path.dirname(key), true);
      }

      // Copy from download bucket and main bucket to authoring bucket
      let data = await Promise.all(operations);

      let published = true;
      for (let i = 0; i < data.length; i++) {
        let failedCount = data[i].FailedCount;
        if (failedCount != 0) published = false;
      }

      if (published) {
        return success.Success(`The content was pulled successfully`);
      } else {
        throw success.MultiStatus(`Not all files were copied successfully. Pull back failed.`, data);
      }
    }
  } catch (error) {
    if (error.code) throw error;
    else {
      log.error(error.toString());
      throw errors.InternalServerError();
    }
  }
}
//--------------------------------------------------------------------------------
/*
Logic to set cookie for Cloudfront authentication
*/
function setCookie(path, res) {
  console.log("setCookie1 Called===>", path);

  let cookies = helper.getCFCookies(path);
  // Set the cookies
  for (let id in cookies) {
    console.log("setCookie1 id===>", id)
    console.log("setCookie1 ===>" + id + "==========>", cookies[id])
    res.cookie(id, cookies[id]);
  }
  console.log('Cookies is', res.headers);
  res.send(success.Success(`Cookie has been set`, {
    path
  }));
}

function deNormalizeBase64(str) {
  return str
    .replace('-', /\+/g)
    .replace('_', /=/g)
    .replace('~', /\//g);
}

function deCodeCloudfrontPolicy(encodedPolicy) {
  let buffer = new Buffer(deNormalizeBase64(encodedPolicy), 'base64');
  return buffer.toString('ascii');
}

function getContentMeta(contentId) {
  let body = {
    "query": {
      "term": {
        "identifier": {
          "value": contentId
        }
      }
    }
  };

  let url = `${esUrl}/${searchIndex}/_search`;

  let options = {
    headers: esAuthHeaders,
    rejectUnauthorized: false,
    method: 'GET',
    body: body,
    json: true,
    url: url
  };

  return new Promise((resolve, reject) => {
    request(options, function (error, response, body) {
      if (error) {
        reject(error);
      } else {
        if (body.hits) {
          if (body.hits.hits && body.hits.hits.length > 0) {
            let hits = body.hits.hits;
            resolve(hits[0]);
          } else {
            reject(errors.NotFound('Content Id not found'));
          }
        } else {
          reject(errors.NotFound('Content Id not found'));
        }
      }
    });
  });
}
// Set the cookie according to the logic
async function setCookieFromAccessPaths(uuid, artifactUrl, contentAccessPaths, res) {
  try {
    // Fetch the access paths for the user
    let userAccessPaths = await getUserAccessPaths(uuid);
    userAccessPaths.replaceWhiteSpacesWithUnderscore();

    let matchedPaths = [];
    // Get the matched paths between user access paths and content access paths
    for (let i = 0; i < contentAccessPaths.length; i++) {
      for (let j = 0; j < userAccessPaths.length; j++) {
        if (contentAccessPaths[i] === userAccessPaths[j]) {
          matchedPaths.push(contentAccessPaths[i]);
        }
      }
    }

    // If no path is matched between content access path and user access path, deny access to user
    if (matchedPaths.length === 0) {
      res.status(401).send(errors.Unauthorized(`You are not allowed to view this content`));
    } else { // If a match is found
      // Set the cookie value to artifact url - until path/lexid
      let lexId;
      try {
        lexId = findLexIdFromUrl(artifactUrl);
      } catch (e) {
        return res.send(errors.InternalServerError('Could not find  the Lex id'));
      }
      let artifactUrlBeforeLexId = artifactUrl.split(lexId)[0];
      let cookieValue = `${artifactUrlBeforeLexId}${lexId}/*`;
      console.log('Initial Cookie value is: ', cookieValue);

      // If the content is exiting at the Level of user's access path
      // We will update the access path, so that it is moved to the Level of the user's Org
      // This will help reduce the calls

      // Sorting so that the path is set the top most level of match
      matchedPaths = sortAccessPathsOnLevels(matchedPaths);
      const urlToValidate = artifactUrlBeforeLexId.split('/content-store/')[1];
      for (let i = 0; i < matchedPaths.length; i++) {
        if (urlToValidate === matchedPaths[i] + '/Public/') {
          cookieValue = artifactUrlBeforeLexId + '*';
          console.log('Cookie value changed to', cookieValue);
          break; // Breaking on first match, since it is sorted to highest level
        }
      }
      // Set the cookie on the cookie path
      setCookie(cookieValue, res);
    }
  } catch (err) {
    res.send(errors.InternalServerError('Exception while setting up the cookie'));
  }
}

function sortAccessPathsOnLevels(accessPaths) {
  return accessPaths.sort((a, b) => a.split('/').length - b.split('/').length);
}

/**
 * Future Scbope for the cookie API
 *
 * We will use a second cookie which will comprise of user details and which will be encrypted
 * Using this cookie, we will verify if the correct user is accessing the cookie
 */
/**
 * API to set cookies for cloudfront authentication
 *
 * We are using signed cookies for cloudfront.
 * This cookie is comprised of a policy statement which includes parameters such as Resource, DateLessThan
 * Resource => The path on which the access is provided
 * DateLessThan => Timestamp in seconds till which the cookie is valid.
 *
 * Cookies are set only after the access path is checked through Cassandra and Elasticsearch
 *
 */

async function setCookieOnResource(uuid, contentId, cookiePolicy, res) {
  try {
    let meta = await getContentMeta(contentId);

    let artifactUrl = meta._source.artifactUrl;
    let contentAccessPaths = meta._source.accessPaths;

    if (!artifactUrl || !contentAccessPaths) {
      throw errors.NotFound('Content access path or/and artifact Url were not found in the meta')
    }

    // If the url is not being served from CDN
    const key = artifactUrl.split('/').splice(3, artifactUrl.split('/').length).join('/');

    const {
      IMAGES_CDN,
      CONTENT_CDN
    } = getCDNsFromKey(key);

    if (!artifactUrl.includes(IMAGES_CDN) && !artifactUrl.includes(CONTENT_CDN)) {
      throw errors.BadRequest('The artifact URL does not point to S3');
    }

    contentAccessPaths.replaceWhiteSpacesWithUnderscore();

    // Check the old cookie policy, and if its scope is within the current artifactURL, then dont change the cookie
    if (cookiePolicy) {
      let decodedPolicy = deCodeCloudfrontPolicy(cookiePolicy);

      if (decodedPolicy[decodedPolicy.length - 1] !== '}') {
        decodedPolicy = decodedPolicy.slice(0, decodedPolicy.length - 1);
      }
      // Decode it and get path
      let policy = JSON.parse(decodedPolicy);
      let currentPath = policy.Statement[0].Resource;
      let Condition = policy.Statement[0].Condition;

      // If there is no expiry date in the cookie, then set the cookie
      if (!Condition || !Condition.DateLessThan) {
        if (artifactUrl.startsWith(currentPath.slice(0, currentPath.length - 1))) {
          // If the cookie was previously set on current path, then allow
          setCookie(currentPath, res);
        } else {
          // Set the cookie after checking access
          setCookieFromAccessPaths(uuid, artifactUrl, contentAccessPaths, res);
        }
      }

      let cookieUpdated = false;

      // If there is an expiry date, check if the cookie has expired
      if (Condition.DateLessThan) {
        let timeInMilliSeconds = Condition.DateLessThan["AWS:EpochTime"] * 1000;
        // If the time in the policy is greater than the current time, then we dont need to
        // set the cookie again
        if (timeInMilliSeconds - new Date().getTime() > 0) {
          if (artifactUrl.startsWith(currentPath.slice(0, currentPath.length - 1))) {
            cookieUpdated = true;
            res.send(success.Success(`Cookie has been set`, {
              path: currentPath
            }));
          }
        }
      }

      // If the cookie has expired, then check the access again and set the cookie
      if (!cookieUpdated) {
        setCookieFromAccessPaths(uuid, artifactUrl, contentAccessPaths, res);
      }
    } else {
      // Check the access paths and set the cookie
      setCookieFromAccessPaths(uuid, artifactUrl, contentAccessPaths, res);
    }
  } catch (err) {
    // If the cookie policy cant be parsed, that means, it was tampered
    if (err.message === "Unexpected token U in JSON at position 0") {
      res.send(errors.BadRequest(`Invalid cookie policy found`));
    } else {
      if (err.message && err.code) {
        res.status(err.code).send(err);
      } else {
        log.error(err);
        res.status(500).send(errors.InternalServerError());
      }
    }
  }
}

// Run a cassandra query to get user access paths from access paths table
function getUserAccessPathsFromCass(userId) {
  return new Promise((resolve, reject) => {
    cassandraUtil.executeQuery(`SELECT * FROM ${accessPathTable} where user_id=?;`, [userId], (err, result) => {
      if (err) {
        reject(err);
      } else {
        let {
          rows,
          rowLength
        } = result;
        if (rowLength > 0) {
          resolve(rows[0]['access_paths']);
        } else {
          resolve(errors.NotFound('User Id does not exist in the table'));
        }
      }
    });

  });
}

// Run an elasticsearch query to get user access path from elasticsearch
function getUserAccessPathsFromES(userId) {
  let query = {
    "query": {
      "term": {
        "userIds": userId
      }
    }
  };

  let options = {
    headers: esAuthHeaders,
    rejectUnauthorized: false,
    method: 'post',
    body: query,
    json: true,
    url: `${esUrl}/${accessControlIndex}/group/_search`
  };

  return new Promise((resolve, reject) => {
    request(options, function (error, response, body) {
      if (error) {
        reject(error);
      } else {
        let accessPaths = [];

        if (body && body.hits && body.hits.total > 0) {
          let hits = body.hits.hits;
          hits.forEach(hit => {
            let accessPath = hit._source.accessPaths;
            if (!accessPaths.includes(accessPath)) accessPaths = accessPaths.concat(accessPath);
          });
          resolve(accessPaths);
        } else resolve([]);
      }
    });
  })
}

/**
 * Asynchronously get access paths from cassandra and elasticsearch
 */

async function getUserAccessPaths(userId) {
  try {
    let paths = await Promise.all([getUserAccessPathsFromCass(userId), getUserAccessPathsFromES(userId)]);

    let accessPaths = [];
    paths.forEach(path => {
      path.forEach(p => {
        if (!accessPaths.includes(p)) accessPaths = accessPaths.concat(p);
      })
    });
    return accessPaths;
  } catch (err) {
    if (err.message && err.message.startsWith('Invalid string representation of Uuid')) {
      return 400;
    } else {
      log.error(err);
      return 500;
    }
  }
}

/**
 * Create invalidation for a distribution
 */
async function createInvalidation(distributionId, path) {
  try {
    if (path.includes('*')) {
      return errors.BadRequest(`Path must not include *`);
    }

    let invalidationPath, result;
    if (path.toLowerCase() === 'all') {
      invalidationPath = '/*';
    } else {
      invalidationPath = path[0] === '/' ? `${path}*` : `/${path}*`;
    }
    result = await helper.createInvalidation(distributionId, invalidationPath);
    return success.Success(`The cache is being cleared at ${invalidationPath}`, result.Invalidation);
  } catch (err) {
    if (err.code & err.message) {
      throw err;
    } else {
      log.error(err.toString());
      throw errors.InternalServerError();
    }
  }
}

function getDomainNameFromUrl(url) {
  return url.split('/')[2];
}

function validateIfAllPathsFromSameDomain(urls) {
  const domainName = getDomainNameFromUrl(urls[0]);
  for (let i = 1; i < urls.length; i++) {
    if (getDomainNameFromUrl(urls[i]) !== domainName) {
      return false;
    }
  }
  return true;
}

/**
 * Multiple path invalidation. Supports only one distribution now.
 * TODO: Add multiple distributions
 */
function createMultiplePathInvalidations(urls) {
  return new Promise(async (resolve, reject) => {
    if (!Array.isArray(urls)) {
      return reject(new Error("URLS passed must be an array"));
    }
    // Since we run invalidations for a distribution id
    // Making sure that if more than one path is sent, all of them belong to same domain.
    if (urls.length > 0) {
      if (!validateIfAllPathsFromSameDomain(urls)) {
        return reject(new Error("INVALID PATHS. ALL URL MUST BELONG TO SAME DOMAIN"));
      }
    }
    // All paths belong to same domain. 
    // Now we get the distribution id and run the invalidation.
    const domainName = getDomainNameFromUrl(urls[0]);

    // Now fetching the invalidation paths.
    // If the path is ending with a file name, we do not do anything, else we add a * to the path.
    // AWS accepts invalidation paths startning with '/'. Hence adding them
    const invalidationPaths = urls.map((url) => {
      let invalidationPath = url.split(domainName)[1];
      if (!path.extname(invalidationPath)) {
        invalidationPath = `${invalidationPath}*`
      }
      return invalidationPath;
    });

    log.info(`Invalidation paths received: ${invalidationPaths.join('|')}\nURL received: ${urls}`);

    try {
      // Now we get the distribution id from the domain name
      const distributionId = await helper.getCloudFronDistributionId(domainName);
      // Now calling the cloudfront invalidation
      helper.createMultiplePathInvalidations(distributionId, invalidationPaths)
        .then(() => resolve({
          domainName,
          invalidationPaths
        }))
        .catch(ex => reject(ex));
    } catch (ex) {
      reject(ex);
    }
  });
}
//--------------------------------------------------------------------------------

/**
 * Get the duration of the videos inside the path from S3
 */
async function getVideoLength(key, bucket) {
  try {
    if (key.includes('%2F')) {
      key = key.replaceAll('%2F', '/');
    }

    let objects = await helper.listObjectsByPath(bucket, key);

    if (objects.KeyCount === 0) {
      throw errors.NotFound(`The path does not exist`);
    }

    let extension = path.extname(key);
    let duration;

    if (audioExtensions.includes(extension) || supportedETSExtensions.includes(extension)) {
      let signedURL = helper.generateSignedURL(bucket, key);
      duration = await getVideoDurationInSeconds(signedURL);

      return success.Success('Fetched video duration successfully', {
        fileName: key,
        duration: duration,
        unit: 'sec',
        origin: "S3"
      });
    } else {
      throw errors.BadRequest(`This file type is not supported`);
    }
  } catch (err) {
    if (err.code && err.message) throw err;
    else {
      log.error(err);
      throw errors.InternalServerError();
    }
  }
}

/**
 *	Get video Length for location inside EC2
 */

async function getVideoLengthFromEC2File(key) {

  try {
    if (!key) {
      throw errors.BadRequest(`Invalid location value`);
    } else {
      if (fse.existsSync(key)) {
        let duration = await getVideoDurationInSeconds(key);
        if (duration) {
          return {
            filename: path.basename(key),
            duration,
            unit: 'sec',
            origin: "EC2"
          };
        }
      } else {
        throw errors.InternalServerError(`${key} not uploaded on the server`);
      }
    }
  } catch (err) {
    console.error(err); // eslint-disable-line
    if (err.code === 'ENOENT')
      throw errors.NotFound(`File does not exist on the server`)
    else
      throw errors.InternalServerError();
  }
}

//--------------------------------------------------------------------------------

async function getKey(url) {
  try {
    let key = await helper.getObject({
      Bucket: keysBucket,
      Key: url
    });

    if (key && key.Body) {
      let res = await helper.decrypt({
        CiphertextBlob: key.Body,
        EncryptionContext: {
          'service': 'elastictranscoder.amazonaws.com'
        }
      });
      return res.Plaintext;
    } else {
      throw errors.NotFound(`Key not found`);
    }
  } catch (err) {
    log.error(err);
    throw errors.InternalServerError();
  }
}

/**
 * Archive content in S3
 * Need to get rid of this API asap with a workaround
 * Not the best way - written 7 June 2019
 * Updated this API on - ...
 */
async function archiveAndUpload(location, root, type) {
  return new Promise(async (resolve, reject) => {
    try {
      // console.log("archiveAndUpload => location =>", location);
      // console.log("archiveAndUpload => type =>", type);
      const {
        authoringBucket,
        mainBucket
      } = getBucketsFromKey(location);
      // console.log("mainBucket=======>", mainBucket)
      // console.log("authoringBucket=======>", authoringBucket)
      let exists = await helper.exists(mainBucket, location);
      // console.log(`Is File exists @location=>${exists}`);


      const finalbucket = exists ? mainBucket : authoringBucket;
      // console.log("finalbucket Passed", finalbucket);
      const outputFileName = `${path.basename(location)}.zip`;
      // console.log(`${path.basename(location)}.zip ======================> Path`);
      let outputArchiveFilePath = await archiveS3Location(finalbucket, location, outputFileName);
      // console.log("outputArchiveFilePath ===================>", outputArchiveFilePath);
      // Getting the size of the file to be stored in the download information.
      let stats = fs.statSync(outputArchiveFilePath);
      // console.log("stats ===================>", stats);
      let size;
      if (stats) {
        size = stats.size;
      }

      try {
        const s3UploadLocation = `${location}/ecar_files/${outputFileName}`;
        await uploadArchiveFileToS3(finalbucket, s3UploadLocation, outputArchiveFilePath, true);

        // Replace the root location with '' for the api call
        let loc = s3UploadLocation.replaceAll(root + '/', '').replaceAll('/', '%2F');

        // Generating the artifact URL for authroing tool to consume
        let authArtifactUrl = `${appConfig.getProperty('NETWORK_ALIAS')}/contentv3/download/${loc}`;

        resolve(
          success.Success(
            `Archive created successfully`, {
              authArtifactUrl,
              downloadUrl: `${authArtifactUrl}?type=main`,
              sizeInBytes: size
            }
          )
        );
      } catch (e) {
        console.log(e);
        console.error('Exception: Could not upload the zip file to S3 properly');
        reject(errors.InternalServerError());
      }
    } catch (err) {
      console.error('Exception: Could not zip the file properly', err);
      throw errors.InternalServerError();
    }
  });
}

function archiveS3Location(bucket, s3Location, outputFileName) {
  return new Promise(async (resolve, reject) => {
    try {
      // console.log("archiveS3Location init");

      s3Location = s3Location.replaceSlashes();
      // console.log("archiveS3Location=>>", s3Location);

      // Check if the directory being archived is a lexid or not
      if (
        !s3Location.includes('lex_') &&
        !s3Location.includes('do_')
      ) {
        console.log("Error: lex_ or do_ not in s3 path xxxxxxxxxxxxxxxxxxxxx");
        throw errors.BadRequest(`You can only archive at content level`);
      } else if (
        !path.basename(s3Location).includes('lex_') &&
        !path.basename(s3Location).includes('do_')
      ) {
        console.log("Error: lex_ or do_ in s3 path xxxxxxxxxxxxxxxxxxxxx");
        throw errors.BadRequest(`You can only zip content at lex_id folder`);
      } else {
        // Directory on the server that will temporarily contain the zip file
        // console.log("Directory on the server that will temporarily contain the zip file");
        let temp = appConfig.getProperty('WEB_HOST_TEMP_DIR');
        // console.log("temp=================>>", temp);
        // Ensuring that the zipped directory exists before creating the file
        // console.log("Ensuring that the zipped directory exists before creating the file");
        fse.ensureDirSync(temp);
        // console.log("fse.ensureDirSync(temp): Passed");

        // Check if the content exists
        // console.log("Check if the content exists");
        // console.log("bucket, s3Location", bucket, s3Location);

        let exists = await helper.exists(bucket, s3Location);
        // console.log("Check if the content exists: Passed=>", exists);

        // Return a 404 if content not found
        if (!exists) {
          console.log("Content not found @bucket and s3", bucket, s3Location);

          throw errors.NotFound(`Content not found`);
        } else {
          // console.log("content exist we are in Else now");
          // console.log("temp==========>", temp);
          // console.log("outputFileName==============>", outputFileName);

          let zipFile = `${temp}/${outputFileName}`;
          console.log("Create a write stream to write to the server s3Location");
          // Create a write stream to write to the server s3Location
          let writeStream = fs.createWriteStream(zipFile);

          // console.log('Zip file location is: ', zipFile);

          // Initialize the archiver
          // console.log("Initialize the archiver");
          let archive = archiver('zip', {
            zlib: {
              level: 9
            }
          });
          // console.log("archiver Initialized");

          // Archiver warning handler
          archive.on('warning', function (err) {
            console.log("Throw =>archiver Onwarning", err);
            log.warn(err);
            throw errors.InternalServerError();
          });

          // Archiver error handler
          archive.on('error', function (err) {
            console.log("Throw =>archiver OnError", err);

            log.error(err);
            throw errors.InternalServerError();
          });

          archive.on('data', () => {});

          // console.log("Get all the content in the lex_id");

          // Get all the content in the lex_id
          if (!s3Location.endsWith('/')) {
            s3Location += '/';
          }
          console.log("s3Location====>", s3Location);
          console.log("bucket===>", bucket)
          let data = await helper.listObjectsByPath(bucket, s3Location);
          let {
            Contents,
            KeyCount
          } = data;
          // console.log("data ===> await passed with keyCount", KeyCount)
          // Pipe the archiver with the writestream
          archive.pipe(writeStream);
          // console.log("archive.pipe(writeStream) : Done")
          // Iterate through all the contents of lex id and add them to the archiver
          // console.log("Iterate through all the contents of lex id and add them to the archiver")
          for (let i = 0; i < KeyCount; i++) {
            let {
              Key
            } = Contents[i];

            // Exclude if the file is a zip file
            if (path.extname(Key) !== '.zip') {
              let name = Key.split(s3Location)[1];

              if (name !== '/') {
                archive.append(helper.getObjectStream({
                  Bucket: bucket,
                  Key: Key
                }), {
                  name: name
                });
              }
            }
          }
          // console.log("Run the archiver");
          // Run the archiver
          archive.finalize();
          // console.log("Run the archiver: finished");
          // When the writestream has closed, upload the content to S3 in the ecar_files folder of the lex_id
          writeStream.on('close', () => {
            console.log("writeStream.on('close') called")
            resolve(zipFile);
          });

          // TODO
          writeStream.on('error', (error) => {
            console.log("writeStream.on('error') called")
            reject(error);
          });
        }
      }
    } catch (e) {
      console.log("Error in archiveS3Location");
      console.error(e);
      reject(e);
    }
  });
}

async function uploadArchiveFileToS3(bucket, s3Location, file, deleteOnFinish) {
  return new Promise(async (resolve, reject) => {
    console.log('Uploading the file at location ', file, ' to ', s3Location);
    try {
      await helper.uploadContent(bucket, s3Location, fs.createReadStream(file));
      resolve(true);
    } catch (err) {
      console.error(err);
      log.error(err);
      reject(false);
    } finally {
      // Remove the lex_id data stored in the server
      if (deleteOnFinish) {
        fs.unlinkSync(file);
      }
    }
  });
}

/**
 * This function gets the transcoding status of a job submitted to the Elastic Transcoder
 */
async function getTranscodingStatus(path) {
  try {
    const {
      authoringBucket
    } = getBucketsFromKey(path)
    // Get the json meta file created by the lambda function as a buffer
    let videoMeta = await helper.getObject({
      Bucket: authoringBucket,
      Key: path + '.json'
    });

    // If the body is not present, then the lambda function didnt trigger, send error
    if (!videoMeta.Body || videoMeta.Body.length === 0) {
      // This situation should never arise ideally
      throw errors.InternalServerError('Meta file not found');
    } else {
      // Convert the buffer to a JSON
      let status = JSON.parse(videoMeta.Body.toString());

      if (status) {
        let id = status.Id;
        let transcodingStatus = await helper.getETSStatus(id);

        // Only send the appropriate fields as result
        let result = {
          Input: transcodingStatus.Job.Input,
          Status: transcodingStatus.Job.Status,
          Timing: transcodingStatus.Job.Timing
        }
        return result;
      } else {
        throw errors.InternalServerError('Could not parse meta file');
      }
    }
  } catch (err) {
    if (err.code && err.message) {
      if (err.code === 'NoSuchKey') {
        throw errors.BadRequest(`The file could not be found.`);
      } else {
        console.log(err.code);
        console.log('---');
        throw err;
      }
    } else {
      log.error(err);
      let error = errors.InternalServerError();
      throw error;
    }
  }
}

function startTranscoding(lexId, location, retryMode) {
  const clientName = 'content-service';
  let uri = `http://${appConfig.getProperty('transcoding_server_ip')}:${appConfig.getProperty('transcoding_server_port')}/transcode-video`;
  return new Promise((resolve, reject) => {
    request.post({
      method: 'POST',
      uri: uri,
      body: {
        lexId,
        videoPath: location,
        retryMode,
        clientName,
        webhookURL: appConfig.getProperty('transcoding_webhook_endpoint')
      },
      json: true
    }, (error, response, body) => {
      if (error) reject(error);
      else resolve({
        response,
        body
      });
    });
  })
}

async function checkIfFileExists(path) {
  try {

    let key = path.split(extractDomainName(path))[1].substr(1);

    const {
      imageBucket,
      mainBucket
    } = getBucketsFromKey(key)
    const bucket = path.includes('https://images') ? imageBucket : mainBucket;

    let params = {
      Bucket: bucket,
      Key: key
    };
    let data = await helper.headObject(params).catch((err) => {
      console.error(err);
    });
    return data;
  } catch (e) {
    console.error(e);
  }
  return false;
}

function getAllFilePaths(dirPath, result) {
  const filesList = fs.readdirSync(dirPath);

  for (let i = 0; i < filesList.length; i++) {
    const filePath = path.join(dirPath, filesList[i]);
    const stat = fs.statSync(filePath);
    if (stat.isFile()) {
      result.push(path.resolve(filePath));
    } else if (stat.isDirectory()) {
      getAllFilePaths(filePath, result);
    }
  }
}

function unzipContent(zipFileStream, outputLocation) {
  return new Promise((resolve, reject) => {
    // If there is any old directory, removing it
    fse.removeSync(outputLocation);
    // Creating the output directory
    fse.ensureDirSync(outputLocation);

    // Creating the extractor
    const unzipExtractor = unzip.Extract({
      path: outputLocation
    });

    // Handling their events
    unzipExtractor.on('error', (err) => {
      reject(err);
    });
    unzipExtractor.on('close', () => {
      console.log('Close event called');
      resolve();
    });

    // Performing the zip extraction.
    zipFileStream.pipe(unzipExtractor);
  });
}

const orgUtil = require('./org-util');

function uploadZipFile(fileStream, key) {
  console.log("uploadZipFile called");

  const outputLocation = path.join(appConfig.getProperty('resource_directory'), 'unzip-temp', key.split('/')[key.split('/').length - 1]);
  console.log("uploadZipFile ==============>", outputLocation);
  console.log('Output dir: ', outputLocation, 'Key: ', key);
  const uploadBaseKey = key.endsWith('/') ? key : key + '/';
  console.log('uploadBaseKey: ', uploadBaseKey);
  return new Promise((resolve, reject) => {
    unzipContent(fileStream, outputLocation).then(() => {
      console.log("unzipContent : done");

      // Arr to save file paths
      const filePaths = [];
      // Getting all the files from the zip location
      getAllFilePaths(outputLocation, filePaths);

      // Now creating the S3 location and file path mapping
      const fileAndS3Loc = [];
      filePaths
        .forEach(filePath => {
          fileAndS3Loc.push({
            fileLocation: filePath,
            s3Location: uploadBaseKey +
              filePath
              .split(outputLocation)[1]
              .split(path.sep)
              .filter(elem => elem.length !== 0)
              .join('/')
          });
        });
      console.log('File location and S3 paths: ', fileAndS3Loc);

      const {
        mainBucket
      } = orgUtil.getBucketsFromKey(key);

      console.log('Bucket Name: =============================>', mainBucket);
      const promiseArr = [];
      fileAndS3Loc.forEach(element => {
        promiseArr.push(helper.uploadContent(mainBucket, element.s3Location, fs.createReadStream(element.fileLocation)));
      });

      Promise.all(promiseArr).then((values) => {
        resolve();
        console.log('All unzipped files uploaded', values);
        // Removing the temporary directory
        fse.remove(outputLocation);

        // Removing the cache from the background
        const {
          CONTENT_CDN
        } = orgUtil.getCDNsFromKey(key);
        createMultiplePathInvalidations([`${CONTENT_CDN}/${key}`]);
      }).catch((ex) => {
        reject(ex);
        // Removing the temporary directory
        fse.remove(outputLocation);
      });
    }).catch((ex) => {
      reject(ex)
      // Removing the temporary directory
      fse.remove(outputLocation);
    });
  });
}

module.exports = {
  // Variables
  supportedETSExtensions,

  // Functions
  archiveAndUpload,
  createPath,
  checkPathExists,
  createInvalidation,
  copyContent,
  deletePath,
  downloadContent,
  downloadAssessmentContent,
  getKey,
  getVideoLength,
  getVideoLengthFromEC2File,
  getTranscodingStatus,
  moveContent,
  publish,
  pullback,
  setCookie,
  setCookieOnResource,
  startTranscoding,
  createInvalidation,
  createMultiplePathInvalidations,
  getKey,
  getVideoLength,
  getTranscodingStatus,
  imagesBehaviourRoute,
  uploadContent,
  contentRoot,
  checkIfFileExists,
  archiveS3Location,
  copyDirInSameBucket,
  uploadZipFile,
}
