/* global _, $, document */
'use strict';

angular.module('metadatamanagementApp')
  .controller('StudyEditOrCreateController',
    function(entity, PageTitleService, $document, $timeout,
      $state, BreadcrumbService, Principal, SimpleMessageToastService,
      CurrentProjectService, StudyIdBuilderService, StudyResource, $scope,
      ElasticSearchAdminService, $transitions,
      CommonDialogsService, LanguageService, StudySearchService,
      StudyAttachmentResource, $q, CleanJSObjectService,
      DataAcquisitionProjectResource, ProjectUpdateAccessService,
      AttachmentDialogService, StudyAttachmentUploadService,
      StudyAttachmentVersionsResource, ChoosePreviousVersionService,
      StudyVersionsResource) {

      var ctrl = this;
      var studySeriesCache = {};
      var sponsorsCache = {};
      ctrl.currentInstitutions = [];
      var attachmentTypes = [
        {de: 'Daten- und Methodenbericht', en: 'Method Report'},
        {de: 'Sonstiges', en: 'Other'}
      ];

      var getDialogLabels = function() {
        return {
          createTitle: {
            key: 'study-management.detail.attachments.create-title',
            params: {
              studyId: ctrl.study.id
            }
          },
          editTitle: {
            key: 'study-management.detail.attachments.edit-title',
            params: {
              studyId: ctrl.study.id
            }
          },
          hints: {
            file: {
              key: 'study-management.detail.attachments.hints.filename'
            }
          }
        };
      };

      ctrl.findTags = StudySearchService.findTags;

      $scope.$watch('ctrl.study.studySeries', function() {
        ctrl.onStudySeriesChanged();
      }, true);

      var updateToolbarHeaderAndPageTitle = function() {
        if (ctrl.createMode) {
          PageTitleService.setPageTitle(
            'study-management.edit.create-page-title', {
              studyId: ctrl.study.id
            });
        } else {
          PageTitleService.setPageTitle(
            'study-management.edit.edit-page-title', {
              studyId: ctrl.study.id
            });
        }
        BreadcrumbService.updateToolbarHeader({
          'stateName': $state.current.name,
          'id': ctrl.study.id,
          'studyId': ctrl.study.id,
          'studyIsPresent': !ctrl.createMode,
          'projectId': ctrl.study.dataAcquisitionProjectId,
          'enableLastItem': true
        });
      };

      var redirectToSearchView = function() {
        $timeout(function() {
          $state.go('search', {
            lang: LanguageService.getCurrentInstantly(),
            type: 'studies'
          });
        }, 1000);
      };

      var handleReleasedProject = function() {
        SimpleMessageToastService.openAlertMessageToast(
          'study-management.edit.choose-unreleased-project-toast');
        redirectToSearchView();
      };

      var initEditMode = function(study) {
        ctrl.createMode = false;
        ctrl.isInitializingStudySeries = true;
        DataAcquisitionProjectResource.get({
          id: study.dataAcquisitionProjectId
        }).$promise.then(function(project) {
          if (project.release != null) {
            handleReleasedProject();
          } else if (!ProjectUpdateAccessService
              .isUpdateAllowed(project, 'studies', true)) {
            redirectToSearchView();
          } else {
            CurrentProjectService.setCurrentProject(project);
            ctrl.study = study;
            ctrl.currentStudySeries = study.studySeries;
            ctrl.currentSponsor = study.sponsor;
            ctrl.currentInstitutions = angular.copy(ctrl.study.institutions);
            ctrl.loadAttachments();
            updateToolbarHeaderAndPageTitle();
            $scope.registerConfirmOnDirtyHook();
          }
        });
      };

      var init = function() {
        if (Principal
            .hasAnyAuthority(['ROLE_PUBLISHER', 'ROLE_DATA_PROVIDER'])) {
          if (entity) {
            entity.$promise.then(function(study) {
              initEditMode(study);
            });
          } else {
            if (CurrentProjectService.getCurrentProject() &&
              !CurrentProjectService.getCurrentProject().release) {
              if (!ProjectUpdateAccessService
                   .isUpdateAllowed(CurrentProjectService.getCurrentProject(),
                    'studies', true)) {
                redirectToSearchView();
              } else {
                StudyResource.get({
                  id: StudyIdBuilderService.buildStudyId(
                    CurrentProjectService.getCurrentProject().id)
                }).$promise.then(function(study) {
                  initEditMode(study);
                }).catch(function() {
                    ctrl.isInitializingStudySeries = true;
                    ctrl.createMode = true;
                    ctrl.study = new StudyResource({
                      id: StudyIdBuilderService.buildStudyId(
                        CurrentProjectService.getCurrentProject().id),
                      dataAcquisitionProjectId: CurrentProjectService
                      .getCurrentProject()
                      .id,
                      projectContributors: [{
                        firstName: '',
                        lastName: ''
                      }],
                      dataCurators: [{
                        firstName: '',
                        lastName: ''
                      }],
                      institutions: [{
                        de: '',
                        en: ''
                      }]
                    });
                    ctrl.currentInstitutions = new Array(1);
                    updateToolbarHeaderAndPageTitle();
                    $scope.registerConfirmOnDirtyHook();
                  });
              }
            } else {
              handleReleasedProject();
            }
          }
        } else {
          SimpleMessageToastService.openAlertMessageToast(
            'study-management.edit.not-authorized-toast');
        }
      };

      ctrl.dataAvailabilities = [{
        de: 'Verfügbar',
        en: 'Available'
      }, {
        de: 'In Aufbereitung',
        en: 'In preparation'
      }, {
        de: 'Nicht verfügbar',
        en: 'Not available'
      }];

      ctrl.surveyDesigns = [{
        de: 'Panel',
        en: 'Panel'
      }, {
        de: 'Querschnitt',
        en: 'Cross-Section'
      }];

      ctrl.deleteProjectContributor = function(index) {
        ctrl.study.projectContributors.splice(index, 1);
        $scope.studyForm.$setDirty();
      };

      ctrl.addProjectContributor = function() {
        ctrl.study.projectContributors.push({
          firstName: '',
          lastName: ''
        });
        $timeout(function() {
          $document.find('input[name="projectContributorsFirstName_' +
              (ctrl.study.projectContributors.length - 1) + '"]')
            .focus();
        });
      };

      ctrl.setCurrentProjectContributor = function(index, event) {
        ctrl.currentProjectContributorInputName = event.target.name;
        ctrl.currentProjectContributorIndex = index;
      };

      var timeoutActive = null;
      ctrl.deleteCurrentProjectContributor = function(event) {
        if (timeoutActive) {
          $timeout.cancel(timeoutActive);
        }
        timeoutActive = $timeout(function() {
          timeoutActive = false;
          // msie workaround: inputs unfocus on button mousedown
          if (document.activeElement &&
            $(document.activeElement).parents('#move-container').length) {
            return;
          }
          if (event.relatedTarget && (
              event.relatedTarget.id === 'move-contributor-up-button' ||
              event.relatedTarget.id === 'move-contributor-down-button')) {
            return;
          }
          delete ctrl.currentProjectContributorIndex;
          timeoutActive = null;
        }, 500);
      };

      ctrl.moveCurrentProjectContributorUp = function() {
        var a = ctrl.study.projectContributors[
          ctrl.currentProjectContributorIndex - 1];
        ctrl.study.projectContributors[
          ctrl.currentProjectContributorIndex - 1] =
          ctrl.study.projectContributors[ctrl.currentProjectContributorIndex];
        ctrl.study.projectContributors[ctrl.currentProjectContributorIndex] = a;
        ctrl.currentProjectContributorInputName = ctrl
          .currentProjectContributorInputName
          .replace('_' + ctrl.currentProjectContributorIndex,
            '_' + (ctrl.currentProjectContributorIndex - 1));
        $document.find('input[name="' +
          ctrl.currentProjectContributorInputName + '"]')
          .focus();
        $scope.studyForm.$setDirty();
      };

      ctrl.moveCurrentProjectContributorDown = function() {
        var a = ctrl.study.projectContributors[
          ctrl.currentProjectContributorIndex + 1];
        ctrl.study.projectContributors[
          ctrl.currentProjectContributorIndex + 1] =
          ctrl.study.projectContributors[ctrl.currentProjectContributorIndex];
        ctrl.study.projectContributors[ctrl.currentProjectContributorIndex] = a;
        ctrl.currentProjectContributorInputName = ctrl
          .currentProjectContributorInputName
          .replace('_' + ctrl.currentProjectContributorIndex,
            '_' + (ctrl.currentProjectContributorIndex + 1));
        $document.find('input[name="' +
          ctrl.currentProjectContributorInputName + '"]')
          .focus();
        $scope.studyForm.$setDirty();
      };

      ctrl.deleteCurator = function(index) {
        ctrl.study.dataCurators.splice(index, 1);
        $scope.studyForm.$setDirty();
      };

      ctrl.addCurator = function() {
        ctrl.study.dataCurators.push({
          firstName: '',
          lastName: ''
        });
        $timeout(function() {
          $document.find('input[name="curatorsFirstName_' +
              (ctrl.study.dataCurators.length - 1) + '"]')
            .focus();
        });
      };

      ctrl.setCurrentCurator = function(index, event) {
        ctrl.currentCuratorInputName = event.target.name;
        ctrl.currentCuratorIndex = index;
      };

      ctrl.deleteCurrentCurator = function(event) {
        if (timeoutActive) {
          $timeout.cancel(timeoutActive);
        }
        timeoutActive = $timeout(function() {
          timeoutActive = false;
          // msie workaround: inputs unfocus on button mousedown
          if (document.activeElement &&
            $(document.activeElement).parents('#move-curators-container')
              .length) {
            return;
          }
          if (event.relatedTarget && (
              event.relatedTarget.id === 'move-curator-up-button' ||
              event.relatedTarget.id === 'move-curator-down-button')) {
            return;
          }
          delete ctrl.currentCuratorIndex;
          timeoutActive = null;
        }, 500);
      };

      ctrl.moveCurrentCuratorUp = function() {
        var a = ctrl.study.dataCurators[ctrl.currentCuratorIndex - 1];
        ctrl.study.dataCurators[ctrl.currentCuratorIndex - 1] =
          ctrl.study.dataCurators[ctrl.currentCuratorIndex];
        ctrl.study.dataCurators[ctrl.currentCuratorIndex] = a;
        ctrl.currentCuratorInputName = ctrl.currentCuratorInputName
          .replace('_' + ctrl.currentCuratorIndex,
            '_' + (ctrl.currentCuratorIndex - 1));
        $document.find('input[name="' + ctrl.currentCuratorInputName + '"]')
          .focus();
        $scope.studyForm.$setDirty();
      };

      ctrl.moveCurrentCuratorDown = function() {
        var a = ctrl.study.dataCurators[ctrl.currentCuratorIndex + 1];
        ctrl.study.dataCurators[ctrl.currentCuratorIndex + 1] =
          ctrl.study.dataCurators[ctrl.currentCuratorIndex];
        ctrl.study.dataCurators[ctrl.currentCuratorIndex] = a;
        ctrl.currentCuratorInputName = ctrl.currentCuratorInputName
          .replace('_' + ctrl.currentCuratorIndex,
            '_' + (ctrl.currentCuratorIndex + 1));
        $document.find('input[name="' + ctrl.currentCuratorInputName + '"]')
          .focus();
        $scope.studyForm.$setDirty();
      };

      ctrl.deleteInstitution = function(index) {
        ctrl.study.institutions.splice(index, 1);
        ctrl.currentInstitutions.splice(index, 1);
        $scope.studyForm.$setDirty();
      };

      ctrl.addInstitution = function() {
        ctrl.currentInstitutions.push(null);
        $timeout(function() {
          $document.find('input[name="institutionDe_' +
              (ctrl.study.institutions.length - 1) + '"]')
            .focus();
        }, 200);
      };

      ctrl.setCurrentInstitution = function(index, event) {
        ctrl.currentInstitutionInputName = event.target.name;
        ctrl.currentInstitutionIndex = index;
      };

      ctrl.deleteCurrentInstitution = function(event) {
        if (timeoutActive) {
          $timeout.cancel(timeoutActive);
        }
        timeoutActive = $timeout(function() {
          timeoutActive = false;
          // msie workaround: inputs unfocus on button mousedown
          if (document.activeElement &&
            $(document.activeElement).parents('#move-institution-container')
              .length) {
            return;
          }
          if (event.relatedTarget && (
              event.relatedTarget.id === 'move-institution-up-button' ||
              event.relatedTarget.id === 'move-institution-down-button')) {
            return;
          }
          delete ctrl.currentInstitutionIndex;
          timeoutActive = null;
        }, 500);
      };

      ctrl.moveCurrentInstitutionUp = function() {
        var a = ctrl.study.institutions[ctrl.currentInstitutionIndex - 1];
        ctrl.study.institutions[ctrl.currentInstitutionIndex - 1] =
          ctrl.study.institutions[ctrl.currentInstitutionIndex];
        ctrl.study.institutions[ctrl.currentInstitutionIndex] = a;
        a = ctrl.currentInstitutions[ctrl.currentInstitutionIndex - 1];
        ctrl.currentInstitutions[ctrl.currentInstitutionIndex - 1] =
          ctrl.currentInstitutions[ctrl.currentInstitutionIndex];
        ctrl.currentInstitutions[ctrl.currentInstitutionIndex] = a;
        ctrl.currentInstitutionInputName = ctrl.currentInstitutionInputName
          .replace('_' + ctrl.currentInstitutionIndex,
            '_' + (ctrl.currentInstitutionIndex - 1));
        $document.find('input[name="' + ctrl.currentInstitutionInputName + '"]')
          .focus();
        $scope.studyForm.$setDirty();
      };

      ctrl.moveCurrentInstitutionDown = function() {
        var a = ctrl.study.institutions[ctrl.currentInstitutionIndex + 1];
        ctrl.study.institutions[ctrl.currentInstitutionIndex + 1] =
          ctrl.study.institutions[ctrl.currentInstitutionIndex];
        ctrl.study.institutions[ctrl.currentInstitutionIndex] = a;
        a = ctrl.currentInstitutions[ctrl.currentInstitutionIndex + 1];
        ctrl.currentInstitutions[ctrl.currentInstitutionIndex + 1] =
          ctrl.currentInstitutions[ctrl.currentInstitutionIndex];
        ctrl.currentInstitutions[ctrl.currentInstitutionIndex] = a;
        ctrl.currentInstitutionInputName = ctrl.currentInstitutionInputName
          .replace('_' + ctrl.currentInstitutionIndex,
            '_' + (ctrl.currentInstitutionIndex + 1));
        $document.find('input[name="' + ctrl.currentInstitutionInputName + '"]')
          .focus();
        $scope.studyForm.$setDirty();
      };

      ctrl.saveStudy = function() {
        if ($scope.studyForm.$valid) {
          if (angular.isUndefined(ctrl.study.masterId)) {
            ctrl.study.masterId = ctrl.study.id;
          }
          ctrl.study.$save()
            .then(ctrl.updateElasticSearchIndex)
            .then(ctrl.onSavedSuccessfully)
            .catch(function() {
              SimpleMessageToastService.openAlertMessageToast(
                'study-management.edit.error-on-save-toast', {
                  studyId: ctrl.study.id
                });
            });
        } else {
          // ensure that all validation errors are visible
          angular.forEach($scope.studyForm.$error, function(field) {
            angular.forEach(field, function(errorField) {
              if (errorField.$setTouched) {
                errorField.$setTouched();
              } else if (errorField.$setDirty) {
                errorField.$setDirty();
              }
            });
          });
          SimpleMessageToastService.openAlertMessageToast(
            'study-management.edit.study-has-validation-errors-toast',
            null);
        }
      };

      ctrl.updateElasticSearchIndex = function() {
        return ElasticSearchAdminService.processUpdateQueue('studies');
      };

      ctrl.onSavedSuccessfully = function() {
        $scope.studyForm.$setPristine();
        SimpleMessageToastService.openSimpleMessageToast(
          'study-management.edit.success-on-save-toast', {
            studyId: ctrl.study.id
          });
        if (ctrl.createMode) {
          $state.go('studyEdit', {
            id: ctrl.study.id
          });
        }
      };

      ctrl.openRestorePreviousVersionDialog = function(event) {
        var getVersions = function(id, limit, skip) {
          return StudyVersionsResource.get({
            id: id,
            limit: limit,
            skip: skip
          }).$promise;
        };

        var dialogConfig = {
          domainId: ctrl.study.id,
          getPreviousVersionsCallback: getVersions,
          labels: {
            title: {
              key: 'study-management.edit.choose-previous-version.title',
              params: {
                studyId: ctrl.study.id
              }
            },
            text: {
              key: 'study-management.edit.choose-previous-version.text'
            },
            cancelTooltip: {
              key: 'study-management.edit.choose-previous-version.' +
                  'cancel-tooltip'
            },
            noVersionsFound: {
              key: 'study-management.edit.choose-previous-version.' +
                  'no-versions-found',
              params: {
                studyId: ctrl.study.id
              }
            },
            deleted: {
              key: 'study-management.edit.choose-previous-version.study-deleted'
            }
          }
        };

        ChoosePreviousVersionService.showDialog(dialogConfig, event)
          .then(function(wrapper) {
            ctrl.study = new StudyResource(wrapper.selection);
            if (!ctrl.study.projectContributors) {
              ctrl.study.projectContributors = [{
                firstName: '',
                lastName: ''
              }];
            }
            if (!ctrl.study.dataCurators) {
              ctrl.study.dataCurators = [{
                firstName: '',
                lastName: ''
              }];
            }
            if (ctrl.study.institutions && ctrl.study.institutions.length > 0) {
              ctrl.currentInstitutions = angular.copy(ctrl.study.institutions);
            } else {
              ctrl.currentInstitutions = new Array(1);
              ctrl.study.institutions = [{
                de: '',
                en: ''
              }];
            }
            if (wrapper.isCurrentVersion) {
              $scope.studyForm.$setPristine();
              SimpleMessageToastService.openSimpleMessageToast(
                  'study-management.edit.current-version-restored-toast', {
                    studyId: ctrl.study.id
                  });
            } else {
              $scope.studyForm.$setDirty();
              SimpleMessageToastService.openSimpleMessageToast(
                  'study-management.edit.previous-version-restored-toast', {
                    studyId: ctrl.study.id
                  });
            }
          });
      };

      $scope.registerConfirmOnDirtyHook = function() {
        var unregisterTransitionHook = $transitions.onBefore({}, function() {
          if ($scope.studyForm.$dirty || ctrl.attachmentOrderIsDirty) {
            return CommonDialogsService.showConfirmOnDirtyDialog();
          }
        });

        $scope.$on('$destroy', unregisterTransitionHook);
      };

      $scope.searchStudySeries = function(searchText, language) {
        if (searchText === studySeriesCache.searchText &&
          language === studySeriesCache.language) {
          return studySeriesCache.searchResult;
        }

        //Search Call to Elasticsearch
        return StudySearchService.findStudySeries(searchText, {},
            language, null, null, null, true)
          .then(function(studySeries) {
            studySeriesCache.searchText = searchText;
            studySeriesCache.language = language;
            studySeriesCache.searchResult = studySeries;
            return studySeries;
          });
      };

      $scope.searchSponsors = function(searchText, language) {
        if (searchText === sponsorsCache.searchText &&
          language === sponsorsCache.language) {
          return sponsorsCache.searchResult;
        }

        //Search Call to Elasticsearch
        return StudySearchService.findSponsors(searchText, {},
            language, true)
          .then(function(sponsors) {
            sponsorsCache.searchText = searchText;
            sponsorsCache.language = language;
            sponsorsCache.searchResult = sponsors;
            return sponsors;
          });
      };

      $scope.searchInstitutions = function(searchText, language) {
        //Search Call to Elasticsearch
        return StudySearchService.findInstitutions(searchText, {},
            language, true, ctrl.currentInstitutions)
          .then(function(institutions) {
            return institutions;
          });
      };

      ctrl.loadAttachments = function(selectLastAttachment) {
        StudyAttachmentResource.findByStudyId({
          studyId: ctrl.study.id
        }).$promise.then(
          function(attachments) {
            if (attachments.length > 0) {
              ctrl.attachments = attachments;
              if (selectLastAttachment) {
                ctrl.currentAttachmentIndex = attachments.length - 1;
              }
            }
          });
      };

      ctrl.deleteAttachment = function(attachment, index) {
        CommonDialogsService.showConfirmFileDeletionDialog(attachment.fileName)
          .then(function() {
            attachment.$delete().then(function() {
              SimpleMessageToastService.openSimpleMessageToast(
                'study-management.detail.attachments.attachment-deleted-toast',
                {
                  filename: attachment.fileName
                }
              );
              ctrl.attachments.splice(index, 1);
              delete ctrl.currentAttachmentIndex;
            });
          });
      };

      ctrl.editAttachment = function(attachment, event) {

        var upload = function(file, newAttachmentMetadata) {
          var metadata = _.extend(attachment, newAttachmentMetadata);
          return StudyAttachmentUploadService.uploadAttachment(file, metadata);
        };

        var labels = getDialogLabels();
        labels.editTitle.params.filename = attachment.fileName;

        var getAttachmentVersions = function(id, filename, limit, skip) {
          return StudyAttachmentVersionsResource.get({
                studyId: id,
                filename: filename,
                limit: limit,
                skip: skip
              }).$promise;
        };

        var createStudyAttachmentResource = function(attachmentWrapper) {
          return new StudyAttachmentResource(attachmentWrapper.studyAttachment);
        };

        var dialogConfig = {
          attachmentMetadata: attachment,
          attachmentTypes: attachmentTypes,
          uploadCallback: upload,
          attachmentDomainIdAttribute: 'studyId',
          getAttachmentVersionsCallback: getAttachmentVersions,
          createAttachmentResource: createStudyAttachmentResource,
          labels: labels
        };

        AttachmentDialogService.showDialog(dialogConfig, event)
          .then(ctrl.loadAttachments);
      };

      ctrl.getNextIndexInStudy = function() {
        if (!ctrl.attachments || ctrl.attachments.length === 0) {
          return 0;
        }
        return _.maxBy(ctrl.attachments, function(attachment) {
          return attachment.indexInStudy;
        }).indexInStudy + 1;
      };

      ctrl.addAttachment = function(event) {

        var upload = function(file, attachmentMetadata) {
          var metadata = _.extend({}, attachmentMetadata, {
            studyId: ctrl.study.id,
            dataAcquisitionProjectId: ctrl.study.dataAcquisitionProjectId,
            indexInStudy: ctrl.getNextIndexInStudy()
          });
          return StudyAttachmentUploadService.uploadAttachment(file, metadata);
        };

        var dialogConfig = {
          attachmentMetadata: null,
          attachmentTypes: attachmentTypes,
          uploadCallback: upload,
          labels: getDialogLabels()
        };

        AttachmentDialogService
          .showDialog(dialogConfig, event)
          .then(function() {
            ctrl.loadAttachments(true);
          });
      };

      ctrl.moveAttachmentUp = function() {
        var a = ctrl.attachments[ctrl.currentAttachmentIndex - 1];
        ctrl.attachments[ctrl.currentAttachmentIndex - 1] =
          ctrl.attachments[ctrl.currentAttachmentIndex];
        ctrl.attachments[ctrl.currentAttachmentIndex] = a;
        ctrl.currentAttachmentIndex--;
        ctrl.attachmentOrderIsDirty = true;
      };

      ctrl.moveAttachmentDown = function() {
        var a = ctrl.attachments[ctrl.currentAttachmentIndex + 1];
        ctrl.attachments[ctrl.currentAttachmentIndex + 1] =
          ctrl.attachments[ctrl.currentAttachmentIndex];
        ctrl.attachments[ctrl.currentAttachmentIndex] = a;
        ctrl.currentAttachmentIndex++;
        ctrl.attachmentOrderIsDirty = true;
      };

      ctrl.saveAttachmentOrder = function() {
        var promises = [];
        ctrl.attachments.forEach(function(attachment, index) {
          attachment.indexInStudy = index;
          promises.push(attachment.$save());
        });
        $q.all(promises).then(function() {
          SimpleMessageToastService.openSimpleMessageToast(
            'study-management.detail.attachments.attachment-order-saved-toast',
            {});
          ctrl.attachmentOrderIsDirty = false;
        });
      };

      ctrl.selectAttachment = function(index) {
        if (Principal
            .hasAnyAuthority(['ROLE_PUBLISHER', 'ROLE_DATA_PROVIDER'])) {
          ctrl.currentAttachmentIndex = index;
        }
      };

      ctrl.onStudySeriesChanged = function() {
        //The fields of study series are undefined
        //at the moment of the first initial Call
        if (!$scope.studyForm.studySeriesDe ||
            !$scope.studyForm.studySeriesEn ||
            !ctrl.study ||
            !ctrl.study.studySeries) {
          return;
        }

        if (CleanJSObjectService
          .isNullOrEmpty(ctrl.study.studySeries.de) &&
          !CleanJSObjectService
          .isNullOrEmpty(ctrl.study.studySeries.en)) {
          $scope.studyForm.studySeriesDe.$setValidity('fdz-required', false);
        }

        if (CleanJSObjectService
          .isNullOrEmpty(ctrl.study.studySeries.en) &&
          !CleanJSObjectService
          .isNullOrEmpty(ctrl.study.studySeries.de)) {
          $scope.studyForm.studySeriesEn.$setValidity('fdz-required', false);
        }

        if ((CleanJSObjectService
            .isNullOrEmpty(ctrl.study.studySeries.de) &&
            CleanJSObjectService
            .isNullOrEmpty(ctrl.study.studySeries.en)) ||
          (!CleanJSObjectService
            .isNullOrEmpty(ctrl.study.studySeries.de) &&
            !CleanJSObjectService
            .isNullOrEmpty(ctrl.study.studySeries.en))) {
          $scope.studyForm.studySeriesDe.$setValidity('fdz-required', true);
          $scope.studyForm.studySeriesEn.$setValidity('fdz-required', true);
        }

        if (!ctrl.isInitializingStudySeries) {
          $scope.studyForm.$setDirty();
        } else {
          ctrl.isInitializingStudySeries = false;
        }
      };

      init();
    });
