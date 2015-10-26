// http://mathjs.org/examples/advanced/web_server/index.html

var express = require('express');
var workerpool = require('workerpool');
var bodyParser = require('body-parser');
var request = require('request');
var AV = require('leanengine');

var app = express();
app.use(bodyParser.json());
var pool = workerpool.pool(__dirname + '/math_worker.js');

var TIMEOUT = 10000; // milliseconds

var APP_ID = process.env.LC_APP_ID; // your app id
var APP_KEY = process.env.LC_APP_KEY; // your app key
var MASTER_KEY = process.env.LC_APP_MASTER_KEY; // your app master key

AV.initialize(APP_ID, APP_KEY, MASTER_KEY);

app.post('/webhook', function (req, res) {
  var messages = req.body;
  console.log('messages recieved: ' + JSON.stringify(messages));

  messages.forEach(function (message) {
    var convId = message.conv.objectId;
    var peerId = message.from;
    Promise.resolve().then(function () {
        var expr = JSON.parse(message.data)._lctext;
        return expr;
      }).then(function (expr) {
        return pool.exec('evaluate', [expr]).timeout(TIMEOUT);
      }).then(function (result) {
        sendMessage(result, peerId, convId);
      })
      .catch(function (err) {
        sendMessage(formatError(err), peerId, convId);
      });
  });

  res.send('');
});

function sendMessage(content, peerId, convId) {
  console.log('sending message [' + content + '] to peer [' + peerId + ']');
  request.post({
    url: 'https://leancloud.cn/1.1/rtm/messages',
    headers: {
      'X-LC-Id': APP_ID,
      'X-LC-Key': MASTER_KEY + ',master'
    },
    json:true,
    body: {
      'from_peer': 'MathBot',
      'message': JSON.stringify({
        '_lctext': content,
        '_lctype': -1
      }),
      'conv_id': convId,
      'to_peers': [peerId],
      'transient': false
    }
  }, function (error, response, body) {
    if (!error && response.statusCode == 200) {
      console.log('sended: ' + JSON.stringify(body));
    }
    else {
      console.log('send message error: ' + response.statusCode + JSON.stringify(body));
    }
  });
}

/**
 * Format error messages as string
 * @param {Error} err
 * @return {String} message
 */
function formatError(err) {
  if (err instanceof workerpool.Promise.TimeoutError) {
    return 'TimeoutError: Evaluation exceeded maximum duration of ' + TIMEOUT / 1000 + ' seconds';
  } else {
    return err.toString();
  }
}

// handle uncaught exceptions so the application cannot crash
process.on('uncaughtException', function (err) {
  console.log('Caught exception: ' + err);
  console.trace();
});

// leanengine health checker
app.use(AV.Cloud);

// start the server
var PORT = process.env.LC_APP_PORT | process.env.PORT || 8080;
app.listen(PORT, function () {
  console.log('Listening at http://localhost:' + PORT);
});
