import './conversation.message.scss';
import {Message, TextMessage} from 'leancloud-realtime';
import {ImageMessage} from 'leancloud-realtime-plugin-typed-messages';
import AV from 'leancloud-storage';
import {TypingStatusMessage} from '../../../typing-indicator';
import {smileys, stickers} from '../../../configs';
import StickerMessage from '../../../sticker-message';

const getMentionPrefix = (text, position) => {
  const result = text.slice(0, position).match(/(^|\s)@(\S*)$/);
  if (result) {
    return result[2].toLowerCase();
  }
  return null;
};

export default ($scope, LeanRT, $location, $timeout, $anchorScroll, $mdDialog, $mdSidenav, $stateParams, $element) => {
  'ngInject';

  const scrollToBottom = () => {
    $scope.$digest();
    $timeout(() => {
      $anchorScroll('message-view-bottom');
    }, 0);
  };

  const replaceRecalledMessage = recalledMessage => {
    $scope.messages = $scope.messages.map(message => (message.id === recalledMessage.id ? recalledMessage : message));
    $scope.$digest();
  };

  $scope.messages = [];
  $scope.imClient = LeanRT.imClient;
  $scope.hasLoadAllMessages = false;
  $scope.maxResultsAmount = 50;
  $scope.draft = '';

  $scope.smileysInited = false;
  $scope.smileysShow = false;
  $scope.toggleSmileysSelector = () => {
    if (!$scope.smileysInited) {
      $scope.smileysInited = true;
    }
    $scope.smileysShow = !$scope.smileysShow;
  };
  $scope.smileys = smileys;
  $scope.stickers = stickers;

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
      // 特殊情况：暂态对话的所有消息都是暂态的，因此暂态对话收到消息全部标记
      if (msg.transient && !conversation.transient) {
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
    conversation.on('messagerecall', replaceRecalledMessage);
    document.addEventListener("visibilitychange", handleVisibilityChange);

    $scope.$on("$destroy", () => {
      conversation.off('membersjoined', membersJoinedHandler);
      conversation.off('message', readMarker);
      conversation.off('message', messageUpdater);
      conversation.off('lastdeliveredatupdate', receiptUpdateHandler);
      conversation.off('lastreadatupdate', receiptUpdateHandler);
      conversation.off('lastreadtimestampsupdate', receiptUpdateHandler);
      conversation.off('messagerecall', replaceRecalledMessage);
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

  $scope.send = message => {
    console.log(message);
    return $scope.getCurrentConversation
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
        console.error(err);
      });
  };

  $scope.sendText = () => {
    const {draft} = $scope;
    if (!draft) {
      return;
    }
    const message = new TextMessage(draft);
    // 匹配出所有的 @
    const results = draft.match(/(^|\s)@(\S*)/g);
    if (results) {
      const mentionMatchResults = new Set(results.map(match => match.trim().slice(1)));
      console.log(mentionMatchResults);
      // 找出 @all
      if (mentionMatchResults.has('all')) {
        message.mentionAll();
        mentionMatchResults.delete('all');
      }
      message.setMentionList(Array.from(mentionMatchResults));
    }
    $scope.draft = '';
    $scope.smileysShow = false;

    return $scope.send(message);
  };

  $scope.sendImage = event => {
    const files = [...event.target.files];
    event.target.form.reset(); // 否则无法连续发送相同的图片
    return Promise.all(files.map(file =>
      new AV.File(file.name, file).save().then(savedFile => $scope.send(new ImageMessage(savedFile)))
    )).catch(console.error);
  };

  $scope.sendStickers = (groupName, stickerName) => {
    const message = new StickerMessage(groupName, stickerName);
    return $scope.send(message).catch(console.error);
  };

  $scope.insertSmiley = smiley => {
    const textarea = $element.find('textarea')[0];
    const currentPosition = textarea.selectionStart;
    $scope.draft = $scope.draft.slice(0, currentPosition) + smiley + $scope.draft.slice(textarea.selectionEnd);
    $timeout(() => {
      const newPosition = currentPosition + smiley.length;
      textarea.setSelectionRange(newPosition, newPosition);
    }, 1);
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

  const MAX_SUGGESTION = 5;
  $scope.updateMentionSuggestion = name => {
    $scope.getCurrentConversation.then(conversation => {
      const clients = conversation.transient ?
        $scope.messages.map(message => message.from) :
        conversation.members;
      const set = new Set(clients);
      set.add('all');
      set.delete($scope.imClient.id);
      return set;
    }).then(clients => {
      const matchedClients = [];
      let matchedExactly = [];
      clients.forEach(client => {
        const index = client.indexOf(name);
        if (index === 0) {
          if (client.length === name.length) {
            matchedExactly = [name];
            return;
          }
          return matchedClients.push(client);
        } else if (index > 0 && matchedExactly.length + matchedClients.length < MAX_SUGGESTION) {
          matchedClients.unshift(client);
        }
      });
      return [...matchedClients, ...matchedExactly].slice(-MAX_SUGGESTION);
    }).then(matchedClients => {
      $scope.matchedClients = matchedClients;
      $scope.suggestionActive = matchedClients.length - 1;
      $scope.$digest();
    });
  };
  $scope.hideMentionSuggestion = () => {
    $scope.matchedClients = [];
  };
  let _prefix = null;
  $scope.replaceSelectedMentionedClient = (index = $scope.suggestionActive) => {
    const textarea = $element.find('textarea')[0];
    const currentPosition = textarea.selectionStart;
    const arr = $scope.draft.slice(0, currentPosition).split('@');
    arr.pop();
    arr.push(`${$scope.matchedClients[index]} ${$scope.draft.slice(currentPosition, $scope.draft.length)}`);
    $scope.draft = arr.join('@');
    $timeout(() => {
      const newPosition = currentPosition - _prefix.length + $scope.matchedClients[index].length + 1;
      textarea.setSelectionRange(newPosition, newPosition);
    }, 1);
  };
  $scope.editorCursorPositionCheck = event => {
    const prefix = getMentionPrefix($scope.draft, event.target.selectionStart);
    if (prefix === _prefix) {
      return;
    }
    _prefix = prefix;
    if (prefix === null) {
      $scope.hideMentionSuggestion();
    } else {
      $scope.updateMentionSuggestion(prefix);
    }
  };
  $scope.editorKeydownHandler = event => {
    const {matchedClients} = $scope;
    if (matchedClients && matchedClients.length) { // 提及补全模式
      const {keyCode} = event;
      if (keyCode === 38 || keyCode === 40) {
        event.preventDefault();
        $scope.suggestionActive = (matchedClients.length + $scope.suggestionActive + keyCode - 39) % matchedClients.length;
        return false;
      }
      if (event.keyCode === 27) {
        return $scope.hideMentionSuggestion();
      }
    }
  };

  $scope.editorChangedHandler = event => {
    console.log($scope.draft);
    if ($scope.draft) {
      $scope.typingIndicator.updateStatus(TypingStatusMessage.STATUS.TYPING);
    } else {
      $scope.typingIndicator.updateStatus(TypingStatusMessage.STATUS.FINISHED);
    }
    if (event.keyCode === 13) {
      if ($scope.matchedClients && $scope.matchedClients.length) { // 提及补全模式
        $scope.replaceSelectedMentionedClient();
        event.preventDefault();
        return false;
      }
      if (!event.shiftKey) {
        $scope.sendText();
        event.preventDefault();
        return false;
      }
    }
  };

  $scope.recall = message => {
    console.log('recall', message);
    return $scope.getCurrentConversation
      .then(conversation => conversation.recall(message))
      .then(recalledMessage => {
        console.log(recalledMessage);
        replaceRecalledMessage(recalledMessage);
      }).catch(err => {
        console.error(err);
      });
  };
};
