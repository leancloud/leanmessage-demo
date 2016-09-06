import angular from 'angular';
import 'angular-ui-router';
import 'angular-material';
import 'angular-material/angular-material.css';
import {Realtime} from 'leancloud-realtime';
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

angular
  .module(app, ['ui.router', 'ngMaterial'])
  .config(routesConfig)
  .factory('LeanRT', () => {
    const LeanRT = {};
    const realtime = new Realtime({
      appId,
      region: 'cn' // 美国节点为 "us"
    });
    LeanRT.realtime = realtime;
    LeanRT.imClient = null;
    LeanRT.currentConversation = null;
    return LeanRT;
  })
  .service('userService', userService)
  .run(runBlock)
  .directive('infiniteList', reverseInfiniteListDirective)
  .directive('message', messageDirective)
  .controller('loginCtrl', loginController)
  .controller('loggingCtrl', loggingController)
  .controller('convCtrl', convController)
  .controller('convMsgCtrl', convMsgController);
