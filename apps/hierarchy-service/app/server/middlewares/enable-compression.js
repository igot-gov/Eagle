const compress = require('compression');

function enable(app) {
  // Enabling the compression of data being sent
  app.use(compress());
}

module.exports = {
  enable,
};
