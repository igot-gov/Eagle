/*
include this snippet if you're changing sources/adding a new set of markers in order to clear up the markers on your timeline before setting new ones
var clear = document.getElementsByClassName('amp-timeline-marker');
                 var i=0; 
                 for (i<= 0; i < clear.length; i++){
                     //do thing
                     document.getElementsByClassName('amp-timeline-marker')[i].style.visibility='hidden';
                 }


*/

    duration= 0;
     (function () {

        amp.plugin('timelineMarker', function (options) {
            var player = this;
			
            player.addEventListener(amp.eventName.durationchange, function () {
            duration  = player.duration();
            var progressControlSlider = getElementsByClassName("vjs-progress-control", "vjs-slider");
            function getElementsByClassName(className, childClass) {
                var elements = document.getElementById("azuremediaplayer").getElementsByClassName(className);
                var matches = [];

                function traverse(node) {
                    if (node && node.childNodes) {
                        for (var i = 0; i < node.childNodes.length; i++) {
                            if (node.childNodes[i].childNodes.length > 0) {
                                traverse(node.childNodes[i]);
                            }

                            if (node.childNodes[i].getAttribute && node.childNodes[i].getAttribute('class')) {
                                if (node.childNodes[i].getAttribute('class').split(" ").indexOf(childClass) >= 0) {
                                    matches.push(node.childNodes[i]);
                                }
                            }
                        }
                    }
                }
                if (!childClass)
                    return elements && elements.length > 0 ? elements[0] : null;

                if (elements && elements.length > 0) {
                    for (var i = 0; i < elements.length; i++)
                        traverse(elements[i]);
                }
                return matches && matches.length > 0 ? matches[0] : null;
            }

            if (progressControlSlider) {
                for (var index = 0; index < options.markertime.length; index++) {
                    var marker = options.markertime[index];               
                    if (marker) {
                        var secs = convertTimeFormatToSecs(marker);
                        if (secs >= 0 && secs <= duration) {
                            var markerLeftPosition = (secs / duration * 100);
                            var div = document.createElement('div');
                            div.className = "amp-timeline-marker";
                            div.style.left = markerLeftPosition + "%";
                            div.innerHTML = "&nbsp;&nbsp;"
                            progressControlSlider.appendChild(div);
                        }
                    }
                }
            }
        });

       

            function convertTimeFormatToSecs(timeFormat) {
                if (timeFormat) {
                    var timeFragments = timeFormat.split(":");
                    if (timeFragments.length > 0) {
                        switch (timeFragments.length) {
                            case 4: return (parseInt(timeFragments[0], 10) * 60 * 60) + (parseInt(timeFragments[1], 10) * 60) + parseInt(timeFragments[2], 10) + (timeFragments[3] / 100);
                            case 3: return (parseInt(timeFragments[0], 10) * 60 * 60) + (parseInt(timeFragments[1], 10) * 60) + parseInt(timeFragments[2], 10);
                            case 2: return parseInt(timeFragments[0], 10) * 60 + parseInt(timeFragments[1], 10);
                            case 1: return parseInt(timeFragments[0], 10);
                            default: return parseInt(timeFragments[0], 10);
                        }
                    }
                    else
                        return parseInt(timeFormat, 10);
                }

                return 0;
            }
    
        
           

        });

    }).call(this);
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   
   