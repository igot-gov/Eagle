Mobile Application 

EXTERNAL DEPENDENCIES:

AZURE MEDIA PLAYER DEPENDENCY:

Use case: For playing video contents which are downloaded within the app. 
The downloaded content can’t be played outside the app as they are encrypted while downloading and decrypted only while playing.

As other players won’t be able to access the application path, we need player supporting mp4 format within the application itself to play that so for that purpose Azure media player is used.


Without Azure Media Player: Without this the video downloaded content won’t play.
If you need to play downloaded video content: azure media player needs to be added within the app.


For setting up the dependencies:

Download amp min css and js file from (Version used : 2.1.6)
https://amp.azure.net/libs/amp/2.1.6/skins/amp-default/azuremediaplayer.min.css
https://amp.azure.net/libs/amp/2.1.6/azuremediaplayer.min.js

Place it within: 
src/main/assets/app-player/amp/2.1.6


For license : 
Check https://amp.azure.net/libs/amp/latest/docs/

