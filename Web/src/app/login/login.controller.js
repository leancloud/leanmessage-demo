class LoginController {
  constructor($state, user, md5) {
    'ngInject';

    this.$state = $state;
    this.userService = user;
    this.md5 = md5;
    this.user = {};
  }

  login() {
    this.userService.login(this.user.id, this.md5(this.user.email || (Math.random() + Date.now())))
      .then(() => this.$state.go('conversation.message'));
  }
}

export default LoginController;
