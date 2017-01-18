'use strict';

angular.module('metadatamanagementApp').factory(
  'Principal',
  function Principal($q, AccountResource, AuthServerProvider) {
    var _identity;
    var _authenticated = false;

    return {
      isIdentityResolved: function() {
        return angular.isDefined(_identity);
      },
      isAuthenticated: function() {
        return _authenticated;
      },
      hasAuthority: function(authority) {
        if (!_authenticated || !_identity || !_identity.authorities) {
          return false;
        }

        return (_identity.authorities.indexOf(authority) !== -1);
      },
      hasAnyAuthority: function(authorities) {
        if (!_authenticated || !_identity || !_identity.authorities) {
          return false;
        }

        for (var i = 0; i < authorities.length; i++) {
          if (_identity.authorities.indexOf(authorities[i]) !== -1) {
            return true;
          }
        }

        return false;
      },
      authenticate: function(identity) {
        _identity = identity;
        _authenticated = identity !== null;
      },
      identity: function(force) {
        var deferred = $q.defer();

        if (force === true) {
          _identity = undefined;
        }

        // check and see if we have retrieved the identity data from the
        // server.
        // if we have, reuse it by immediately resolving
        if (angular.isDefined(_identity)) {
          deferred.resolve(_identity);

          return deferred.promise;
        }

        // retrieve the identity data from the server, update the
        // identity object, and then resolve.
        if (AuthServerProvider.hasValidToken()) {
          AccountResource.get().$promise.then(function(account) {
            _identity = account.data;
            _authenticated = true;
            deferred.resolve(_identity);
          }).catch(function() {
            _identity = null;
            _authenticated = false;
            deferred.resolve(_identity);
          });
        } else {
          _identity = null;
          _authenticated = false;
          deferred.resolve(_identity);
        }
        return deferred.promise;
      }
    };
  });
