const config = require('../../util/app/config/loader');
const logger = require('./app-logger');
const security = require('./helmet-security');
const compression = require('./enable-compression');
const cors = require('./add-cors');
const cache = require('./add-cache-disabler');
const bodyParser = require('./body-parser');
const favicon = require('./add-favicon');
const rTracer = require('./add-rtracer');

function enable(app) {
  const enableFullCors = config.getProperty('enable_full_cors');
  const corsValue = enableFullCors === '0' ? config.getProperty('cors_domain_url') : '*';

  // Adding request id to the app.
  rTracer.addRequestIdToRequest(app);
  // Adding the morgan loggin
  logger.addLogger(app);
  // Helmet security
  security.addSecurity(app);
  // Enable compression
  compression.enable(app);
  // Eanabling the conditional Cors
  cors.checkConditionalCors(app, corsValue);
  // Disabling cache for API calls
  cache.disbaleCache(app);
  // Adding the body parser for accpeting post body
  bodyParser.addBodyParser(app);
  // Adding the favicon
  favicon.addFavicon(app);
}

module.exports = {
  enable,
};
