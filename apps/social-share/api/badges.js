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
          imagePath='https://d136953gtttd92.cloudfront.net/assets/instances/eagle/Achievements/Badges/assets/'+(badgeImage.split("/").pop()).split('?').slice(0, -1).join('.')
        }        
        if (badge) {
          let html = `<!DOCTYPE html>
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
              <meta property="twitter:description" content="${badge.message}" />
              <meta property="twitter:image" content="${imagePath}" />
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
                <a href="${config.HTTPS_HOST}/app/profile/competency/badges">
                  <div class="image-container">
                    <img
                      class="badge-img"
                      src="${imagePath}"
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
