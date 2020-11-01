const express = require("express");
const router = express.Router();
var request = require("request");

router.get("/badge/:userId/:id", function (req, res) {
  let URL = `http://lex-core:7001/v3/users/${req.params.userId}/badges`;
  console.log("api service started"); 
  let badges;
  request(
    {
      headers: {
        local: "en",
        rootOrg: "igot",
      },
      uri: URL,
      method: "GET"
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
        let badge;
        if (badges.earned && badges.earned.length > 0) {
          badgeArray = badges.earned.filter(function (badge) {
            return badge.badge_id == req.params.id;
          });
          badge = badgeArray.length > 0 ? badgeArray[0] : null;
        }
        //badge = _.where(badges.earned, {badge_id: req.params.id});
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
              <meta property="og:url" content="${`https://d136953gtttd92.cloudfront.net/share/badge/${req.params.userId}/${req.params.id}`}" />
              <meta property="og:title" content="${badge.badge_name}" />
              <meta property="og:description" content="${badge.message}" />
              <meta property="og:image" content="${badge.image}" />  
              <meta property="og:image:secure_url" content="${
                badge.image
              }" />        
          
              <meta property="twitter:card" content="summary_large_image" />
              <meta property="twitter:url" content="${`https://d136953gtttd92.cloudfront.net/share/badge/${req.params.userId}/${req.params.id}`}" />
              <meta property="twitter:title" content="${badge.badge_name}" />
              <meta property="twitter:description" content="${
                badge.message
              }" />
              <meta property="twitter:image" content="${badge.image}" />
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
                <a href="https://d136953gtttd92.cloudfront.net/app/toc/${req.params.id}/overview">
                  <img src="${badge.image}" alt="${badge.badge_name}"  class="social-card-img" />
                  <div class="sub-card">
                    <pclass="title">${badge.badge_name}</p>
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
