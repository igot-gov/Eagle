const router = require('express').Router();
const healthUtil = require('./health');
const log = require('../../../util/log/log');

router.get('/', (req, res) => {
  Promise.all([healthUtil.getCassandraHealth(), healthUtil.getESHealth()]).then(() => {
    log.info('Healthy...!!!');
    res.send({
      cassandra: true,
      elasticsearch: true,
    });
  }).catch((ex) => {
    console.error(ex); // eslint-disable-line
  });
});

module.exports = router;
