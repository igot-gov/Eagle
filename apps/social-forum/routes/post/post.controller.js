/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/**
 * GET /fetchPost/:postId
 **/

const esDb = require('../../ESUtil/elasticSearch');
const log = require('../../Logger/log');

var cassDb = require('../../CassandraUtil/cassandra')
const followServices = require("../follow/follow.controller")
const timelineUtilServices = require("../../util/timelineUtil")



/**Given a post id get all related posts and comments */
/**page number to be started from 0 */
async function viewConversation(request,pgno=0,pgsize=10,sortOrder='latest-asc') {
  try {
    //get the accepted answer from the thread
    //get the parent post and accepted answer from post
    //get latest 10 post from post where parentid=postid in asc order
    //if pgno=0 get main post, accepted ans
    //

    const result = {
      "postCount": 0,
      "mainPost": {},
      "acceptedAnswer": {},
      "replyPost": [],
      "newPostCount": 0
    }

    let postKind = []
    //postKind in the request body will tell whether to get the Replies or comments of the postid given in the request body
    //default will be reply
    if(request.postKind.length == 0){
      postKind = ['Reply']
    }
    else{
      postKind = request.postKind
    }

    //check of the post is a valid post
    let checkPostValidity = postBodyQueryBuilder(1,0,request.rootOrg,request.org,[request.postId])
    let checkPostValidityResult = await esDb.templateSearch(checkPostValidity, "postType", "postIndex", "postTemplate")
    
    if(checkPostValidityResult.hits.total>0){
      //console.log(checkPostValidityResult.hits.hits[0]._source)
      if(checkPostValidityResult.hits.hits[0]._source.status=='Inactive'){
        throw { statuscode: 404, message: "The content is deleted", err: 'The content is deleted' }
      }
      else{
        //checking user access on the post
        userAccessPaths =await timelineUtilServices.getAccessPaths(request.userId,request.rootOrg)
        //console.log(userAccessPaths)
        postAccessPaths = checkPostValidityResult.hits.hits[0]._source.accessPaths
        //console.log(postAccessPaths)
        const found = userAccessPaths.some(r=> postAccessPaths.indexOf(r) >= 0)
        //console.log("found : ",found)
        if(!found){
          throw { statuscode: 404, err: "Access Restricted", message: "Access Restricted" }
        }
      }
    }
    else{
      throw { statuscode: 404, message: "No Data found for given Post Id", err: 'No Data Found' }
    }

    //checking if the post has any accepted answer , also if thread index gives 0 result, it may be because the postid is a drafted content and it needs to be fetched from post index
    //main post and acceptedAnswer is provided only at the first call ,i.e. when pgno=0
    if(pgno == 0){
      let ids =[]
      ids.push(request.postId)

      //query to find the accepted answer from  thread index
      let query = {
        "_source": ["acceptedAnswers","status"], 
        "query": {
          "bool": {
            "must": [
              {
                "term": {
                  "rootOrg": {
                    "value": request['rootOrg']
                  }
                }
              },
              {
                "term": {
                  "org": {
                    "value": request['org']
                  }
                }
              },
              {
                "term": {
                  "id": {
                    "value" : request['postId']
                  }
                }
              }
            ]
          }
        }
      }
      let acceptedAnswerResult = await esDb.getData(query,"threadType","threadIndex")
      if(acceptedAnswerResult.hits.total>0){
        let sourceData = acceptedAnswerResult.hits.hits
        for(let data of sourceData){
          data = data._source
          if(data.status =='Inactive'){
            throw { statuscode: 404, message: "The content is deleted", err: 'The content is deleted' }
          }
          if (data.acceptedAnswers != "" && data.acceptedAnswers != null){
            ids.push(data.acceptedAnswers)
          }
        }
        let postParams = postBodyQueryBuilder(2,0,request.rootOrg,request.org,ids,"Active")
        let postDataResult = await esDb.templateSearch(postParams, "postType", "postIndex", "postTemplate")

        
        if(postDataResult.hits.total>0){
          //getting post related activity
          let activityObject = {
            userId: request['userId'],
            rootOrg: request['rootOrg'],
            org: request['org'],
            postId: ids
          }

          let activityResult = await timelineUtilServices.fetchActivity(activityObject)
          let postDataResultMeta = postDataResult.hits.hits
          for(let obj of postDataResultMeta){
            obj = obj._source
            if(obj.id == request.postId){
              obj.activity = activityResult.get(obj.id)
              result.mainPost = obj
            }
            else{
              obj.activity = activityResult.get(obj.id)
              result.acceptedAnswer = obj
            }
          }
          
        }
      }
      else{

        //to get the draft post from post index
        let draftPostParams = postBodyQueryBuilder(1,0,request.rootOrg,request.org,ids)
        let draftPostDataResult = await esDb.templateSearch(draftPostParams, "postType", "postIndex", "postTemplate")
        if(draftPostDataResult.hits.total>0){
          let draftSourceData = draftPostDataResult.hits.hits
          for(let data of draftSourceData){
            result.mainPost = data['_source']
          }
          if(result.mainPost.status == 'Draft'){
            return result
          }
          else if(result.mainPost.status =='Inactive'){
            throw { statuscode: 404, message: "The content is deleted", err: 'The content is deleted' }
          }
        }
        else{
          throw { statuscode: 404, message: "No Data found for given Post Id", err: 'No Data Found' }
        }
      }
    }


    //get replies
    let replySortOrder = "desc"
    if(sortOrder.includes("earliest")){
      replySortOrder = "asc"
    }
    let replyParams = repliesBodyQuery(pgsize,pgno*pgsize,request.rootOrg,request.org,request.postId,postKind,"Active",replySortOrder)
    let replyDataList =[]
    
    let replyDataResult = await esDb.templateSearch(replyParams, "postType", "postIndex", "postTemplate")
    if(replyDataResult.hits.total>0){
      if ((replyDataResult.hits.total > pgsize) && (replyDataResult.hits.total > ((pgno + 1) * pgsize)) ) {
        //console.log("replyDataResult.hits.total : ",replyDataResult.hits.total)
        //console.log("result.postCount :",result.postCount )
        result.postCount = Number(replyDataResult.hits.total) - ((pgno + 1) * pgsize)
        //console.log("result.postCount :",result.postCount )
      }

      let replyData = replyDataResult.hits.hits
      let idsList =[]
      let replyDataList =[]
      //sorting the result 
      //console.log(sortOrder)
      if(sortOrder == 'earliest-desc' || sortOrder == 'latest-asc'){
        //console.log(sortOrder)
        for (let index = replyData.length-1; index >= 0; index--) {
          replyDataList.push(replyData[index]._source)
          idsList.push(replyData[index]._source.id)
          
        }
      }
      else if(sortOrder == 'earliest-asc' || sortOrder == 'latest-desc'){
        //console.log(sortOrder)
        for (let index = 0; index < replyData.length; index++) {
          replyDataList.push(replyData[index]._source)
          idsList.push(replyData[index]._source.id)
          
        }
      }
      //console.log("replyDataList" ,replyDataList)
      if(idsList.length>0){
        let activityObject = {
          userId: request.userId,
          rootOrg: request.rootOrg,
          org: request.org,
          postId: idsList
        }

        let activityResult1 = await timelineUtilServices.fetchActivity(activityObject)
        replyDataList.forEach(element => {
          element.activity = activityResult1.get(element['id'])
        });

      }
      result.replyPost = replyDataList
    }

    //get if any new post was created for the given postId w.r.t given sessionId
    let newPostCountParams = repliesBodyQuery(10,0,request.rootOrg,request.org,request.postId,postKind,"Active",replySortOrder,request.sessionId)
    let newPostDataResult = await esDb.templateSearch(newPostCountParams,"postType", "postIndex", "postTemplate")
    result.newPostCount = newPostDataResult.hits.total
    return result
    
  } catch (error) {
    if(error.statuscode){
      throw error
    }
    else{
      log.error("unexpected error" + error)
      throw { statuscode: 500, err: "internal server error", message: "unexpected error" }
    }
  }  
}

async function viewConversationv2(request,pgno=0,pgsize=10,sortOrder='latest-asc') {
  try {
    //get the accepted answer from the thread
    //get the parent post and accepted answer from post
    //get latest 10 post from post where parentid=postid in asc order
    //if pgno=0 get main post, accepted ans
    //

    const result = {}

    let postKind = []
    //postKind in the request body will tell whether to get the Replies or comments of the postid given in the request body
    //default will be reply
    if(request.postKind.length == 0){
      postKind = ['Reply']
    }
    else{
      postKind = request.postKind
    }

    //check of the post is a valid post
    let checkPostValidity = postBodyQueryBuilder(1,0,request.rootOrg,request.org,request.postId)
    let checkPostValidityResult = await esDb.templateSearch(checkPostValidity, "postType", "postIndex", "postTemplate")
    
    if(checkPostValidityResult.hits.total>0){
      //console.log(checkPostValidityResult.hits.hits[0]._source)
      for(postValidityResult of checkPostValidityResult.hits.hits){
        if(postValidityResult._source.status=='Inactive'){
          throw { statuscode: 404, message: "The content is deleted", err: 'The content is deleted' }
        }
        else{
          //checking user access on the post
          userAccessPaths =await timelineUtilServices.getAccessPaths(request.userId,request.rootOrg)
          //console.log(userAccessPaths)
          postAccessPaths = postValidityResult._source.accessPaths
          //console.log(postAccessPaths)
          const found = userAccessPaths.some(r=> postAccessPaths.indexOf(r) >= 0)
          //console.log("found : ",found)
          if(!found){
            throw { statuscode: 404, err: "Access Restricted", message: "Access Restricted" }
          }
        }
      }
    }
    else{
      throw { statuscode: 404, message: "No Data found for given Post Id", err: 'No Data Found' }
    }

    for(id of request.postId) {
      result[id] = {
        "postCount": 0,
        "mainPost": {},
        "acceptedAnswer": {},
        "replyPost": [],
        "newPostCount": 0
      }
    }
    //checking if the post has any accepted answer , also if thread index gives 0 result, it may be because the postid is a drafted content and it needs to be fetched from post index
    //main post and acceptedAnswer is provided only at the first call ,i.e. when pgno=0
    if(pgno == 0){
      let ids =[]
      ids = request.postId
      

      //query to find the accepted answer from  thread index
      let query = {
        "_source": ["acceptedAnswers","status"], 
        "query": {
          "bool": {
            "must": [
              {
                "term": {
                  "rootOrg": {
                    "value": request['rootOrg']
                  }
                }
              },
              {
                "term": {
                  "org": {
                    "value": request['org']
                  }
                }
              },
              {
                "terms": {
                  "id": request['postId']
                }
              }
            ]
          }
        }
      }

      let acceptedAnswerResult = await esDb.getData(query,"threadType","threadIndex")
      if(acceptedAnswerResult.hits.total>0){
        let sourceData = acceptedAnswerResult.hits.hits
        for(let data of sourceData){
          data = data._source
          console.log(data)
          if(data.status =='Inactive'){
            throw { statuscode: 404, message: "The content is deleted", err: 'The content is deleted' }
          }
          if (data.acceptedAnswers != "" && data.acceptedAnswers != null){
            ids.push(data.acceptedAnswers)
          }
        }
        let postParams = postBodyQueryBuilder(ids.length,0,request.rootOrg,request.org,ids,"Active")
        let postDataResult = await esDb.templateSearch(postParams, "postType", "postIndex", "postTemplate")
        
        if(postDataResult.hits.total>0){
          //getting post related activity
          let activityObject = {
            userId: request['userId'],
            rootOrg: request['rootOrg'],
            org: request['org'],
            postId: ids
          }

          let activityResult = await timelineUtilServices.fetchActivity(activityObject)
          let postDataResultMeta = postDataResult.hits.hits
          for(let obj of postDataResultMeta){
            obj = obj._source
            if(request.postId.includes(obj.id)){
              obj.activity = activityResult.get(obj.id)
              result[obj.id].mainPost = obj
            }
            else{
              obj.activity = activityResult.get(obj.id)
              result[obj.id].acceptedAnswer = obj
            }
          }
          
        }
      }
      else{

        //to get the draft post from post index
        let draftPostParams = postBodyQueryBuilder(len(ids),0,request.rootOrg,request.org,ids)
        let draftPostDataResult = await esDb.templateSearch(draftPostParams, "postType", "postIndex", "postTemplate")
        if(draftPostDataResult.hits.total>0){
          let draftSourceData = draftPostDataResult.hits.hits
          for(let data of draftSourceData){
            result[data['_source'].id].mainPost = data['_source']
          }
          if(result[data['_source'].id].mainPost.status == 'Draft'){
            return result
          }
          else if(result[data['_source'].id].mainPost.status =='Inactive'){
            throw { statuscode: 404, message: "The content is deleted", err: 'The content is deleted' }
          }
        }
        else{
          throw { statuscode: 404, message: "No Data found for given Post Id", err: 'No Data Found' }
        }
      }
    }

    //get replies
    let replySortOrder = "desc"
    if(sortOrder.includes("earliest")){
      replySortOrder = "asc"
    }
    let replyParams = repliesBodyQuery(pgsize,pgno*pgsize,request.rootOrg,request.org,request.postId,postKind,"Active",replySortOrder, "",true)
    let replyDataList =[]
    
    let replyDataResult = await esDb.templateSearch(replyParams, "postType", "postIndex", "postTemplate")
    
    replyDataParentIdCount = {}
    
    if(replyDataResult.hits.total>0){
      if ((replyDataResult.hits.total > pgsize) && (replyDataResult.hits.total > ((pgno + 1) * pgsize)) ) {
        //console.log("replyDataResult.hits.total : ",replyDataResult.hits.total)
        //console.log("result.postCount :",result.postCount )
        result.postCount = Number(replyDataResult.hits.total) - ((pgno + 1) * pgsize)
        //console.log("result.postCount :",result.postCount )
      }

      let replyData = replyDataResult.hits.hits
      let idsList =[]
      let replyDataMap ={}
      //sorting the result 
      //console.log(sortOrder)
      if(sortOrder == 'earliest-desc' || sortOrder == 'latest-asc'){
        //console.log(sortOrder)
        for (let index = replyData.length-1; index >= 0; index--) {
          if(replyData[index]._source.parentId in replyDataMap){
            replyDataMap[replyData[index]._source.parentId]['replyDataList'].push(replyData[index]._source)
          }else{
            replyDataMap[replyData[index]._source.parentId] = {'replyDataList': [replyData[index]._source]}
          }
          idsList.push(replyData[index]._source.id)
          
        }
      }
      else if(sortOrder == 'earliest-asc' || sortOrder == 'latest-desc'){
        //console.log(sortOrder)
        for (let index = 0; index < replyData.length; index++) {
          if(replyData[index]._source.parentId in replyDataMap){
            replyDataMap[replyData[index]._source.parentId]['replyDataList'].push(replyData[index]._source)
          }else{
            replyDataMap[replyData[index]._source.parentId] = {'replyDataList': [replyData[index]._source]}
          }
          idsList.push(replyData[index]._source.id)
          
        }
      }
      //console.log("replyDataList" ,replyDataList)
      if(idsList.length>0){
        let activityObject = {
          userId: request.userId,
          rootOrg: request.rootOrg,
          org: request.org,
          postId: idsList
        }

        let activityResult1 = await timelineUtilServices.fetchActivity(activityObject)
        for(id in replyDataMap){
          replyDataMap[id]['replyDataList'].forEach(element => {
            element.activity = activityResult1.get(element['id'])
          });
          
          result[id].replyPost = replyDataMap[id]["replyDataList"]
        }
      }
    }

    //get if any new post was created for the given postId w.r.t given sessionId
    let newPostCountParams = repliesBodyQuery(10,0,request.rootOrg,request.org,request.postId,postKind,"Active",replySortOrder,request.sessionId, true)
    let newPostDataResult = await esDb.templateSearch(newPostCountParams,"postType", "postIndex", "postTemplate")
    return result
    for(newPostData of newPostDataResult.hits.hits){
      result[newPostData._source.id].newPostCount += 1
    }
    return result
    
  } catch (error) {
    if(error.statuscode){
      throw error
    }
    else{
      log.error("unexpected error" + error)
      throw { statuscode: 500, err: "internal server error", message: "unexpected error" }
    }
  }  
}

// to generate the query body, either for reply or comment , and also query to find the no of replies w.r.t to given parentId and timestamp
function repliesBodyQuery(size,from,rootOrg,org,parentId,postKind,status,sortOrder="desc",sessionId ="", parentIdIsList = false){
  //console.log("sortorder" , sortOrder)
  let replyPostParams ={}
  replyPostParams.sizeValue = size
  replyPostParams.fromValue = from
  replyPostParams.rootOrgValue = rootOrg
  replyPostParams.orgValue = org
  replyPostParams.mustfilterstatus = true
  replyPostParams.mustfilterstatusValue = [status]
  replyPostParams.mustfilterparentId = true
  if(parentIdIsList){
    replyPostParams.mustfilterparentIdValue = parentId
  }else{
    replyPostParams.mustfilterparentIdValue = [parentId]
  }
  replyPostParams.mustfilterpostKind = true
  replyPostParams.mustfilterpostKindValue = postKind
  replyPostParams.mustfilterisAcceptedAnswer = true
  replyPostParams.mustfilterisAcceptedAnswerValue = [false]
  replyPostParams.sort = true
  replyPostParams.sortField = "dtLastModified"
  replyPostParams.sortOrder = sortOrder
  if(sessionId!=""){
    replyPostParams.mustfilterdtLastModified = true
    replyPostParams.mustfilterdtLastModifiedgteValue = sessionId
    replyPostParams.mustfilterdtLastModifiedltValue = Date.now()
  }
  //console.log("repliesPostParams : ",replyPostParams)
  return replyPostParams
}

// to generate post query
function postBodyQueryBuilder(size,from,rootOrg,org,ids,status=""){
  let postParams ={}
  postParams.sizeValue = size
  postParams.fromValue = from
  postParams.rootOrgValue = rootOrg
  postParams.orgValue = org
  postParams.mustfilterId = true
  postParams.mustfilterIdValue = ids
  if(status!=""){
    postParams.mustfilterstatus = true
    postParams.mustfilterstatusValue = [status]
  }
  return postParams
}


function mustFilter(params, key, value) {
  try {
    params['mustfilter' + key] = true
    params['mustfilter' + key + 'Value'] = value
  } catch (error) {
    log.error('error', error)
    throw error.toString()
  }
  return params;
}

function shouldFilter(params, key, value, shouldComma) {
  try {
    if(key=="threadContributors"){
      params['shouldfilter' + key] = true
      params['shouldfilter' + key +'Value'] = value
    }
    else{
      filterKey = 'shouldfilter' + key + 'Value'
      params['shouldfilter' + key] = [
        {
          'shouldComma': shouldComma
        }
      ]
      params['shouldfilter' + key][0]['shouldfilter' + key + 'Value'] = value

    }
    
  } catch (error) {
    log.error('error', error)
    throw error.toString()
  }
  return params;
}
//to fetch the timeline for discussion
async function discussList(request, pgNo, pgSize) {
  try {
    let result = {}
    let hitCount = 0
    let newDataCount = 0
    let idsList = []
    let timelineContent = []
    let followData = {}
    let threadcontributorsList = []
    let accessPathsList = []
    estype = "threadType"
    esindex = "threadIndex"
    estemplate = "threadTimelineTemplate"

    let pageNo = pgNo * pgSize
    accessPathsList =await timelineUtilServices.getAccessPaths(request.userId,request.rootOrg)

    let userId = request['userId']
    let rootOrg = request['rootOrg']
    let org = request['org']
    let params = {}
    params['sizeValue'] = pgSize
    params['fromValue'] = pageNo
    params['sortField'] = "dtLastModified"
    params['sortOrder'] = "desc"
    params['rootOrgValue'] = request['rootOrg']
    params['orgValue'] = request['org']
    params['mustfilterdtLastModified'] = true
    params['mustfilterdtLastModifiedgteValue'] = 0
    params['mustfilterdtLastModifiedltValue'] = request['sessionId']
    params['mustfilteraccessPathsValue'] = accessPathsList
    if (request['postKind'] != "") {
      params['mustfilterpostKind'] = true
      params['mustfilterpostKindValue'] = request['postKind']
    }

    if (request['type'] != 'myDrafts') {
      params = mustFilter(params, 'status', ['Active'])
    }
    // if(request['type'] == 'discussionForum'){
    //   params['mustfiltersourceid'] = true
    //   params['']
    // }

    /**
     * type =
     * all => get all the active data w.r.t current timeline
     * myTimeline => get all active data ,where user have contributed,tags followed,persons followed, groups part of ,posted to user
     * people => get all active data contributed by people user follow
     * tags => get all active data tagged to tags user follow
     * groups => get all data posted to me and groups iam part of ,currently no groups
     * unanswered => get all questions where replycount=0
     * mydraft => get all my contents where the status is draft
     * myContributions =>get all thread where the user id is present in threadcontributors
     * myPublished => get all thread where user is the creator and status active
     */
    if (request['type'] == 'myTimeline') {
      //activating the shouldfilter in mustfilter of es template
      params['mustshouldfilter'] = true
      //get following 

      followquery = "select type,targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and root_org = ? and org =?;"
      let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
      if (result.rowLength > 0) {
        result.rows.forEach(element => {
          console.log(element)
          value = []
          if (element['type'] in followData) {
            value = followData[element['type']]
          }
          value.push(element['targetid'].toString())
          followData[element['type']] = value
        });
      }

      if ("person" in followData) {
        for (let i = 0; i < followData['person'].length; i++) {
          threadcontributorsList.push(followData['person'][i])
        }
      }
      threadcontributorsList.push(userId)
      params = shouldFilter(params, 'threadContributors', threadcontributorsList, true)
      //console.log(threadcontributorsList)
      //getting accessPaths of user


      //tags to be added
      if ("tags" in followData) {
        params = shouldFilter(params, 'tags', followData['tags'], true)
      }
    }
    else if (request['type'] == "people") {
      //get followings from cassandra
      followquery = "select targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and type='person' and root_org = ? and org =?;"
      let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
      if (result.rowLength > 0) {
        result.rows.forEach(element => {
          threadcontributorsList.push(element['targetid'].toString())
        });
        params = mustFilter(params, 'threadContributors', threadcontributorsList)
      }
      else {
        return 'You have not yet followed any one!!'
      }
    }
    else if (request['type'] == "tags") {
      //if there is a searchWord in the request body ,it means the threads associated with that searchWord should be returned
      //get followings from cassandra
      let tagsList = []
      if(request.hasOwnProperty('searchWord')==true && request.searchWord.length>0){
        tagsList = request.searchWord
        params = mustFilter(params, 'tags', tagsList)
      }
      else{
        followquery = "select targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and type='tags' and root_org = ? and org =?;"
        let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
        if (result.rowLength > 0) {
          result.rows.forEach(element => {
            tagsList.push(element['targetid'].toString())
          });
          params = mustFilter(params, 'tags', tagsList)
        }
        else {
          return 'You have not yet followed any tags!!'
        }
      }
      
    }
    else if (request['type'] == "groups") {
      //get followings from cassandra
      let groupsList = []
      groupsList.push(userId)
      followquery = "select targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and type='groups' and root_org = ? and org =?;"
      let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
      if (result.rowLength > 0) {
        result.rows.forEach(element => {
          groupsList.push(element['targetid'].toString())
        });
      }
      // else{
      //   return 'You have not yet part of any groups!!'
      // }
    }
    else if (request['type'] == 'unanswered') {
      params = mustFilter(params, 'replyCount', 0)
    }
    else if (request['type'] == 'myDrafts') {
      threadcontributorsList.push(userId)
      params = mustFilter(params, 'postCreator', threadcontributorsList)
      params = mustFilter(params, 'status', ['Draft'])
      estype = "postType"
      esindex = "postIndex"
      estemplate = "postTemplate"
    }
    else if(request['type'] == 'hashTags'){
      //threads based on hashtags
      //`TODO: Check if searchWord check is there
      params = mustFilter(params,'hashTags',request.searchWord)
    }
    if(request['type'] == 'discussionForum'){
      params['mustfiltersourceid'] = true
      params['mustfiltersourceidValue'] = [request['source']['id']]
      params['mustfiltersourcename'] = true
      params['mustfiltersourcenameValue'] = [request['source']['name']]
    }
    else{
      params['mustfiltersourcename'] = true
      params['mustfiltersourcenameValue'] = ["Social"]
    }

    console.log(params)
    let timelineResult = await esDb.templateSearch(params, estype, esindex, estemplate)
    result_Hits = timelineResult["hits"]
    hitCount = 0
    if (result_Hits["total"] > 0 && result_Hits['hits'].length>0) {
      hitCount = result_Hits["total"]
      sourceData = result_Hits["hits"]
      for (let data of sourceData) {
        data = data['_source']
        idsList.push(data["id"])
        timelineContent.push(data)
        if (request['type'] != 'myDrafts') {
          latestReply = data["latestReply"]
          if (Object.keys(latestReply).length) {
            idsList.push(latestReply["id"])
          }
        }

      }
    }

    params['mustfilterdtLastModifiedgteValue'] = request['sessionId']
    params['mustfilterdtLastModifiedltValue'] = Date.now()
    //console.log(params)
    let newDataResult = await esDb.templateSearch(params, estype, esindex, estemplate)
    newDataCount = newDataResult["hits"]["total"]
    
    if(idsList.length>0 && request['type'] != 'myDrafts'){
      activityObject = {
        userId: request['userId'],
        rootOrg: request['rootOrg'],
        org: request['org'],
        postId: idsList
      }
  
      let activityResult = await timelineUtilServices.fetchActivity(activityObject)
      timelineContent.forEach(element => {
        element.activity = activityResult.get(element['id'])
        latestReply = element['latestReply']
        latestReply.activity = activityResult.get(latestReply['id'])
        element.latestReply = latestReply
      });
    }

    result = {
      "hits": hitCount,
      "result": timelineContent,
      "sessionId": request['sessionId'],
      "newDataCount": newDataCount,
    }
    return result


  } catch (error) {
    log.error('error', JSON.stringify(error))
    throw error.toString()
  }
}


//to fetch the timeline
async function timeline(request, pgNo, pgSize) {
  try {
    let result = {}
    let hitCount = 0
    let newDataCount = 0
    let idsList = []
    let timelineContent = []
    let followData = {}
    let threadcontributorsList = []
    let accessPathsList = []
    estype = "threadType"
    esindex = "threadIndex"
    estemplate = "threadTimelineTemplate"

    let pageNo = pgNo * pgSize
    accessPathsList =await timelineUtilServices.getAccessPaths(request.userId,request.rootOrg)

    let userId = request['userId']
    let rootOrg = request['rootOrg']
    let org = request['org']
    let params = {}
    params['sizeValue'] = pgSize
    params['fromValue'] = pageNo
    params['sortField'] = "dtLastModified"
    params['sortOrder'] = "desc"
    params['rootOrgValue'] = request['rootOrg']
    params['orgValue'] = request['org']
    params['mustfilterdtLastModified'] = true
    params['mustfilterdtLastModifiedgteValue'] = 0
    params['mustfilterdtLastModifiedltValue'] = request['sessionId']
    params['mustfilteraccessPathsValue'] = accessPathsList
    if (request['postKind'] != "") {
      params['mustfilterpostKind'] = true
      params['mustfilterpostKindValue'] = request['postKind']
    }

    if (request['type'] != 'myDrafts') {
      params = mustFilter(params, 'status', ['Active'])
    }
    // if(request['type'] == 'discussionForum'){
    //   params['mustfiltersourceid'] = true
    //   params['']
    // }

    /**
     * type =
     * all => get all the active data w.r.t current timeline
     * myTimeline => get all active data ,where user have contributed,tags followed,persons followed, groups part of ,posted to user
     * people => get all active data contributed by people user follow
     * tags => get all active data tagged to tags user follow
     * groups => get all data posted to me and groups iam part of ,currently no groups
     * unanswered => get all questions where replycount=0
     * mydraft => get all my contents where the status is draft
     * myContributions =>get all thread where the user id is present in threadcontributors
     * myPublished => get all thread where user is the creator and status active
     */
    if (request['type'] == 'myTimeline') {
      //activating the shouldfilter in mustfilter of es template
      params['mustshouldfilter'] = true
      //get following 

      followquery = "select type,targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and root_org = ? and org =?;"
      let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
      if (result.rowLength > 0) {
        result.rows.forEach(element => {
          console.log(element)
          value = []
          if (element['type'] in followData) {
            value = followData[element['type']]
          }
          value.push(element['targetid'].toString())
          followData[element['type']] = value
        });
      }

      if ("person" in followData) {
        for (let i = 0; i < followData['person'].length; i++) {
          threadcontributorsList.push(followData['person'][i])
        }
      }
      threadcontributorsList.push(userId)
      params = shouldFilter(params, 'threadContributors', threadcontributorsList, true)
      //console.log(threadcontributorsList)
      //getting accessPaths of user


      //tags to be added
      if ("tags" in followData) {
        params = shouldFilter(params, 'tags', followData['tags'], true)
      }
    }
    else if (request['type'] == "people") {
      //get followings from cassandra
      followquery = "select targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and type='person' and root_org = ? and org =?;"
      let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
      if (result.rowLength > 0) {
        result.rows.forEach(element => {
          threadcontributorsList.push(element['targetid'].toString())
        });
        params = mustFilter(params, 'threadContributors', threadcontributorsList)
      }
      else {
        return 'You have not yet followed any one!!'
      }
    }
    else if (request['type'] == "tags") {
      //if there is a searchWord in the request body ,it means the threads associated with that searchWord should be returned
      //get followings from cassandra
      let tagsList = []
      if(request.hasOwnProperty('searchWord')==true && request.searchWord.length>0){
        tagsList = request.searchWord
        params = mustFilter(params, 'tags', tagsList)
      }
      else{
        followquery = "select targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and type='tags' and root_org = ? and org =?;"
        let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
        if (result.rowLength > 0) {
          result.rows.forEach(element => {
            tagsList.push(element['targetid'].toString())
          });
          params = mustFilter(params, 'tags', tagsList)
        }
        else {
          return 'You have not yet followed any tags!!'
        }
      }
      
    }
    else if (request['type'] == "groups") {
      //get followings from cassandra
      let groupsList = []
      groupsList.push(userId)
      followquery = "select targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and type='groups' and root_org = ? and org =?;"
      let result = await cassDb.executeQuery(followquery, [userId, rootOrg, org])
      if (result.rowLength > 0) {
        result.rows.forEach(element => {
          groupsList.push(element['targetid'].toString())
        });
      }
      // else{
      //   return 'You have not yet part of any groups!!'
      // }
    }
    else if (request['type'] == 'unanswered') {
      params = mustFilter(params, 'replyCount', 0)
    }
    else if (request['type'] == 'myDrafts') {
      threadcontributorsList.push(userId)
      params = mustFilter(params, 'postCreator', threadcontributorsList)
      params = mustFilter(params, 'status', ['Draft'])
      estype = "postType"
      esindex = "postIndex"
      estemplate = "postTemplate"
    }
    else if(request['type'] == 'hashTags'){
      //threads based on hashtags
      //`TODO: Check if searchWord check is there
      params = mustFilter(params,'hashTags',request.searchWord)
    }
    if(request['type'] == 'discussionForum'){
      params['mustfiltersourceid'] = true
      params['mustfiltersourceidValue'] = [request['source']['id']]
      params['mustfiltersourcename'] = true
      params['mustfiltersourcenameValue'] = [request['source']['name']]
    }
    else{
      params['mustfiltersourcename'] = true
      params['mustfiltersourcenameValue'] = ["Social"]
    }

    console.log(params)
    let timelineResult = await esDb.templateSearch(params, estype, esindex, estemplate)
    result_Hits = timelineResult["hits"]
    hitCount = 0
    if (result_Hits["total"] > 0 && result_Hits['hits'].length>0) {
      hitCount = result_Hits["total"]
      sourceData = result_Hits["hits"]
      for (let data of sourceData) {
        data = data['_source']
        idsList.push(data["id"])
        timelineContent.push(data)
        if (request['type'] != 'myDrafts') {
          latestReply = data["latestReply"]
          if (Object.keys(latestReply).length) {
            idsList.push(latestReply["id"])
          }
        }

      }
    }

    params['mustfilterdtLastModifiedgteValue'] = request['sessionId']
    params['mustfilterdtLastModifiedltValue'] = Date.now()
    //console.log(params)
    let newDataResult = await esDb.templateSearch(params, estype, esindex, estemplate)
    newDataCount = newDataResult["hits"]["total"]
    
    if(idsList.length>0 && request['type'] != 'myDrafts'){
      activityObject = {
        userId: request['userId'],
        rootOrg: request['rootOrg'],
        org: request['org'],
        postId: idsList
      }
  
      let activityResult = await timelineUtilServices.fetchActivity(activityObject)
      timelineContent.forEach(element => {
        element.activity = activityResult.get(element['id'])
        latestReply = element['latestReply']
        latestReply.activity = activityResult.get(latestReply['id'])
        element.latestReply = latestReply
      });
    }

    result = {
      "hits": hitCount,
      "result": timelineContent,
      "sessionId": request['sessionId'],
      "newDataCount": newDataCount,
    }
    return result


  } catch (error) {
    log.error('error', JSON.stringify(error))
    throw error.toString()
  }
}

//for autocomplete of tags
async function tagsAutocomplete(searchword, pgno = 0, pgsize = 10) {
  try {
    let pageNo = pgno * pgsize
    let result = []
    let params = {}
    params['sizeValue'] = pgsize
    params['fromValue'] = pageNo
    params['tokenValue'] = searchword
    let tagsAutocompleteResult = await esDb.templateSearch(params, "tagsIndex", "tagsType", "tagsTemplate")
    if (tagsAutocompleteResult['hits']['total'] > 0) {
      sourceData = tagsAutocompleteResult["hits"]['hits']
      for (let data of sourceData) {
        data = data['_source']
        result.push(data)
      }
    }
    return result

  } catch (error) {
    log.error('error', error)
    throw error.toString()
  }
}


//to validate the whether the given user is author of the given post id, for delete purpose
async function validateAuthor(rootOrg,org,userId,postId) {
  try {
    //validating user
    // query = "select id from sunbird.user where id=?"
    // let result = await cassDb.executeQuery(query, [userId])
    // if (result.rowLength <= 0){
    //   return {status: 404 , message : "USER_NOT_FOUND"}
    // }

    //get the post
    let params ={}
    params['sizeValue'] = 10
    params['fromValue'] = 0
    params['rootOrgValue'] = rootOrg
    params['orgValue'] = org
    params['mustfilterId'] = true
    params['mustfilterIdValue'] = [postId.toString()]
    
    let postDataResult = await esDb.templateSearch(params, "postType", "postIndex", "postTemplate")
    if (postDataResult['hits']['total'] > 0) {
      sourceData = postDataResult["hits"]['hits']
      for (let data of sourceData) {
        data = data['_source']
        postCreator = data['postCreator']
        postEditor = data['postEditor']
        if(postCreator['postCreatorId']== userId){
          return {status: 200 , message : "VALID_AUTHOR"}
        }
        else if(postEditor.length>0){
          postEditor.forEach(element => {
            if(element['postEditorId']==userId){
              return {status: 200 , message : "VALID_AUTHOR"}
            }
          });
        }
      }
    }
    else
    {
      throw { statuscode: 404, err: "POST_NOT_FOUND", message: "POST_NOT_FOUND" }
    }
    throw { statuscode: 401, err: "UNAUTHORIZED", message: "UNAUTHORIZED" }

  } catch (error) {

    if (error.statuscode) {
      //console.log("statuscode : ",error.statuscode)
      throw error
    }
    else {
      log.error("unexpected error" + error)
      throw { statuscode: 500, err: "internal server error", message: "unexpected error" }
    }
  }
}

async function fetchUsers(request,pgNo=0,pgSize=10,activityType='like'){
  try {
    let result ={
      "total":0,
      "users":[]
    }
    usersobj={}

    //querying using template
    let params = {}
    params.sizeValue = pgSize
    params.rootOrgValue = request.rootOrg
    params.orgValue = request.org
    params.mustpostidstatus = true
    params.postidValue = [request.postId]
    params.mustfilteractivity = true
    
    if(activityType == 'like' || activityType.toLowerCase() == 'like'){
      params.activityField = "like.isLiked"
      activityType = 'like'
    }
    if(activityType == 'upVote' || activityType.toLowerCase() =='upvote'){
      params.activityField = "upVote.isupVoted"
      activityType = 'upVote'
    }
    if(activityType == 'downVote' || activityType.toLowerCase() == 'downvote'){
      params.activityField = "downVote.isdownVoted"
      activityType = 'downVote'
    }
    params.activityPath = activityType
    params.activityValue = true
    params.fromfilter = true
    params.fromValue = pgNo
    params.sortfilter = true
    params.sortField = activityType+".dtActivity"
    params.sortOrder = "desc"
    params.sortPath = activityType
    let activityResult = await  esDb.templateSearch(params, "userpostactivityType", "userpostactivityIndex", "userpostactivityTemplate")
    if(activityResult.hits.total >0){
      result.total = activityResult.hits.total
      let usersList = []
      let activityResultData = activityResult.hits.hits
      for(let data of activityResultData){
        usersList.push(data._source.userId)
      }
      if(usersList.length>0){
        usersobj=await followServices.getPersonData(request.rootOrg,request.org,usersList)
        //get user details from cassandra
        // userDetailsQuery = "select id,firstname,lastname from sunbird.user where id in ?"
        // let userDetailsResult = await cassDb.executeQuery(userDetailsQuery,[usersList])
        // if(userDetailsResult.rowLength>0){
        //   let userDetailsList =[]
        //   userDetailsResult.rows.forEach(element => {
        //     let userData = {
        //       "id":"",
        //       "name":""
        //     }
        //     userData.id = element['id']
        //     userData.name = element['firstname'] +' ' + element['lastname']
        //     userDetailsList.push(userData)
        //   });
        //   result.users = userDetailsList
        // }
        result.users=usersobj["person"]
        return result
      }
      else{
        throw { statuscode: 404, err: "No User Activity Found", message: "No User Activity Found" }
      }
    }
    else{
      throw { statuscode: 404, err: "No Data Found", message: "No Data Found" }
    }

  } catch (error) {
    if(error.statuscode){
      throw error
    }
    else{
      log.error("unexpected error" + error)
      throw { statuscode: 500, err: "internal server error", message: "unexpected error" }
    }
  }
}

//for autocomplete of hashtags
//search word needs to be lowercased ,as it is stored in lowercase in hashtag index
async function hashtagsAutocomplete(searchword, pgno = 0, pgsize = 10){
  try {
    let pageNo = pgno * pgsize
    let result = []
    let params = {}
    params['sizeValue'] = pgsize
    params['fromValue'] = pageNo
    params['nameValue'] = searchword.toLowerCase()
    let tagsAutocompleteResult = await esDb.templateSearch(params, "hashtagsIndex", "hashtagsType", "hashtagsTemplate")
    if (tagsAutocompleteResult['hits']['total'] > 0) {
      sourceData = tagsAutocompleteResult["hits"]['hits']
      for (let data of sourceData) {
        data = data['_source']
        result.push(data)
      }
    }
    return result

  } catch (error) {
    log.error('error', error)
    throw error.toString()
  }
}

async function timelinev2(request,pgNo,pgSize){
  try {
    /**
    * type =>
    *all -> get all activities , and aggs without any conditions
    *myTimeline  => get all active data ,where user have contributed,tags followed,persons followed, groups part of ,posted to user, aggs includes these fields only
    *people => get all active data contributed by people user follow , aggs includes only these values
    *tags =>get all active data tagged to tags user follow , aggs includes only these values
    *groups => get all active data that belongs to group where user is part of , aggs includes only these values
    *unanswered => get all questions where replycount=0
    * mydraft => get all my contents where the status is draft
    * myContributions =>get all thread where the user id is present in threadcontributors
    * myPublished => get all thread where user is the creator and status active
    */
    let result = {}
    let idsList = []
    let params = {}
    let accessPaths = []
    let newDocCount = 0
    let totalDocCount = 0
    let timelineContent = []
    let aggsFilters = []
    let followData = []
    let threadcontributorsList = []

    let pageNo = pgNo * pgSize

    estype = "threadType"
    esindex = "threadIndex"
    estemplate = "threadTimelineTemplate_v1"

    accessPaths =await timelineUtilServices.getAccessPaths(request.userId,request.rootOrg)
    // if accessPath is provided as a filter, checking if the user is having access to those access paths
    if(request.hasOwnProperty("filters")== true && request.filters.hasOwnProperty("accessPaths")==true){
      finalList = []
      accessPaths.forEach(e1 => request.filters.accessPaths.forEach(e2 =>{
          if(e1==e2){
              finalList.push(e1)
          }
      })
      );
      accessPaths = finalList
      delete request.filters.accessPaths
    }
    if(accessPaths.length==0){
        throw { statuscode: 404, err: "Access Restricted", message: "Access Restricted" }
    }
    params['sizeValue'] = pgSize
    params['fromValue'] = pageNo
    params['sortField'] = "dtLastModified"
    params['sortOrder'] = "desc"

    //setting all implicit filters
    params['rootOrgValue'] = request['rootOrg']
    params['orgValue'] = request['org']
    params['mustfilterstatusValue'] =['Active']
    params['mustfilterdtLastModifiedgteValue'] = 0
    params['mustfilterdtLastModifiedltValue'] = request['sessionId']
    params['mustfilterpostKindValue'] = request['postKind']
    
    params['tagsAggregation'] = true
    params['threadContributorsAggregation'] = true
    params['hashtagsAggregation'] = true
    //removing accessPath restriction for admin
    if(request['type'] != "admin"){
      params["includeaccessPaths"] = true
      params['mustfilteraccessPaths'] = true
      params['mustfilteraccessPathsValue'] = accessPaths
    }

    if(request['type'] == 'myTimeline'){
      params["mustshouldfilter"] = true
      followQuery = "select type,targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and root_org = ? and org =?;"
      //let result = await cassDb.executeQuery(followQuery, [userId, rootOrg, org])
      let result = await cassDb.executeQuery(followQuery, [request.userId,request.rootOrg, request.org])
      //console.log(result)
      if(result.rowLength > 0){
        result.rows.forEach(element =>{
          value = []
          if(element['type'] in followData){
            value = followData[element['type']]
          }
          value.push(element['targetid'].toString())
          followData[element['type']] =value
        });
      }

      if("person" in followData){
        for (let i = 0; i < followData['person'].length; i++){
          threadcontributorsList.push(followData['person'][i])
        }
      }
      threadcontributorsList.push(request.userId)
      params = shouldFilter(params, 'threadContributors', threadcontributorsList, true)
      params['includethreadContributors'] = true
      params['includethreadContributorsValue'] = threadcontributorsList

      if("tags" in followData){
        params = shouldFilter(params, 'tags', followData['tags'], true)
        params['includetags'] = true
        params['includetagsValue'] = followData['tags']
      }

    }
    else if(request['type'] == 'people'){
      followQuery = "select targetid from bodhi.follow_by_sourceid where sourceidv1 = ? and status='follow' and type='person' and root_org = ? and org =?;"
      //let result = await cassDb.executeQuery(followQuery, [userId, rootOrg, org])
      let result = await cassDb.executeQuery(followQuery, [request.userId,request.rootOrg, request.org])
      if(result.rowLength > 0){
        result.rows.forEach(element =>{
          threadcontributorsList.push(element['targetid'].toString())
        });
        params = mustFilter(params, 'threadContributors', threadcontributorsList)
        params['includethreadContributors'] = true
        params['includethreadContributorsValue'] = threadcontributorsList
      }
    
    }
    else if(request['type'] == 'tags'){
      let tagsList = []
      followQuery = "select targetid from bodhi.follow_by_sourceid where sourceid = ? and status='follow' and type='tags' and root_org = ? and org =?;"
      //let result = await cassDb.executeQuery(followQuery, [userId, rootOrg, org])
      let result = await cassDb.executeQuery(followQuery, [request.userId,request.rootOrg, request.org])
      if (result.rowLength > 0){
        result.rows.forEach(element=>{
          tagsList.push(element['targetid'].toString())
        });
        params = mustFilter(params,'tags',tagsList)
        params['includetags'] = true
        params['includetagsValue'] =  tagsList
      }
    }
    else if(request['type'] == 'unanswered'){
      params = mustFilter(params, 'replyCount', 0)
    }
    else if(request['type'] == 'myDrafts'){
      threadcontributorsList.push(userId)
      params = mustFilter(params, 'postCreator', threadcontributorsList)
      params = mustFilter(params, 'status', ['Draft'])
      estype = "postType"
      esindex = "postIndex"
      estemplate = "postTemplate"
    }
    else if(request['type'] == 'hashTags'){
      //threads based on hashtags
      params = mustFilter(params,'hashTags',request.filters["hashTags"])
    }
    else if(request['type'] == 'discussionForum'){
      params['mustfiltersourceid'] = true
      params['mustfiltersourceidValue'] = [request['source']['id']]
      params['mustfiltersourcename'] = true
      params['mustfiltersourcenameValue'] = [request['source']['name']]
    }
    else if(request['type'] == 'myContributions'){
      threadcontributorsList.push(userId)
      params = mustFilter(params, 'threadContributors', threadcontributorsList)
    }
    else if(request['type'] == 'myPublished'){
      threadcontributorsList.push(userId)
      params = mustFilter(params, 'postCreator', threadcontributorsList)
    }
    else if(request['type'] == 'selected'){
      if(request.hasOwnProperty('filters')== true){
        for(var key in request.filters){
          params = mustFilter(params,key,request.filters[key])
        }
        if(request.filters.hasOwnProperty('threadContributors')==true){
          params['threadContributorsAggregation'] = false
        }
        else if(request.filters.hasOwnProperty('tags')==true){
          params['tagsAggregation'] = false
        }
        else if(request.filters.hasOwnProperty('hashTags')==true){
          params['hashtagsAggregation'] = false
        }
      }
    }

    //checking if any filters are applied
    if(request.hasOwnProperty('filters')== true){
      for(var key in request.filters){
        params = mustFilter(params,key,request.filters[key])
      }
    }

    //console.log(params)
    let timelineResult = await esDb.templateSearch(params,estype,esindex,estemplate)
    let timelineResultHits = timelineResult['hits']
    if(timelineResultHits['total']>0 && timelineResultHits['hits'].length>0){
      totalDocCount = timelineResultHits['total']
      let timelineSourceData = timelineResultHits['hits']
      for(let data of timelineSourceData){
        data = data['_source']
        idsList.push(data['id'])
        timelineContent.push(data)
        if(data.hasOwnProperty('latestReply')==true && Object.keys(data['latestReply']).length){
          idsList.push(data['latestReply']['id'])
        }
      }

      //getting the aggs/filters
      let aggsResultHits = timelineResult['aggregations']['TotalAggs']
      //console.log(aggsResultHits)
      aggsFilters = timelineUtilServices.aggegrationsData(aggsResultHits)

      //activity data
      if(idsList.length>0 && request['type']!='myDrafts'){
        let activityObject = {
          userId : request['userId'],
          rootOrg : request['rootOrg'],
          org : request['org'],
          postId : idsList
        }

        let activityResult = await timelineUtilServices.fetchActivity(activityObject)
        timelineContent.forEach(element =>{
          element.activity = activityResult.get(element['id'])
          latestReply = element['latestReply']
          latestReply.activity =activityResult.get(latestReply['id'])
          element.latestReply = latestReply
        })
      }

      //new data count
      params['mustfilterdtLastModifiedgteValue'] = request['sessionId']
      params['mustfilterdtLastModifiedltValue'] = Date.now()
      let newDataResult = await esDb.templateSearch(params,estype,esindex,estemplate)
      newDocCount = newDataResult["hits"]["total"]

    }
    else{
      // no data found
    }

    result = {
      "hits" : totalDocCount,
      "result" : timelineContent,
      "sessionId" : request['sessionId'],
      "newDataCount" : newDocCount,
      "filters" : aggsFilters
    }

    return result

  } catch (error) {
    if (error.statuscode) {
      throw error;
    } 
    else {
      log.error("Unexpected error : ",error)
      throw { statuscode: 500, err: "internal server error", message: "unexpected error" }
    }
  }
}

module.exports = {
  viewConversation,
  discussList,
  timeline,
  tagsAutocomplete,
  validateAuthor,
  fetchUsers,
  hashtagsAutocomplete,
  timelinev2,
  mustFilter,
  shouldFilter,
  viewConversationv2
}