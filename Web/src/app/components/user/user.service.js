export default LeanRT => {
  'ngInject';

  return {
    isCached: () => {
      try {
        return localStorage.getItem('clientId') !== null;
      } catch (e) {
        return false;
      }
    },
    cache: clientId => {
      localStorage.setItem('clientId', clientId);
    },
    getCachedInfo: () => {
      try {
        return localStorage.getItem('clientId');
      } catch (e) {
        return undefined;
      }
    },
    login: clientId => {
      return LeanRT.realtime.createIMClient(clientId);
    },
    isLoggedin: () => {
      if (LeanRT.imClient) {
        return true;
      }
      return false;
    },
    logout: () => {
      localStorage.removeItem('clientId');
      return LeanRT.imClient.close();
    }
  };
};
