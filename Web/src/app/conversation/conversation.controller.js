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
  $scope.currentConversation = null;

  const getNormalConvs = () => {
    return $scope.imClient.getQuery().withLastMessagesRefreshed().containsMembers([$scope.imClient.id]).find();
  };

  const getTransConvs = () => {
    return $scope.imClient.getQuery().withLastMessagesRefreshed().equalTo('tr', true).addDescending('lm').limit(1).find();
  };

  const getSysConvs = () => {
    return $scope.imClient.getQuery().withLastMessagesRefreshed().equalTo('sys', true).find();
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
    // 将切换后的 conversation 标记为已读
    conv.read()
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
    ['online', 'menu'].map(id => $mdSidenav(id).close());
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
    // 加入第一个暂态聊天室
    return $scope.transConvs[0].join().then(() => {
      $scope.joinedTransConvs.push($scope.imClient.id);
    }).catch(console.error.bind(console));
  }).catch(console.error.bind(console));

  $scope.logout = () => {
    userService.logout().then(() => {
      LeanRT.imClient = null;
      $state.go('login');
    });
  };

  const messageHandler = (msg, conv) => {
    // 更新左侧对话列表
    // 暂态对话
    if (conv.transient && $scope.transConvs.indexOf(conv) === -1) {
      $scope.transConvs.push(conv);
    }
    // TODO: 暂时无法判断系统对话, 目前需求上也只需要一个系统对话, 因此跳过更新系统对话列表的逻辑

    // 普通对话
    if (!conv.transient && $scope.normalConvs.indexOf(conv) === -1) {
      $scope.normalConvs.push(conv);
    }
    $scope.$apply();
  };
  const invitedHandler = (payload, conversation) => {
    if (conversation.transient && $scope.transConvs.indexOf(conversation) === -1) {
      // 暂态对话
      $scope.transConvs.push(conversation);
    } else if ($scope.normalConvs.indexOf(conversation) === -1) {
      $scope.normalConvs.push(conversation);
    }
    $scope.$apply();
  };

  $scope.imClient.on('message', messageHandler);
  $scope.imClient.on('invited', invitedHandler);
  $scope.imClient.on('unreadmessagescountupdate', () => $scope.$apply());

  $scope.$on("$destroy", () => {
    $scope.imClient.off('message', messageHandler);
    $scope.imClient.off('invited', invitedHandler);
    $scope.imClient.on('unreadmessagescountupdate');
  });
};
