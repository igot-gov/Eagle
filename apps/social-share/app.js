const express = require("express");
const cors = require("cors");
const app = express();
app.use(cors());
var compress = require("compression");
app.use(compress({ threshold: 0 }));
const healthcheck = require('express-healthcheck')

const routes = require("./api/index");
app.use("/", routes);

const path = require("path");
//const { stringify } = require("querystring");
const router = express.Router();

app.use(express.static("public"));
app.use('/healthcheck', healthcheck({
  healthy() {
    return { everything: 'is ok' }
  },
}))

router.get("/", function (req, res) {
  res.sendFile(path.join(__dirname, "index.html"));
});

//add the router
app.listen(process.env.port || 3009);
console.log("Running at Port 3009");

module.exports = app;
