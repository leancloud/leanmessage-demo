class ConversationController {
  constructor($scope, $mdSidenav, user, $state, $mdToast, rt, conversationCache, defaultConversation) {
    'ngInject';

    this.$mdSidenav = $mdSidenav;
    this.userService = user;
    this.$state = $state;
    this.conversationCache = conversationCache;

    this.defaultConversation = defaultConversation;

    this.conversations = [];

    this.isMenuOpen = undefined;

    rt.getMyConvs().then((convs) => {
      this.conversations = convs;
      console.log(convs);
      // 每次重新连接都需要加入一次暂态的默认会话
      var joinDefaultConversationPromise = rt.conv(defaultConversation.id)
      if (convs.length === 0) {
        // 首次使用提示
        joinDefaultConversationPromise.then(
          (conv) => $mdToast.show(
            $mdToast.simple()
            .content(`欢迎使用 LeanMessage，自动加入默认群聊「${conv.name}」`)
            .position('top right')
          )
        );
      }
      joinDefaultConversationPromise.then(
        (conv) => this.conversations.push(conv)
      );
    });

    rt.on('message', (message) => {
      console.log(message);
      // 某个对话收到消息后更新该对话的 lastMessageTime 字段
      let conv = this.findFirstMatch(
        this.conversations,
        conv => conv.id === message.cid
      );
      if (conv) {
        if (conv.id !== this.currentConversation.id) {
          if (typeof conv.unreadMessagesCount !== 'number') {
            conv.unreadMessagesCount = 0;
          }
          conv.unreadMessagesCount++;
          $scope.$broadcast('unreadMessageAdd');
        }
      }
    });
    rt.on('invited', (data) => {
      console.log(data);

    });

    $scope.$on('conv.created', (event, conv) => {
      this.currentConversation = conv;

      let currentConv = this.findFirstMatch(
        this.conversations,
        conv => conv.id === this.currentConversation.id
      );
      if (currentConv) {
        currentConv.unreadMessagesCount = 0;
      }
      let totalUnreadMessageCount = this.conversations.reduce(
        (previous, conv) => previous + (conv.unreadMessagesCount || 0),
        0
      );
      if (totalUnreadMessageCount > 0) {
        $scope.$broadcast('unreadMessageAdd');
      }
    });
    $scope.$on('conv.messagesent', () => {
      let currentConv = this.findFirstMatch(
        this.conversations,
        conv => conv.id === this.currentConversation.id
      );
      if (currentConv) {
        currentConv.lastMessageTime = Date.now();
      }
    });

  }

  findFirstMatch(arr, check) {
    if (!arr) {
      return;
    }
    for (let i = 0, len = arr.length; i < len; i++) {
      if (check(arr[i], i)) {
        return arr[i];
      }
    }
  }

  getSingleConvTarget(members) {
    if (members[0] === this.userService.user.id) {
      return members[1];
    } else {
      return members[0];
    }
  }

  changeTo(clientId) {
    this.$state.go('conversation.message', {
      clientId: clientId
    });
    this.close('menu');
  }

  toggle(id) {
    this.$mdSidenav(id).toggle();
  }
  close(id) {
    this.$mdSidenav(id).close();
  }

  logout() {
    this.conversationCache.clearAll();
    this.userService.logout();
    this.$state.go('login');
  }

}

export default ConversationController;
