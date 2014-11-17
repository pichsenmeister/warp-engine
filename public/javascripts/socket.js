var wsUri = "ws://localhost:9000/socket/"+channel;
var output;

function init() {
    output = document.getElementById("output");
    testWebSocket();
}

function testWebSocket() {
    websocket = new WebSocket(wsUri);

    websocket.onopen = function(evt) {
        onOpen(evt)
    };

    websocket.onclose = function(evt) {
        onClose(evt)
    };

    websocket.onmessage = function(evt) {
        onMessage(evt)
    };

    websocket.onerror = function(evt) {
        onError(evt)
    };
}

function onOpen(evt) {
    writeToScreen("CONNECTED");
    var obj = {"msg": "Websocket rocks!"};
    doSend(obj);
}

function onClose(evt) {
    writeToScreen("DISCONNECTED");
}

function onMessage(evt) {
    writeToScreen("RESPONSE: "+evt.data);
}

function onError(evt) {
    writeToScreen("ERROR" + evt.data);
}

function doSend(message) {
    writeToScreen(message);
    websocket.send(JSON.stringify(message));
}

function writeToScreen(message) {
    console.log(message)
}

window.addEventListener("load", init, false);