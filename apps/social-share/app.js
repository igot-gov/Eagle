const express = require("express");
const cors = require("cors");
const app = express();
app.use(cors());
let compress = require("compression");
app.use(compress({ threshold: 0 }));
let  env = require('./utils/environment')


const routes = require("./api/index");
app.use("/", routes);

const path = require("path");
//const { stringify } = require("querystring");
const router = express.Router();

app.use(express.static("public"));


router.get("/", function (req, res) {
  res.sendFile(path.join(__dirname, "index.html"));
});

//add the router
app.listen(env.PORT || 3009);
console.log("Running at Port : ",env.PORT);

module.exports = app;
