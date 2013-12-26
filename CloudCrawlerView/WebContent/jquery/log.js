$(function () {
    "use strict";

    var header = $('#header_log');
    var content = $('#content_web');
    //var input = $('#input');
    var status = $('#status');
    var myName = false;    
    var logged = false;
    var socket = $.atmosphere;
    var subSocket;
    var transport = 'websocket';
    
    var endpoint_url = 'http://'+window.document.location.host+'/CloudCrawler/console'
    
    //alert(endpoint_url)
    
    // We are now ready to cut the request
    var request = {
    	url: endpoint_url,
    	//url: 'http://192.168.33.10:28080/CloudCrawler/console',
        contentType : "application/json",
        logLevel : 'debug',
        transport : transport ,
        trackMessageLength : true,
        fallbackTransport: 'long-polling'};
    

    request.onOpen = function(response) {
        content.html($('<p>', { text: 'Atmosphere connected using ' + response.transport }));
        //input.removeAttr('disabled').focus();        
        status.text('On line');
        transport = response.transport;
    };

    <!-- For demonstration of how you can customize the fallbackTransport using the onTransportFailure function -->
    request.onTransportFailure = function(errorMsg, request) {
        jQuery.atmosphere.info(errorMsg);
        if (window.EventSource) {
            request.fallbackTransport = "sse";
        }
        header.html($('<h3>', { text: 'Atmosphere Chat. Default transport is WebSocket, fallback is ' + request.fallbackTransport }));
    };

    request.onMessage = function (response) {

        var message = response.responseBody;
        try {
            var json = jQuery.parseJSON(message);
        } catch (e) {
            console.log('This doesn\'t look like a valid JSON: ', message);
            return;
        }

        //input.removeAttr('disabled').focus();
        if (!logged && myName) {
            logged = true;
            status.text(myName + ': ').css('color', 'blue');
        } else {            
            addMessage(json.message);
        }
    };

    request.onClose = function(response) {
        subSocket.push(jQuery.stringifyJSON({message: 'disconnecting' }));
        logged = false;
    };

    request.onError = function(response) {
        content.html($('<p>', { text: 'Sorry, but there\'s some problem with your '
            + 'socket or the server is down' }));
    };

    subSocket = socket.subscribe(request);
    //subSocket.push('{"message":"Hello World","author":"John Doe"}');
    

    function addMessage(message) {
        content.append('<p class="prettyprint">' + message + '</p>');
    }
});