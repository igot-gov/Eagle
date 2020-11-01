const express = require("express");
const router = express.Router();
var request = require("request");
var _ = require("underscore");

router.get("/badge/:userId/:id", function (req, res) { 
  let URL = `http://lex-core:7001/v3/users/${req.params.userId}/badges`;
  console.log("api service started",req.params.id);
  console.log("url",URL);
  console.log("userId",req.params.userId);
  let badges,badge;
  request(
    {
      headers: {
        local: "en",
        rootOrg :"igot"
      },
      uri: URL,
      method: "GET"     
    },
    function (err, response, body) {
      if (err) {
        console.log("error:", err);
        res.send(err);
      } else {
        // if (typeof body === "string"){
        //   JSON.parse(body)
        //   badges = body;  
        // }else{
        //   badges = body; 
        // }          
        // badge = _.where(badges.earned, {badge_id: req.params.id});
        // badge= badge[0];
       
          var html = `<!DOCTYPE html>
          <html lang="en">
            <head>
              <meta charset="utf-8" />
              <meta property="og:title" content="European Travel Destinations" />
              <meta
                property="og:description"
                content="Offering tour packages for individuals or groups."
              />
              <meta
                property="og:image"
                content="http://euro-travel-example.com/thumbnail.jpg"
              />
              <meta
                property="og:url"
                content="http://euro-travel-example.com/index.htm"
              />
              <meta name="twitter:title" content="European Travel Destinations ">
              <meta name="twitter:description" content=" Offering tour packages for individuals or groups.">
              <meta name="twitter:image" content=" http://euro-travel-example.com/thumbnail.jpg">
              <meta name="twitter:card" content="summary_large_image">
            </head>
            <body>
              <h1>Social Share</h1>
            </body>
          </html>          
              `;
          res.send(html);
        }
      }
    
  );
});

module.exports = router;
