function runBlock($rootScope, $state, userService, $stateParams) {
  'ngInject';

  $rootScope.$on('$stateChangeStart', function(event, toState, toParams, fromState, fromParams, options) {
    if (toState.name === 'login') {
      return;
    }

    if (userService.isLoggedin()) {
      return;
    }
    if (!userService.isCached()) {
      setTimeout(() => $state.go('login'), 0);
    } else {
      if (toState.name === 'logging') {
        return;
      } else {
        event.preventDefault();
        if (toParams.convId) {
          localStorage.setItem('initConvId', toParams.convId);  
        }
        $state.go('logging');
      }
    }
  });
};

export default runBlock;