const rTracer = require('cls-rtracer');

function addRequestIdToRequest(app) {
  // optionally, you can override default middleware config:
  app.use(rTracer.expressMiddleware({
    useHeader: true,
    headerName: 'X-Tracer-Request-Id',
  }));

  app.use((req, res, next) => {
    res.set('X-Tracer-Request-Id', rTracer.id());
    next();
  });
}

module.exports = {
  addRequestIdToRequest,
};
