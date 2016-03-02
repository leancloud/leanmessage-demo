function config($mdThemingProvider, gravatarServiceProvider) {
  'ngInject';

  $mdThemingProvider.theme('default')
    .primaryPalette('blue')
    .accentPalette('grey');

  gravatarServiceProvider.defaults = {
    "default": 'retro'
  };
}

export default config;
