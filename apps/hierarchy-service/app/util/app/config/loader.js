const appRoot = process.cwd();

// Path used to read the properties file
const path = require('path');
const fs = require('fs');

// Properties which can be configured for application
const PropertiesReader = require('properties-reader');

// Logger
const logger = require('../../log/log');

// Properties reader
let appProperties = null;
const propsFilePath = path.join(`${appRoot}/app.properties`);
if (fs.existsSync(propsFilePath)) {
  appProperties = PropertiesReader(propsFilePath);
}

const { env } = process;

function validateGetPropertyInput(name) {
  if ((!name || typeof name !== 'string') && name.toString().length < 1) {
    return false;
  }
  return true;
}

// This will get the property from the environment variables.
// If the env variables are null or empty,
// then the data in the property file is read and the respective data of
// those variables are picked up.
function getProperty(name) {
  // Validating the input
  if (!validateGetPropertyInput(name)) {
    return null;
  }
  // Checking if the variable is coming from the env or props
  if (env[name.toString()]) {
    logger.silly(`${name} is being picked up from the environment -- `);
    logger.silly((name.includes('password') || name.includes('secret')) ? 'XXXXpasswordXXXX' : env[name.toString()]);
    return env[name.toString()];
  }
  // console.log('Current env is ', env);
  logger.silly(`${name} is ${env[name.toString()]} in environment, reading the data from property`);
  return appProperties ? appProperties.get(name) : null;
}


module.exports = {
  getProperty,
};
