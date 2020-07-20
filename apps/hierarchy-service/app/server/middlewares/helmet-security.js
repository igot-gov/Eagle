// Helmet for security
const helmet = require('helmet');
// For content security policy
const csp = require('helmet-csp');

function addSecurity(app) {
  // Adding the default security checks
  app.use(helmet());

  // Reference https://github.com/helmetjs/csp
  // Adding the content security policy
  app.use(
    csp({
      // Specify directives as normal.
      directives: {
        defaultSrc: ["'self'"], //eslint-disable-line
        scriptSrc: [
          "'self'",
          "'unsafe-inline'",
        ], //eslint-disable-line
        imgSrc: ["'self'", 'data:', 'https:', 'http:'], //eslint-disable-line
        styleSrc: [
          "'self'",
          "'unsafe-inline'",
        ], //eslint-disable-line
        fontSrc: ["'self'"], //eslint-disable-line
        frameSrc: ["'self'"], //eslint-disable-line
        connectSrc: ["'self'"], //eslint-disable-line
        objectSrc: ["'none'"], //eslint-disable-line
        // upgradeInsecureRequests: true,
        workerSrc: ['https:', 'blob:', "'self'"], //eslint-disable-line
        mediaSrc: ['*', 'blob:'],
      },

      // This module will detect common mistakes in your directives and throw errors
      // if it finds any. To disable this, enable 'loose mode'.
      loose: false,

      // Set to true if you only want browsers to report errors, not block them.
      // You may also set this to a function(req, res) in order to decide dynamically
      // whether to use reportOnly mode, e.g., to allow for a dynamic kill switch.
      reportOnly: false,

      // Set to true if you want to blindly set all headers: Content-Security-Policy,
      // X-WebKit-CSP, and X-Content-Security-Policy.
      setAllHeaders: true,

      // Set to true if you want to disable CSP on Android where it can be buggy.
      disableAndroid: true,

      // Set to false if you want to completely disable any user-agent sniffing.
      // This may make the headers less compatible but it will be much faster.
      // This defaults to `true`.
      browserSniff: false,
    }),
  );
}

module.exports = {
  addSecurity,
};
