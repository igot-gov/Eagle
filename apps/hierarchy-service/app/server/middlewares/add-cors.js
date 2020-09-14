const log = require('../../util/log/log');

function checkConditionalCors(app, corsValue) {
  // Enabling the CORS for all requests if Dev and on prod environments, enabling only on the domain
  app.use((req, res, next) => {
    // Reading the current whitelist of the domains, if the reqyest's origin is from this domain
    // adding the header, else ignoring.
    if (corsValue !== undefined && corsValue !== null && corsValue !== '*') {
      const whiteListDomains = corsValue.split(',').map(val => val.trim());
      const currentOrigin = req.headers.origin;

      if (currentOrigin && whiteListDomains.includes(currentOrigin.trim())) {
        res.header('Access-Control-Allow-Origin', currentOrigin);
      } else {
        log.silly('The servers whitelist is empty');
      }
    } else {
      res.header('Access-Control-Allow-Origin', corsValue);
    }
    res.header(
      'Access-Control-Allow-Headers',
      'Origin, X-Requested-With, Content-Type, Accept',
    );
    next();
  });
}

module.exports = {
  checkConditionalCors,
};
