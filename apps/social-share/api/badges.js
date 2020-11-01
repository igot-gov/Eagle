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
        if (typeof body === "string"){
          JSON.parse(body)
          badges = body;  
        }else{
          badges = body; 
        }    
        let badgeArray = [];
        if(badges.earned && badges.earned.length>0){
          badgeArray= badges.earned.filter(function(badge){
            return badge.badge_id == req.params.id;
          });
          badge= badgeArray.length >0 ? badgeArray[0]:null;
        }
        //badge = _.where(badges.earned, {badge_id: req.params.id});
        
        if (badge) {
          var html = `<!DOCTYPE html>
            <html lang="en">
              <head>             
                <meta charset="utf-8" />
                <title>"${badge.badge_name}"</title>
                <link id="id-app-fav-icon" rel="icon" type="image/png" href="/image/favicon.png")
                " />
                <meta id="id-social-description" name="description"  content="${
                  badge.message
                }">             
                <meta property="og:type" content="website">
                <meta property="og:url" content="${`https://d136953gtttd92.cloudfront.net/share/badge/${req.params.userId}/${req.params.id}`}" />
                <meta property="og:title" content="${badge.badge_name}" />
                <meta property="og:description" content="${badge.message}" />
                <meta property="og:image" content="${badge.image}" />  
                <meta property="og:image:secure_url" content="${badge.image}" />        
            
                <meta property="twitter:card" content="summary_large_image" />
                <meta property="twitter:url" content="${`https://d136953gtttd92.cloudfront.net/share/badge/${req.params.userId}/${req.params.id}`}" />
                <meta property="twitter:title" content="${badge.badge_name}" />
                <meta property="twitter:description" content="${badge.message}" />
                <meta property="twitter:image" content="${badge.image}" />
                <style>
                a {
                  text-decoration: none;
                  font-size: 22px;
                  color: black;
                  display: flex;
                }
                .card-container {
                  box-shadow: 0 2px 1px -1px rgba(0, 0, 0, 0.2),
                    0 1px 1px 0 rgba(0, 0, 0, 0.14), 0 1px 3px 0 rgba(0, 0, 0, 0.12);
                  width: 340px;
                  height: 120px;
                  margin: 20px 10px;
                  display: flex;
                  padding: 16px;
                  border-radius: 4px;
                  font-family: Lato;
                }
                .badge-data {
                  margin-top: 5px;
                  margin-left: 16px;
                }
                .title {
                  font: 600 16px/24px Montserrat;
                  margin: 0;
                }
                .message {
                  font: 400 14px/24px Montserrat;
                }
                .badge-img {
                  width: 94px;
                }
          
                .image-container {
                  box-sizing: border-box;
                  width: 114px;
                }             
                </style>
              </head>
              <body>
              <div class="card-container">
                  <a href="https://d136953gtttd92.cloudfront.net/app/profile/competency/badges">
                    <div class="image-container">
                      <img
                        class="badge-img"
                        src="${badge.image}"
                      />
                    </div>
                    <div class="badge-data">
                      <p class="title">${badge.badge_name}</p>
                      <p class="message">${badge.message}</p>
                    </div>
                  </a> 
                </div>                  
              </body>
            </html>          
              `;
          res.send(html);
        }
      }
    }
  );
});

module.exports = router;
