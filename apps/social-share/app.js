const express = require("express");
const cors = require("cors");
const app = express();
app.use(cors());
var compress = require("compression");
app.use(compress({ threshold: 0 }));

const path = require("path");
//const { stringify } = require("querystring");
const router = express.Router();
var request = require("request");
request.gzip = true;
app.use(express.static("public"));
router.get("/", function (req, res) {
  res.sendFile(path.join(__dirname, "index.html"));
});

router.get("/content/:id", function (req, res) {
  console.log("api service started");
  let URL = `http://lex-core:7001/v1/content/hierarchy/${req.params.id}?hierarchyType=minimal`;
  let data;
 
  request(
    {
      headers: {
        "content-type": "application/json",
        rootorg: req.get("rootorg") ? req.get("rootorg") : "igot",
        org: req.get("org") ? req.get("org") : "dopt",
        wid: req.get("wid")
          ? req.get("wid")
          : "9cdd9f76-2584-4ae5-940d-3f51dce020dc",
        //authorization: req.get("authorization"),
      },
      uri: URL,
      method: "POST",
      body: JSON.stringify({
        fetchOneLevel: false,
        fields: ["name", "description", "appIcon"],
        fieldsPassed: true,
        org: "dopt",
        rootOrg: "igot",
        userId: "9cdd9f76-2584-4ae5-940d-3f51dce020dc",
      }),
    },
    function (err, response, body) {
      if (err) {
        console.log("error:", err);
        res.send(err);
      } else {
        console.log("res", body);
        data = JSON.parse(body);
        if (data) {
          var html = `<!DOCTYPE html>
          <html lang="en">
            <head>
              <title>"${data.name}"</title>
              <meta charset="utf-8" />
              <meta name="viewport" content="width=device-width, initial-scale=1" />
              <meta name="title" content="${data.name}" />
              <meta name="description" content="${data.description}" />
       
             
              <meta property="og:type" content="website">
              <meta property="og:url" content="${`https://d136953gtttd92.cloudfront.net/app/toc/${req.params.id}/overview`}" />
              <meta property="og:title" content="${data.name}" />
              <meta property="og:description" expr:content="${data.description}" />
              <meta property="og:image" content="${data.appIcon}" />          
          
              <meta property="twitter:card" content="summary_large_image" />
              <meta property="twitter:url" content="${`https://d136953gtttd92.cloudfront.net/app/toc/${req.params.id}/overview`}" />
              <meta property="twitter:title" content="${data.name}" />
              <meta property="twitter:description" content="${data.description}" />
              <meta property="twitter:image" content="${data.appIcon}" />
            </head>
            <body></body>
          </html>          
            `;
          res.send(html);
        }
      }
    }
  );
});
//add the router
app.use("/", router);
app.listen(process.env.port || 3009);
console.log("Running at Port 3009");
