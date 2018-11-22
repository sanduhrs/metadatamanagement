/* global _ */
'use strict';

angular.module('metadatamanagementApp').controller('ProjectCockpitController',
  function($q, $scope, $state, $stateParams, $location, UserResource, Principal,
           PageTitleService, LanguageService,
           ToolbarHeaderService, CurrentProjectService,
           DataAcquisitionProjectResource, SimpleMessageToastService) {

    PageTitleService.setPageTitle(
      'data-acquisition-project-management.project-cockpit.title');
    ToolbarHeaderService.updateToolbarHeader({
      stateName: $state.current.name
    });

    $scope.$on('current-project-changed', function() {
      var changedProject = CurrentProjectService.getCurrentProject();
      if (changedProject) {
        $location.url('/' + LanguageService.getCurrentInstantly() +
          '/projects/' + changedProject.id);
      }
    });

    var selectedProject = CurrentProjectService.getCurrentProject();
    var requestedProjectId = $stateParams.id;
    var requiredTypesWatch;

    if (!selectedProject && !requestedProjectId) {
      return;
    } else if (requestedProjectId &&
      requestedProjectId !== selectedProject.id) {
      DataAcquisitionProjectResource.get({id: requestedProjectId})
        .$promise.then(function(project) {
        if (project.id) {
          CurrentProjectService.setCurrentProject(project);
          $state.reload();
        }
      });
      return;
    }

    selectedProject = CurrentProjectService.getCurrentProject();
    if (!selectedProject) {
      console.error('no project selected');
      return;
    }

    $scope.saveChanges = function() {
      if (!$scope.project.configuration) {
        $scope.project.configuration = {};
      }

      $scope.project.configuration.publishers =
        $scope.activeUsers.publishers.map(function(identity) {
          return identity.login;
        });
      $scope.project.configuration.dataProviders =
        $scope.activeUsers.dataProviders.map(function(identity) {
          return identity.login;
        });

      DataAcquisitionProjectResource.save(
        $scope.project,
        //Success
        function() {
          $scope.changed = false;
          SimpleMessageToastService
            .openSimpleMessageToast(
              'data-acquisition-project-management.log-messages.' +
              'data-acquisition-project.saved', {
                id: $scope.project.id
              });
        },
        //Server Error
        function() {
          SimpleMessageToastService
            .openAlertMessageToast(
              'data-acquisition-project-management.log-messages.' +
              'data-acquisition-project.server-error'
            );
        }
      );
    };

    $scope.changed = false;

    $scope.searchText = {
      publishers: '',
      dataProviders: ''
    };

    $scope.selectedUser = {
      publishers: null,
      dataProviders: null
    };

    $scope.selectedUserChanged = function(user, role) {
      if (user) {
        $scope.activeUsers[role].push(user);
        $scope.changed = true;
        $scope.searchText[role] = '';
        $scope.selectedUser[role] = null;
      }
    };

    $scope.activeUsers = {
      publishers: [],
      dataProviders: []
    };

    $scope.usersFetched = false;

    // load all users assigned to the currrent project
    DataAcquisitionProjectResource.get({id: selectedProject.id}).$promise.then(
      function(project) {
        $scope.project = project;

        if (requiredTypesWatch) {
          requiredTypesWatch();
        }

        if (project.configuration.requiredObjectTypes) {
          $scope.$watch(function() {
            return $scope.project.configuration.requiredObjectTypes;
          }, function(newVal, oldVal) {
            if (newVal !== oldVal && !$scope.changed) {
              $scope.changed = true;
            }
          }, true);
        }

        function getAndAddUsers(key) {
          // get users of type {key} asynchronously
          // and return promise resolving when all are fetched
          if (project.configuration[key]) {
            return $q.all(
              project.configuration[key].map(function(userLogin) {
                return (
                  UserResource.getPublic({
                    login: userLogin
                  }).$promise.then(function(userResult) {
                    if (!_.includes($scope.activeUsers[key].map(function(u) {
                      return u.login;
                    }), userResult.login)) {
                      $scope.activeUsers[key].push(userResult);
                    }
                  })
                );
              })
            );
          } else {
            return $q.resolve([]);
          }
        }

        $q.resolve($q.all([
          getAndAddUsers('publishers'),
          getAndAddUsers('dataProviders')
        ])).then(function() {
          $scope.usersFetched = true;
        }).catch(function(error) {
          console.log(error);
          SimpleMessageToastService
            .openAlertMessageToast(
              'global.error.server-error.internal-server-error', {
                status: error.data.error_description
              });
        });
      });

    $scope.advancedPrivileges = Principal.hasAnyAuthority(['ROLE_PUBLISHER',
      'ROLE_ADMIN']);

    $scope.canDeleteUser = function(user, role) {
      if (user.restricted) {
        // cannot modify user whose details we can't read
        return false;
      }
      if (!$scope.advancedPrivileges && role === 'publishers') {
        // cannot remove publishers without advanced privilege
        return false;
      }
      return $scope.activeUsers[role].length > 1;
    };

    $state.currentPromise = null;
    $scope.searchUsers = function(search, role, roleInternal) {
      if (!search || !$state.loadComplete) {
        return [];
      }
      if (!$state.currentPromise) {
        $state.currentPromise = UserResource.search({
          login: search,
          role: roleInternal
        }).$promise.then(function(result) {
          $state.currentPromise = null;

          return result.filter(function(x) {
            // filter out already added users
            return $scope.activeUsers[role].map(function(u) {
              return u.login;
            }).indexOf(x.login) < 0 && _.includes(x.authorities, roleInternal);
          });
        });
      }
      return $state.currentPromise;
    };

    $scope.removeUser = function(user, role) {
      $scope.changed = true;
      $scope.activeUsers[role] = $scope.activeUsers[role]
        .filter(function(item) {
          return item.login !== user.login;
        });
    };

    $state.loadComplete = true;
  });
