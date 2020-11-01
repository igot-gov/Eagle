const express = require("express");
const router = express.Router();
var request = require("request");
var _ = require("underscore");

router.get("/badge/:userId/:id", function (req, res) { 

  let testBadges={"canEarn":[{"image":"/apis/proxies/v8/content/Achievements/Badges/Duelist.png?type=assets","badge_group":"Quiz","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete your first quiz resource","hover_text":"1 more quizzes to go!","badge_name":"Duelist","badge_group_text":"no data","badge_id":"Quiz1","progress":0,"badge_type":"O","threshold1":1},{"image":"/apis/proxies/v8/content/Achievements/Badges/Ace.png?type=assets","badge_group":"Quiz","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete 25 quiz resources","hover_text":"25 more quizzes to go!","badge_name":"Ace","badge_group_text":"no data","badge_id":"Quiz25","progress":0,"badge_type":"O","threshold1":25},{"image":"/apis/proxies/v8/content/Achievements/Badges/Genie.png?type=assets","badge_group":"Quiz","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete 100 quiz resources","hover_text":"100 more quizzes to go!","badge_name":"Genie","badge_group_text":"no data","badge_id":"Quiz100","progress":0,"badge_type":"O","threshold1":100},{"image":"/apis/proxies/v8/content/Achievements/Badges/Wizard.png?type=assets","badge_group":"Quiz","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete 250 quiz resources","hover_text":"250 more quizzes to go!","badge_name":"Wizard","badge_group_text":"no data","badge_id":"Quiz250","progress":0,"badge_type":"O","threshold1":250},{"image":"/apis/proxies/v8/content/Achievements/Badges/Quizzard.png?type=assets","badge_group":"Quiz","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete 1000 quiz resources","hover_text":"1000 more quizzes to go!","badge_name":"Quizzard","badge_group_text":"no data","badge_id":"Quiz1000","progress":0,"badge_type":"O","threshold1":1000},{"image":"/apis/proxies/v8/content/Achievements/Badges/Knight.png?type=assets","badge_group":"Course","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete 10 courses","hover_text":"9 more courses to go!","badge_name":"Knight","badge_group_text":"no data","badge_id":"Course10","progress":10,"badge_type":"O","threshold1":10},{"image":"/apis/proxies/v8/content/Achievements/Badges/Champion.png?type=assets","badge_group":"Course","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete 25 courses","hover_text":"24 more courses to go!","badge_name":"Champion","badge_group_text":"no data","badge_id":"Course25","progress":4,"badge_type":"O","threshold1":25},{"image":"/apis/proxies/v8/content/Achievements/Badges/Sensei.png?type=assets","badge_group":"Course","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete 100 courses","hover_text":"99 more courses to go!","badge_name":"Sensei","badge_group_text":"no data","badge_id":"Course100","progress":1,"badge_type":"O","threshold1":100},{"image":"/apis/proxies/v8/content/Achievements/Badges/Lightning Deer.png?type=assets","badge_group":"Duration","is_new":0,"received_count":0,"language":"en","how_to_earn":"Learn 4 hours a day","hover_text":"240 minutes to go!","message":"You are the lord of lightning! That was quick, do you want to continue learning?","badge_name":"Lightning Deer","badge_description":"Learn 4 hours a day","badge_group_text":"no data","badge_id":"4Day","progress":0,"badge_type":"R","created_date":1544528913000,"threshold1":4},{"image":"/apis/proxies/v8/content/Achievements/Badges/Power Tiger.png?type=assets","badge_group":"Duration","is_new":0,"received_count":0,"language":"en","how_to_earn":"Learn 20 hours in a week","hover_text":"1200 minutes to go!","message":"Your steadfastness is commendable. Don't stop the learning.","badge_name":"Power Tiger","badge_description":"Learn 20 hours in a week","badge_group_text":"no data","badge_id":"20Week","progress":0,"badge_type":"R","created_date":1544528913000,"threshold1":20},{"image":"/apis/proxies/v8/content/Achievements/Badges/Studious Bee.png?type=assets","badge_group":"Duration","is_new":0,"received_count":0,"language":"en","how_to_earn":"Learn for 30 minutes each day for 5 days in a week","hover_text":"5 more days to go!","message":"Discipline is the bridge between goals and accomplishment. Continue learning.","badge_name":"Studious Bee","badge_description":"Learn for 30 minutes each day for 5 days in a week","badge_group_text":"no data","badge_id":"30MWeek","progress":0,"badge_type":"R","created_date":1544528913000,"threshold1":0.5},{"image":"/apis/proxies/v8/content/Achievements/Badges/Soaring Eagle.png?type=assets","badge_group":"Duration","is_new":0,"received_count":0,"language":"en","how_to_earn":"Learn 30 minutes each day for 25 days in a month","hover_text":"25 more days to go!","message":"You have displayed the highest level of discipline and focus to achieve the ultimate badge.","badge_name":"Soaring Eagle","badge_description":"Learn 30 minutes each day for 25 days in a month","badge_group_text":"no data","badge_id":"30MMonth","progress":0,"badge_type":"R","created_date":1544528913000,"threshold1":0.5},{"image":"/apis/proxies/v8/content/Achievements/Badges/Santa.png?type=assets","badge_group":"User","is_new":0,"received_count":0,"language":"en","how_to_earn":"Come to oGOT in December","hover_text":"","message":"Ho, Ho, Ho, Welcome to iGOT!","badge_name":"Santa","badge_description":"Come to oGOT in December","badge_group_text":"no data","badge_id":"Santa","progress":0,"badge_type":"O","created_date":1544528913000,"threshold1":0},{"image":"/apis/proxies/v8/content/Achievements/Badges/Exploring Elf.png?type=assets","badge_group":"Learning Path","is_new":0,"received_count":0,"language":"en","how_to_earn":"Complete a Learning Path in December","hover_text":"","message":"Learn On! You're on the right path of learning. Wanna continue?","badge_name":"Exploring Elf","badge_description":"Complete a Learning Path in December","badge_group_text":"no data","badge_id":"Elf","progress":0,"badge_type":"O","created_date":1544528913000,"threshold1":0}],"closeToEarning":[],"earned":[{"image":"/apis/proxies/v8/content/Achievements/Badges/Warrior.png?type=assets","last_received_date":"2020-10-27","badge_group":"Course","is_new":1,"received_count":1,"language":"en","first_received_date":"2020-10-27","how_to_earn":"Complete your first course","hover_text":"0 more courses to go!","message":"You took one giant leap towards your goal. Welcome to lifelong learning.","badge_name":"Warrior","badge_group_text":"no data","badge_id":"Course1","progress":100,"badge_type":"O","threshold1":1},{"image":"/apis/proxies/v8/content/Achievements/Badges/Fledgling.png?type=assets","last_received_date":"2020-07-30","badge_group":"User","is_new":0,"received_count":1,"language":"en","first_received_date":"2020-07-30","how_to_earn":"Register on iGOT platform","hover_text":"","message":"You've got on to iGOT for learning. Good start!","badge_name":"The Fledgling","badge_group_text":"no data","badge_id":"NewUser","progress":100,"badge_type":"O","threshold1":0}],"lastUpdatedDate":"01 Jan 1970 00:00 UTC","recent":[{"last_received_date":"2020-10-27","image":"/apis/proxies/v8/content/Achievements/Achieved/Certificate.png?type=assets","badge_group":"C","is_new":1,"badge_order":"C","received_count":1,"first_received_date":"2020-10-27","threshold":1,"hover_text":"For completing 'courseee' course.","how_to_earn":"Completed the course","message":"Congratulations on completing the course 'courseee'.","badge_name":"courseee","badge_id":"lex_auth_013099179260837888124","progress":100,"badge_type":"C"}],"totalPoints":[{"learning_points":0,"collaborative_points":0}]}
 

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
        
        let badgeArray = badges.earned.filter(function(badge){
          return badge.badge_id == req.params.id;
        });
        //badge = _.where(badges.earned, {badge_id: req.params.id});
        badge= badgeArray[0];
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
