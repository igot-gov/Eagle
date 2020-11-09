const express = require("express");
const router = express.Router();
var request = require("request");
request.gzip = true;
var config = require('../utils/environment');

router.get("/badge/:userId/:id", function (req, res) {
  let URL = `${config.API_HOST}/v3/users/${req.params.userId}/badges`;
  console.log("api service started");
  let badges;
  request(
    {
      headers: {
        local: "en",
        rootOrg: "igot",
      },
      uri: URL,
      method: "GET",
    },
    function (err, response, body) {
      if (err) {
        console.log("error:", err);
        res.send(err);
      } else {
        if (typeof body == "string") {
          badges = JSON.parse(body);
        } else {
          badges = body;
        }
        let badgeArray = [];
        let badge,imagePath;
        if (badges.earned && badges.earned.length > 0) {
          badgeArray = badges.earned.filter(function (badge) {
            return badge.badge_id == req.params.id;
          });
          badge = badgeArray.length > 0 ? badgeArray[0] : null;
          let badgeImage= badge?badge.image:null;
          imagePath=`${config.HTTPS_HOST}/assets/instances/eagle/Achievements/Badges/assets/`+ (badgeImage.split("/").pop()).split('?').slice(0, -1).join('.')
        }
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
                <meta property="og:url" content="${`${config.HTTPS_HOST}/share/badge/${req.params.userId}/${req.params.id}`}" />
                <meta property="og:title" content="${badge.badge_name}" />
                <meta property="og:description" content="${badge.message}" />
                <meta property="og:image" content="${imagePath}" />
                <meta property="og:image:secure_url" content="${
                  imagePath
                }" />
            
                <meta property="twitter:card" content="summary_large_image" />
                <meta property="twitter:url" content="${`${config.HTTPS_HOST}/share/badge/${req.params.userId}/${req.params.id}`}" />
                <meta property="twitter:title" content="${badge.badge_name}" />
                <meta property="twitter:description" content="${
                  badge.message
                }" />
                <meta property="twitter:image" content="${imagePath}" />
                <style>
                  .social-card {
                    box-shadow: 0 0 0 1px rgba(0,0,0,.15), 0 2px 3px rgba(0,0,0,.2);
                    max-width: 300px;
                    margin: 16px;
                    text-align: center;
                    font-family: arial;
                  }
                  
                  .sub-card {
                      padding: 10px;
                    
                  }
                  
                  .title {
                      font-size: 1.2rem;    
                      font-weight: 600;
                      color: rgba(0,0,0,.9);
                  }
                  
                  .desc{
                    font-size: 0.6rem;  
                      font-weight: 600;
                      color: rgba(0,0,0,.9);    
                  }
                  
                  .social-card-img {	
                    width:100%;   
                  }
                  
                  a {
                    text-decoration: none;
                    font-size: 22px;
                    color: black;
                  }                          
                </style>
              </head>
              <body>
                <div class="social-card">
                  <a href="${config.HTTPS_HOST}/app/profile/competency/badges">
                    <img src="${imagePath}" alt="${badge.badge_name}"  class="social-card-img" />
                    <div class="sub-card">
                      <p class="title">${badge.badge_name}</p>
                      <p class="desc">${badge.message}</p>
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
