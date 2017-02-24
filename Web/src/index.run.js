function runBlock($rootScope, $state, userService) {
  'ngInject';

  $rootScope.$on('$stateChangeStart', (event, toState, toParams) => {
    if (toState.name === 'login') {
      return;
    }

    if (userService.isLoggedin()) {
      return;
    }

    if (userService.isCached()) {
      if (toState.name === 'logging') {
        return;
      }
      event.preventDefault();
      // if (toParams.convId) {
      //   localStorage.setItem('initConvId', toParams.convId);
      // }
      console.log(toState, toParams);
      $state.go('logging', {
        redirect: JSON.stringify({
          name: toState.name,
          params: toParams
        })
      });
    } else {
      setTimeout(() => $state.go('login'), 0);
    }
  });
}

export default runBlock;
