const winston = require('winston');

var myCustomLevels = {
	levels: {
		error: 0,
		warn: 1,
		info: 2,
		verbose: 3,
		debug: 4,
		silly: 5
	},
	colors: {
		error: 'red',
		warn: 'orange',
		info: 'yellow',
		verbose: 'blue',
		debug: 'green',
		silly: 'pink'
	}
};
var config = winston.config;
var logger = new(winston.Logger)({
	levels: myCustomLevels.levels,
	transports: [
		new(winston.transports.Console)({
			timestamp: function () {
				return Date.now();
			},
			formatter: function (options) {
				// - Return string will be passed to logger.
				// - Optionally, use options.colorize(options.level, <string>) to
				//   colorize output based on the log level.
				return new Date(options.timestamp()) + ' ' +
					config.colorize(options.level, options.level.toUpperCase()) + ' ' +
					(options.message ? options.message : '') +
					(options.meta && Object.keys(options.meta).length ? '\n\t' + JSON.stringify(options.meta) : '');
			}
		})
	]
});

module.exports = {
	error: function (obj) {
		logger.error(obj);
	},
	warn: function (obj) {
		logger.warn(obj);
	},
	info: function (obj) {
		logger.info(obj);
	},
	verbose: function (obj) {
		logger.verbose(obj);
	},
	debug: function (obj) {
		logger.debug(obj);
	},
	silly: function (obj) {
		logger.silly(obj);
	}
};
