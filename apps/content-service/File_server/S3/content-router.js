/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
const path = require('path');
const fs = require('fs');
const stream = require('stream');
const fse = require('fs-extra');
const express = require('express');
const request = require('request');
const avUtil = require('../AvUtil/util');
const utility = require('./util');
const { errors, success } = require('./status-codes');
const appConfig = require('../ConfigReader/loader');
const { getBucketsFromKey, getCDNFromRootOrg, CDN_TYPES } = require('./org-util');
// Logger
const log = require('../Logger/log');
// Busboy for file upload
const busboy = require('connect-busboy');

const router = express.Router();
router.use(busboy());

// Name of the root folder
const contentRoot = utility.contentRoot;

function increaseTimeout(delay) {
  return function (req, res, next) {
    res.connection.setTimeout(delay);
    next();
  };
}

function pathValidation(req, res, next) {
  let location = req.params.location;

  if (location) {
    let pattern = /[/.A-Za-z%0-9_-]+/g;

    if (!pattern.test(location)) {
      res.send(errors.BadRequest(`Invalid character found in the URL`));
    } else {
      next();
    }
  } else {
    res.send(errors.BadRequest(`location is not present in the params`));
  }
}

/**
 * @apiDescription API to create a directory for the content. This will create the directory structure for the content to be stored. The directory structure is flexible and will be as follows
 * 	organization
 * 	--contentID
 * 		--assets = contains streamable content
 * 		--artifacts = contains non streamable content
 *  global_contentID
 *  On successful creation, a valid 201 response will be sent back as a response and if the directory already exists,an error code and if there is a problem creating the directory, the server error will be returned.
 *
 * @api {POST} /directory/* Request to create a content directory
 * @apiName CreateDirectory
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {String} message A verbose status message about the status of the API call
 * @apiSuccessExample Success response: 201
 * 	HTTPS 201 Created
 * {
 *   "code": 201,
 * }
 *  @apiError (Response Error) Conflict:409 Content directory already exists
 *  @apiErrorExample Error-Response: 409
 *  HTTP/1.1 409 Conflict
 * {
 *    "code": 409,
 *    "error": "Conflict",
 * }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 *  HTTP/1.1 500 InternalServerError
 * {
 *    "code": 500,
 *    "error": "Internal Server Error"
 * }
 *
 */

router.post('/directory/:location', pathValidation, (req, res) => {
  // Remove the root
  let key = `${contentRoot}/${req.params.location}`;
  utility
    .createPath(key)
    .then(result => {
      log.info(result.message);
      res.status(result.code).send(result);
    })
    .catch(error => {
      if (error.message && error.code !== 500)
        log.error(error.message);
      res.status(error.code).send(error);
    });
});

async function checkPath(key, res, bucket) {
  try {
    let doesExist = await utility.checkPathExists(key, bucket);
    if (doesExist) {
      return res.status(204).send();
    }
    res.status(404).send({
      code: 404,
      msg: 'Path does not exist',
    });
  } catch (ex) {
    console.error(ex);
    res.status(500).send({
      code: 500,
      msg: 'Exception while performing the operation',
    });
  }
}

/**
 * API to get know if the directory exists at the location or not.
 */

router.head('/directory/:location', pathValidation, (req, res) => {
  let key = `${contentRoot}/${req.params.location}`;
  const { authoringBucket } = getBucketsFromKey(key);

  checkPath(key, res, authoringBucket);
});

/**
 * API to know if the images directory exists at the location or not.
 */
router.head('/images-directory/:location', pathValidation, (req, res) => {
  let key = `${contentRoot}/${req.params.location}`;
  const { imageAuthoringBucket } = getBucketsFromKey(key);

  checkPath(key, res, imageAuthoringBucket);
});


/**
 * @apiDescription API to delete an existing content directory. This will delete a directory and return a valid 200 status code on deletion of the directory. On any error or if the directory does not exists, the error response for the same is returned
 *
 * @api {DELETE} /directory/* Request to delete a content directory
 * @apiName DeleteDirectory
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 *
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {String} message A verbose status message about the status of the API call
 * @apiSuccessExample Success response: 202
 * 	HTTPS 202 Accepted
 * {
 *    "code": 202,
 * }

 *  @apiError (Response Error) NotFound:404 Content directory not found
 *  @apiErrorExample Error-Response: 404
 *  HTTP/1.1 404 NotFound
 * {
 *     "code": 404,
 *     "error": "Not Found",
 * }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 *  HTTP/1.1 500 InternalServerError
 * {
 *    "code": 500,
 *    "error": "Internal Server Error"
 * }
 */
router.delete('/directory/:location', pathValidation, (req, res) => {
  let force = req.query.force;
  let key = `${contentRoot}/${req.params.location}`;

  if (force === 'true') force = true;
  else force = false;

  utility
    .deletePath(key, force)
    .then(result => {
      log.info(result.message);
      res.status(result.code).send(result);
    })
    .catch(error => {
      if (error && error.message && error.code !== 500) {
        log.error(error.message);
        if (error.code) {
          res.status(error.code).send(error);
        }
      }
      res.status(500).send({
        code: 500,
        msg: 'Error while processing the request',
      });
    });
});

/**
 * @apiDescription API to upload a file to the content directory
 *  On successful creation, a valid 201 response will be sent back as a response and if the directory already exists,an error code and if there is a problem creating the directory, the server error will be returned.
 *
 * @api {POST} /upload/:location Request to upload a file
 * @apiName UploadFle
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 *
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {String} message A verbose status message about the status of the API call
 * @apiSuccess {String} contentURL The url where this content will be available after the successful upload
 * @apiSuccessExample Success response: 201
 *  HTTPS 201 Created
 * {
 *     "code": 201,
 * }
 *
 *  @apiError (Response Error) Not Found:404 A content directory with this contentId and  organization does not exist
 *  @apiErrorExample Error-Response: 404
 *  HTTP/1.1 404 Not Found
 * {
 *     "code": 404,
 *     "error": "Not Found",
 * }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 *  HTTP/1.1 500 InternalServerError
 * {
 *    "code": 500,
 *    "error": "Internal Server Error"
 * }
 */

String.prototype.replaceAll = function (search, replacement) {
  let target = this;
  return target.replace(new RegExp(search, 'g'), replacement);
};

router.post('/upload/:location', pathValidation, [increaseTimeout(5 * 60 * 1000)], (req, res) => {

  let key = contentRoot + '/' + req.params.location.replace(/%2f/g, '%2F');

  // If key does not end with /, append it
  if (!key.endsWith('/')) {
    key += '/';
  }

  // If type is sent as query, add it to key
  if (req.query.type) {
    let type = req.query.type;
    if (!type.endsWith('/')) {
      type += '/'
    }
    key += type;
  }

  try {
    req.pipe(req.busboy);
    let fileStream;
    let writingToFile = false;

    const inputFile = new stream.PassThrough();
    req.busboy.on('file', async function (_, file, fileName) {
      // Validating if the input stream is proper and virus-free
      /*try {
        pump(cloneable(file).clone(), inputFile);

        const streamForAVCheck = new stream.PassThrough();
        cloneable(file).clone().pipe(streamForAVCheck);

        await avUtil.checkVirusInInputStream(streamForAVCheck);
        console.log('File is scanned with no virus detected');
      } catch (e) {
        console.error(e);
        if (e && e.result === 'VIRUS DETECTED') {
          return res.status(400).send({
            msg: 'Virus file detected in upload',
            foundVirus: e.virus,
          });
        }
        return res.status(500).send({
          code: 500,
          msg: 'Exception while trying to do the antivirus check',
        });
      }*/

      // console.log('Stream is now: ', file);

      console.log('Key name is: ', key);
      // If the file is a video upload to EC2
      if (
        utility.supportedETSExtensions.includes(
          path.extname(fileName).toLowerCase()
        )
        && (!key.includes('%2Fweb-hosted%2F') && !key.includes('/web-hosted/'))
        && (!key.includes('%2Fmeta-assets%2F') && !key.includes('/meta-assets/'))
      ) {
        let loc = `${appConfig.getProperty('resource_directory')}/${req.params.location}/${fileName}`;

        fse.ensureDirSync(path.dirname(loc));
        fileStream = fs.createWriteStream(loc);

        try {
          // inputFile.pipe(fileStream);
          file.on('error', (error) => {
            console.error(error);
            response = errors.InternalServerError('Error while writing file to FileSystem');
            return res.send(response.code).send(response);
          });

          await avUtil.checkForVirusInStreamCopy(file, fileStream);

          let downloadKey = req.params.location + '/' + fileName;
          downloadKey = downloadKey.replaceAll('/', '%2F');

          // Calculate the authArtifactURL and downloadURL using the download key
          const authArtifactURL = `http://${appConfig.getProperty('NETWORK_ALIAS')}/contentv3/download/${downloadKey}`;
          const downloadURL = `http://${appConfig.getProperty('NETWORK_ALIAS')}/contentv3/download/${downloadKey}?type=main`;

          // Get the length of the video file after upload
          let videoLength = {
            success: false
          };
          try {
            let videoLengthResult = await utility.getVideoLengthFromEC2File(loc);

            // Check if got the video length of the file successfully
            if (videoLengthResult.duration) {
              videoLength = {
                success: true,
                fileName: videoLengthResult.fileName,
                duration: videoLengthResult.duration,
                unit: videoLengthResult.unit,
              };
            }
          } catch (e) {
            console.error('Could not calculate the video duration');
          }
          // Send the success response
          let response = success.Success(`${key} uploaded successfully`, {
            authArtifactURL,
            downloadURL,
            videoLength
          });

          const successCode = 201;
          res.status(response.code != 200 ? response.code : successCode).send(response);
        } catch (err) {
          fs.unlinkSync(loc);
          // Send error response
          console.error(err);
          let response;
          if (e.virus) {
            response = errors.BadRequest(`Malicious file uploaded`, {
              threat: e.virus
            });
          } else {
            response = errors.InternalServerError();
          }
          res.send(response.code).send(response);
        }
      }
      // Else upload directly to S3
      else {
        utility
          .uploadContent(key, fileName, file)
          .then(result => {
            log.info(result.message);
            res.status(result.code).send(result);
          })
          .catch(error => {
            if (error.message && error.code !== 500)
              console.error(error.message);
            res.status(error.code).send(error);
          });
      }
    });
  } catch (err) {
    res.status(500).send({
      error: 'Error',
      msg: 'File uploaded is mandatory for this request'
    });
  }
});

router.post('/transform-and-upload/:decodeType/:location', pathValidation, [increaseTimeout(5 * 60 * 1000)], (req, res) => {
  transformOrCreateAndUploadToS3(req, res, req.params.decodeType);
});

router.post('/create-and-upload/:location', pathValidation, [increaseTimeout(5 * 60 * 1000)], (req, res) => {
  transformOrCreateAndUploadToS3(req, res);
});

function transformOrCreateAndUploadToS3(req, res, decodeType) {
  let text = req.body.text;
  const fileName = req.body.fileName;

  if (text && fileName) {
    if (decodeType) {
      const defaultDecodeType = decodeURIComponent(text);
      switch (decodeType) {
        case 'urldecode':
          text = decodeURIComponent(text);
          break;
        // TODO
        /*case 'unescapeHtml':
          break;*/
        case 'base64':
          text = Buffer.from(text, 'base64').toString('utf-16le');
          /*const sBinaryString = Buffer.from(text, 'base64').toString('binary');
          const aBinaryView = new Uint8Array(sBinaryString.length);
          Array.prototype.forEach.call(
            aBinaryView, (_el, idx, arr) => arr[idx] = sBinaryString.charCodeAt(idx)
          );
          text = new Uint16Array(aBinaryView.buffer).reduce((str, byte) => str + String.fromCharCode(byte), '');*/
          break;
        default:
          text = defaultDecodeType;
      }
    }

    console.log('Text is: ', text);

    let key = contentRoot + '/' + req.params.location.replace(/%2f/g, '%2F');

    // If key does not end with /, append it
    if (!key.endsWith('/')) {
      key += '/';
    }

    // If type is sent as query, add it to key
    if (req.query.type) {
      let type = req.query.type;
      if (!type.endsWith('/')) {
        type += '/'
      }
      key += type;
    }

    utility
      .uploadContent(key, fileName, Buffer.from(text))
      .then(result => {
        log.info(result.message);
        res.status(result.code).send(result);
      })
      .catch(error => {
        if (error.message && error.code !== 500)
          log.error(error.message);
        res.status(error.code).send(error);
      });
  } else {
    res.send(errors.BadRequest('content and filename are required'));
  }
}

/**
 * @apiDescription API to fetch the content from s3.
 *
 * @api {GET} /download/root/* Request to fetch a file from s3
 * @apiName FetchFile
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 *
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {String} message A verbose status message about the status of the API call
 * @apiSuccessExample Success response: 200
 * 	HTTPS 200 OK (File BLOB)

 *  @apiError (Response Error) NotFound:404 Content directory not found
 *  @apiErrorExample Error-Response: 404
 *  HTTP/1.1 404 NotFound
 *  {
 *    "message": "Requested resource is not found",
 *    "error": "not found"
 *  }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 *  HTTP/1.1 500 InternalServerError
 *  {
 *     "code": 500,
 *     "error": "Internal Server Error"
 *  }
 */
router.get('/download/:location', pathValidation, (req, res) => {
  let key = req.params.location;

  // Path is a video and type is not main or  then serve from EC2
  if (!req.query.type
    && !key.includes('/web-hosted/')
    && utility.supportedETSExtensions.includes(path.extname(key).toLowerCase())) {
    console.log('Streaming from ' + path.join(appConfig.getProperty('resource_directory'), key));
    // fs.createReadStream(path.join(appConfig.getProperty('resource_directory'), key )).pipe(res);

    const downloadLocation = path.join(appConfig.getProperty('resource_directory'), key);
    res.download(downloadLocation);
  } else {
    key = contentRoot + '/' + req.params.location;
    utility.downloadContent(key, req, res);
  }
});

/**
 * @api {GET} /download-assessment/:location Request to download the assessment key file
 * @apiName GetAssementKet
 * @apiGroup Assessment
 *
 * @apiParam {Location} location location of the assessment key in the S3
 *
 * @apiSuccess {File} Assessment key file.
 */
router.get('/download-assessment-key/:location', pathValidation, (req, res) => {
  let key = contentRoot + '/' + req.params.location;
  utility.downloadAssessmentContent(key, res);
});

/**
 * @apiDescription API to copy the data of a content directory
 *
 * @api {POST} /copy/root/* /to/* Request to copy the content.
 * @apiName CopyDirectory
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 *
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {JSON} response Data for the success and failure movements.
 * @apiSuccessExample Success response: 200
 * HTTP/1.1 200 Success
 * {
 *     "code": 200,
 *     "message": "All files were copied successfully from root/destorg to root/infy/"
 * }
 * @apiSuccessExample Success response : 207
 * HTTP/1.1 207 Multi-Status
 * {
 *    "code": 207,
 *    "message": "Not all files were copied successfully. Copy failed.",
 *    "Success": [
 *        "root/destorg/assets/ -to- root/infy/assets/",
 *        "root/destorg/111.JPG -to- root/infy/111.JPG",
 *        "root/destorg/aaa.jpg -to- root/infy/aaa.jpg",
 *        "root/destorg/assets/chmodThings.PNG -to- root/infy/assets/chmodThings.PNG"
 *    ],
 *    "SuccessCount": 4,
 *    "Failed": [
 *        "src to dest"
 *    ],
 *    "FailedCount": 1
 *}
 *
 *  @apiErrorExample Error-Response: 404
 * HTTP/1.1 404 Not Found
 * {
 *     "code": 404,
 *     "error": "Not Found",
 * }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 * HTTP/1.1 500 InternalServerError
 * {
 *   "code": "500",
 *   "error": "Internal Server Error"
 * }
 */

function srcPathValidation(req, res, next) {
  try {

    let source = req.params.source;
    let destination = req.params.destination;

    let pattern = /[/.A-Za-z%0-9_-]+/g;

    let validate = pattern.test(source) && pattern.test(destination);

    if (validate) {
      next();
    } else {
      let error = errors.BadRequest(`source or destination is invalid in the params`);
      res.status(error.code).send(error);
    }
  }
  catch (err) {
    log.error(err);
    res.send(errors.InternalServerError());
  }
}

router.post('/copy/:source/to/:destination', srcPathValidation, (req, res) => {
  let source = contentRoot + '/' + req.params.source;
  let destination = contentRoot + '/' + req.params.destination;
  let type = req.query.type || 'authoring';
  let incudeFolder = true;

  if (req.query.incf === 'false') {
    incudeFolder = false;
  }

  if (['authoring', 'main'].includes(type.toLowerCase())) {
    utility
      .copyContent(source, destination, type, incudeFolder)
      .then(result => {
        log.info(result.message);
        res.status(result.code).send(result);
      })
      .catch(error => {
        if (error.message && error.code !== 500)
          log.error(error.message);
        res.status(error.code).send(error);
      });
  } else {
    let response = errors.BadRequest(`Type does not exist`);
    res.status(response.code).send(response);
  }
});

/**
 * @apiDescription API to move the data of a content directory
 *
 * @api {POST} /move/root/* /to/* Request to move the content.
 * @apiName CopyDirectory
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 *
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {JSON} response Data for the success and failure movements.
 * @apiSuccessExample Success response: 200
 * HTTP/1.1 200 Success
 * {
 *     "code": 200,
 *     "message": "All files were moved successfully from root/destorg to root/infy/"
 * }
 * @apiSuccessExample Success response : 207
 * HTTP/1.1 207 Multi-Status
 * {
 *    "code": 207,
 *    "message": "Not all files were copied successfully. Move failed.",
 *    "Success": [
 *        "root/destorg/assets/ -to- root/infy/assets/",
 *        "root/destorg/111.JPG -to- root/infy/111.JPG",
 *        "root/destorg/aaa.jpg -to- root/infy/aaa.jpg",
 *        "root/destorg/assets/chmodThings.PNG -to- root/infy/assets/chmodThings.PNG"
 *    ],
 *    "SuccessCount": 4,
 *    "Failed": [
 *        "src to dest"
 *    ],
 *    "FailedCount": 1
 *}
 *
 *  @apiErrorExample Error-Response: 404
 * HTTP/1.1 404 Not Found
 * {
 *     "code": 404,
 *     "error": "Not Found",
 * }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 * HTTP/1.1 500 InternalServerError
 * {
 *   "code": "500",
 *   "error": "Internal Server Error"
 * }
 */
router.post('/move/:source/to/:destination', srcPathValidation, (req, res) => {
  let source = `${contentRoot}/${req.params.source}`;
  let destination = `${contentRoot}/${req.params.destination}`;
  let type = req.query.type;
  let incf = true;

  if (req.query.incf === 'false') {
    incf = false;
  }

  type ? type : (type = 'authoring');

  if (['authoring', 'main'].includes(type.toLowerCase()))
    utility
      .moveContent(source, destination, type, incf)
      .then(result => {
        log.info(result.message);
        res.status(result.code).send(result);
      })
      .catch(error => {
        if (error.message && error.code !== 500)
          log.error(error.message);
        res.status(error.code).send(error);
      });
  else {
    let response = errors.BadRequest(`Type does not exist`);
    res.status(response.code).send(response);
  }
});

/**
 * @apiDescription API to copy the content from authoring bucket to main
 *
 * @api {POST} /publish/root/* Request to publish the content.
 * @apiName Publish
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 *
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {JSON} response Data for the success and failure movements.
 * @apiSuccessExample Success response: 200
 * HTTP/1.1 200 Success
 * {
 *     "code": 200,
 *     "message": "The content was published successfully"
 * }
 * @apiSuccessExample Success response : 207
 * HTTP/1.1 207 Multi-Status
 * {
 *    "code": 207,
 *    "message": "Not all files were copied successfully. Publish failed.",
 *    "Success": [
 *        "root/destorg/assets/ -to- root/infy/assets/",
 *        "root/destorg/111.JPG -to- root/infy/111.JPG",
 *        "root/destorg/aaa.jpg -to- root/infy/aaa.jpg",
 *        "root/destorg/assets/chmodThings.PNG -to- root/infy/assets/chmodThings.PNG"
 *    ],
 *    "SuccessCount": 4,
 *    "Failed": [
 *        "src to dest"
 *    ],
 *    "FailedCount": 1
 *}
 *
 *  @apiErrorExample Error-Response: 404
 * HTTP/1.1 404 Not Found
 * {
 *     "code": 404,
 *     "error": "Not Found",
 * }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 * HTTP/1.1 500 InternalServerError
 * {
 *   "code": "500",
 *   "error": "Internal Server Error"
 * }
 */
router.post('/publish/:location', pathValidation, (req, res) => {
  let key = `${contentRoot}/${req.params.location}`;

  // Publish the content
  utility
    .publish(key)
    .then(result => {
      log.info(result.message);
      res.status(result.code).send(result);
    })
    .catch(error => {
      if (error.message && error.code !== 500)
        log.error(error.message);
      res.status(error.code).send(error);
    });
});

router.post('/pullback/:location', pathValidation, (req, res) => {
  let key = `${contentRoot}/${req.params.location}`;

  // Pull back the content from main bucket to pre publish bucket
  utility
    .pullback(key)
    .then(result => {
      log.info(result.message);
      res.status(result.code).send(result);
    })
    .catch(error => {
      if (error.message && error.code !== 500)
        log.error(error.message);
      res.status(error.code).send(error);
    });

});

/**
 * @apiDescription API to set the cookie for Cloudfront authentication
 * @api {POST} /cookie/root/* Request to set the cookie.
 * @apiName Publish
 * @apiGroup S3ContentDirectory
 * @apiParam (URI Param) {String} path directory path on S3
 *
 * @apiSuccess {Number} code Success status code
 * @apiSuccess {JSON} response Data for the success and failure movements.
 * @apiSuccessExample Success response: 200
 * HTTP/1.1 200 Success
 * {
 *     "code": 200,
 *     "message": "The cookie was set successfully"
 * }
 *
 *  @apiErrorExample Error-Response: 400
 * HTTP/1.1 400 Bad Request
 * {
 *     "code": 404,
 *     "error": "Bad Request",
 *     "message": "uuid and contentId are required"
 * }
 *  @apiError (Response Error) InternalServerError:500 Server error while processing the request
 *  @apiErrorExample Error-Response: 500
 * HTTP/1.1 500 InternalServerError
 * {
 *   "code": "500",
 *   "error": "Internal Server Error"
 * }
 */
/*
	The request body should be
    {
        path : 'url'
	}
	where the path should point to an image being served from our CDN
	or
	{
		uuid : "",
		contentId : ""
	}
*/

// Change this to get from PID service
const rootOrgToDomain = {
};


// Set cookie for images
router.post('/cookie/images', (req, res) => {
  const orgName = rootOrgToDomain[req.headers.host] || appConfig.getProperty('default_images_org');
  if (!orgName) {
    console.error(`The domain ${req.headers.host} does not have an ROOT ORG mapped`);
    return res.status(400).send({
      code: 400,
      msg: `Requests domain ${req.headers.host} does not have any ROOT_ORG entry to set the cookie`,
    });
  }
  const cookiePath = `${getCDNFromRootOrg(CDN_TYPES.images, orgName)}/${utility.imagesBehaviourRoute}/${orgName}/*`;
  utility.setCookie(cookiePath, res);
});

// Set cookie for content
router.post('/cookie/', (req, res) => {
  try {
    let uuid = req.body.uuid;
    let contentId = req.body.contentId;
    let cookiePolicy = req.headers.policy;

    if (!uuid || !contentId) res.send(errors.BadRequest(`uuid and contentId are required`));
    // Set the cookie
    else utility.setCookieOnResource(uuid, contentId, cookiePolicy, res);
  }
  catch (err) {
    log.error(err);
    res.send(errors.InternalServerError());
  }
});

router.get('/invalidate/:distributionId/:path', (req, res) => {
  let { distributionId, path } = req.params;

  if (!distributionId || !path) {
    res.send(errors.BadRequest(`Distribution Id or Path not present`));
  } else {
    utility
      .createInvalidation(distributionId, path)
      .then(result => res.status(result.code).send(result))
      .catch(err => res.status(err.code).send(err));
  }
});

router.post('/invalidate-cache', (req, res) => {
  const urlArray = req.body;
  utility.createMultiplePathInvalidations(urlArray)
    .then(result => res.send(result))
    .catch((err) => {
      console.error(err);
      res.status(500).send({
        msg: err.message ? err.message : 'Exception while trying to clear the cache',
      });
    });
});

router.get('/video_length/:location', (req, res) => {

  let location = contentRoot + '/' + req.params.location;

  utility.getVideoLength(location).then(result => {
    log.info(result.message);
    res.status(result.code).send(result);
  }).catch(error => {
    if (error.message && error.code !== 500)
      log.error(error.message);
    res.status(error.code).send(error);
  });
});

// Create a zip at the location
router.post('/zip/:location', (req, res) => {
  req.setTimeout(1000 * 5 * 60);
  let key = contentRoot + '/' + req.params.location;

  utility.archiveAndUpload(key, contentRoot)
    .then(result => {
      log.info(result.message);
      res.send(result);
    })
    .catch(error => {
      if (error.message && error.code !== 500)
        log.error(error.message);
      res.status(error.code).send(error);
    });
});

router.get('/download-zip/:bucketName/:location', async (req, res) => {
  try {
    const zipFileLocation = await utility.archiveS3Location(req.params.bucketName, `${contentRoot}/${req.params.location}`, `${new Date().getTime()}.zip`);
    res.download(zipFileLocation, (err) => {
      if (err) {
        throw new Error('Could not send the file for download');
      }
      fs.unlinkSync(zipFileLocation);
    });
  } catch (e) {
    res.status(500).send({
      code: 500,
      msg: 'Internal Server error',
    });
  }
});

// Get the ETS status
router.get('/video_transcoding_status/:location', (req, res) => {
  let location = `${contentRoot}/${req.params.location}`;

  utility.getTranscodingStatus(location)
    .then(result => {
      log.info(result.message);
      res.send(result);
    }).catch(error => {
      if (error.message && error.code !== 500)
        log.error(error.message);
      res.status(error.code).send(error);
    });
});

// Getting the status for inhouse transcoding

router.get('/video-transcoding/status/:lexId', (req, res) => {
  request({ json: true, url }, (err, response, body) => {
    if (err) {
      return res.status(500).send({
        msg: 'Error while fetching the status',
        code: 500
      });
    }
    res.send(body);
  })
});

// Start video transcoding
router.post('/video-transcoding/start/:lexId', (req, res) => {
  let lexId = req.params.lexId;
  let authArtifactURL = req.body.authArtifactURL;
  if (!authArtifactURL) {
    res.status(400).send({
      code: 400,
      msg: 'authartifact url is not found'
    });
  }

  let location = authArtifactURL.split(`${appConfig.getProperty('NETWORK_ALIAS')}/contentv3/download/`)[1]
    .replaceAll('%2F', '/');
  let retryMode = req.body.retryMode || false;
  utility.startTranscoding(lexId, location, retryMode)
    .then(result => {
      log.info(result.message);
      res.status(result.response.statusCode).send(result.response.body);
    }).catch(error => {
      if (error.message && error.code !== 500)
        log.error(error.message);
      res.status(error.code).send(error);
    });
});

// Head object on S3 for finding if the content is present or not
router.post('/head-object', async (req, res) => {
  if (await utility.checkIfFileExists(req.query.url)) {
    res.send({
      msg: 'success'
    });
  } else {
    res.status(404).send({
      msg: 'File not present'
    });
  }
});

router.post('/copy/:sourceFolder/:destinationFolder', (req, res) => {
  const sourceDir = contentRoot + '/' + req.params.sourceFolder.replaceAll('%2F', '/').replaceAll('%2f', '/');
  const destDir = contentRoot + '/' + req.params.destinationFolder.replaceAll('%2F', '/').replaceAll('%2f', '/');
  utility.copyDirInSameBucket(sourceDir, destDir).then((result) => {
    res.send({
      code: 200,
      msg: 'Content copied',
    });
  }).catch((e) => {
    console.error(e);
    res.status(500).send({
      code: 500,
      msg: 'Error while copying the content',
    });
  })
});

router.post('/upload-zip/:location', [increaseTimeout(5 * 60 * 1000)], (req, res) => {
  const key = contentRoot + '/' + req.params.location.replaceAll('%2F', '/');

  req.pipe(req.busboy);
  req.busboy.on('file', async function (_, file, fileName) {
    utility.uploadZipFile(file, key).then(() => res.status(201).send()).catch((ex) => {
      console.error(ex);
      res.status(500).send(ex)
    });
  });
});

module.exports = router;
