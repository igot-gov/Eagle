const express = require('express');
const metaUtil = require('../../../services/meta/util');
const cacheService = require('../../../services/app-services/cache/cache-service');
const hierarchy = require('./hierarchy');
const accessPathUtils = require('../../../services/access-paths/util');

const log = require('../../../util/log/log');

const uiLiteQueryParamValue = 'UI_LITE';
const defaultFields = ['identifier', 'children', 'status', 'accessPaths'];

function getHierarchyDataAndSave(contentId, params, options) {
  return new Promise((resolve, reject) => {
    hierarchy
      .getHierarchyData(contentId, params, options)
      .then((hierarchyRes) => {
        try {
          resolve(hierarchyRes);
        } catch (e) {
          console.error(e); // eslint-disable-line
        }
      })
      .catch((e) => {
        console.error(e); // eslint-disable-line
        reject(e);
      });
  });
}

function filerHierarchyRequest(req, res, next) {
  // Validating the root org and wid headers.
  let { wid, rootorg } = req.headers;

  // Checking if the body has userId or rootOrg
  if (req.body) {
    if (req.body.userId) {
      wid = req.body.userId;
    }
    if (req.body.rootOrg) {
      rootorg = req.body.rootOrg;
    }
  }
  // If neither headers not body has the required values, throw Invalid request
  if (!wid || !rootorg) {
    return res.status(400).send({
      code: 400,
      msg: 'Invalid wid and rootorg values',
    });
  }

  // Validation the _source field value.
  if (req.query._source) { // eslint-disable-line
    try {
      JSON.parse(req.query._source); // eslint-disable-line
    } catch (e) {
      if (e instanceof SyntaxError) {
        return res.status(400).send({
          code: 400,
          msg: 'Invalid _source field. Source field must be a JSON object',
          error: e.message,
        });
      }
      console.error(e); //eslint-disable-line
      return res.status(500).send({
        code: 500,
        msg: 'Error while processing the request',
      });
    }
  }
  return next();
}

function validateAccessPathsForCache(accessPathsArr, isContent) {
  // Checking if the access paths has only one rootOrg and rootOrg/org value.
  // We ignore if more than one rootOrg is present.
  // We ignore if more than one rootOrg/org is present.
  if (isContent) {
    const filterdAp = accessPathsArr
      .filter(val => val.split('/').length === 2);
    if (filterdAp.length === 1) {
      return {
        rootOrg: filterdAp[0].split('/')[0],
        org: filterdAp[0].split('/')[1],
      };
    }
    throw new Error('Unable to fetch the root org and org information');
  }
  if (accessPathsArr.length === 2) {
    const rootOrgOfAccessPaths = accessPathsArr.filter(val => val.split('/').length === 1)[0];
    const orgOfAccessPaths = accessPathsArr
      .filter(val => val.split('/').length === 2)
      .map((val) => { // eslint-disable-line
        if (val.split(`${rootOrgOfAccessPaths}/`).length === 2) {
          return val.split(`${rootOrgOfAccessPaths}/`)[1];
        }
      })[0];

    if (rootOrgOfAccessPaths && orgOfAccessPaths) {
      return {
        rootOrg: rootOrgOfAccessPaths,
        org: orgOfAccessPaths,
      };
    }
    throw new Error('Unable to fetch the root org and org information');
  } else {
    throw new Error('Access paths not in expected format');
  }
}

function getUserAccessPathsForCache(accessPaths) {
  const entriesList = [];
  // Getting all the root_orgs
  const rootOrgs = accessPaths.filter(item => !item.includes('/'));

  // Getting the orgs for each root_org
  rootOrgs.forEach((rootOrg) => {
    accessPaths.filter(item => item.startsWith(rootOrg) && item.includes('/')).forEach((item) => {
      if (item.split('/').length === 2) {
        entriesList.push({
          rootOrg,
          org: item.split('/')[1],
        });
      }
    });
  });
  return entriesList;
}

function processHierarchyRequest(req, res) { // eslint-disable-line
  let { wid, rootorg } = req.headers;
  const { contentId } = req.params;

  if (req.body) {
    if (req.body.userId) {
      wid = req.body.userId;
    }
    if (req.body.rootOrg) {
      rootorg = req.body.rootOrg;
    }
  }
  if (!wid || !rootorg) {
    return res.status(400).send({
      code: 400,
      msg: 'Invalid wid and rootorg values',
    });
  }

  const options = {
    wid,
    rootOrg: rootorg,
  };

  let sourceField = new Set();
  if (req.query.dt && req.query.dt === uiLiteQueryParamValue) {
    sourceField = metaUtil.UI_LITE_KEYS;
  }
  if (req.query._source) { // eslint-disable-line
    const querySourceFields = JSON.parse(req.query._source); // eslint-disable-line
    if (querySourceFields instanceof Array) {
      querySourceFields.forEach(source => sourceField.add(source));
      sourceField = new Set(...sourceField, ...querySourceFields);
    }
  }
  // Adding the default fields if something is requested.
  if (sourceField && sourceField.size > 0) {
    defaultFields.map(val => sourceField.add(val));
  }

  const paramsObj = {};
  if (sourceField && sourceField.size > 0) {
    paramsObj._source = [...sourceField]; // eslint-disable-line
  }

  // Getting the current access paths for this user.
  accessPathUtils.getAccessPathOfAUser(wid, rootorg).then((apResponse) => { // eslint-disable-line
    log.info(`Access Paths for WID: ${wid} and ROOT_ORG: ${rootorg} is: ${JSON.stringify(apResponse)}`);
    options.accessPaths = apResponse.accessPaths; // eslint-disable-line
    options.org = apResponse.org; // eslint-disable-line

    // Caching if the request is from UI and no source fields are present.
    if (req.query.dt === uiLiteQueryParamValue
      && !req.query._source // eslint-disable-line
      && (!req.query.force || req.query.force !== 'true')) {
      try {
        const rOrgAndOrgs = getUserAccessPathsForCache(options.accessPaths);

        if (rOrgAndOrgs.length > 1) {
          log.info(`Multiple root orgs and orgs fetched for this user: ${wid}`);
        }

        // Checking the data for each root org and org.
        for (let i = 0; i < rOrgAndOrgs.length; i += 1) {
          const rOrgAndOrg = rOrgAndOrgs[i];

          // Checking if te data is present in the cache.
          const cachedFileStream = cacheService.hierarchyData.get(
            rOrgAndOrg.rootOrg, rOrgAndOrg.org, contentId,
          );
          if (cachedFileStream) {
            log.info(`Sending the cached response for ${contentId}`);
            res.set('X-Api-Cached', 'HIT');
            // return res.send(cachedData);
            res.type('json');
            return cachedFileStream.pipe(res);
          }
        }
        // No cached data
      } catch (e) {
        // console.error(e); // eslint-disable-line
      }
    }

    // Adding a new functionality, where the _source field is passed to the hierarchy
    getHierarchyDataAndSave(contentId, paramsObj, options)
      .then((result) => {
        res.set('X-Api-Cached', 'MISS');
        res.send(result);
        try {
          validateAccessPathsForCache(result.accessPaths, true);
          cacheService.hierarchyData.save(options.rootOrg, options.org, contentId, result);
        } catch (ex) {
          console.error('Access paths: ', result.accessPaths, ex); // eslint-disable-line
        }
      }).catch((e) => {
        console.error(e); // eslint-disable-line
        if (!e || !e.params) {
          e = { // eslint-disable-line
            params: {
              status: 500,
            },
          };
        }
        res.status(e.params.status).send(e);
      });
  }).catch((ex) => {
    console.error('Exception while fetching the access paths: ', ex); // eslint-disable-line
    res.status(500).send({
      code: 500,
      msg: 'Exception while fetching the access paths for this user',
    });
  });
}

const router = express.Router();
router.get('/hierarchy/:contentId', filerHierarchyRequest, (req, res) => {
  processHierarchyRequest(req, res);
});

router.post('/hierarchy/:contentId', filerHierarchyRequest, (req, res) => {
  processHierarchyRequest(req, res);
});

router.delete('/hierarchy-cache/:identifier', (req, res) => {
  if (req.params.identifier) {
    if (req.params.identifier === '*') {
      cacheService.removeAll();
    } else {
      cacheService.remove(req.params.identifier);
    }
    res.status(204).send();
    return;
  }
  res.status(400).send({
    code: 400,
    msg: 'Invalid identifier',
  });
});

module.exports = router;
