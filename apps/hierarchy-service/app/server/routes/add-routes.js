const hierarchyRouter = require('./hierarchy/router');
const lexIdsWithFilterFetch = require('./fetches/router');
const healthRouter = require('./health/router');
const cacheRouter = require('./cache/router');

function addRoutes(app) {
  app.use('/health', healthRouter);

  // Hierarchy router
  app.use(hierarchyRouter);

  // LexIds with filter fetch
  app.use(lexIdsWithFilterFetch);

  // Delete cache router
  app.use('/cache', cacheRouter);
}

module.exports = {
  addRoutes,
};
