import './login.scss';

export default ($scope, LeanRT, $state, userService) => {
  'ngInject';
  $scope.user = {};

  $scope.login = () => {
    userService.login($scope.user.id).then(userClient => {
      LeanRT.imClient = userClient;
      userService.cache(userClient.id);
      userService.connected = true;
      $state.go('conversations');
    }).catch(console.error.bind(console));
  };
};
