import {MessageStatus, TextMessage, RecalledMessage} from 'leancloud-realtime';
import {ImageMessage} from 'leancloud-realtime-plugin-typed-messages';
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
      groupLastReadAt: '=',
      isGroup: '=',
      isMine: '=',
      onRecallClick: '&',
      onNameClick: '&'
    },
    link: $scope => {
      Object.assign($scope, {MessageStatus, TextMessage, ImageMessage, RecalledMessage});
      if (!$scope.message.type) {
        $scope.message.text = '[不支持的消息类型]';
      }
      // 显示时间
      if (!$scope.previousMessage) {
        $scope.displayTime = true;
      } else if ($scope.previousMessage.timestamp) {
        const thisMinute = Math.floor($scope.message.timestamp / 60000);
        const previousMinute = Math.floor($scope.previousMessage.timestamp / 60000);
        if (thisMinute !== previousMinute) {
          $scope.displayTime = true;
        }
      }
      // 显示 clientId
      if (!($scope.isMine || !$scope.displayTime && $scope.previousMessage.from === $scope.message.from)) {
        $scope.displayClientId = true;
      }
      if ($scope.isMine) {
        if ($scope.isGroup) {
          // 群聊已读标记
          $scope.unwatchGroupReceipts = $scope.$watch('groupLastReadAt', () => {
            if (!$scope.groupLastReadAt) {
              return;
            }
            const clients = Object.keys($scope.groupLastReadAt);
            const readClients = clients.filter(client => $scope.groupLastReadAt[client] >= $scope.message.timestamp);
            $scope.groupReadHint = `${readClients.length} 人已读`;
            $scope.groupReadClients = readClients.join(', ');
            if (readClients.length === clients.length) {
              $scope.groupReadHint = '全部已读';
              $scope.unwatchGroupReceipts();
            }
          }, true);
        } else {
          // 单聊已读、已送达标记
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
    }
  };
};
