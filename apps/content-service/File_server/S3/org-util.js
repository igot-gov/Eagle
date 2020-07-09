/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
/*               "Copyright 2020 Infosys Ltd.
               Use of this source code is governed by GPL v3 license that can be found in the LICENSE file or at https://opensource.org/licenses/GPL-3.0
               This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License version 3" */
const config = require('../ConfigReader/loader');
const {
  contentRoot
} = require('./constants');

// Live buckets
const liveContentBucketsEnv = config.getProperty('live_content_buckets_list');
const liveImageBucketsEnv = config.getProperty('live_image_buckets_list');

// Prepublish buckets
const prePublishContentBucketsEnv = config.getProperty('pre_publish_content_buckets_list');
const prePublishImageBucketsEnv = config.getProperty('pre_publish_image_buckets_list');

// Downloads buckets
const downloadsBucketEnv = config.getProperty('downloads_buckets_list');

// Content CDN
const contentCDNListEnv = config.getProperty('content_cdn_list');

// Images CDN
const imagesCDNListEnv = config.getProperty('images_cdn_list');

const BUCKET_TYPES = {
  content: 'CONTENT',
  images: 'IMAGES',
  downloads: 'DOWNLOADS',
};

const HOSTING_TYPES = {
  prePublish: 'PRE_PUBLISH',
  main: 'MAIN',
  downloads: 'DOWNLOADS',
};

const CDN_TYPES = {
  content: 'CONTENT',
  images: 'IMAGES',
};

// Getting the bucket name depending on the root_org
function getBucketFromRootOrg(bucketType, hostingType, rootOrg) {
  console.log("bucketType, hostingType, rootOrg====>", bucketType, hostingType, rootOrg)
  let currentHostingType = null;
  let bucketEnvVal = null;

  // Checking if the request is for pre-publish or main
  if (hostingType === HOSTING_TYPES.prePublish) {
    currentHostingType = HOSTING_TYPES.prePublish;
  } else if (hostingType === HOSTING_TYPES.main) {
    currentHostingType = HOSTING_TYPES.main;
  } else if (hostingType === HOSTING_TYPES.downloads) {
    currentHostingType = HOSTING_TYPES.downloads;
  } else {
    throw new Error('Invalid hosting type');
  }

  // Now setting the respective live or pre-publish bucket
  switch (bucketType) {
    case BUCKET_TYPES.content:
      if (currentHostingType === HOSTING_TYPES.main) {
        bucketEnvVal = liveContentBucketsEnv;
      } else {
        bucketEnvVal = prePublishContentBucketsEnv;
      }
      break;
    case BUCKET_TYPES.images:
      if (currentHostingType === HOSTING_TYPES.main) {
        bucketEnvVal = liveImageBucketsEnv;
      } else {
        bucketEnvVal = prePublishImageBucketsEnv;
      }
      break;
    case BUCKET_TYPES.downloads:
      bucketEnvVal = downloadsBucketEnv;
      break;
    default:
      throw new Error('Invalid bucket type received');
  }

  // Now splitting the value and getting the respective bucket name
  const allRootOrgsAndBucketsArr = bucketEnvVal.split(';');

  for (let i = 0; i < allRootOrgsAndBucketsArr.length; i++) {
    const rootOrgAndBucket = allRootOrgsAndBucketsArr[i].split(':');
    const _i_rootOrg = rootOrgAndBucket[0];
    const _i_bucket = rootOrgAndBucket[1];

    if (_i_rootOrg.toLowerCase() === rootOrg.toLowerCase()) {
      return _i_bucket.trim();
    }
  }
  throw new Error(`No bucket found for this rootOrg ${rootOrg}`);
}

function getCDNFromRootOrg(cdnType, rootOrg) {
  let cdnTypeSelected = null;
  switch (cdnType) {
    case CDN_TYPES.images:
      cdnTypeSelected = imagesCDNListEnv;
      break;
    case CDN_TYPES.content:
      cdnTypeSelected = contentCDNListEnv;
      break;
    default:
      throw new Error('Invalid CDN type.');
  }

  const cdnArray = cdnTypeSelected.split(';');

  for (let i = 0; i < cdnArray.length; i++) {
    const _i_rootOrg = cdnArray[i].split(':')[0];
    const _i_cdnName = cdnArray[i].split(':')[1];

    if (rootOrg.toLowerCase() === _i_rootOrg.toLowerCase()) {
      return `https://${_i_cdnName.trim()}`;
    }
  }
  throw new Error('No CDN found for this rootOrg');
}

function getRootOrgFromKey(key) {
  console.log('Key is: ', key, ' content root is: ', contentRoot);
  return key.split('content-store/')[1].split('/')[0];
}

function getBucketsFromKey(key) {
  console.log("key===========>", key);
  const rootOrgName = getRootOrgFromKey(key);
  console("key===========>", BUCKET_TYPES.content, HOSTING_TYPES.prePublish, rootOrgName)
  return {
    authoringBucket: getBucketFromRootOrg(BUCKET_TYPES.content, HOSTING_TYPES.prePublish, rootOrgName),
    mainBucket: getBucketFromRootOrg(BUCKET_TYPES.content, HOSTING_TYPES.main, rootOrgName),
    imageAuthoringBucket: getBucketFromRootOrg(BUCKET_TYPES.images, HOSTING_TYPES.prePublish, rootOrgName),
    imageBucket: getBucketFromRootOrg(BUCKET_TYPES.images, HOSTING_TYPES.main, rootOrgName),
    downloadBucket: getBucketFromRootOrg(BUCKET_TYPES.downloads, HOSTING_TYPES.downloads, rootOrgName),
  };
}

function getCDNsFromKey(key) {
  const rootOrgName = getRootOrgFromKey(key);
  return {
    IMAGES_CDN: getCDNFromRootOrg(CDN_TYPES.images, rootOrgName),
    CONTENT_CDN: getCDNFromRootOrg(CDN_TYPES.content, rootOrgName),
  };
}

module.exports = {
  BUCKET_TYPES,
  HOSTING_TYPES,
  CDN_TYPES,
  getBucketsFromKey,
  getCDNsFromKey,
  getCDNFromRootOrg,
}
