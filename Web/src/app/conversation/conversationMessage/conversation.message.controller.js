import './conversation.message.scss';
import {TextMessage} from 'leancloud-realtime';

export default ($scope, LeanRT, $location, $timeout, $anchorScroll, $mdDialog, $mdSidenav, $stateParams) => {
  'ngInject';

  const scrollToBottom = () => {
    $scope.$digest();
    $timeout(() => {
      $anchorScroll('message-view-bottom');
    }, 0);
  };

  $scope.messages = [];
  $scope.imClient = LeanRT.imClient;
  $scope.hasLoadAllMessages = false;
  $scope.maxResultsAmount = 50;
  $scope.draft = '';

  $scope.getCurrentConversation = LeanRT.imClient.getConversation($stateParams.convId).then(conversation => {
    $scope.$parent.currentConversation = conversation;
    $scope.messageIterator = conversation.createMessagesIterator({limit: 20});

    const membersJoinedHandler = payload => {
      $scope.messages.push({
        text: `${payload.invitedBy} 邀请 ${payload.members} 进入该对话`,
        timestamp: new Date()
      });
      $scope.$digest();
    };

    const messageHandler = msg => {
      if (!document.hidden) {
        // 当前对话标记为已读
        conversation.read();
      }
      // 消息列表滚动
      $scope.messages.push(msg);
      scrollToBottom();
    };

    const receiptUpdateHandler = () => {
      $scope.$digest();
    };

    const handleVisibilityChange = () => {
      if (!document.hidden && conversation.unreadMessagesCount) {
        conversation.read();
      }
    };

    conversation.on('membersjoined', membersJoinedHandler);
    conversation.on('message', messageHandler);
    conversation.on('lastdeliveredatupdate', receiptUpdateHandler);
    conversation.on('lastreadatupdate', receiptUpdateHandler);
    document.addEventListener("visibilitychange", handleVisibilityChange);

    $scope.$on("$destroy", () => {
      conversation.off('membersjoined', membersJoinedHandler);
      conversation.off('message', messageHandler);
      conversation.off('lastdeliveredatupdate', receiptUpdateHandler);
      conversation.off('lastreadatupdate', receiptUpdateHandler);
      document.removeEventListener("visibilitychange", handleVisibilityChange);
    });

    // 刚进入页面时, 展示最近 20 条消息
    $scope.loadMoreMessages().then(() => {
      scrollToBottom();
    });
    // 标记为已读
    conversation.read();

    return conversation;
  });

  $scope.send = () => {
    if ($scope.draft) {
      const message = new TextMessage($scope.draft);
      $scope.draft = '';
      $scope.getCurrentConversation
      .then(conversation => {
        const sendPromise = conversation.send(message, {
          receipt: conversation.members.length === 2
        });
        $scope.messages.push(message);
        return sendPromise;
      })
      .then(() => {
        scrollToBottom();
      }).catch(err => {
        console.log(err);
      });
    }
  };

  $scope.loadMoreMessages = () => {
    if ($scope.hasLoadAllMessages) {
      return;
    }
    return $scope.messageIterator.next().then(result => {
      if (result.done) {
        $scope.hasLoadAllMessages = true;
      }

      $scope.messages = result.value.concat($scope.messages);
      $scope.$digest();
    });
  };

  $scope.showAddUserDialog = ev => {
    const confirm = $mdDialog.prompt()
      .title('邀请成员加入会话')
      .placeholder('用户 ID')
      .ariaLabel('用户 ID')
      .targetEvent(ev)
      .ok('邀请')
      .cancel('取消');
    $mdDialog.show(confirm).then(clientId => {
      // 添加其他成员
      return $scope.getCurrentConversation.then(conversation => conversation.add([clientId]));
    }).then(() => {
      // 添加成功, 在 membersjoined 事件中更新 UI
    }).catch(err => {
      if (err) {
        console.log(err);
      }
    });
  };

  $scope.toggle = id => {
    $mdSidenav(id).toggle();
  };

  $scope.editorChangedHandler = event => {
    if (event.keyCode === 13 && !event.shiftKey) {
      $scope.send();
      event.preventDefault();
      return false;
    }
  };
};
