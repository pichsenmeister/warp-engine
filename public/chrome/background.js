// Copyright (c) 2011 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

/*
  Displays a notification with the current time. Requires "notifications"
  permission in the manifest file (or calling
  "Notification.requestPermission" beforehand).
*/
function show(msg) {  
  new Notification('warp-engine message', {
    icon: '48.png',
    body: msg
  });
}

// Conditionally initialize the options.
if (!localStorage.isInitialized) {
  localStorage.isActivated = true;   // The display activation.
  localStorage.frequency = 1;        // The display frequency, in minutes.
  localStorage.isInitialized = true; // The option initialization.
}

// Test for notification support.
if (window.Notification) {
  

   connect();

  
}

function connect() {
  var wsUri = "ws://localhost:9000/socket/channel";

    websocket = new WebSocket(wsUri);
    
    websocket.onopen = function(evt) {
        show('ready')
    };

    websocket.onclose = function(evt) {
        show('close')
    };

    websocket.onmessage = function(evt) {
	console.log(evt);
	if(evt.data) {
	  show(evt.data);
	}
    };

    websocket.onerror = function(evt) {
        show('error')
    };
}
