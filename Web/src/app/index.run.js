function runBlock($rootScope, $state, user) {
  'ngInject';

  $rootScope.$on('$stateChangeStart', function(event, next) {
    if (next.name === 'login') {
      return;
    }
    if (!user.isLoggedin()) {
      if (!user.isCached()) {
        setTimeout(() => $state.go('login'), 0);
      } else {
        var userInfo = user.getCachedInfo();
        user.login(userInfo.id, userInfo.email);
      }
    }
  });
}

export default runBlock;
