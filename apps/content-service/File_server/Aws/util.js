/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
const cf = require("aws-cloudfront-sign");
const fs = require('fs');
const path = require('path');
const appConfig = require('../ConfigReader/loader');

// Function to set cookie at object level

function setCookie(artifactURL) {
  console.log("AWS setCookie Called");
  
  return new Promise((resolve, reject) => {
    if (artifactURL.includes("*")) {
      reject({
        code: 400,
        message: "Request cannot contain asterisk",
        error: "Bad request"
      });
    }

    let pathToObj = artifactURL;
    if (path.extname(artifactURL) === ".m3u8") {
      pathToObj = path.dirname(artifactURL) + "/*";
    }
    else if (path.extname(artifactURL) === "") {
      pathToObj += "*";
    }

    // Cloudfront Credentials
    const CLOUDFRONT_KEY_PAIR_ID = appConfig.getProperty('AWS_CF_ACCESS_KEY');
    const PRIVATE_KEY_PATH = appConfig.getProperty('AWS_PVT_KEY_FILE_PATH');

    if (CLOUDFRONT_KEY_PAIR_ID && PRIVATE_KEY_PATH) {
      if (PRIVATE_KEY_PATH.toString().trim() !== '' && fs.existsSync(PRIVATE_KEY_PATH)) {
        const PRIVATE_KEY_STRING = fs.readFileSync(PRIVATE_KEY_PATH).toString();

        let options = {
          keypairId: CLOUDFRONT_KEY_PAIR_ID,
          privateKeyString: PRIVATE_KEY_STRING
        };

        // Generate an object with signed cookies to authenticate CF requests
        let signedCookies = cf.getSignedCookies(
          pathToObj,
          options
        );

        resolve(signedCookies);
      }
      else {
        reject({
          code: 500,
          message: "Internal server error"
        })
      }

    }
    else {
      reject({
        code: 500,
        message: "Internal server error"
      })
    }
  });
}

module.exports = {
  setCookie,
}
