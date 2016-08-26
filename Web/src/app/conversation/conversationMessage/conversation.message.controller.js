import './conversation.message.scss';
import {_} from 'underscore' ;
import {TextMessage} from 'leancloud-realtime';

export default ($scope, LeanRT, $location, $anchorScroll, $mdDialog, $stateParams) => {
  'ngInject';

  $scope.messages = [];
  $scope.imClient = LeanRT.imClient;
  $scope.messageIterator = $scope.currentConversation.createMessagesIterator({ limit: 20 });
  $scope.hasLoadAllMessages = false;
  $scope.maxResultsAmount = 50;
  $scope.draft = '';

  const scrollToBottom = () => {
    setTimeout(() => {
      $anchorScroll('message-view-bottom')
    }, 0);
  };

  $scope.send = () => {
    if ($scope.draft) {
      const message = new TextMessage($scope.draft);
      $scope.messages.push(message);
      $scope.currentConversation.send(message)
      .then(msg => {
        $scope.$digest();
        scrollToBottom();
        $scope.draft = '';
      }).catch(err => {
        console.log(err);
      });
    }

  };

  $scope.toSingleConv = (clientId) => {
    $scope.imClient.createConversation({
      members: [clientId],
      name: `${clientId} 和 ${$scope.imClient.id} 的对话`,
      transient: false,
      unique: true
    }).then(conv => {
      // 此时 onInvited 会被调用, 在下方 onInvited 中更新 conversation list
    }).catch(console.error.bind(console));
  };

  $scope.loadMoreMessages = () => {
    if (!$scope.hasLoadAllMessages) {
      return $scope.messageIterator.next().then(result => {
        if (!result.done) {
          $scope.messages = result.value.concat($scope.messages);
          $scope.$digest();
        } else {
          $scope.hasLoadAllMessages = true;
        }
      });
    } else {
      return;
    }
    return;
  };

  $scope.showAddUserDialog = (ev) => {
    const confirm = $mdDialog.prompt()
      .title('请输入要邀请的成员的 ClientId')
      .placeholder('ClientId')
      .ariaLabel('ClientId')
      .targetEvent(ev)
      .ok('邀请他进入对话')
      .cancel('取消');
    $mdDialog.show(confirm).then(clientId => {
      // 添加其他成员
      return $scope.currentConversation.add([clientId]);
    }).then(() => {
      // 添加成功, 在 membersjoined 事件中更新 UI
    }).catch(err => {
      if (err) {
        console.log(err);
      }
    });
  };

  $scope.currentConversation.on('membersjoined', ((payload) => {
    $scope.messages.push({
      text: `${payload.invitedBy} 邀请 ${payload.members} 进入该对话`,
      timestamp: new Date()
    });
    $scope.$digest();
  }));

  $scope.imClient.on('message', (msg, conv) => {
    // 更新消息界面
    if (conv === $scope.currentConversation) {
      // 当前对话标记为已读
      $scope.currentConversation.markAsRead();
      // 消息列表滚动
      $scope.messages.push(msg);
      scrollToBottom();
    }
    // 更新左侧对话列表
    // 暂态对话
    if (conv.transient && $scope.transConvs.indexOf(conv) === 1) {
      $scope.transConvs.push(conv);
    }
    // TODO: 暂时无法判断系统对话, 目前需求上也只需要一个系统对话, 因此跳过更新系统对话列表的逻辑

    // 普通对话
    if (!conv.transient && $scope.normalConvs.indexOf(conv) === -1) {
      $scope.normalConvs.push(conv);
    }
    $scope.$apply();
  });

  $scope.imClient.on('invited', (payload, conversation) => {
    if (conversation.transient) {
      // 暂态对话
      $scope.transConvs.push(conversation);
    } else {
      // 普通对话
      let isNewConv = true;
      $scope.normalConvs.forEach((c) => {
        if (c.id === conversation.id) {
          isNewConv = false;
        }
      });
      if (isNewConv) {
        $scope.normalConvs.push(conversation);
      }
    }
    $scope.$apply();
  });

  // 通过 url 切换 conversationId
  if ($stateParams.convId === $scope.currentConversation.id) {
    $scope.currentConversation = LeanRT.currentConversation;
  } else {
    const sysConv = _.find($scope.sysConvs, conv => {return conv.id === $stateParams.convId});
    const transConv = _.find($scope.transConvs, conv => {return conv.id === $stateParams.convId});
    const normalConv = _.find($scope.normalConvs, conv => {return conv.id === $stateParams.convId});

    const  currentConversation = sysConv || transConv || normalConv;

    if (currentConversation) {
      $scope.changeTo(currentConversation);
    }
  }


  // 刚进入页面时, 展示最近 20 条消息
  $scope.messageIterator.next().then(result => {
    $scope.messages = result.value.concat($scope.messages);
    $scope.$digest();
    scrollToBottom();
    if (result.done) {
      $scope.hasLoadAllMessages = true;
    }
  }).catch(console.error.bind(console));
}