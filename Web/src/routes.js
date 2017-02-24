export default routesConfig;

/** @ngInject */
function routesConfig($stateProvider, $urlRouterProvider) {
  $urlRouterProvider.otherwise('/');

  $stateProvider
    .state('login', {
      url: '/',
      templateUrl: 'app/login/login.html',
      controller: 'loginCtrl'
    })
    .state('conversations', {
      url: '/conversations',
      templateUrl: 'app/conversation/conversation.html',
      controller: 'convCtrl'
    })
    .state('conversations.message', {
      url: '/:convId',
      templateUrl: 'app/conversation/conversationMessage/conversation.message.html',
      controller: 'convMsgCtrl'
    })
    .state('logging', {
      url: '/logging/:redirect',
      templateUrl: 'app/login/logging.html',
      controller: 'loggingCtrl'
    });

  // $urlRouterProvider.otherwise('/conversations/' + defaultConversation.id);
}
