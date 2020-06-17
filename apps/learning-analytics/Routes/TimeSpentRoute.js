const timeSpentController = require("../Controllers/TimeSpentController");
const { auth } = require("../Services/auth");
const { getClient } = require("../Services/getClientName");

module.exports = (app) => {
    app.get("/api/timeSpent"/* ,auth */,getClient,  timeSpentController.timeSpent);
}