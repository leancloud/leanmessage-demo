import './conversation.scss';

export default ($scope, $rootScope, LeanRT, $state, $stateParams, $mdSidenav, userService) => {
  'ngInject';

  $scope.$mdSidenav = $mdSidenav;
  $scope.imClient = LeanRT.imClient;
  $scope.typingIndicator = LeanRT.imClient.createTypingIndicator();
  $scope.normalConvs = [];
  $scope.transConvs = [];
  $scope.sysConvs = [];
  $scope.joinedTransConv = null;
  $scope.transientEmail = 'test@test.com';
  $scope.currentConversation = null;
  $scope.networkError = null;
  $scope.networkErrorIcon = null;
  $scope.networkShowRetry = false;

  const getNormalConvs = () => {
    return $scope.imClient.getQuery().withLastMessagesRefreshed().containsMembers([$scope.imClient.id]).find();
  };

  const getTransConvs = () => {
    return $scope.imClient.getQuery().withLastMessagesRefreshed().equalTo('tr', true).addAscending('createdAt').limit(1).find();
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

  const getTotalUnreadCount = () => [...$scope.sysConvs, ...$scope.transConvs, ...$scope.normalConvs]
    .reduce((sum, {unreadMessagesCount}) => sum + unreadMessagesCount, 0);

  $scope.getConversations = () => {
    return Promise.all([getSysConvs(), getTransConvs(), getNormalConvs()])
      .then(datas => {
        $scope.sysConvs = datas[0];
        $scope.transConvs = datas[1];
        $scope.normalConvs = datas[2];
        $scope.$emit('unreadCountUpdate', getTotalUnreadCount());
        $scope.$digest();
      });
  };

  $scope.switchToConv = conv => {
    $state.go('conversations.message', {
      convId: conv.id
    });
  };

  $scope.changeTo = conv => {
    ['online', 'menu'].map(id => $mdSidenav(id).close());
    if (conv.tr === true) {
      // join transiant conversation
      if ($scope.joinedTransConv !== conv) {
        conv.join().then(conv => {
          $scope.joinedTransConv = conv;
          $scope.switchToConv(conv);
        });
      }
    } else {
      // change user interface
      $scope.switchToConv(conv);
    }
  };

  $scope.createSingleConv = clientId => {
    const targetConv = $scope.normalConvs.find(conv => (conv.members.length === 2 && $scope.getSingleConvTarget(conv.members) === clientId));
    if (targetConv) {
      return $scope.switchToConv(targetConv);
    }
    return $scope.imClient.createConversation({
      members: [clientId],
      name: `${clientId} 和 ${$scope.imClient.id} 的对话`,
      transient: false,
      unique: true
    }).then(conversation => {
      // 跳转到刚创建好的对话中
      $scope.switchToConv(conversation);
      // 此时 onInvited 会被调用, 在下方 onInvited 中更新 conversation list
    }).catch(console.error.bind(console));
  };

  $scope.getConversations()
  .then(() => {
    // 加入第一个暂态聊天室
    if (!$scope.transConvs[0]) {
      return console.warn('该应用还未创建聊天室，请前往 LeanCloud 控制台创建一个暂态对话（tr 为 true 的 Conversation）。');
    }
    return $scope.transConvs[0].join().then(conv => {
      $scope.joinedTransConv = conv;
    });
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
    $scope.$digest();
  };
  const invitedHandler = (payload, conversation) => {
    if (conversation.transient) {
      // 暂态对话
      if ($scope.transConvs.indexOf(conversation) === -1) {
        $scope.transConvs.push(conversation);
      }
    } else if ($scope.normalConvs.indexOf(conversation) === -1) {
      $scope.normalConvs.push(conversation);
    }
    $scope.$digest();
  };

  const client = $scope.imClient;
  client.on('message', messageHandler);
  client.on('invited', invitedHandler);
  client.on('unreadmessagescountupdate', () => {
    $scope.$emit('unreadCountUpdate', getTotalUnreadCount());
  });
  client.on('disconnect', () => {
    $scope.networkError = '连接已断开';
    $scope.networkErrorIcon = 'sync_problem';
    $scope.$digest();
  });
  client.on('offline', () => {
    $scope.networkError = '网络不可用，请检查网络设置';
    $scope.networkErrorIcon = 'signal_wifi_off';
    $scope.networkShowRetry = false;
    $scope.$digest();
  });
  client.on('online', () => {
    $scope.networkError = '网络已恢复';
    $scope.$digest();
  });
  client.on('schedule', (attempt, time) => {
    $scope.networkError = `${time / 1000}s 后进行第 ${attempt + 1} 次重连`;
    $scope.networkShowRetry = true;
    $scope.$digest();
  });
  client.on('retry', attempt => {
    $scope.networkError = `正在进行第 ${attempt + 1} 次重连`;
    $scope.networkErrorIcon = 'sync';
    $scope.networkShowRetry = false;
    $scope.$digest();
  });
  client.on('reconnect', () => {
    $scope.networkError = null;
    if ($scope.joinedTransConv) {
      $scope.joinedTransConv.join();
    }
    $scope.$digest();
  });
  client.on('reconnecterror', () => {
    $scope.networkError = '重连失败，请刷新页面重试';
    $scope.networkErrorIcon = 'error_outline';
    $scope.$digest();
  });

  $scope.$on("$destroy", () => {
    client.off('message', messageHandler);
    client.off('invited', invitedHandler);
    [
      'unreadmessagescountupdate',
      'disconnect',
      'offline',
      'online',
      'schedule',
      'retry',
      'reconnect',
      'reconnecterror'
    ].forEach(event => client.off(event));
  });

  $scope.retry = () => setTimeout(() => LeanRT.realtime.retry());
};
