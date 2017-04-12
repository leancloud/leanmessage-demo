import './conversation.message.scss';
import {Message, TextMessage} from 'leancloud-realtime';
import {TypingStatusMessage} from '../../../typing-indicator';

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
    $scope.typingIndicator = $scope.$parent.typingIndicator;
    $scope.typingIndicator.setConversation(conversation);

    $scope.typingIndicator.on('change', () => {
      if ($scope.typingIndicator.typingClients.length) {
        $scope.typingClients = `${$scope.typingIndicator.typingClients.join(', ')} 正在输入`;
      } else {
        $scope.typingClients = null;
      }
      $scope.$digest();
    });

    const membersJoinedHandler = payload => {
      $scope.messages.push({
        type: Symbol('system'),
        text: `${payload.invitedBy} 邀请 ${payload.members} 进入该对话`,
        timestamp: new Date()
      });
      $scope.$digest();
    };

    const readMarker = msg => {
      // 暂态消息不标记
      if (msg.transient) {
        return;
      }
      // 当前 tab 未激活不标记
      if (document.hidden) {
        return;
      }
      // 当前对话标记为已读
      conversation.read();
    };
    const messageUpdater = msg => {
      // 如果收到未知类型的暂态消息，直接丢弃
      if (msg.transient && msg.type === Message.TYPE) {
        return;
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
    conversation.on('message', readMarker);
    conversation.on('message', messageUpdater);
    conversation.on('lastdeliveredatupdate', receiptUpdateHandler);
    conversation.on('lastreadatupdate', receiptUpdateHandler);
    conversation.on('lastreadtimestampsupdate', receiptUpdateHandler);
    document.addEventListener("visibilitychange", handleVisibilityChange);

    $scope.$on("$destroy", () => {
      conversation.off('membersjoined', membersJoinedHandler);
      conversation.off('message', readMarker);
      conversation.off('message', messageUpdater);
      conversation.off('lastdeliveredatupdate', receiptUpdateHandler);
      conversation.off('lastreadatupdate', receiptUpdateHandler);
      conversation.off('lastreadtimestampsupdate', receiptUpdateHandler);
      $scope.typingIndicator.off('change');
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
        $scope.typingIndicator.updateStatus(0);
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
    console.log($scope.draft);
    if ($scope.draft) {
      $scope.typingIndicator.updateStatus(TypingStatusMessage.STATUS.TYPING);
    } else {
      $scope.typingIndicator.updateStatus(TypingStatusMessage.STATUS.FINISHED);
    }
    if (event.keyCode === 13 && !event.shiftKey) {
      $scope.send();
      event.preventDefault();
      return false;
    }
  };
};
