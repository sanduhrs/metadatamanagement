'use strict';

angular.module('metadatamanagementApp').directive('activeMenu',
    function($translate) {
      return {
        restrict: 'A',
        link: function(scope, element, attrs) {
          var language = attrs.activeMenu;

          scope.$watch(function() {
            return $translate.use();
          }, function(selectedLanguage) {
            if (language === selectedLanguage) {
              element.addClass('active');
            } else {
              element.removeClass('active');
            }
          });
        }
      };
    }).directive('activeLink', function(location) {
  return {
    restrict: 'A',
    link: function(scope, element, attrs) {
      var clazz = attrs.activeLink;
      var path = attrs.href;
      // hack because path does bot return including hashbang
      path = path.substring(1);
      scope.location = location;
      scope.$watch('location.path()', function(newPath) {
        if (path === newPath) {
          element.addClass(clazz);
        } else {
          element.removeClass(clazz);
        }
      });
    }
  };
});
