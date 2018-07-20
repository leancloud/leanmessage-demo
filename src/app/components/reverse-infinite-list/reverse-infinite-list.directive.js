import $ from 'jquery';

export default $timeout => {
  'ngInject';

  return {
    transclude: true,
    scope: {
      infiniteScroll: '&',
      infiniteScrollDistance: '=?'
    },
    template: '<div ng-transclude></div>',
    link: ($scope, $element) => {
      if ($scope.infiniteScrollDistance === undefined) {
        $scope.infiniteScrollDistance = 200;
      }

      let loading = false;
      const container = $('.messages-wrapper');
      $element.on('scroll', () => {
        if (loading) {
          return;
        }

        const scrollElem = $($element[0]);
        if (scrollElem.scrollTop() < $scope.infiniteScrollDistance) {
          loading = true;
          const height = container.height();
          const result = $scope.infiniteScroll();
          (resortPosition => {
            if (result && typeof result.then === 'function') {
              result.then(resortPosition);
            } else {
              resortPosition();
            }
          })(() => $timeout(() => {
            scrollElem.scrollTop(scrollElem.scrollTop() + container.height() - height);
            loading = false;
          }, 0));
        }
      });
    }
  };
};
