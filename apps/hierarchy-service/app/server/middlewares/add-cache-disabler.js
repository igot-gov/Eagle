function disbaleCache(app) {
  // Disabling the cache for all the API
  app.use((req, res, next) => {
    res.header(
      'Cache-Control',
      'no-store, no-cache, must-revalidate, proxy-revalidate',
    );
    res.header('Pragma', 'no-cache');
    res.header('Expires', '0');
    res.header('Surrogate-Control', 'no-store');
    next();
  });
}
module.exports = {
  disbaleCache,
};
