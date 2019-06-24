'use strict';

angular.module('metadatamanagementApp').directive('conceptSearchResult',
  function() {
    return {
      restrict: 'E',
      templateUrl: 'scripts/searchmanagement/directives/' +
        'concept-search-result.html.tmpl',
      scope: {
        searchResult: '=',
        currentLanguage: '=',
        bowser: '=',
        searchResultIndex: '='
      },
      controller: function($scope, CommonDialogsService, ConceptResource,
        ElasticSearchAdminService, $rootScope, SimpleMessageToastService,
        $q) {

        var showGenericErrorMessage = function(status) {
          SimpleMessageToastService
            .openAlertMessageToast('global.error.server-error',
              {status: status});
        };

        var handleError = function(data) {
          if (data.status === 400) {
            var errorMessages = [];
            data.data.errors.forEach(function(error) {
              if (error.message
                  .match(/^concept-management\.error\.concept\.in-use\..*/)) {
                errorMessages.push({messageId: error.message,
                  messageParams: {ids: error.invalidValue.join(', ')}});
              }
            });

            if (errorMessages.length > 0) {
              SimpleMessageToastService.openAlertMessageToasts(errorMessages);
            } else {
              showGenericErrorMessage(data.status);
            }
          } else {
            showGenericErrorMessage(data.status);
          }
        };

        $scope.deleteConcept = function(conceptId) {
          CommonDialogsService.showConfirmDeletionDialog({
            type: 'concept',
            id: conceptId
          }).then(function() {
            return ConceptResource.delete({id: conceptId}).$promise;
          }).then(function() {
            return $q.all([
              ElasticSearchAdminService.
                processUpdateQueue('concepts')
            ]).promise;
          }).then(function() {
            $rootScope.$broadcast('deletion-completed');
            SimpleMessageToastService.openSimpleMessageToast(
              'concept-management.edit.concept-deleted-toast',
              {id: conceptId});
          }, handleError);
        };
      }
    };
  });
