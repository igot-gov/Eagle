// Logger
var log = require('../Logger/log');

// Config loader
var config = require('../ConfigReader/loader');

const cassandra = require('cassandra-driver');

const cassandraHost = config.getProperty('cassandra_host');
const cassandraKeyspace = config.getProperty('cassandra_keyspace');
const cassandraUsername = config.getProperty('cassandra_username');
const cassandraPassword = config.getProperty('cassandra_password');
var client = null;

// This method will try to connect to cassandra.
/**
 * Will make a pooled connection with cassandra
 */
function connectClient() {
	log.info(`Cassandra username: ${cassandraUsername}`);
	log.info('Cassandra password: xxxx cassandra password xxxx');

	var authProvider = new cassandra.auth.PlainTextAuthProvider(cassandraUsername, cassandraPassword); //eslint-disable-line
	const options = {
		contactPoints: [cassandraHost],
		keyspace: cassandraKeyspace,
		authProvider: authProvider
	};
	// console.log('Cassandra options:', options); //eslint-disable-line
	client = new cassandra.Client(options);
}

/**
 * Will execute the query and callback the error and results
 * @param {Query to execute} query
 * @param {Array of params} params
 * @param {*} callback
 */
function executeQuery(query, params, callback) {
	if (client === null) {
		connectClient();
	}
	client.execute(query, params, {
		prepare: true
	}).then((result) => {
		log.info('Results: ' + result);
		if (callback) {
			callback(null, result);
		}
	}, (err) => {
		// log.error(err, null);
		callback(err, null);
	});
}

/**
 * Will execute the queries supplied as a batch.
 * @param {queriesArray} queriesWithParamsObjArr
 * @param {*} callback
 */
function executeBatch(queriesWithParamsObjArr, callback) {
	if (validateBatchQueries(queriesWithParamsObjArr)) {
		if (client === null) {
			connectClient();
		}
		client.batch(queriesWithParamsObjArr, {
			prepare: true
		}).then(result => {
			if (callback) {
				callback(null, result);
			}
		}, (err) => {
			if (err) {
				log.error(err, null);
				callback(err, null);
			}
		});
	} else {
		callback({
			'err': 'Internal Server Error',
			'msg': 'Batch queries were not properly structured'
		});
	}
}

/**
 *
 * @param {String} query Query that needs to be executed for paginated results
 * @param {Array} parameters Array of parameters if the query is needs preparation
 * @param {Object} options JSON options for the pagination
 */
function executePaginatedQueries(query, parameters, options) {
	return new Promise((resolve, reject) => {
		if (client === null) {
			connectClient();
		}
		let results = [];
		client.eachRow(query, parameters, options, function (n, row) {
			// Row callback.
			results.push(row);
		}, function (err, result) {
			if (err) {
				reject(err);
			}
			resolve({
				results: results,
				pageState: results.length>0 ? result.pageState: undefined,
				pageSize: results.length>0 ? options.fetchSize: undefined
			});
		});
	});
}

/**
 * Validates the input for the queries arrays.
 * Sample structure for queries array
 * queries = [
  {
    query: 'UPDATE user_profiles SET email=? WHERE key=?',
    params: [ emailAddress, 'hendrix' ]
  },
  {
    query: 'INSERT INTO user_track (key, text, date) VALUES (?, ?, ?)',
    params: [ 'hendrix', 'Changed email', new Date() ]
  }
]
 * @param {Array of query objects} queriesArr
 */
function validateBatchQueries(queriesArr) {
	if (queriesArr instanceof Array) {
		for (var i = 0; i < queriesArr; i++) {
			if (queriesArr[i].query && typeof queriesArr[i].query == 'string' && queriesArr[i].params && queriesArr[i].params instanceof Array) {
				// Valid structure, can make come changes to data here later
			} else {
				return false;
			}
		}
		return true;
	} else {
		return false;
	}
}
module.exports = {
	executeQuery: executeQuery,
	executePaginatedQueries: executePaginatedQueries,
	executeBatch: executeBatch
};
