import './conversation.scss';

export default ($scope, LeanRT, $state, $stateParams, $mdSidenav, userService) => {
  'ngInject';

  $scope.$mdSidenav = $mdSidenav;
  $scope.imClient = LeanRT.imClient;
  $scope.normalConvs = [];
  $scope.transConvs = [];
  $scope.sysConvs = [];
  $scope.joinedTransConvs = [];
  $scope.transientEmail = 'test@test.com';

  const getNormalConvs = () => {
    return $scope.imClient.getQuery().containsMembers([$scope.imClient.id]).find();
  };

  const getTransConvs = () => {
    return $scope.imClient.getQuery().equalTo('tr', true).addDescending('lm').limit(1).find();
  };

  const getSysConvs = () => {
    return $scope.imClient.getQuery().equalTo('sys', true).find();
  };

  $scope.getSingleConvTarget = members => {
    if (members[0] === $scope.imClient.id) {
      return members[1];
    }

    return members[0];
  };

  $scope.getConversations = () => {
    return Promise.all([getSysConvs(), getTransConvs(), getNormalConvs()])
      .then(datas => {
        $scope.sysConvs = datas[0];
        $scope.transConvs = datas[1];
        $scope.normalConvs = datas[2];
        $scope.$digest();
      });
  };

  $scope.switchToConv = conv => {
    $scope.currentConversation = conv;
    LeanRT.currentConversation = conv;
    // 将切换后的 conversation 标记为已读
    $scope.currentConversation.markAsRead()
      .then(() => {
        return setTimeout(() => {
          $state.go('conversations.message', {
            convId: conv.id
          });
        }, 0);
      })
      .then(() => {
        $scope.$digest();
      }).catch(console.error.bind(console));
  };

  $scope.changeTo = conv => {
    if (conv.tr === true) {
      // join transiant conversation
      if ($scope.joinedTransConvs.findIndex($scope.imClient.id) === -1) {
        conv.join().then(conv => {
          $scope.joinedTransConvs.push($scope.imClient.id);
          $scope.switchToConv(conv);
        });
      }
    } else {
      // change user interface
      $scope.switchToConv(conv);
    }
  };

  $scope.getConversations()
  .then(() => {
    const initConvId = localStorage.getItem('initConvId');
    if (initConvId) {
      $scope.imClient.getConversation(initConvId)
        .then(conversation => {
          localStorage.removeItem('initConvId');
          if (conversation) {
            $scope.switchToConv(conversation);
          }
        }).catch(console.error.bind(console));
    } else {
      // 加入第一个暂态聊天室
      $scope.transConvs[0].join().then(conv => {
        $scope.joinedTransConvs.push($scope.imClient.id);
        $scope.switchToConv(conv);
      }).catch(console.error.bind(console));
    }
  }).catch(console.error.bind(console));

  $scope.logout = () => {
    userService.logout().then(() => {
      LeanRT.imClient = null;
      $state.go('login');
    });
  };
};
