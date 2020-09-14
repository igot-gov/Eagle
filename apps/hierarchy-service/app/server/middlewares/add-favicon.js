// For favicons
const favicon = require('express-favicon');

function addFavicon(app) {
  // Adding the favicon
  app.use(favicon(`${__dirname}../../../favicon.ico`));
}

module.exports = {
  addFavicon,
};
