// Morgan for request logging
const morgan = require('morgan');
// App logger
const log = require('../../util/log/log');

function addLogger(app) {
  // Adding the morgan logging
  // app.use(morgan('combined', { stream: log.logger.stream }));
  app.use(morgan('tiny', { stream: log.logger.stream }));
}

module.exports = {
  addLogger,
};
