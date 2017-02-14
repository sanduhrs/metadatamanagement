/* global _*/
'use strict';

angular.module('metadatamanagementApp').service('SearchDao',
  function(ElasticSearchClient, CleanJSObjectService) {
    var keyMapping = {
      'variables': {
        'data-set': 'dataSetId',
        'panel-identifier': 'panelIdentifier'
      },
      'surveys': {
        'instrument': 'instrumentIds',
        'study': 'dataAcquisitionProjectId',
        'variable': 'variableIds',
        'data-set': 'dataSetIds'
      },
      'questions': {
        'instrument': 'instrumentId',
        'variable': 'variableIds'
      },
      'instruments': {
        'survey': 'surveyIds'
      },
      'data_sets': {
        'survey': 'surveyIds',
        'study': 'dataAcquisitionProjectId',
      },
      'related_publications': {
        'variable': 'variableIds',
        'data-set': 'dataSetIds'
      }
    };
    return {
      search: function(queryterm, pageNumber, dataAcquisitionProjectId,
        filter, elasticsearchType, pageSize, sortBy) {
        var query = {};
        var projectFilter;
        var studiesFilter;
        query.index = elasticsearchType;
        query.type = elasticsearchType;
        query.body = {};
        //use source filtering for returning only required attributes
        query.body._source = ['id', 'number', 'questionText', 'title',
        'description','type', 'doi', 'publicationAbstract', 'authors',
        'surveyMethod', 'fieldPeriod', 'label', 'name', 'dataType',
        'scaleLevel', 'dataAcquisitionProjectId', 'dataSetNumber',
        'instrumentNumber'];
        if (sortBy && sortBy !== '') {
          var sortCriteria = {};
          sortCriteria[sortBy] = {
            'order': 'asc'
          };
          query.body.sort = [];
          query.body.sort.push(sortCriteria);
        }
        //a query term
        if (!CleanJSObjectService.isNullOrEmpty(queryterm)) {
          query.body.query = {
            'bool': {
              'must': [{
                'match': {
                  '_all': {
                    'query': queryterm,
                    'type': 'boolean',
                    'operator': 'AND',
                    'minimum_should_match': '100%',
                    'zero_terms_query': 'NONE'
                  }
                }
              }]
            }
          };
          //no query term
        } else {
          query.body.query = {
            'bool': {
              'must': [{
                'match_all': {}
              }],
            }
          };
        }
        //define from
        query.body.from = (pageNumber - 1) * pageSize;
        //define size
        query.body.size = pageSize;
        query.body.query.bool.filter = {};
        query.body.query.bool.filter.bool = {};
        //aggregations if user is on the all tab
        if (CleanJSObjectService.isNullOrEmpty(elasticsearchType)) {
          //define aggregations
          query.body.aggs = {
            'countByType': {
              'terms': {
                'field': '_type'
              }
            }
          };
        }
        // this filter section should be refactored
        // filter by projectId
        if (dataAcquisitionProjectId && CleanJSObjectService
          .isNullOrEmpty(filter)) {
          projectFilter = {
            'term': {
              'dataAcquisitionProjectId': dataAcquisitionProjectId
            }
          };
          studiesFilter = {
            'term': {
              'studyIds': dataAcquisitionProjectId
            }
          };
          query.body.query.bool.filter.bool.should = [];
          query.body.query.bool.filter.bool.should.push(projectFilter);
          query.body.query.bool.filter.bool.should.push(studiesFilter);
        }

        if (!CleanJSObjectService.isNullOrEmpty(filter)) {
          projectFilter = {
            'term': {
              'dataAcquisitionProjectId': dataAcquisitionProjectId
            }
          };
          query.body.query.bool.filter.bool.must = [];
          _.each(filter, function(value, key) {
            var filterKeyValue = {
              'term': {}
            };
            if (elasticsearchType) {
              var subKeyMapping = keyMapping[elasticsearchType];
              key = subKeyMapping[key];
              filterKeyValue.term[key] = value;
              query.body.query.bool.filter.bool.must.push(
                filterKeyValue);
            }
          });
        }
        return ElasticSearchClient.search(query);
      }
    };
  });
