var appRoot = process.cwd();

// Path used to read the properties file
const path = require('path');

// Properties which can be configured for application
var PropertiesReader = require('properties-reader');

// Logger
var logger = require('../Logger/log');

const fs = require('fs');

// Properties reader
var appProperties = null;
if (fs.existsSync(path.join(appRoot + '/app.properties'))) {
	appProperties = PropertiesReader(path.join(appRoot + '/app.properties'));
}

var env = process.env;

// This will get the property from the environment variables. If the env variables are null or empty, then the data in the property file is read and the respective data of those variables are picked up.
function getProperty(name) {
	// Validating the input
	if (!validateGetPropertyInput(name)) {
		return null;
	}
	// Checking if the variable is coming from the env or props
	if (env[name.toString()]) {
		logger.info(env[name.toString()] + ' is being picked up from the environment');
		return env[name.toString()];
	} else {
		// console.log('Current env is ', env);
		logger.info(name + ' is ' + env[name.toString()] + ' in environment, reading the data from property');
		return appProperties ? appProperties.get(name) : null;
	}
}

function validateGetPropertyInput(name) {
	if (!name || typeof name != 'string' && name.toString().length < 1) {
		return false;
	} else {
		return true;
	}
}
module.exports = {
	getProperty: getProperty
};
