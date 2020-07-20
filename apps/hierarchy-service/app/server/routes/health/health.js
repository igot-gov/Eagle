const cassandraUtil = require('../../../util/db/cassandra');
const esUtil = require('../../../util/db/es-util');

function getCassandraHealth() {
  const query = 'select user_id from bodhi.user_roles limit 1';
  return new Promise((resolve, reject) => { // eslint-disable-line
    cassandraUtil.executeQuery(query, [], (err) => { // eslint-disable-line
      if (err) {
        return reject(err);
      }
      return resolve();
    });
  });
}

function getESHealth() {
  return new Promise((resolve, reject) => {
    esUtil.getESHealth().then((result) => {
      if (result && result.status !== 'red') {
        return resolve();
      }
      console.error('ES is unhealthy', result); // eslint-disable-line
      return reject(new Error('Elasticsearch unhealthy'));
    }).catch((err) => {
      console.error(err); // eslint-disable-line
      reject(err);
    });
  });
}

module.exports = {
  getCassandraHealth,
  getESHealth,
};
