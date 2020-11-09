const express = require("express");
const router = express.Router();
var request = require("request");
request.gzip = true;
var config = require('../utils/environment');

router.get("/goal/:userId/:id", function (req, res) {
  console.log("api service started"); 
  let URL = `http://${config.API_HOST}/v5/users/${req.params.userId}/goals?goal_type=user&sourceFields=isInIntranet`;
  let goals;
  request(
    {
      headers: {        
        rootorg: "igot",  
        local: "en",      
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
            goals = JSON.parse(body);
        } else {
            goals = body;
        }
        let goalArray = [];
        let goal;
        if (goals.goals_in_progress && goals.goals_in_progress.length > 0) {
            goalArray = goals.goals_in_progress.filter(function (goal) {
              return goal.goal_id == req.params.id;
            });
            goal = goalArray.length > 0 ? goalArray[0] : null;
        }
        if (goal) {
          var html = `<!DOCTYPE html>
            <html lang="en">
              <head>             
                <meta charset="utf-8" />
                <title>"${goal.goal_title}"</title>
                <link id="id-app-fav-icon" rel="icon" type="image/png" href="/image/favicon.png")
                " />
                <meta id="id-social-description" name="description"  content="${
                  goal.goal_desc
                }">             
                <meta property="og:type" content="website">
                <meta property="og:url" content="${`https://${config.HTTPS_HOST}/share/goal/${req.params.userId}/${req.params.id}`}" />
                <meta property="og:title" content="${goal.goal_title}" />
                <meta property="og:description" content="${goal.goal_desc}" />
                <meta property="og:image" content="${goal.resource_progress[0].appIcon}" />  
                <meta property="og:image:secure_url" content="${
                   goal.resource_progress[0].appIcon
                }" />        
            
                <meta property="twitter:card" content="summary_large_image" />
                <meta property="twitter:url" content="${`https://${config.HTTPS_HOST}/share/goal/${req.params.userId}/${req.params.id}`}" />
                <meta property="twitter:title" content="${goal.goal_title}" />
                <meta property="twitter:description" content="${
                  goal.goal_desc
                }" />
                <meta property="twitter:image" content="${goal.resource_progress[0].appIcon}" />
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
                  <a href="https://${config.HTTPS_HOST}/app/goals/me/all">
                    <img src="${goal.resource_progress[0].appIcon}" alt="${goal.goal_title}"  class="social-card-img" />
                    <div class="sub-card">
                      <p class="title">${goal.goal_title}</p>
                      <p class="desc">${goal.goal_desc}</p>
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
