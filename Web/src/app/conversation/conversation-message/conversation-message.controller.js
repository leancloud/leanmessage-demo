class ConversationMessageController {
  constructor($mdSidenav, user, $state, $scope, $q, rt, LCTextMessage, conversationCache, defaultConversation, $timeout, $anchorScroll) {
    'ngInject';

    this.$mdSidenav = $mdSidenav;
    this.user = user;
    this.$state = $state;
    this.$timeout = $timeout;
    this.$anchorScroll = $anchorScroll;
    this.$scope = $scope;

    this.LCTextMessage = LCTextMessage;

    this.queryString = '';
    this.queryClients = [];

    this.defaultConversation = defaultConversation;
    this.maxResultsAmount = 64;

    var conversationRoute = $state.params.clientId;
    this.isGroupConversation = false;
    this.messages = [];

    var conversationPromise, conversationId;

    if (conversationRoute[0] === '@' && conversationRoute.length > 1) {
      // 单聊
      var clientId = conversationRoute.slice(1);
      conversationId = conversationCache.getConversationId(clientId);

      if (conversationId === null) {
        // 新建一个对话
        conversationPromise = rt.conv({
          members: [clientId]
        });
        // 写入 cache
        conversationPromise.then((conv) => conversationCache.setConversationId(clientId, conv.id));
      } else {
        // 获取一个已存在的对话
        conversationPromise = rt.conv(conversationId);
      }
    } else if (conversationRoute.match(/^[0-9a-f]{24}$/)) {
      // 群聊
      this.isGroupConversation = true;
      conversationId = conversationRoute;
      conversationPromise = rt.conv(conversationId);
    } else if (conversationRoute === '') {
      this.changeTo(defaultConversation.id, {
        location: 'replace'
      });
    } else {
      conversationPromise = $q.reject(new Error('404: Illegal id in URI'));
    }

    if (conversationPromise) {
      // 将 conversation 与当前 controller 绑定
      conversationPromise.then((conv) => {
        this.conv = conv;
        conv.log().then((messages) => {
          this.messages = messages.concat(this.messages);
          this.scrollToBottom();
        });
        conv.on('message', (message) => {
          this.messages.push(message);
          this.scrollToBottom();
        });
        $scope.$on('$destroy', () => conv.destroy());
        $scope.$emit('conv.created', conv);
      }.bind(this)).catch((e) => {
        // 将异常信息显示在页面上
        this.conv = {
          name: '👻' + e.message
        };
      }.bind(this));
    }

    // 当有未读消息时，菜单按钮显示小红点
    $scope.$on('unreadMessageAdd', () => this.hasUnreadMessage = true);

  }

  send() {
    var message = new this.LCTextMessage(this.currentconversationMessage.draft);
    message._state = 'sending';
    this.messages.push(message);
    this.scrollToBottom();
    this.conv.send(message).then(
      (message) => message._state = 'sended', () => message._stats = 'failed'
    );
    this.$scope.$emit('conv.messagesent');
    this.currentconversationMessage.draft = '';
  }

  query(queryString) {
    var result = queryString ? this.conv.members.filter(this.createFilterFor(queryString)) : this.conv.members;
    this.queryClients = result;
  }
  createFilterFor(queryString) {
    var lowercaseQuery = angular.lowercase(queryString);
    return function filterFn(state) {
      return (state.indexOf(lowercaseQuery) !== -1);
    };
  }
  clearQuery() {
    this.queryString = '';
    this.query();
  }

  changeTo(clientId, options) {
    this.$state.go('conversation.message', {
      clientId: clientId
    }, options);
    this.closeAll();
  }

  toggle(id) {
    this.$mdSidenav(id).toggle();
  }
  close(id) {
    this.$mdSidenav(id).close();
  }
  closeAll() {
    ['online', 'online-search'].map((id) => this.close(id));
  }

  scrollToBottom() {
    return this.$timeout(() => this.$anchorScroll('message-view-bottom'), 0);
  }

  loadMore() {
    if (this.conv === undefined) {
      return;
    }
    return this.conv.log({
      t: this.messages.length ? this.messages[0].timestamp : null
    }, null).then((messages) => {
      this.messages = messages.concat(this.messages);
    });
  }

}

export default ConversationMessageController;
