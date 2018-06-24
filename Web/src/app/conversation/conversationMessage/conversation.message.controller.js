import './conversation.message.scss';
import {Message, TextMessage, Event, ConversationMemberRole} from 'leancloud-realtime';
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

const system = Symbol('system');

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
  $scope.memberInfos = [];
  $scope.memberInfoError = null;
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
        type: system,
        text: `${payload.invitedBy} 邀请 ${payload.members} 加入对话`,
        timestamp: new Date()
      });
      $scope.refreshMemberInfo();
      $scope.$digest();
    };

    const membersLeftHandler = payload => {
      $scope.messages.push({
        type: system,
        text: `${payload.kickedBy} 将 ${payload.members} 移出对话`,
        timestamp: new Date()
      });
      $scope.refreshMemberInfo();
      $scope.$digest();
    };

    const kickedHandler = payload => {
      $scope.messages.push({
        type: system,
        text: `${payload.kickedBy} 将你移出对话`,
        timestamp: new Date()
      });
      $scope.refreshMemberInfo();
      $scope.$digest();
    };

    const memberInfoUpdateHandler = ({
      member,
      memberInfo,
      updatedBy
    }) => {
      $scope.messages.push({
        type: system,
        text: `${updatedBy} 将 ${member} 设为 ${memberInfo.role}`,
        timestamp: new Date()
      });
      $scope.refreshMemberInfo();
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

    conversation.on(Event.MEMBERS_JOINED, membersJoinedHandler);
    conversation.on(Event.MEMBERS_LEFT, membersLeftHandler);
    conversation.on(Event.KICKED, kickedHandler);
    conversation.on(Event.MEMBER_INFO_UPDATED, memberInfoUpdateHandler);
    conversation.on(Event.MESSAGE, readMarker);
    conversation.on(Event.MESSAGE, messageUpdater);
    conversation.on(Event.LAST_DELIVERED_AT_UPDATE, receiptUpdateHandler);
    conversation.on(Event.LAST_READ_AT_UPDATE, receiptUpdateHandler);
    conversation.on('lastreadtimestampsupdate', receiptUpdateHandler);
    conversation.on(Event.MESSAGE_RECALL, replaceRecalledMessage);
    document.addEventListener("visibilitychange", handleVisibilityChange);

    $scope.$on("$destroy", () => {
      conversation.off(Event.MEMBERS_JOINED, membersJoinedHandler);
      conversation.off(Event.MEMBERS_LEFT, membersLeftHandler);
      conversation.off(Event.KICKED, kickedHandler);
      conversation.off(Event.MEMBER_INFO_UPDATED, memberInfoUpdateHandler);
      conversation.off(Event.MESSAGE, readMarker);
      conversation.off(Event.MESSAGE, messageUpdater);
      conversation.off(Event.LAST_DELIVERED_AT_UPDATE, receiptUpdateHandler);
      conversation.off(Event.LAST_READ_AT_UPDATE, receiptUpdateHandler);
      conversation.off('lastreadtimestampsupdate', receiptUpdateHandler);
      conversation.off(Event.MESSAGE_RECALL, replaceRecalledMessage);
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
      textarea.focus();
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
      .required(true)
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

  $scope.remove = id => $scope.getCurrentConversation
      .then(conversation => conversation.remove([id]))
      .catch(err => {
        if (err) {
          console.log(err);
        }
      });

  $scope.toggle = id => {
    $mdSidenav(id).toggle();
  };

  $scope.MemberRole = ConversationMemberRole;
  $scope.refreshMemberInfo = () =>
    $scope.getCurrentConversation
      .then(conversation => conversation.getAllMemberInfo()
        .then(memberInfos => {
          $scope.memberInfos = memberInfos;
          return conversation.getMemberInfo($scope.imClient.id).then(info => {
            $scope.currentClientInfo = info;
          });
        }))
      .catch(e => {
        console.error(e);
        $scope.memberInfoError = e;
      }).then(() => $scope.$digest());

  $scope.showMembersList = () => {
    $mdSidenav('online').open();
    $scope.getCurrentConversation.then(conversation => {
      // 应用未开启「成员角色管理」功能，不再尝试获取。
      if ($scope.memberInfoError && $scope.memberInfoError.code === 119) {
        return;
      }
      // 在首次查询到成员信息之前，用 mock 的 MemberInfo 显示用户 id
      $scope.memberInfos = $scope.memberInfos.length ? $scope.memberInfos : conversation.members.map(memberId => ({
        memberId
      }));
      return $scope.refreshMemberInfo();
    });
  };

  $scope.isManager = memberInfo => memberInfo && memberInfo.role === ConversationMemberRole.MANAGER;
  $scope.isAuthed = memberInfo => memberInfo && memberInfo.isOwner || $scope.isManager(memberInfo);

  $scope.updateRole = (id, role) => {
    return $scope.$parent.currentConversation.updateMemberRole(id, role).then(() => $scope.$digest());
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
