window.addEventListener('load', function() {
  // Initialize the option controls.
  var chStr
  try {
    var chArray = JSON.parse(localStorage.channels);
    chString = chArray.forEach(function(elem) {
      chString+=elem.replaceAll(" ", "")+",";
    });
    
  } catch(error) {
    console.log(error);
    chString = "default,";
  }
  options.channels.value = chString;
  
  if(!localStorage.host) {
   localStorage.host = "http://localhost:9000"; 
  }
                                         // The display activation.
  options.host.value = localStorage.host;
                                         // The display frequency, in minutes.

  // Set the display activation and frequency.
  options.channels.onchange = function() {
    var channels = options.channels.value.split(",");
    console.log(channels);
    localStorage.channels = JSON.stringify(channels);
  };

  options.host.onchange = function() {
    localStorage.host = options.host.value;
  };
});
