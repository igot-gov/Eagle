const cassandraUtil = require('../CassandraUtil/cassandra');

function getClientDetails(clientId) {
  return new Promise((resolve, reject) => {
    const query =
      'select key, name, urls, value from bodhi.api_authentication where key=?';
    cassandraUtil.executeQuery(query, [clientId], function(err, result) {
      if (err) {
        reject (err)
      }
      resolve(result.rows);
    });
  });
}

module.exports = {
  getClientDetails
};
