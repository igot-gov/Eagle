const winston = require('winston');
const rTracer = require('cls-rtracer');

const myCustomLevels = {
  levels: {
    error: 0,
    warn: 1,
    info: 2,
    verbose: 3,
    debug: 4,
    silly: 5,
  },
  colors: {
    error: 'red',
    warn: 'orange',
    info: 'yellow',
    verbose: 'blue',
    debug: 'green',
    silly: 'pink',
  },
};
const { config } = winston;
const logger = new winston.Logger({
  levels: myCustomLevels.levels,
  transports: [
    new winston.transports.Console({
      timestamp: () => Date.now(),
      formatter: options => `${new Date(options.timestamp()).toISOString()} ${rTracer.id()} ${config.colorize(
        options.level,
        options.level.toUpperCase(),
      )} ${(options.message ? options.message : '')} ${(options.meta && Object.keys(options.meta).length
        ? JSON.stringify(options.meta)
        : '')}`,
      level: 'debug',
      // json: true,
      colorize: true,
      exitOnError: false,
    }),
  ],
});

logger.stream = {
  write: (message) => {
    // use the 'info' log level so the output will be picked up by both
    //  transports (file and console)
    logger.info(message);
  },
};

module.exports = {
  error: (obj) => {
    logger.error(obj);
  },
  warn: (obj) => {
    logger.warn(obj);
  },
  info: (obj) => {
    logger.info(obj);
  },
  verbose: (obj) => {
    logger.verbose(obj);
  },
  debug: (obj) => {
    logger.debug(obj);
  },
  silly: (obj) => {
    logger.silly(obj);
  },
  logger,
};
