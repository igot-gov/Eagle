const elasticsearch = require('elasticsearch');

// Config loader
const config = require('../app/config/loader');

// Locale util
const localeService = require('../../services/select-locales/service');

const esUsername = config.getProperty('es_username');
const esPassword = config.getProperty('es_password');

const esHostsListEnv = config.getProperty('es_hosts_list');
const hosts = esHostsListEnv.split(',').map(val => `http://${esUsername}:${esPassword}@${val}/`);
const client = new elasticsearch.Client({
  hosts,
  apiVersion: '6.8',
  sniffOnStart: true,
  sniffInterval: 10,
  sniffOnConnectionFault: true,
  requestTimeout: 10000,
  sniffedNodesProtocol: 'http',
  log: ['error'],
});

const allowedStatus = ['Live', 'Expired', 'Deleted', 'MarkedForDeletion', 'Unpublished'];

// Size of result for Lex ids when requesting for data from ES.
const MAX_DATA_SIZE_CONTENT = 5000;
// Size of result for access paths when requesting for data from ES.
const MAX_DATA_SIZE_ACCESS_PATHS = 500;

function getIdentifierDataForHierarchy(contentIdArr, params, rootOrg) {
  return new Promise((resolve, reject) => {
    const requestBody = {
      query: {
        bool: {
          filter: [
            {
              terms: {
                identifier: contentIdArr,
              },
            },
            {
              terms: {
                status: allowedStatus,
              },
            },
            {
              term: {
                rootOrg,
              },
            },
          ],
        },
      },
    };
    const indicesAsPerRootOrg = localeService.getLocalesList(rootOrg);
    client.search({
      index: indicesAsPerRootOrg,
      ...params,
      body: requestBody,
    }).then((res) => {
      if (res && res.hits && res.hits.hits) {
        resolve(res.hits.hits.map(val => val._source)); // eslint-disable-line
      }
    }).catch(err => reject(err)); // eslint-disable-line
  });
}

function getDataForLexIdsAndSourceFieldWithFilters(indexNames, sourceFields, filters) {
  return new Promise((resolve, reject) => {
    if (Object.keys(filters).length < 1) {
      return reject(new Error('INVALID_FILTER_REQUEST'));
    }
    const searchObject = {
      index: indexNames,
      _source: sourceFields,
      body: {
        query: {
          bool: {
            filter: filters,
          },
        },
      },
      size: MAX_DATA_SIZE_CONTENT,
    };

    return client.search(searchObject).then((res) => {
      if (res && res.hits && res.hits.hits) {
        resolve(res.hits.hits.map(val => val._source)); // eslint-disable-line
      }
    }).catch(err => reject(err)); // eslint-disable-line
  });
}

function getDataFromIndexWithMustClause(indexNames, sourceArr, clauses) {
  if (!clauses && !Array.isArray(clauses)) {
    throw new Error('Must filter clause mismatch');
  }
  const searchObject = {
    index: indexNames,
    _source: sourceArr,
    body: {
      query: {
        bool: {
          must: clauses,
        },
      },
    },
    size: MAX_DATA_SIZE_ACCESS_PATHS,
  };

  return new Promise((resolve, reject) => {
    client.search(searchObject).then((res) => {
      if (res && res.hits && res.hits.hits) {
        resolve(res.hits.hits.map(val => val._source)); // eslint-disable-line
      }
    }).catch(err => reject(err)); // eslint-disable-line
  });
}

function getESHealth() {
  return client.cluster.health();
}

module.exports = {
  getIdentifierDataForHierarchy,
  getDataForLexIdsAndSourceFieldWithFilters,
  getDataFromIndexWithMustClause,
  getESHealth,
};
