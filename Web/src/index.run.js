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

  $rootScope.$on('unreadCountUpdate', (event, count) => {
    console.log(count);
    $rootScope.totalUnreadMessagesCount = count;
    $rootScope.$digest();
  });
}

export default runBlock;
