const express = require('express');
const router = express.Router();
const app = express();
const config = require('./ConfigReader/loader');

const port = config.getProperty('port');
const log = require('./Logger/log');
const fs = require('fs');
const path = require('path');

router.get('/api/fetch/:fileName', (req, res) => {
  let headerUsername = req.headers['api-username'];
  let headerPassword = req.headers['api-password'];

  if (headerUsername && headerPassword) {
    // Validate the credentials
    let password = config.getProperty(headerUsername);
    if (password && password === headerPassword) {
      // Sending the response back to the user
      streamFiles(req, res);
    } else {
      res.status(401).send({
        code: 401,
        msg: 'Invalid username or password'
      });
    }
  } else {
    // Bad request
    res.status(400).send({
      code: 400,
      msg: 'Credentials missing in the request'
    });
  }
  // streamFiles(req, res);
});

function filterDownloadReq(req, res, next) {
  authUtil
    .getClientDetails(req.headers['client_id'])
    .then(result => {
      // Checking for the credentials
      if (result.length === 1) {
        // Check if the value matches
        if (req.headers['api_key'] != result[0].value) {
          res.status(401).send({
            code: 401,
            msg: 'Access Denied'
          });
          return;
        }
        // Checking if the file requested has a url match
        let matched = false;

        for (let i = 0; i < result[0].urls.length; i++) {
          if (
            req.url.startsWith(
              result[0].urls[i].replace(new RegExp('\\*', 'g'), '')
            )
          ) {
            matched = true;
            break;
          }
        }
        if (matched) {
          next();
        } else {
          res.status(403).send({
            code: 403,
            msg: 'Access forbidden for this directory'
          });
        }
      } else {
        res.status(401).send({
          code: 401,
          msg: 'Unauthorized'
        });
      }
    })
    .catch(e => {
      console.error(e);
      res.status(500).send({
        code: 500,
        msg: 'Internal Server Error'
      });
    });
}
// New router for reading the config for client id and client secret from cassandra. Also the URL pattern
const authUtil = require('./Utils/authUtil');
app.get('/api/download/:dirName/:fileName', filterDownloadReq, (req, res) => {
  streamFiles(req, res);
});

function streamFiles(req, res) {
  const backupsDir = config.getProperty('backups_location');
  const fileName = req.params.fileName;
  try {
    // Defining the folder path from where the resource must be picked up.
    let filePath = backupsDir + '/' + fileName;
    if (req.params.dirName) {
      filePath = `${backupsDir}/${req.params.dirName}/${fileName}`;
    }

    // Logic used to create a stream if the requested resource had a stream in the header.
    try {
      log.info('file path: ' + filePath);
      fs.statSync(path.resolve(filePath));
    } catch (e) {
      // File is not found. Will throw an error if the request does not have any explicitly mentioned header
      if (e.code == 'ENOENT') {
        res.status(404).send({
          error: 'not found',
          message: 'Requested resource is not found'
        });
        return;
      }
      console.error(e);
    }
    // Serving the head to let know if the file exists or not.
    if (req.method.toString().toLowerCase() == 'head') {
      res.status(200).send();
      return;
    } else {
      // Sending the file as a downloadable instance
      res.download(filePath);
      // Streaming the file now
      // fs.createReadStream(filePath).pipe(res);
    }
  } catch (e) {
    // Throwing the exception when the server came across an unexpected behavior.
    console.error(e); // eslint-disable-line
    log.error(e);
    res.status(500).send({
      code: 500,
      msg: 'Error while processing the request'
    });
    return;
  }
}

app.use(router);
const server = app.listen(port, () => {
  console.log(`App successfully started on: ${port}`);
});

module.exports = server;