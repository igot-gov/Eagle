/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
// Native Node Js Libraries
const path = require('path');
const fs = require('fs');

// Imported Libraries
const proxy = require('proxy-agent');
const AWS = require('aws-sdk');
const cf = require('aws-cloudfront-sign');

// App config to load environment variables and error status codes
const appConfig = require('../ConfigReader/loader');
const {
  errors
} = require('./status-codes');

// Logger
const log = require('../Logger/log');

// AWS Credentials
//--------------------------------------------------------------------------------

const region = 'ap-south-1';

AWS.config = new AWS.Config({
  accessKeyId: appConfig.getProperty('AWS_ACCESS_KEY'),
  secretAccessKey: appConfig.getProperty('AWS_SECRET_KEY'),
  region: region
});

// Set proxy if running locally
if (appConfig.getProperty('MY_PC')) {
  let proxyURL = appConfig.getProperty('http-proxy');
  AWS.config.update({
    httpOptions: {
      agent: proxy(proxyURL)
    }
  });
}

// Cloudfront Credentials
//--------------------------------------------------------------------------------
const CLOUDFRONT_KEY_PAIR_ID = appConfig.getProperty('AWS_CF_ACCESS_KEY');
const PRIVATE_KEY_PATH = appConfig.getProperty('AWS_PVT_KEY_FILE_PATH');

let PRIVATE_KEY_STRING;
if (PRIVATE_KEY_PATH) {
  if (fs.existsSync(PRIVATE_KEY_PATH)) {
    PRIVATE_KEY_STRING = fs.readFileSync(PRIVATE_KEY_PATH).toString();
  } else {
    log.error(
      'The cloudfront PRIVATE_KEY_PATH is not correct in the env'
    );
  }
}
//--------------------------------------------------------------------------------

let s3 = new AWS.S3();
let kms = new AWS.KMS();
let cloudfront = new AWS.CloudFront();
let ets = new AWS.ElasticTranscoder();
/*
    Promisified aws-sdk functions
*/
//--------------------------------------------------------------------------------

/**
 * Promisify kms.decrypt()
 */

function decrypt(params) {
  return kms.decrypt(params).promise();
}

// Promisify s3.listObjectsV2
/**
 * List 1000 keys at a time
 */

function listObjectsPromise(params) {
  return s3.listObjectsV2(params).promise();
}
/**
 * Promisify s3.upload()
 */
function upload(params) {
  return s3.upload(params).promise();
}

/**
 * Promisify s3.getObject()
 */
function getObject(params) {
  return s3.getObject(params).promise();
}

function getObjectStream(params) {
  return s3.getObject(params).createReadStream();
}

/**
 * Promisify s3.deleteObjects()
 */
function deleteObjects(params) {
  return s3.deleteObjects(params).promise();
}

/**
 * Promisify s3.copyObject()
 */
function copyObject(params) {
  return s3.copyObject(params).promise();
}

/**
 * Promisify s3.headObject()
 */

function headObject(params) {
  return s3.headObject(params).promise();
}

const contentUtil = require('../Content/util');
// const { Transform } = require('stream');

// const newHtmlTransform = new Transform({
//   transform: (chunk, encoding, done) => {
//       const result = chunk.toString().toUpperCase()
//       done(null, result)
//   }
// })

/**
 * Promisify s3.upload()
 */

function uploadContent(bucket, key, file) {
  console.log('Started the content upload process...');
  return new Promise((resolve, reject) => {
    // console.log(file.pipe(newHtmlTransform))

    /*if (!avUtil.getClamScan()) {
      return reject({
        error: 'Unable to connect to AV endpoint'
      });
    }

    const av = avUtil.getClamScan().passthrough();
    const outputStream = new stream.PassThrough();

    file.pipe(av).pipe(outputStream);

    let isScanCompleted = false;
    let isOutputSaved = false;

    av.on('error', error => {
      if ('data' in error && error.data.is_infected) {
        console.error("Input stream containes a virus(es):", error.data.viruses);
      } else {
        console.error(error);
      }
      return reject({
        result: 'VIRUS DETECTED',
        virus: error.data.viruses,
      });
    }).on('finish', () => {
      console.log("All data has been sent to virus scanner");
    }).on('end', () => {
      console.log("All data has been scanned sent on to the destination!");
    }).on('scan-complete', result => {
      isScanCompleted = true
      console.log("Scan Complete: Result: ", result);
      if (result.is_infected === true) {
        console.log(`You've downloaded a virus (${result.viruses.join(', ')})! Don't worry, it's only a test one and is not malicious...`);
        return reject({
          result: 'VIRUS DETECTED',
          virus: result.viruses.join(', '),
        });
      } else if (result.is_infected === null) {
        console.log('ISSUE WHILE SCANNING THE FILE');
        return reject({
          result: 'ISSUE WHILE SCANNING THE FILE'
        });
      } else {
        console.log(`Input stream does not contain any kind of virus....Resolving!`);
        if (isOutputSaved) {
          return resolve({
            result: 'OK',
          });
        } else {
          console.log('Output is not finished writing yet...');
        }
      }
    });

    outputStream.on('finish', () => {
      isOutputSaved = true;
      console.log("Data has been fully written to the outputStream...");
      // Sending a response only if the scan was successfull
      if (isScanCompleted) {
        
      }
    });

    outputStream.on('error', error => {
      console.log("Final Output Fail: ", error);
      return reject({
        result: 'Error while writing to output stream'
      });
    });
    av.on('scan-complete', result => {
      console.log('Scanning completed');
      const { is_infected, viruses } = result;
      // Do stuff if you want
      if (is_infected) {
        isVirusDetected = true;
        reject({
          error: 'Virus found in input file',
          viruses: viruses.join(', ')
        })
      }
    });*/

    let params = {
      Bucket: bucket,
      Key: key,
      Body: file
    };

    console.log('File name Key: ', key);
    // File name Key:  content-store/igot/dopt/Public/lex_auth_01309428963604070456/web-hosted/CB_Negotiation-Understanding_Perspective_COLLABORATE/analytics-frame.html
    try {
      params['ContentType'] = contentUtil.getMimeTypeFromFileName(key);
    } catch (e) {
      // If mimeType is not found, this defaults to the ContentType added by S3.
      console.error(e);
    }
    s3.upload(params, (err, data) => {
      if (err) reject(err);
      else resolve(data);
    }).on('httpUploadProgress', evt => {
      console.log(evt); // eslint-disable-line
    });
  });
}

// Function for stream upload, returns stream
// Flow stream upload to s3, then scan
// Ideally should be scan and upload to S3
// Antivirus implementation can be done using this function
// Upload to S3 using this function and the stream it returns,
// Pass that to the antivirus function.
// If the file has a virus, you will have to call the delete function
// to delete whatever has been uploaded to S3
const uploadStream = (bucket, key, stream) => {

  return {
    writeStream: pass,
    promise: s3.upload({
      Bucket: bucket,
      Key: key,
      Body: stream
    }).promise(),
  };
}

/**
 * Promisify cloudfront.createInvalidation()
 */
function createInvalidationPromise(params) {
  return cloudfront.createInvalidation(params).promise();
}

/**
 * Promisify s3.getSignedURL()
 */
function getSignedUrl(params) {
  return s3.getSignedUrl('getObject', params);
}

/**
 * Promisify ets.readJob
 */
function readJob(params) {
  return ets.readJob(params).promise();
}

//--------------------------------------------------------------------------------

/*
    Implementations for aws-sdk functions
*/
//--------------------------------------------------------------------------------

/**
 * List all the objects in the bucket as the aws sdk listobjects function lists out 1000 keys at max by default
 */
async function listAllObjects(params) {
  try {
    // -- List the first 1000 objects in the S3 bucket --
    let data = await listObjectsPromise(params);

    // A main array where all the Content data will be stored
    let result = data.Contents;
    let keyCount = data.KeyCount;

    if (keyCount === 1000) {
      // Store the last element in the array to list objects from there
      let startAfter = data.Contents[999].Key;

      while (data.KeyCount === 1000) {
        // List objects after the previous 1000th element
        params.StartAfter = startAfter;
        data = await listObjectsPromise(params);

        // Define to start listing after next 1000th element
        startAfter = data.Contents[data.KeyCount - 1].Key;
        keyCount += data.KeyCount;

        // Store in result array
        result = result.concat(data.Contents);
      }

      return {
        Contents: result,
        KeyCount: keyCount
      };
    }
    // If key count is less than 1000
    else {
      return {
        Contents: data.Contents,
        KeyCount: keyCount
      };
    }
  } catch (err) {
    throw err.toString();
  }
}

/**
 * Removes the limit of the s3.listObjects()
 */
async function listObjects(params) {
  // List out the first 1000 objects
  let objects = await listObjectsPromise(params);

  // If the keycount is 1000, list out every object
  if (objects.KeyCount === 1000) {
    objects = await listAllObjects(params);
  }

  return objects;
}

/**
 * Check if the key exists in the bucket
 */
async function exists(bucket, key) {
  key = key[key.length - 1] === '/' ? key : key + '/';

  // List objects and if the key count is 0, return true, else return false
  try {
    let objects = await listObjectsPromise({
      Bucket: bucket,
      Prefix: key
    });

    if (objects.KeyCount === 0) {
      return false;
    }
    return true;
  } catch (error) {
    console.error(error); // eslint-disable-line
    throw error;
  }
}

/**
 * Create a folder in the bucket
 */
function createFolder(bucket, key) {
  // Since the key must always end with /, ensure that it does
  // key[key.length - 1] !== '/' ? (key += '/') : key;
  if (!key.endsWith('/')) {
    key += '/';
  }

  let params = {
    Bucket: bucket,
    Key: key,
    Body: 'body does not matter'
  };

  return upload(params);
}

/**
 * Delete all objects
 * s3.deleteObjects() is limited to delete 1000 keys at a time
 * This function deletes all the objects in the bucket by running deleteObjects till the bucket is empty
 * It returns an array of promises which need to be resolved.
 */
function deleteAllObjects(bucket, objects) {
  let params = {
    Bucket: bucket
  };

  let deleteOperations = [];
  while (true) { // eslint-disable-line
    let remainingObjects = objects.splice(1000);
    params.Delete = {
      Objects: objects
    };
    deleteOperations.push(deleteObjects(params));

    if (remainingObjects.length === 0) {
      break;
    } else {
      objects = remainingObjects;
    }
  }
  return deleteOperations;
}

/**
 * Deletes the folder and it's content
 */
async function deletePath(bucket, key) {
  try {
    // List out everything in the key
    let objects = await listObjects({
      Bucket: bucket,
      Prefix: key
    });

    if (objects.KeyCount === 0) throw 'Folder does not exist';
    // Take the keys and put them in the objects array
    // Delete everything
    else {
      let contents = objects.Contents;
      let keys = [];
      contents.forEach(content => keys.push({
        Key: content.Key
      }));
      let result = await deleteAllObjects(bucket, keys);
      return result;
    }
  } catch (error) {
    console.error(error); // eslint-disable-line
    throw error;
  }
}

/**
 * Download the object from the bucket
 */
async function download(bucket, key, req, res, shouldStream) {
  // Check whether the object exists or not
  let params = {
    Bucket: bucket,
    Key: key
  };

  let data = await headObject(params).catch(err => err);

  // If the content is not found
  if (data.code === "NotFound") {
    res.status(404).send(
      errors.NotFound(`${key} not found in ${bucket}`)
    );
  }
  // Else stream the content
  else {
    let extension = path.extname(key).toLowerCase();
    let mimeType;
    mimeNames[extension] ?
      (mimeType = mimeNames[extension]) :
      (mimeType = '');

    // Set Content-Type
    res.set('Content-Type', mimeType);
    let size = data.ContentLength;

    // Set up range headers if any
    let range = req.headers.range;

    if (range) {
      let [start, end] = range.replace(/bytes=/, '').split('-');
      start = parseInt(start, 10);
      end = end ? parseInt(end, 10) : size - 1;

      res.writeHead(206, {
        'Content-Range': `bytes ${start}-${end}/${size}`,
        'Accept-Ranges': 'bytes',
        'Content-Length': end - start + 1
      });

      params.Range = range;
    } else {
      if (!shouldStream) {
        res.setHeader('Content-Disposition', 'attachment; ' + 'fileName' + '=' + path.basename(key));
      }
    }

    s3.getObject(params)
      .createReadStream()
      .pipe(res);
  }
}

/**
 * Copy from source to destination
 */
async function copy(sourceBucket, destinationBucket, source, destination, includeFolder) {
  try {
    // Remove '/' from the end of the destination if exists, because it will cause problems
    destination = destination[destination.length - 1] === '/' ? destination.slice(0, destination.length - 1) : destination;
    // Add '/' to the end of the source, otherwise it will cause problems
    source = source[source.length - 1] === '/' ? source : source + '/';

    // Boolean values to check if the source and destinations exist
    let destinationFound = true;
    let sourceFound = false;

    // Check if the source and destination exists
    let result = await listObjects({
      Bucket: sourceBucket,
      Prefix: source
    });

    if (result.KeyCount === 0) {
      sourceFound = false;
    } else {
      sourceFound = true;
    }

    if (sourceFound && destinationFound) {
      let sourceContents = result;

      // Promises to be resolved asynchronously
      let promises = [];

      // Successfull and failed copies
      let success = [],
        failed = [];
      let successCount = 0,
        failedCount = 0;

      // Iterate over all the keys in the source and copy them to the destination
      sourceContents.Contents.forEach(content => {
        // Generate destination and source for the S3 API
        let src = content.Key;
        let copySource = `/${sourceBucket}/${src}`;
        let dest = destination;

        // If copy the entire folder and it's contents

        if (includeFolder) {
          let keys = src.split(path.dirname(source));
          keys[1] ? (dest += keys[1]) : dest;
        }
        // else copy the content inside the folder, excluding the folder
        else {
          let keys = src.split(source);
          keys[1] ? (dest += '/' + keys[1]) : dest;
        }

        if (dest !== destination) {
          // Insert the promises into an array
          promises.push(
            // Push the copy promises
            copyObject({
              Bucket: destinationBucket,
              CopySource: copySource,
              Key: dest
            }).then(result => { // Success in the success array
              success.push(`${src} -to- ${dest}`);
              successCount += 1;
              return result;
            }).catch(err => { // Failure in the failed array
              failed.push(src);
              failedCount += 1;
              return err;
            })
          );
        }
      });

      // Resolve all the promises
      await Promise.all(promises);

      // Return the response
      return {
        Success: success,
        SuccessCount: successCount,
        Failed: failed,
        FailedCount: failedCount
      };
    } else {
      if (!sourceFound)
        throw errors.NotFound(
          `${source} not found in ${sourceBucket}`
        );
      if (!destinationFound)
        throw errors.NotFound(
          `${destination} not found in ${destinationBucket}`
        );
    }
  } catch (error) {
    console.error(error); // eslint-disable-line
    throw error;
  }
}

function copyInSameBucket(sourceDir, destinationDir, bucketName) {
  return new Promise(async (resolve, reject) => {
    if (!sourceDir || !destinationDir || !bucketName) {
      return reject({
        code: 500,
        msg: 'Source Dir, Destiation Directory and Bucket Name are mandatory fields',
      });
    }

    // To get all children, we add the extra / to the path
    if (!sourceDir.endsWith('/')) {
      sourceDir += '/';
    }
    // For copying later, we need to check if the path provided ends with a / or not
    if (!destinationDir.endsWith('/')) {
      destinationDir += '/';
    }

    let result = await listObjects({
      Bucket: bucketName,
      Prefix: sourceDir
    });

    if (result.KeyCount === 0 || result.Contents.length === 0) {
      return reject({
        code: 404,
        msg: 'Source Directory not Found',
      });
    }

    const copyResults = {
      failed: [],
      success: [],
    };

    const copyPromisesArr = [];
    result.Contents.forEach((file) => {
      const params = {
        Bucket: bucketName,
        CopySource: bucketName + '/' + file.Key,
        Key: file.Key.replace(sourceDir, destinationDir),
      };

      copyPromisesArr.push(
        copyObject(params).then(() => {
          copyResults.success.push(file.Key);
        }).catch((e) => {
          console.error(e);
          copyResults.failed.push(file.Key);
        })
      );
    });

    // Resolve all the promises
    await Promise.all(copyPromisesArr);

    if (result.Contents.length === copyResults.success.length) {
      resolve(copyResults);
    } else {
      reject(copyResults);
    }
  });
}


/**
 *  Generate cookies for cloudfront authentication
 */
function getCFCookies(key) {
  console.log("getCFCookies====>", key);

  let options = {
    keypairId: CLOUDFRONT_KEY_PAIR_ID,
    privateKeyString: PRIVATE_KEY_STRING,
    expireTime: new Date().getTime() + 8.64e7 // 1 day
  };
  console.log("Generate an object with signed cookies to authenticate CF requests=>");

  // Generate an object with signed cookies to authenticate CF requests
  let signedCookies = cf.getSignedCookies(key, options);
  console.log("signedCookies===>", signedCookies);

  return signedCookies;
}

/**
 *  Zip content on S3
 */
function archiveContent(bucket, key, outputKey) {

}

//--------------------------------------------------------------------------------

// List of mime types
const mimeNames = {
  '.css': 'text/css',
  '.html': 'text/html',
  '.js': 'application/javascript',
  '.json': 'application/json',
  '.mp3': 'audio/mpeg',
  '.mp4': 'video/mp4',
  '.ogg': 'application/ogg',
  '.ogv': 'video/ogg',
  '.oga': 'audio/ogg',
  '.txt': 'text/plain',
  '.wav': 'audio/x-wav',
  '.webm': 'video/webm',
  '.pdf': 'application/pdf',
  '.epub': 'application/epub+zip',
  '.jpg': 'image/jpeg',
  '.jpeg': 'image/jpeg',
  '.png': 'image/png',
  '.zip': 'application/zip',
  '.rar': 'application/x-rar-compressed'
};

function getCloudFronDistributionId(domainName) {
  return new Promise((resolve, reject) => {
    cloudfront.listDistributions({}).promise()
      .then((response) => {
        const cloudFrontDistributions = response.DistributionList.Items;

        for (let i = 0; i < cloudFrontDistributions.length; i++) {
          if (cloudFrontDistributions[i].DomainName === domainName) {
            return resolve(cloudFrontDistributions[i].Id);
          }
          for (let j = 0; j < cloudFrontDistributions[i].Aliases.Items.length; j++) {
            if (cloudFrontDistributions[i].Aliases.Items[j] === domainName) {
              return resolve(cloudFrontDistributions[i].Id);
            }
          }
        }
      })
      .catch(ex => console.error(ex));
  });
}



function createMultiplePathInvalidations(distributionId, invalidationPaths) {
  return cloudfront.createInvalidation({
    DistributionId: distributionId,
    InvalidationBatch: {
      CallerReference: new Date().getTime().toString(),
      Paths: {
        Quantity: invalidationPaths.length,
        Items: invalidationPaths,
      },
    },
  }).promise();
}

/**
 * Create invalidation for the distribution
 */
function createInvalidation(distributionId, path) {
  let callerReference = Date.now().toString();

  return createInvalidationPromise({
    DistributionId: distributionId,
    InvalidationBatch: {
      CallerReference: callerReference,
      Paths: {
        Quantity: 1,
        Items: [path]
      }
    }
  });
}

/**
 * List objects in the bucket at the given path
 */
function listObjectsByPath(bucket, key) {
  return listObjects({
    Bucket: bucket,
    Prefix: key
  });
}

/**
 * Generate signed URL to temporarily access s3 object
 */
function generateSignedURL(bucket, key) {
  let params = {
    Bucket: bucket,
    Key: key
  };
  return getSignedUrl(params);
}

/**
 * Get the status of ETS Job
 */
function getETSStatus(id) {
  return readJob({
    Id: id
  });
}



module.exports = {
  copy,
  createFolder,
  getCloudFronDistributionId,
  createInvalidation,
  createMultiplePathInvalidations,
  decrypt,
  deletePath,
  download,
  exists,
  generateSignedURL,
  getCFCookies,
  getObject,
  getObjectStream,
  headObject,
  listObjectsByPath,
  uploadContent,
  getETSStatus,
  copyInSameBucket,
};
