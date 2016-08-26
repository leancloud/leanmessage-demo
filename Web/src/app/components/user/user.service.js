export default (LeanRT) => {
  'ngInject';
  
  return {
    isCached: () => {
      try {
        return localStorage.getItem('clientId') !== null;
      } catch (e) {
        return false;
      }
    },
    cache: (clientId) => {
      localStorage.setItem('clientId', clientId);
    },
    getCachedInfo: () => {
      try {
        return localStorage.getItem('clientId');
      } catch (e) {
        return undefined;
      }
    },
    login: function (clientId) {
      return LeanRT.realtime.createIMClient(clientId);
    },
    isLoggedin: function () {
      if (LeanRT.imClient) {
        return true;
      } else {
        return false;
      }
    },
    logout: function() {
      localStorage.removeItem('clientId');
      return LeanRT.imClient.close();
    }
  };
}
