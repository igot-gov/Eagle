const cassandraUtil = require('../../util/db/cassandra');
const esUtil = require('../../util/db/es-util');
const log = require('../../util/log/log');

function getAccessPathsFromCassandra(wid, rootOrg) {
  return new Promise((resolve, reject) => {
    const query = 'SELECT * FROM bodhi.user_access_paths WHERE root_org=? AND user_id=? ALLOW FILTERING';
    cassandraUtil.executeQuery(query, [rootOrg, wid], (err, result) => {
      if (!err && result && result.rows && result.rows.length > 0) {
        resolve(result.rows[0]);
      } else {
        log.error('Error while fetching the access paths data.', err);
        reject(err);
      }
    });
  });
}

function getGroupAccessPathsFromES(wid, rootOrg) {
  return new Promise((resolve, reject) => {
    const indexName = 'accesscontrolgroups';
    const clauseTerms = [
      {
        term: {
          userIds: wid,
        },
      },
      {
        term: {
          rootOrg,
        },
      },
    ];
    const sourceArray = ['accessPaths'];

    esUtil.getDataFromIndexWithMustClause(indexName, sourceArray, clauseTerms).then((result) => {
      const accessPaths = new Set();
      result.forEach((element) => {
        element.accessPaths.forEach(accessPaths.add, accessPaths); // eslint-disable-line
      });
      return resolve(Array.from(accessPaths));
    }).catch((error) => {
      console.error(error); // eslint-disable-line
      reject(new Error('Could not fetch access paths'));
    });
  });
}

function getAccessPathOfAUser(wid, rootOrg) {
  return new Promise((resolve, reject) => {
    const promiseArr = [
      getAccessPathsFromCassandra(wid, rootOrg),
      getGroupAccessPathsFromES(wid, rootOrg),
    ];
    const accessPaths = new Set();
    Promise.all(promiseArr).then((values) => {
      const cassAP = values[0];
      const esAP = values[1];

      // log.info(`Cassandra response: [${cassAP.access_paths}]`);
      // log.info(`ES response: [${esAP}]`);

      esAP.forEach(val => accessPaths.add(val));
      cassAP.access_paths.forEach(val => accessPaths.add(val));
      resolve({
        accessPaths: Array.from(accessPaths),
        org: cassAP.org,
      });
    }).catch((ex) => {
      console.error(ex); // eslint-disable-line
      reject(ex);
    });
  });
}

module.exports = {
  getAccessPathOfAUser,
};
