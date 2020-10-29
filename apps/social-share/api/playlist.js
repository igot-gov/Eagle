const express = require("express");
const router = express.Router();

router.get("/playlist", function (req, res) {
    var html = `<!DOCTYPE html>
    <html lang="en">
      <head>             
        <meta charset="utf-8" />
        <title>"Playlist"</title>
        <link id="id-app-fav-icon" rel="icon" type="image/png" href="/image/favicon.png")
        " />
        </head>
        </html>          
        `;
    res.send(html);
   
});

module.exports = router;
