export default ($state, userService, LeanRT) => {
  'ngInject';

  const clientId = userService.getCachedInfo();

  userService.login(clientId)
    .then(userClient => {
      LeanRT.imClient = userClient;
      userService.connected = true;
      $state.go('conversations');
    }).catch(console.error.bind(console));
};
