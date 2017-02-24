export default ($state, userService, LeanRT) => {
  'ngInject';

  const clientId = userService.getCachedInfo();

  userService.login(clientId)
    .then(userClient => {
      LeanRT.imClient = userClient;
      userService.connected = true;
      if ($state.params && $state.params.redirect) {
        const redirect = JSON.parse($state.params.redirect);
        console.log(redirect);
        return $state.go(redirect.name, redirect.params);
      }
      $state.go('conversations');
    }).catch(console.error.bind(console));
};
