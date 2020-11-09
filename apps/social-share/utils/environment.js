const env = process.env;
const CONSTANTS = {
  PORT: env.PORT || "3009",
  HTTPS_HOST: env.HTTPS_HOST || "https://d136953gtttd92.cloudfront.net",
  API_HOST: env.API_HOST || "https://lex-core:7001",
};

module.exports = CONSTANTS;
