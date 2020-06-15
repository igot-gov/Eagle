const userProgressController = require("../Controllers/UserProgressController");
const { auth } = require("../Services/auth");
const { getClient } = require("../Services/getClientName");

module.exports = (app) => {
    app.get("/api/userProgress", auth,getClient, userProgressController.userProgress);

    app.get("/api/progressSource", auth,getClient, userProgressController.getProgressSource);

}