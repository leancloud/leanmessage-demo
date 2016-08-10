export default routesConfig;

/** @ngInject */
function routesConfig($stateProvider, $urlRouterProvider, $locationProvider) {
  $locationProvider.html5Mode(true).hashPrefix('!');
  $urlRouterProvider.otherwise('/');

  $stateProvider
    .state('leanMessage', {
      url: '/',
      templateUrl: 'app/login/login.html',
      controller: 'loginCtrl'
    });
}
