'use strict';

angular.module('metadatamanagementApp').controller(
  'ToolbarController',
  function($scope, $rootScope, $mdSidenav, ShoppingCartService, Principal,
           SearchResultNavigatorService, LanguageService, Auth, $state,
           MessageBus, $location) {
    $scope.open = $mdSidenav('SideNavBar').isOpen();
    //Toggle Function
    $scope.toggleLeft = function() {
      $mdSidenav('SideNavBar').toggle();
    };
    $scope.isAuthenticated = Principal.isAuthenticated;
    $scope.hasAuthority = Principal.hasAuthority;

    //Set Languages
    $scope.changeLanguage = function(languageKey) {
      LanguageService.setCurrent(languageKey);
    };

    //Goto Logout Page
    $scope.logout = function() {
      Auth.logout();
      $state.go('search', {
        lang: LanguageService.getCurrentInstantly(),
        type: 'studies'
      }, {
        reload: true
      });
    };
    $scope.resetQuery = function() {
      $rootScope.searchQuery = '';
      var searchParams = $location.search();
      if (searchParams && searchParams.hasOwnProperty('query')) {
        delete searchParams.query;
        $location.search(searchParams);
      }
      MessageBus.remove('searchFilter');
    };
    $scope.resetFilter = function() {
      var search = $location.search();
      var params = {};
      params.type = search.type;
      params.page = 1;
      params.size = search.size;
      $location.search(params);
      MessageBus.remove('searchFilter');
    };
    $scope.productsCount = ShoppingCartService.count();
    $scope.$on('shopping-cart-changed',
      function(event, count) { // jshint ignore:line
        $scope.productsCount = count;
      });

    $scope.SearchResultNavigatorService = SearchResultNavigatorService;
    $scope.$watch(function() {
      return $mdSidenav('SideNavBar').isOpen();
    }, function() {
      $scope.open = $mdSidenav('SideNavBar').isOpen();
    });
  });
