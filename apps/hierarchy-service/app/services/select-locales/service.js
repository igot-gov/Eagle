const appConfig = require('../../util/app/config/loader');

function getLocalesList(rootOrgArr) {
  let localeList = new Set();
  try {
    const localeListEnv = appConfig.getProperty('root_org_to_locale_list');

    if (localeListEnv) {
      const rootOrgList = localeListEnv.split(';');
      rootOrgList.forEach((rootOrgLocale) => {
        const rootOrg = rootOrgLocale.split(':')[0];
        const locales = rootOrgLocale.split(':')[1].split(',');

        // Adding the locales to the existing list if the rootOrg matches
        if (rootOrgArr.includes(rootOrg)) {
          localeList = new Set([...localeList, ...locales]);
        }
      });
    }
    if (localeList.has('*')) {
      localeList = new Set(['*']);
    }
    if (localeList.size === 0) {
      localeList = new Set(['en']);
    }
  } catch (ex) {
    console.error('Error while selecting access paths. Sending the default'); // eslint-disable-line
    if (localeList.size === 0) {
      localeList = new Set(['en']);
    }
  }
  return Array.from(localeList).map(locale => `mlsearch_${locale}`);
}

module.exports = {
  getLocalesList,
};
