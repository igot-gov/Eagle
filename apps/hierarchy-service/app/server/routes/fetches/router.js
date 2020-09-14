const router = require('express').Router();
const esUtil = require('../../../util/db/es-util');
const localeService = require('../../../services/select-locales/service');

router.post(['/fetch', '/fetch/:rootOrgs'], (req, res, next) => {
  try {
    // Getting the list of index names for root org.
    const rootOrgsSent = req.params.rootOrgs ? req.params.rootOrgs.split(',') : [];
    const searchLocales = localeService.getLocalesList(rootOrgsSent);

    res.set('X-locales-selected', searchLocales.join(','));

    const {
      sourceFields,
      filters,
    } = req.body;

    if (!sourceFields || !Array.isArray(sourceFields) || !sourceFields.length === 0) {
      return res.status(400).send({
        code: 400,
        error: 'INVALID_SOURCE_FIELDS',
      });
    }
    if (!filters.identifier
      || !Array.isArray(filters.identifier)
      || !filters.identifier.length === 0) {
      return res.status(400).send({
        code: 400,
        error: 'INVALID_CONTENT_IDS',
      });
    }

    // Preparing the structure for filter.
    const filterArray = [];
    Object.keys(filters).forEach((filter) => {
      // If filter value requested is not an array or is an empty array, not attaching it.
      if (Array.isArray(filters[filter]) && filters[filter].length > 0) {
        const pushObject = { terms: {} };
        pushObject.terms[filter] = filters[filter];
        filterArray.push(pushObject);
      }
    });

    return esUtil
      .getDataForLexIdsAndSourceFieldWithFilters(searchLocales, sourceFields, filterArray)
      .then(result => res.send(result)).catch((err) => {
        console.error(err); // eslint-disable-line
        return res.status(500).send({
          code: 500,
          msg: 'INTERNAL_SERVER_ERROR',
        });
      });
  } catch (ex) {
    console.error(ex); // eslint-disable-line
    return next(ex);
  }
});

module.exports = router;
