const router = require('express').Router();

const cacheService = require('../../../services/app-services/cache/cache-service');

function filerReqForHeaders(req, res, next) {
  const { rootorg, org } = req.headers;

  if (!rootorg || !org) {
    return res.status(400).send({
      code: 400,
      msg: 'Invalid rootorg and org values',
    });
  }
  return next();
}

function deleteCacheFiles(lexIds) {
  return new Promise((resolve, reject) => {
    const promiseArr = [];
    lexIds.forEach((element) => {
      promiseArr.push(
        cacheService.hierarchyData.delete(element.rootOrg, element.org, element.lexId),
      );
    });

    Promise.all(promiseArr).then(() => resolve()).catch((err) => {
      console.error(err); // eslint-disable-line
      reject(err);
    });
  });
}

router.delete('/id/:lexId', filerReqForHeaders, (req, res) => {
  const { rootorg, org } = req.headers;

  deleteCacheFiles([{
    rootOrg: rootorg,
    org,
    lexId: req.params.lexId,
  }])
    .then(() => res.status(204).send())
    .catch((ex) => {
      console.error(ex); // eslint-disable-line
      return res.status(500).send({
        msg: 'Error while deleting the cache file',
        code: 500,
      });
    });
});

router.delete('/multiple', (req, res) => {
  const lexIds = req.body;
  if (!Array.isArray(lexIds) || lexIds.length === 0) {
    return res.status(400).send({
      code: 400,
      msg: 'Invalid input received',
    });
  }

  return deleteCacheFiles(lexIds)
    .then(() => res.status(204).send())
    .catch((ex) => {
      console.error(ex); // eslint-disable-line
      return res.status(500).send({
        msg: 'Error while deleting the cache file',
        code: 500,
      });
    });
});

module.exports = router;
