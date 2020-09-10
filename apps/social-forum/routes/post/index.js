/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
var express = require('express');
var controller = require('./post.controller');
var validator = require('validator');


var router = express.Router();

function validateSource(params) {
    flag = false
    if (params.type == 'discussionForum') {
        if (params.hasOwnProperty('source') == false) {
            flag = true
        } else if (params.source.hasOwnProperty('id') == false || params.source.id == null || params.source.id == "") {
            flag = true
        } else if (params.source.hasOwnProperty('name') == false || params.source.name == null || params.source.name == "") {
            flag = true
        }
    }
    return flag
}
//to fetch the post according to a given id
router.post('/viewConversation', function (req, res) {
    if (req.body.hasOwnProperty('rootOrg') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('org') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('postId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('sessionId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('postKind') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('userId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.rootOrg == "" || req.body.rootOrg == null) {
        res.status(400).send('rootOrg is  empty')
    } else if (req.body.org == "" || req.body.org == null) {
        res.status(400).send('org is  empty')
    } else if (req.body.postId == "" || req.body.postId == null) {
        res.status(400).send('Post id is empty')
    } else if (req.body.sessionId == "" || req.body.sessionId == null) {
        res.status(400).send('sessionId is empty')
    } else if (req.body.userId == "" || req.body.userId == null) {
        res.status(400).send('userId is  empty')
    } else if (validator.isUUID(req.body.postId) == false) {
        res.status(400).send('Post id is not a valid uuid')
    } else if (validator.isUUID(req.body.userId) == false) {
        res.status(400).send('user id not a valid uuid')
    } else {
        if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
            pgNo = 0
        } else {
            pgNo = Number(req.body.pgNo)
        }
        if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
            pgSize = 10
        } else {
            pgSize = Number(req.body.pgSize)
        }
        if (req.body.hasOwnProperty('sortOrder') == false || req.body.sortOrder == "") {
            sortOrder = 'latest-asc'
        } else {
            sortOrder = req.body.sortOrder
        }
        //console.log(req.body.postId)
        controller.viewConversation(req.body, pgNo, pgSize, sortOrder)
            .then(data => {
                //console.log("data : ",data)
                res.send(data)
            })
            .catch(err => res.status(err.statuscode).send({
                error: err.err,
                message: err.message
            }))
    }
});

router.post('/viewConversationv2', function (req, res) {
    if (req.body.hasOwnProperty('rootOrg') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('org') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('postId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('sessionId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('postKind') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('userId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.rootOrg == "" || req.body.rootOrg == null) {
        res.status(400).send('rootOrg is  empty')
    } else if (req.body.org == "" || req.body.org == null) {
        res.status(400).send('org is  empty')
    } else if (req.body.postId == null || req.body.postId.length == 0) {
        res.status(400).send('Post id is empty')
    } else if (req.body.sessionId == "" || req.body.sessionId == null) {
        res.status(400).send('sessionId is empty')
    } else if (req.body.userId == "" || req.body.userId == null) {
        res.status(400).send('userId is  empty')
    } else if (validator.isUUID(req.body.userId) == false) {
        res.status(400).send('user id not a valid uuid')
    } else {
        for (postId of req.body.postId) {
            if (validator.isUUID(postId) == false) {
                res.status(400).send('Post id is not a valid uuid')
            }
        }
    }
    if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
        pgNo = 0
    } else {
        pgNo = Number(req.body.pgNo)
    }
    if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
        pgSize = 10
    } else {
        pgSize = Number(req.body.pgSize)
    }
    if (req.body.hasOwnProperty('sortOrder') == false || req.body.sortOrder == "") {
        sortOrder = 'latest-asc'
    } else {
        sortOrder = req.body.sortOrder
    }
    // console.log(req.body.postId)
    controller.viewConversationv2(req.body, pgNo, pgSize, sortOrder)
        .then(data => {
            //console.log("data : ",data)
            res.send(data)
        })
        .catch(err => res.status(err.statuscode).send({
            error: err.err,
            message: err.message
        }))
});

// to fetch discussion timeline activity
router.post('/discussions', function (req, res) {
    if (req.body.hasOwnProperty('rootOrg') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('org') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('userId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('sessionId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('postKind') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('type') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.rootOrg == "" || req.body.rootOrg == null) {
        res.status(400).send('rootOrg is  empty')
    } else if (req.body.org == "" || req.body.org == null) {
        res.status(400).send('org is  empty')
    } else if (req.body.userId == "" || req.body.userId == null) {
        res.status(400).send('userId is  empty')
    } else if (req.body.sessionId == "" || req.body.sessionId == null) {
        res.status(400).send('sessionId is  empty')
    } else if (req.body.type == "" || req.body.type == null) {
        res.status(400).send('type is  empty')
    } else if (validator.isUUID(req.body.userId) == false) {
        res.status(400).send('user id not a valid uuid')
    } else if (validateSource(req.body)) {
        res.status(400).send('wrong request body, source details not valid')
    } else if (req.body.type == "hashTags" && (req.body.hasOwnProperty('searchWord') == false || req.body.searchWord.length <= 0)) {
        res.status(400).send("wrong request body,searchWord is empty")
    } else {
        if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
            pgNo = 0
        } else {
            pgNo = Number(req.body.pgNo)
        }
        if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
            pgSize = 10
        } else {
            pgSize = Number(req.body.pgSize)
        }
        //console.log(pgNo,pgSize)
        controller.discussList(req.body, pgNo, pgSize)
            .then(data => res.send(data))
            .catch(err => res.status(err.statuscode).send(err))
    }
})

// to fetch timeline activity
router.post('/timeline', function (req, res) {
    if (req.body.hasOwnProperty('rootOrg') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('org') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('userId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('sessionId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('postKind') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('type') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.rootOrg == "" || req.body.rootOrg == null) {
        res.status(400).send('rootOrg is  empty')
    } else if (req.body.org == "" || req.body.org == null) {
        res.status(400).send('org is  empty')
    } else if (req.body.userId == "" || req.body.userId == null) {
        res.status(400).send('userId is  empty')
    } else if (req.body.sessionId == "" || req.body.sessionId == null) {
        res.status(400).send('sessionId is  empty')
    } else if (req.body.type == "" || req.body.type == null) {
        res.status(400).send('type is  empty')
    } else if (validator.isUUID(req.body.userId) == false) {
        res.status(400).send('user id not a valid uuid')
    } else if (validateSource(req.body)) {
        res.status(400).send('wrong request body, source details not valid')
    } else if (req.body.type == "hashTags" && (req.body.hasOwnProperty('searchWord') == false || req.body.searchWord.length <= 0)) {
        res.status(400).send("wrong request body,searchWord is empty")
    } else {
        if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
            pgNo = 0
        } else {
            pgNo = Number(req.body.pgNo)
        }
        if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
            pgSize = 10
        } else {
            pgSize = Number(req.body.pgSize)
        }
        //console.log(pgNo,pgSize)
        controller.timeline(req.body, pgNo, pgSize)
            .then(data => res.send(data))
            .catch(err => res.status(err.statuscode).send(err))
    }
})

router.post('/timelinev2', function (req, res) {
    if (req.body.hasOwnProperty('rootOrg') == false || req.body.rootOrg == null || req.body.rootOrg == "") {
        res.status(400).send("bad request, rootOrg cannot be empty")
    } else if (req.body.hasOwnProperty('org') == false || req.body.org == null || req.body.org == "") {
        res.status(400).send("bad request, org cannot be empty")
    } else if (req.body.hasOwnProperty('userId') == false || req.body.userId == null || req.body.userId == "") {
        res.status(400).send("bad request, userId cannot be empty")
    } else if (req.body.hasOwnProperty('sessionId') == false || req.body.sessionId == null || req.body.sessionId == "") {
        res.status(400).send("bad request, sessionId cannot be empty")
    } else if (req.body.hasOwnProperty('type') == false || req.body.type == null || req.body.type == "") {
        res.status(400).send("bad request, type cannot be empty")
    } else {
        if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
            pgNo = 0
        } else {
            pgNo = Number(req.body.pgNo)
        }
        if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
            pgSize = 10
        } else {
            pgSize = Number(req.body.pgSize)
        }
        //console.log(pgNo,pgSize)
        controller.timelinev2(req.body, pgNo, pgSize)
            .then(data => res.send(data))
            .catch(err => res.status(err.statuscode).send(err))
    }

})

//to fetch the post activity and user to post activity
router.post('/activity', function (req, res) {
    if (req.body.hasOwnProperty('rootOrg') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('org') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('userId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.hasOwnProperty('postId') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.rootOrg == "" || req.body.rootOrg == null) {
        res.status(400).send('rootOrg is  empty')
    } else if (req.body.org == "" || req.body.org == null) {
        res.status(400).send('org is  empty')
    } else if (req.body.userId == "" || req.body.userId == null) {
        res.status(400).send('user id empty')
    } else if (req.body.postId == "" || req.body.postId == null) {
        res.status(400).send('post id empty')
    } else if (validator.isUUID(req.body.userId) == false) {
        res.status(400).send('user id not a valid uuid')
    } else {
        controller.fetchActivity(req.body)
            .then(data => res.send(data))
            .catch(err => res.status(err.statuscode).send(err))
    }
})

//to provide autocomplete for tags
router.post('/autocomplete', function (req, res) {
    if (req.body.hasOwnProperty('query') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.query == "" || req.body.query == null) {
        res.status(400).send('search word is empty')
    } else {
        if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
            pgNo = 0
        } else {
            pgNo = Number(req.body.pgNo)
        }
        if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
            pgSize = 10
        } else {
            pgSize = Number(req.body.pgSize)
        }
        controller.tagsAutocomplete(req.body.query, pgNo, pgSize)
            .then(data => res.send(data))
            .catch(err => res.status(err.statuscode).send(err))
    }
})

//+ api to fetch all users related to given post's activity
//pgSize , pgNo ,activityType are optional
//default values for pgNo = 0,pgSize = 10, activity = like
router.post('/users', function (req, res) {
    if (req.body.hasOwnProperty('rootOrg') == false || req.body.rootOrg == "" || req.body.rootOrg == null) {
        res.status(400).send("bad request, rootOrg cannot be empty")
    } else if (req.body.hasOwnProperty('org') == false || req.body.org == "" || req.body.org == null) {
        res.status(400).send("bad request, org cannot be empty")
    } else if (req.body.hasOwnProperty('postId') == false || req.body.postId == "" || req.body.postId == null) {
        res.status(400).send("bad request, post id cannot be empty")
    }
    // else if(req.body.hasOwnProperty('sessionId') == false || req.body.sessionId == "" || req.body.sessionId == null){
    //     res.status(400).send("bad request, session Id cannot be empty")
    // }
    else {
        if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
            pgNo = 0
        } else {
            pgNo = Number(req.body.pgNo)
        }
        if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
            pgSize = 10
        } else {
            pgSize = Number(req.body.pgSize)
        }
        if (req.body.hasOwnProperty('activityType') == false || req.body.activityType == null) {
            activityType = 'like'
        } else {
            activityType = req.body.activityType
        }

        controller.fetchUsers(req.body, pgNo, pgSize, activityType)
            .then(data => res.send(data))
            .catch(err => res.status(err.statuscode).send({
                error: err.err,
                message: err.message
            }))
    }

})


//to provide autocomplete for hashtags
router.post('/hashtags', function (req, res) {
    if (req.body.hasOwnProperty('query') == false) {
        res.status(400).send("wrong request body")
    } else if (req.body.query == "" || req.body.query == null) {
        res.status(400).send('search word is empty')
    } else {
        if (req.body.hasOwnProperty('pgNo') == false || req.body.pgNo == "") {
            pgNo = 0
        } else {
            pgNo = Number(req.body.pgNo)
        }
        if (req.body.hasOwnProperty('pgSize') == false || req.body.pgSize == null) {
            pgSize = 10
        } else {
            pgSize = Number(req.body.pgSize)
        }
        controller.hashtagsAutocomplete(req.body.query, pgNo, pgSize)
            .then(data => res.send(data))
            .catch(err => res.status(err.statuscode).send(err))
    }
})

module.exports = router;