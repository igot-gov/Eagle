const express = require("express");
const cors = require("cors");
const bodyParser = require("body-parser");
const { writeLog } = require("./Utils/utilMethods");
const fs = require("fs");
const compression = require("compression");
const path = require('path');
const dotenv = require('dotenv');
const crypto = require("crypto");

let request_id = 0;
const app = express();

app.use((req, res, next) => {
   request_id =crypto.randomBytes(16).toString("hex");
    writeLog(`${request_id},${req.get("host")},${req.get("rootorg")},request_started-i,${req.get("wid")},${req.originalUrl}`);
    req.request_id = request_id;
    next();
    writeLog(`${request_id},${req.get("host")},${req.get("rootorg")},request_ended-i,${req.get("wid")},${req.originalUrl}`);
});

app.use(cors());
app.use(bodyParser.json({ type: "*/*" }));

app.use(compression());

//route set up
require("./Routes/TimeSpentRoute")(app);
require("./Routes/AssessmentRoute")(app);
require("./Routes/UserProgressRoute")(app);
require("./Routes/NsoArtifactsCollaboratorsRoute")(app);
require("./Routes/SkillQuotientRoute")(app);
require("./Routes/SkillRoute")(app);
require("./Routes/RoleQuotientRoute")(app);
require("./Routes/RoleRoute")(app);
require("./Routes/SearchRoute")(app);
require("./Routes/ProjectEndorsementRoute")(app);
require("./Routes/NsoRoleRoute")(app);
require("./Routes/analyticsRoute")(app);
require("./Routes/downloadReportRoute")(app);
require("./Routes/PathwayDashboard")(app);



// Serve the static files from the React app
app.use(express.static(path.join(__dirname, '/build')));

// Handles any requests that don't match the ones above
app.get('*', (req, res) => {
    res.sendFile(path.join(__dirname + '/build/index.html'));
});


dotenv.config();

//app set up
app.listen(process.env.PORT || 6004);
// app.listen(3000)