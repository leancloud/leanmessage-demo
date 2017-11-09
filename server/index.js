// http://mathjs.org/examples/advanced/web_server/index.html

var express = require('express');
var workerpool = require('workerpool');
var bodyParser = require('body-parser');
var AV = require('leanengine');
var TextMessage = require('leancloud-realtime').TextMessage;

var app = express();
app.use(bodyParser.json());
var pool = workerpool.pool(__dirname + '/math_worker.js');

var TIMEOUT = 10000; // milliseconds

var APP_ID = process.env.LC_APP_ID; // your app id
var APP_KEY = process.env.LC_APP_KEY; // your app key
var MASTER_KEY = process.env.LC_APP_MASTER_KEY; // your app master key

AV.init({
  'appId': APP_ID,
  'appKey': APP_KEY,
  'masterKey': MASTER_KEY
});

var MATH_CONV_ID = process.env.MATH_CONV_ID;
app.post('/webhook', function (req, res) {
  var messages = req.body;
  console.log('messages recieved: ' + JSON.stringify(messages));

  // 过滤掉暂态消息与非通过 MathBot 对话发过来的消息
  messages
    .filter(message => !message.noPersist)
    .filter(message => message.conv.objectId === MATH_CONV_ID)
    .forEach(function (message) {
      var convId = message.conv.objectId;
      var peerId = message.from;
      Promise.resolve().then(function () {
        const data = JSON.parse(message.data);
        if (data._lctype !== -1) throw new TypeError('不支持的消息类型');
        var expr = data._lctext;
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
  var message = new TextMessage(content);
  var conversation = AV.Object.createWithoutData('_Conversation', convId);
  return conversation.send('MathBot', message, { toClients: [peerId] }, { useMasterKey: true }).then(function() {
    console.log('sended: ' + JSON.stringify(message));
  }, function(error) {
    console.error('send message error: ' + error.message);      
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

// 整点报时
var CHIME_CONV_ID = process.env.CHIME_CONV_ID;
AV.Cloud.define('chime', function() {
  if (!CHIME_CONV_ID) return console.warn('CHIME_CONV_ID not set, skip chiming');
  console.log('chime');
  var hours = Math.round((Date.now() / 3600000  + 8) % 24);
  var message = new TextMessage('北京时间 ' + hours + ' 点整');
  var conversation = AV.Object.createWithoutData('_Conversation', CHIME_CONV_ID);
  return conversation.broadcast('LeanObservatory', message, { validTill: Date.now() }, { useMasterKey: true }).then(function() {
    console.log('chimed');
  }, function(error) {
    console.error('broadcast message error: ' + error.message);      
  });
});

// handle uncaught exceptions so the application cannot crash
process.on('uncaughtException', function (err) {
  console.log('Caught exception: ' + err);
  console.trace();
});

// leanengine health checker
app.use(AV.express());

// start the server
var PORT = process.env.LC_APP_PORT | process.env.PORT || 8080;
app.listen(PORT, function () {
  console.log('Listening at http://localhost:' + PORT);
});
