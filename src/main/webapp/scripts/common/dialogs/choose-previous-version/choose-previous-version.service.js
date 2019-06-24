/* globals _ */
'use strict';

angular.module('metadatamanagementApp').service('ChoosePreviousVersionService',
    function($mdDialog) {

      var showDialog = function(dialogConfig, event) {
        var labels = {
          title: {
            key: 'choose-previous-version.title',
            params: {
              id: dialogConfig.domainId
            }
          },
          text: {
            key: 'choose-previous-version.text'
          },
          cancelTooltip: {
            key: 'choose-previous-version.cancel-tooltip'
          },
          noVersionsFound: {
            key: 'choose-previous-version.no-versions-found',
            params: {
              id: dialogConfig.domainId
            }
          }
        };

        var locals = {
          domainId: dialogConfig.domainId,
          getPreviousVersionsCallback: dialogConfig
              .getPreviousVersionsCallback,
          labels: labels
        };

        if (dialogConfig.labels) {
          locals.labels = _.extend(locals.labels, dialogConfig.labels);
        }
        return $mdDialog.show({
          controller: 'ChoosePreviousVersionController',
          templateUrl: 'scripts/common/dialogs/choose-previous-version/' +
              'choose-previous-version.html.tmpl',
          clickOutsideToClose: false,
          fullscreen: true,
          locals: locals,
          targetEvent: event
        });
      };

      return {
        showDialog: showDialog
      };
    });
