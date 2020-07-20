// uuid generator
const uuidv1 = require('uuid/v1');

const log = require('../../../util/log/log');

// EsUtil
const esUtil = require('../../../util/db/es-util');

// Cache model, used to store the cached hierarchy response
const hierarchyCache = {};
const cacheHours = 6;

const ERROR_MSGS = {
  NOT_FOUND: 'NOT_FOUND',
  INVALID_HIERARCHY: 'INVALID_HIERARCHY',
  SERVER_ERROR: 'SERVER_ERROR',
};


// Will check if user has access to this content.
function doesUserHaveAccess(contentAccessPaths, userAccessPaths) {
  for (let i = 0; i < contentAccessPaths.length; i += 1) {
    for (let j = 0; j < userAccessPaths.length; j += 1) {
      if (contentAccessPaths[i] === userAccessPaths[j]) {
        return true;
      }
    }
  }
  return false;
}

// This method will get the data for a content and send the hierarchy data of the item respectively
// as per the data of the children in the order they are mentioned in the content API.
function getHieraricalDataForContentId(contentId, params, extraData, callback) {
  try {
    if (!contentId) {
      throw new Error('Content Id cannot be null');
    }

    esUtil.getIdentifierDataForHierarchy(
      [contentId],
      params,
      extraData.rootOrg,
    ).then((results) => {
      if (results.length > 0) {
        const contentObj = results[0]; // eslint-disable-line

        // const contentChildren = contentObj.children.filter((child) => {
        //   if (!child.identifier.endsWith('.img')) {
        //     return child;
        //   }
        // });

        // Removed the status check since Shyam asked to not do it thr right way
        // Told to use shortcuts instead Of fixing the conent meta in ES
        // which is done below
        const imageNodeIdentifiers = contentObj.children.filter((child) => { // eslint-disable-line
          if (child.identifier.endsWith('.img')) {
            return child.identifier.split('.img')[0];
          }
        });

        // Getting only the children which do not have a .img node
        // After first filter, checking if the existing nodes have any .img nodes.
        // Doing this due to corruption of children data.
        const contentChildren = contentObj.children
          .filter(child => !child.identifier.endsWith('.img'))
          .filter(child => !imageNodeIdentifiers.includes(child.identifier));
        if (contentChildren.length > 0) {
          let respCount = 0;
          contentChildren.forEach((child, index) => {
            getHieraricalDataForContentId(child.identifier, params, extraData, (
              err,
              contentResBody,
            ) => {
              if (err) {
                if (err.message === ERROR_MSGS.NOT_FOUND) {
                  console.error('Parents identifier: ', contentObj.identifier, '==>child resource not found: ', child.identifier); // eslint-disable-line
                  callback(new Error(ERROR_MSGS.INVALID_HIERARCHY), null);
                  // throw new Error('Invalid hierarchy');
                } else {
                  callback(err, null);
                }
                return;
              }
              if (contentResBody) {
                respCount += 1;
                if (params._source // eslint-disable-line
                  && contentResBody.children
                  && !params._source.includes('children') // eslint-disable-line
                  && contentResBody.children.length === 0
                ) {
                  // delete body.children;
                }
                if (doesUserHaveAccess(contentResBody.accessPaths, extraData.accessPaths)) {
                  contentChildren[index] = { ...child, ...contentResBody };
                } else {
                  contentChildren[index] = null;
                }

                // console.log('Body is: ', body);
                if (respCount === contentChildren.length) {
                  contentObj.children = contentChildren
                    .filter(currentChild => currentChild !== null);
                  callback(null, contentObj);
                }
              } else {
                console.error('Body is', contentResBody); // eslint-disable-line
                console.error('Error is, ', err); // eslint-disable-line
                callback(new Error(ERROR_MSGS.SERVER_ERROR), null);
              }
            });
          });
        } else {
          callback(null, contentObj);
        }
      } else {
        callback(new Error(ERROR_MSGS.NOT_FOUND), null);
      }
    }).catch((ex) => {
      console.error(ex); // eslint-disable-line
      const currentError = new Error(ERROR_MSGS.SERVER_ERROR);
      if (ex.message === ERROR_MSGS.INVALID_HIERARCHY) {
        currentError.message = ERROR_MSGS.INVALID_HIERARCHY;
      }
      callback(currentError, null);
    });
  } catch (e) {
    console.error(e); // eslint-disable-line
    throw e;
  }
}

function validateGetHierarchyDataInput(contentId) {
  if (
    !contentId
    || contentId.toString().trim().length < 1
    || contentId instanceof Array
    || typeof contentId !== 'string'
  ) {
    return false;
  }
  return true;
}

// Gets the hierarchy data of a content and will mostly call the method in a recursive manner.
function getHierarchyData(contentId, params, options) {
  // Adding the default data for the object like EkStep
  const respObj = {};
  respObj.id = 'api.course.hierarchy';
  respObj.ver = '0.1';
  respObj.ts = new Date().toISOString();
  respObj.params = {
    status: 500,
  };

  return new Promise((resolve, reject) => {
    // Checking for valid content id
    if (!validateGetHierarchyDataInput(contentId)) {
      log.error('Content id is empty or null or not a string');
      respObj.params = {
        resmsgid: uuidv1(),
        msgid: uuidv1(),
        status: '400',
        err: 'Bad request',
        errmsg: 'ContentId cannot be empty',
      };
      respObj.responseCode = 'Bad request';
      reject(respObj);
    }

    try {
      getHieraricalDataForContentId(contentId, params, options, (err, body) => {
        if (body) {
          respObj.params = {
            resmsgid: uuidv1(),
            msgid: uuidv1(),
            status: '200',
            err: null,
            errmsg: null,
          };
          respObj.responseCode = 'OK';
          respObj.result = {};
          respObj.result.content = body;
          // resolve(respObj);
          resolve(body);
        }
        if (err && err.message === ERROR_MSGS.NOT_FOUND) {
          respObj.params = {
            resmsgid: uuidv1(),
            msgid: uuidv1(),
            status: '404',
            err: 'Not found',
            errmsg: 'Resource not found',
          };
          respObj.responseCode = 'NOT FOUND';
        }
        if (err && err.message === ERROR_MSGS.INVALID_HIERARCHY) {
          respObj.params = {
            resmsgid: uuidv1(),
            msgid: uuidv1(),
            status: '400',
            err: 'Bad request',
            errmsg: 'Hierarchy is not complete for the requested resource',
          };
          respObj.responseCode = 'INVALID STRUCTURE';
        }
        if (err && err.message === ERROR_MSGS.SERVER_ERROR) {
          respObj.params = {
            resmsgid: uuidv1(),
            msgid: uuidv1(),
            status: '500',
            err: 'Error',
            errmsg: 'Internal Server Error',
          };
          respObj.responseCode = 'INTERNAL SERVER ERROR';
        }
        reject(respObj);
      });
    } catch (e) {
      log.error(e);
      respObj.params = {
        resmsgid: uuidv1(),
        msgid: uuidv1(),
        status: '500',
        err: 'Error',
        errmsg: 'Internal Server Error',
      };
      respObj.responseCode = 'Internal Server Error';
      reject(respObj);
    }
  });
}

module.exports = {
  getHierarchyData,
  hierarchyCache,
  cacheHours,
};
