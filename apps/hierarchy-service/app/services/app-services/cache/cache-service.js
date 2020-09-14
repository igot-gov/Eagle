const fs = require('fs-extra');
const path = require('path');
const flatCache = require('flat-cache');

const config = require('../../../util/app/config/loader');

const log = require('../../../util/log/log');

const resourceDirectory = config.getProperty('resource_directory') || '/';
const cacheLifetimeInMinutes = parseInt(config.getProperty('cache_lifetime_minutes') || '60', 10);

const cacheLocation = path.resolve(`${resourceDirectory}${path.sep}.hierarchy-service-cache`);

const CACHE_TIME_MAP_DIR_NAME = 'time';
const CACHE_DATA_DIR_NAME = 'data';


const hierarchyCacheKey = 'hierarchy';
const cache = flatCache.load(hierarchyCacheKey, cacheLocation);

const count = 0;

const KEY_SEPERATOR = '__';

function getKeyForHierarchy(rootOrg, org, contentId) {
  return `${rootOrg.replace(/ /g, '_')}${KEY_SEPERATOR}${org.replace(/ /g, '_')}${KEY_SEPERATOR}${contentId.replace(/ /g, '_')}`;
}

function deleteHierarchyCacheFiles(rootOrg, org, contentId) {
  return new Promise((resolve, reject) => {
    const key = getKeyForHierarchy(rootOrg, org, contentId);

    // Deleting the data file and time file asynchronously.
    const dataFileLocation = `${cacheLocation}${path.sep}${CACHE_DATA_DIR_NAME}${path.sep}${key}`;
    const insertedTimeLocation = `${cacheLocation}${path.sep}${CACHE_TIME_MAP_DIR_NAME}${path.sep}${key}`;

    Promise.all([fs.remove(dataFileLocation), fs.remove(insertedTimeLocation)])
      .then(() => resolve())
      .catch((err) => {
        reject(err);
      });
  });
}

function isPresentInCache(rootOrg, org, contentId) {
  const key = getKeyForHierarchy(rootOrg, org, contentId);

  // Checking if the file exists on the cache location.
  const dataFileLocation = `${cacheLocation}${path.sep}${CACHE_DATA_DIR_NAME}${path.sep}${key}`;
  const insertedTimeLocation = `${cacheLocation}${path.sep}${CACHE_TIME_MAP_DIR_NAME}${path.sep}${key}`;

  if (fs.existsSync(dataFileLocation) && fs.existsSync(insertedTimeLocation)) {
    return true;
  }
  return false;
}

function hasExpired(rootOrg, org, contentId) {
  try {
    const key = getKeyForHierarchy(rootOrg, org, contentId);
    const fileLocation = `${cacheLocation}${path.sep}${CACHE_TIME_MAP_DIR_NAME}${path.sep}${key}`;
    // Getting the data for the Lex id.
    if (isPresentInCache(rootOrg, org, contentId)) {
      const dateString = fs.readFileSync(fileLocation, 'utf-8').toString();
      const insertedDate = new Date(parseInt(dateString, 10));
      const timeDiffInMinutes = Math.abs(((new Date() - insertedDate) / 1000) / 60);
      log.info(`Time difference in minutes: ${timeDiffInMinutes}, Configured expiry: ${cacheLifetimeInMinutes}`);
      if (timeDiffInMinutes < cacheLifetimeInMinutes) {
        return false;
      }
    }
  } catch (e) {
    log.error(`Error while fetching cache expired: ${e.message}`);
  }
  return true;
}

function saveHierarchyData(rootOrg, org, contentId, data) {
  if (!rootOrg || !org || !contentId || !data) {
    throw new Error('Invalid inputs received for cache save');
  }
  const key = getKeyForHierarchy(rootOrg, org, contentId);
  // Saving the data to cache
  const dataFileLocation = `${cacheLocation}${path.sep}${CACHE_DATA_DIR_NAME}${path.sep}${key}`;
  const insertedTimeLocation = `${cacheLocation}${path.sep}${CACHE_TIME_MAP_DIR_NAME}${path.sep}${key}`;

  // Ensuring the files exist before writing to them.
  fs.ensureFileSync(dataFileLocation);
  fs.ensureFileSync(insertedTimeLocation);

  // Writing data into cache.
  fs.writeFile(dataFileLocation, JSON.stringify(data), (dataFileErr) => {
    if (dataFileErr) throw dataFileErr;
    log.info(`Saved data file: ${key}`);
    fs.writeFile(insertedTimeLocation, new Date().getTime().toString(), (err) => {
      if (err) throw err;
      log.info(`Saved time file: ${key}`);
    });
  });
}

function getHierarchyData(rootOrg, org, contentId) { // eslint-disable-line
  const key = getKeyForHierarchy(rootOrg, org, contentId);
  const dataFileLocation = `${cacheLocation}${path.sep}${CACHE_DATA_DIR_NAME}${path.sep}${key}`;

  if (isPresentInCache(rootOrg, org, contentId)) {
    const isExpired = hasExpired(rootOrg, org, contentId);
    try {
      if (!isExpired) {
        return fs.createReadStream(dataFileLocation);
      }
      log.info(`Cache has expired ${contentId}. Now will delete it.`);
      deleteHierarchyCacheFiles(rootOrg, org, contentId);
    } catch (err) {
      if (err) throw err;
    }
  } else {
    log.info('Cache data is not present.. Returning null');
  }
  return null;
}

function set(key, value, forceSave) {
  // Saving the data to cache
  cache.setKey(key, value);
  // Saving to disk, every 10 writes
  if (forceSave || count % 10 === 0) {
    cache.save(true);
  }
}

function get(key) {
  return cache.getKey(key);
}

function remove(key) {
  cache.removeKey(key);
  cache.save(true);
}

function getAll() {
  return cache.all();
}

function removeAll() {
  // flatCache.removeAll();
  cache.removeCacheFile(true);
  flatCache.clearCacheById(hierarchyCacheKey);
}

module.exports = {
  set,
  get,
  remove,
  getAll,
  removeAll,
  hierarchyData: {
    save: saveHierarchyData,
    get: getHierarchyData,
    delete: deleteHierarchyCacheFiles,
  },
};
