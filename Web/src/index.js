import angular from 'angular';

import loginController from './app/login/loginController.js';
import 'angular-ui-router';
import 'angular-material';
import 'angular-material/angular-material.css';
import 'leancloud-realtime/dist/realtime.browser.js';
import routesConfig from './routes';

import './index.scss';

export const app = 'leanMessage';

angular
  .module(app, ['ui.router', 'ngMaterial'])
  .config(routesConfig)
  .factory('realtime', () => {
    const realtime = {};
    const RT = require('leancloud-realtime').Realtime;
    const rt = new RT({
      appId: 'nWV9ebO1JMUIE5K4gBS1i1MA',
      region: 'cn' // 美国节点为 "us"
    });
    realtime.rt = rt;
    return realtime;
  })
  .controller('loginCtrl', loginController);
