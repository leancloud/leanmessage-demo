import {MessageStatus} from 'leancloud-realtime';
import './message.scss';

export default () => {
  'ngInject';

  return {
    restrict: 'E',
    templateUrl: 'app/components/message/message.html',
    scope: {
      message: '=',
      previousMessage: '=',
      isMine: '=',
      onNameClick: '&'
    },
    link: $scope => {
      $scope.messageStatus = MessageStatus;
      if ($scope.previousMessage && $scope.previousMessage) {
        const thisMinute = Math.floor($scope.message.timestamp / 60000);
        const previousMinute = Math.floor($scope.previousMessage.timestamp / 60000);
        if (thisMinute !== previousMinute) {
          $scope.displayTime = true;
        }
      }
    }
  };
};
