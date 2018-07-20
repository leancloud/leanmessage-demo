import 'jquery';
import angular from 'angular';
import 'angular-ui-router';
import 'angular-material';
import 'angular-material/angular-material.css';
import {Realtime} from 'leancloud-realtime';
import AV from 'leancloud-storage';
import {TypedMessagesPlugin} from 'leancloud-realtime-plugin-typed-messages';
import {GroupchatReceiptsPlugin} from 'leancloud-realtime-plugin-groupchat-receipts';
import {TypingIndicatorPlugin} from './typing-indicator';
import StickerMessage from './sticker-message';
import routesConfig from './routes';

import './index.scss';
import runBlock from './index.run.js';

import reverseInfiniteListDirective from './app/components/reverse-infinite-list/reverse-infinite-list.directive.js';
import messageDirective from './app/components/message/message.directive.js';

import userService from './app/components/user/user.service.js';
import loginController from './app/login/login.controller.js';
import loggingController from './app/login/logging.controller.js';
import convController from './app/conversation/conversation.controller.js';
import convMsgController from './app/conversation/conversationMessage/conversation.message.controller.js';

export const app = 'leanMessage';
const appId = 'm7baukzusy3l5coew0b3em5uf4df5i2krky0ypbmee358yon';
const appKey = '2e46velw0mqrq3hl2a047yjtpxn32frm0m253k258xo63ft9';

AV.init({appId, appKey});

angular
  .module(app, ['ui.router', 'ngMaterial'])
  .config(routesConfig)
  .config($mdThemingProvider => {
    'ngInject';
    $mdThemingProvider.theme('default')
      .primaryPalette('blue')
      .accentPalette('blue-grey');
  })
  .factory('LeanRT', () => {
    const LeanRT = {};
    const realtime = new Realtime({
      appId,
      appKey,
      // server: 'rtm51',
      plugins: [TypedMessagesPlugin, GroupchatReceiptsPlugin, TypingIndicatorPlugin],
      region: 'cn' // 美国节点为 "us"
    });
    realtime.register([StickerMessage]);
    LeanRT.realtime = realtime;
    LeanRT.imClient = null;
    LeanRT.currentConversation = null;
    return LeanRT;
  })
  .service('userService', userService)
  .run(runBlock)
  .directive('infiniteList', reverseInfiniteListDirective)
  .directive('message', messageDirective)
  .directive('ngUploadChange', () => ({
    scope: {
      ngUploadChange: "&"
    },
    link: ($scope, $element) => {
      $element.on("change", event => {
        $scope.ngUploadChange({$event: event});
      });
      $scope.$on("$destroy", () => {
        $element.off();
      });
    }
  }))
  .controller('loginCtrl', loginController)
  .controller('loggingCtrl', loggingController)
  .controller('convCtrl', convController)
  .controller('convMsgCtrl', convMsgController);
