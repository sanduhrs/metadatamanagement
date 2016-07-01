/* Author: Daniel Katzberg */
'use strict';

angular.module('metadatamanagementApp').service('SearchToastService',
  function($mdToast) {

    //The Toast for no project is choosen
    var openNoProjectToast = function() {
      $mdToast.show({
        controller: 'SearchToastController',
        templateUrl: 'scripts/searchmanagement/' +
          'views/no-project-toast.html.tmpl',
        hideDelay: 10000,
        position: 'top right'
      });
    };

    //The Toast for the upload complete
    function openLogToast() {
      $mdToast.show({
        controller: 'SearchToastController',
        templateUrl: 'scripts/searchmanagement/' +
          'views/upload-complete-toast.html.tmpl',
        hideDelay: 0,
        position: 'top right'
      });
    }

    return {
      openNoProjectToast: openNoProjectToast,
      openLogToast: openLogToast
    };

  });