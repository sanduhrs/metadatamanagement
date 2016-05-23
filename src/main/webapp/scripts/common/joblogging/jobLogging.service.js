'use strict';

angular.module('metadatamanagementApp').service('JobLoggingService',
function() {
  var job = {
    state: ''
  };
  var getCurrentJob = function() {
    return job;
  };
  var start = function(nameOfDomainObj) {
    job.id = nameOfDomainObj;
    job.errors = 0;
    job.successes = 0;
    job.logMessages = [];
    job.state = 'running';
    return job;
  };
  var error = function(errorMsg) {
    job.errors++;
    job.logMessages.push({
        message: '\n' + errorMsg,
        type: 'error'
      });
  };
  var success = function(successMsg) {
    job.successes++;
    if (successMsg) {
      job.logMessages.push({
          message: '\n' + successMsg,
          type: 'info'
        });
    }
  };
  var finish = function(finishMsg) {
    job.state = 'finished';
    job.logMessages.push({
        message: '\n' + finishMsg,
        type: 'info'
      });
  };
  var cancel = function(cancelMsg) {
    job.state = 'cancelled';
    job.logMessages.push({
        message: '\n' + cancelMsg,
        type: 'error'
      });
  };
  return {
    getCurrentJob: getCurrentJob,
    start: start,
    error: error,
    success: success,
    finish: finish,
    cancel: cancel
  };
});