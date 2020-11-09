const express = require("express");
const router = express.Router();
var request = require("request");
request.gzip = true;
var config = require("../utils/environment");

router.get("/playlist/:userId/:id", function (req, res) {
  console.log("api service started"); 
  let URL = `${config.API_HOST}/v1/users/${req.params.userId}/playlists/${req.params.id}`;
  let data,playlist;
  request(
    {
      headers: {       
        rootorg: "igot"       
      },
      uri: URL,
      method: "GET"     
    },
    function (err, response, body) {
      if (err) {
        console.log("error:", err);
        res.send(err);
      } else {
       
        console.log("api successful");       
        if (typeof body == "string") {
          playlist = JSON.parse(body)         
        } else {
          playlist = body        
        }
        if (playlist.resource_ids && playlist.resource_ids.length > 0) {
          data = playlist.resource_ids[0]
        }
        if (data) {
          var html = `<!DOCTYPE html>
            <html lang="en">
              <head>             
                <meta charset="utf-8" />
                <title>"${playlist.playlistTitle}"</title>
                <link id="id-app-fav-icon" rel="icon" type="image/png" href="/image/favicon.png")
                " />                          
                <meta property="og:type" content="website">
                <meta property="og:url" content="${`${config.HTTPS_HOST}/share/playlist/${req.params.userId}/${req.params.id}`}" />
                <meta property="og:title" content="${playlist.playlistTitle}" />               
                <meta property="og:image" content="${data.appIcon}" />  
                <meta property="og:image:secure_url" content="${
                  data.appIcon
                }" />      
            
                <meta property="twitter:card" content="summary_large_image" />
                <meta property="twitter:url" content="${`${config.HTTPS_HOST}/share/playlist/${req.params.userId}/${req.params.id}`}" />
                <meta property="twitter:title" content="${playlist.playlistTitle}" />               
                <meta property="twitter:image" content="${data.appIcon}" />
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
                  <a href="https://${config.HTTPS_HOST}/app/playlist/me/${req.params.id}">
                    <img src="${data.appIcon}" alt="${playlist.playlistTitle}"  class="social-card-img" />
                    <div class="sub-card">
                      <p class="title">${playlist.playlistTitle}</p>                     
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
