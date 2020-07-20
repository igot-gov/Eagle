// Body parser for reading the JSON body of a request
const bodyParser = require('body-parser');

function addBodyParser(app) {
  app.use(
    bodyParser.urlencoded({
      extended: true,
      limit: 500 * 1024, // 500 kb
    }),
  );
  app.use(
    bodyParser.json({
      limit: '250mb',
    }),
  );
}

module.exports = {
  addBodyParser,
};
