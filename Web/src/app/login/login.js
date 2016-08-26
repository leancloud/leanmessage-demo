import angular from 'angular';
import 'angular-mocks';
import {loginController} from './login/loginController';

describe('hello component', () => {
  beforeEach(() => {
    angular
      .module('fountainHello', ['app/login.html'])
      .component('fountainHello', loginController);
    angular.mock.module('fountainHello');
  });
  it('should render hello world', angular.mock.inject(($rootScope, $compile) => {
    const element = $compile('<fountain-hello>Loading...</fountain-hello>')($rootScope);
    $rootScope.$digest();
    const h1 = element.find('h1');
    expect(h1.html()).toEqual('Hello World!');
  }));
});
