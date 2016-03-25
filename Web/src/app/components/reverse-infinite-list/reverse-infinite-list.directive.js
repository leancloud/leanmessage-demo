function ReverseInfiniteListDirective($timeout) {
  'ngInject';

  return {
    transclude: true,
    scope: {
      infiniteScroll: '&',
      infiniteScrollDistance: '=?'
    },
    template: '<div ng-transclude></div>',
    link: (scope, elem) => {

      if (scope.infiniteScrollDistance === undefined) {
        scope.infiniteScrollDistance = 200;
      }

      var loading = false;
      var container = elem.find('>div');

      elem.on('scroll', () => {
        if (loading) {
          return;
        }
        if (elem.scrollTop() < scope.infiniteScrollDistance) {

          loading = true;
          var height = container.height();
          var result = scope.infiniteScroll();
          ((resortPosition) => {
            if (result && typeof result.then === 'function') {
              result.then(resortPosition);
            } else {
              resortPosition();
            }
          })(() => $timeout(() => {
            elem.scrollTop(elem.scrollTop() + container.height() - height);
            loading = false;
          }, 0));
        }
      });
    }
  };
}

export default ReverseInfiniteListDirective;
