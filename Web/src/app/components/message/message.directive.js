function MessageDirective() {
  'ngInject';

  return {
    restrict: 'E',
    scope: {
      message: '=',
      previousMessage: '=',
      isMine: '=',
      onNameClick: '&'
    },
    templateUrl: 'app/components/message/message.html',
    controller: MessageController,
    controllerAs: 'vm',
    bindToController: true
  };
}

class MessageController {
  constructor() {
    'ngInject';
    if (this.previousMessage && this.previousMessage) {
      var thisMinute = Math.floor(this.message.timestamp / 60000);
      var previousMinute = Math.floor(this.previousMessage.timestamp / 60000);
      if (thisMinute !== previousMinute) {
          this.displayTime = true;
        }
      }
    }
  }

  export default MessageDirective;
