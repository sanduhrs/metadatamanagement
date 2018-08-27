/* global  _*/
'use strict';

angular.module('metadatamanagementApp').factory('VariableSearchService',
  function(ElasticSearchClient, $q, SearchHelperService,
    CleanJSObjectService) {
    var createQueryObject = function() {
      return {
        index: 'variables',
        type: 'variables'
      };
    };

    var findOneById = function(id) {
      var deferred = $q.defer();
      var query = createQueryObject();
      query.id = id;
      ElasticSearchClient.getSource(query, function(error, response) {
          if (error) {
            deferred.reject(error);
          } else {
            deferred.resolve(response);
          }
        });
      return deferred;
    };

    var findByQuestionId = function(questionId, selectedAttributes, from,
      size) {
      var query = createQueryObject();
      query.body = {};
      query.body.from = from;
      query.body.size = size;
      query.body._source = selectedAttributes;
      query.body.query = {
        'bool': {
          'must': [{
            'match_all': {}
          }],
          'filter': [{
            'term': {
              'relatedQuestions.questionId': questionId
            }
          }]
        }
      };
      return ElasticSearchClient.search(query);
    };
    var findByDataSetId = function(dataSetId, selectedAttributes, from,
      size) {
      var query = createQueryObject();
      query.body = {};
      query.body.from = from;
      query.body.size = size;
      query.body._source = selectedAttributes;
      query.body.query = {
        'bool': {
          'must': [{
            'match_all': {}
          }],
          'filter': [{
            'term': {
              'dataSetId': dataSetId
            }
          }]
        }
      };
      return ElasticSearchClient.search(query);
    };
    var findByDataSetIdAndIndexInDataSet = function(dataSetId, indexInDataSet,
      selectedAttributes) {
      var query = createQueryObject();
      query.body = {};
      query.body._source = selectedAttributes;
      query.body.query = {
        'bool': {
          'must': [{
            'match_all': {}
          }],
          'filter': [{
            'term': {
              'dataSetId': dataSetId
            }
          },{
            'term': {
              'indexInDataSet': indexInDataSet
            }
          }]
        }
      };
      return ElasticSearchClient.search(query);
    };
    var countBy = function(term, value, dataSetId) {
      var query = createQueryObject();
      query.body = {};
      query.body.query = {};
      query.body.query = {
        'bool': {
          'must': [{
            'match_all': {}
          }],
          'filter': []
        }
      };
      var mustTerm = {
        'term': {}
      };
      mustTerm.term[term] = value;
      query.body.query.bool.filter.push(mustTerm);
      if (dataSetId) {
        query.body.query.bool.filter.push({
          'term': {
            'dataSetId': dataSetId
          }});
      }
      return ElasticSearchClient.count(query);
    };

    var createTermFilters = function(filter, dataAcquisitionProjectId) {
      var termFilter;
      if (!CleanJSObjectService.isNullOrEmpty(filter) ||
        !CleanJSObjectService.isNullOrEmpty(dataAcquisitionProjectId)) {
        termFilter = [];
      }
      if (!CleanJSObjectService.isNullOrEmpty(dataAcquisitionProjectId)) {
        var projectFilter = {
          term: {
            dataAcquisitionProjectId: dataAcquisitionProjectId
          }
        };
        termFilter.push(projectFilter);
      }
      if (!CleanJSObjectService.isNullOrEmpty(filter)) {
        termFilter = _.concat(termFilter,
          SearchHelperService.createTermFilters('variables', filter));
      }
      return termFilter;
    };

    var findAccessWays = function(term, filter, dataAcquisitionProjectId,
      queryterm) {
      var query = createQueryObject();
      var termFilters = createTermFilters(filter, dataAcquisitionProjectId);
      query.body = {
        'aggs': {
            'accessWays': {
                'terms': {
                  'field': 'accessWays',
                  'include': '.*' + term.toLowerCase() + '.*',
                  'size': 100,
                  'order': {
                    '_key': 'asc'
                  }
                }
              }
          }
      };

      if (termFilters) {
        query.body.query = {
          bool: {
          }
        };
        query.body.query.bool.filter = termFilters;
      }

      SearchHelperService.addQuery(query, queryterm);

      SearchHelperService.addReleaseFilter(query);

      return ElasticSearchClient.search(query).then(function(result) {
        return result.aggregations.accessWays.buckets;
      });
    };

    var findPanelIdentifiers = function(term, filter,
      dataAcquisitionProjectId, queryterm) {
      var query = createQueryObject();
      var termFilters = createTermFilters(filter, dataAcquisitionProjectId);
      query.body = {
        'aggs': {
            'panelIdentifiers': {
                'terms': {
                  'field': 'panelIdentifier',
                  'include': '.*' + term.toLowerCase() + '.*',
                  'size': 100,
                  'order': {
                    '_key': 'asc'
                  }
                }
              }
          }
      };

      if (termFilters) {
        query.body.query = {
          bool: {
          }
        };
        query.body.query.bool.filter = termFilters;
      }

      SearchHelperService.addQuery(query, queryterm);

      SearchHelperService.addReleaseFilter(query);

      return ElasticSearchClient.search(query).then(function(result) {
        return result.aggregations.panelIdentifiers.buckets;
      });
    };

    var findDerivedVariablesIdentifiers = function(term, filter,
      dataAcquisitionProjectId, queryterm) {
      var query = createQueryObject();
      var termFilters = createTermFilters(filter, dataAcquisitionProjectId);
      query.body = {
        'aggs': {
            'derivedVariablesIdentifiers': {
                'terms': {
                  'field': 'derivedVariablesIdentifier',
                  'include': '.*' + term.toLowerCase() + '.*',
                  'size': 100,
                  'order': {
                    '_key': 'asc'
                  }
                }
              }
          }
      };

      if (termFilters) {
        query.body.query = {
          bool: {
          }
        };
        query.body.query.bool.filter = termFilters;
      }

      SearchHelperService.addQuery(query, queryterm);

      SearchHelperService.addReleaseFilter(query);

      return ElasticSearchClient.search(query).then(function(result) {
        return result.aggregations.derivedVariablesIdentifiers.buckets;
      });
    };

    return {
      findOneById: findOneById,
      findByQuestionId: findByQuestionId,
      findByDataSetId: findByDataSetId,
      countBy: countBy,
      findAccessWays: findAccessWays,
      findPanelIdentifiers: findPanelIdentifiers,
      findDerivedVariablesIdentifiers: findDerivedVariablesIdentifiers,
      findByDataSetIdAndIndexInDataSet: findByDataSetIdAndIndexInDataSet
    };
  });
