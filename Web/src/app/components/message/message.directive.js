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
      nextMessage: '=',
      lastDeliveredAt: '=',
      lastReadAt: '=',
      isMine: '=',
      onNameClick: '&'
    },
    link: $scope => {
      $scope.messageStatus = MessageStatus;
      if (!$scope.previousMessage) {
        $scope.displayTime = true;
      } else if ($scope.previousMessage.timestamp) {
        const thisMinute = Math.floor($scope.message.timestamp / 60000);
        const previousMinute = Math.floor($scope.previousMessage.timestamp / 60000);
        if (thisMinute !== previousMinute) {
          $scope.displayTime = true;
        }
      }
      if ($scope.isMine) {
        $scope.unwatch = $scope.$watchGroup(['lastReadAt', 'lastDeliveredAt'], () => {
          if ($scope.lastReadAt) {
            if (!$scope.nextMessage) {
              if ($scope.message.timestamp <= $scope.lastReadAt) {
                $scope.isLastRead = true;
              }
            } else if ($scope.nextMessage.timestamp) {
              if ($scope.nextMessage.timestamp > $scope.lastReadAt) {
                if ($scope.message.timestamp <= $scope.lastReadAt) {
                  $scope.isLastRead = true;
                }
              } else {
                $scope.isLastDelivered = $scope.isLastRead = false;
                return $scope.unwatch();
              }
            }
          }
          if ($scope.isLastRead) {
            $scope.isLastDelivered = false;
            return;
          }
          if ($scope.lastDeliveredAt) {
            if (!$scope.nextMessage) {
              if ($scope.message.timestamp <= $scope.lastDeliveredAt) {
                $scope.isLastDelivered = true;
              }
            } else if ($scope.nextMessage.timestamp) {
              if ($scope.nextMessage.timestamp > $scope.lastDeliveredAt) {
                if ($scope.message.timestamp <= $scope.lastDeliveredAt) {
                  $scope.isLastDelivered = true;
                }
              } else {
                $scope.isLastDelivered = false;
              }
            }
          }
        });
      }
    }
  };
};
