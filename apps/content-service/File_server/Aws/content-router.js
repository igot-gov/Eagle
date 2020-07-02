/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
const express = require('express');
const router = express.Router();
const awsUtil = require('./util');
const path = require('path');
// For file upload
const busboy = require('connect-busboy');
router.use(busboy());

let FILE_SEPARATOR = '/';
const rootOrg = "s3content";

router.get('/', function (req, res) {
    let dirPath = path.join(path.join(__dirname, 'views'), 'index.html')
    res.sendFile(dirPath);
});

router.post('/content/setCookie', function (req, res) {
    console.log("/content/setCookie Called");
    console.log(`path> ${req.body.path}`)
    if (!req.body.path) {
        console.log("Request must include an artifact URL");

        res.status(400).send({
            code: 400,
            message: "Request must include an artifact URL",
            error: "Bad request"
        });
    } else {
        awsUtil.setCookie(req.body.path)
            .then(signedCookies => {
                for (var cookieId in signedCookies) {
                    res.cookie(cookieId, signedCookies[cookieId]);
                }
                res.send({
                    code: 200,
                    message: "Cookie has been set"
                });
            })
            .catch((err) => {
                console.log("Error in /content/setCookie", err);
                res.status(err.code).send(err)
            });
    }
});

// Set cookie on the organization level
router.post('/content/setCookie/:organization', (req, res) => {
    let organization = req.params.organization;
    console.log(" Set cookie on the organization level=====>", organization);

    if (organization) {
        let objectPath = rootOrg + FILE_SEPARATOR + organization + FILE_SEPARATOR + "*";
        console.log("objectPath====>", objectPath);

        awsUtil.setCookie(objectPath)
            .then(signedCookies => {
                for (var cookieId in signedCookies) {
                    res.cookie(cookieId, signedCookies[cookieId]);
                }
                res.send({
                    code: 200,
                    message: "Cookie has been set"
                });
            })
            .catch((err) => {
                console.log("Error!!==>/content/setCookie/:organization: ", err);

                res.status(err.code).send(err)
            });
    } else {
        console.log("/content/setCookie/:organization====> No Org");
        res.send({
            code: 500,
            message: "Failed to set the cookie"
        });
    }
});

// Set cookie on the lex id level
router.post('/content/setCookie/:organization/:contentId', (req, res) => {
    let organization = req.params.organization;
    let lexId = req.params.contentId;

    if (organization && lexId) {
        let objectPath = rootOrg + FILE_SEPARATOR + organization + FILE_SEPARATOR + lexId + FILE_SEPARATOR + "*";

        awsUtil.setCookie(objectPath)
            .then(signedCookies => {
                for (var cookieId in signedCookies) {
                    res.cookie(cookieId, signedCookies[cookieId]);
                }
                res.send({
                    code: 200,
                    message: "Cookie has been set"
                });
            })
            .catch(err => res.status(err.code).send(err));
    } else {
        res.send({
            code: 500,
            message: "Failed to set the cookie"
        });
    }

});

module.exports = router;
