const laTimespentController = require("../Controllers/LaTimespentController");
const contentAnalytics =require("../Controllers/ContentAnalytics")
const { getClient } = require("../Services/getClientName");
const channelAnalytics=require("../Controllers/channelController")
const MylearningController = require("../Controllers/MyLearning");
const hourlyUsage = require("../Controllers/hourlyUsageController");
const { auth } = require("../Services/auth");



module.exports = (app) => {
    app.get("/api/la/timespent",auth, getClient, laTimespentController.timeSpent);

    app.get("/api/la/content",auth, getClient, laTimespentController.timeSpent);

    app.get("/api/la/contentAnalytics",auth, getClient, contentAnalytics.contentAnalytics);
    
    app.post("/api/la/contentViews",auth, getClient, contentAnalytics.contentViews);

    app.get("/api/la/channelAnalytics",auth, getClient, channelAnalytics.channelAnalytics);

    // app.get("/api/la/myLearning", getClient, MylearningController.roleProgress);

    app.get("/api/la/myLearning",auth, getClient, MylearningController.myLearningRole);

    app.get("/api/la/hourlyUsage",auth, getClient, hourlyUsage.hourlyUsage);


    // app.get("/api/la/getConfig", getClientAndConfig, (req, res) => {
    //     res.send(req.client_config);
    // });
}