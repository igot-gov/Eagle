process.env.NODE_TLS_REJECT_UNAUTHORIZED = '0';
// Express for router
const express = require('express');
// Config loader
const config = require('../util/app/config/loader');

const port = config.getProperty('port');
if (port === null || port === '') {
  throw new Error('Application port is not configured');
}

// Initializing the server
const app = express();


// Adding the middleware
require('./middlewares/add-middlewares').enable(app);
// Adding the routes
require('./routes/add-routes').addRoutes(app);

const appName = 'hierarchy-service';
const server = app.listen(port, () => {
  console.log(`${appName} started on port ${port}\nNow Listening to connections...`); // eslint-disable-line
});

// Exporting the server for unit tests and worker creation
module.exports = server;

// so the program will not close instantly
process.stdin.resume();

function exitHandler(options, err) {
  if (options.cleanup) console.log('clean'); // eslint-disable-line
  if (err) console.log(err); // eslint-disable-line
  if (options.exit) process.exit();
}

// Do something when app is closing
process.on(
  'exit',
  exitHandler.bind(null, {
    cleanup: true,
  }),
);

// catches ctrl+c event
process.on(
  'SIGINT',
  exitHandler.bind(null, {
    exit: true,
  }),
);

// catches 'kill pid' (for example: nodemon restart)
process.on(
  'SIGUSR1',
  exitHandler.bind(null, {
    exit: true,
  }),
);
process.on(
  'SIGUSR2',
  exitHandler.bind(null, {
    exit: true,
  }),
);

// catches uncaught exceptions
process.on(
  'uncaughtException',
  exitHandler.bind(null, {
    exit: false,
  }),
);

process.on(
  'unhandledRejection',
  exitHandler.bind(null, {
    exit: false,
  }),
);
