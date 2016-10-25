'use strict';

angular.module('metadatamanagementApp').service('DataSetBuilderService',
    function(DataSetResource, CleanJSObjectService) {
      var buildDataSet = function(dataSet, subDataSets,
        dataAcquisitionProjectId) {
        if (!dataSet || !dataAcquisitionProjectId) {
          return null;
        }

        var dataSetObj = {
            id: dataSet.id,
            dataAcquisitionProjectId: dataAcquisitionProjectId,
            description: {
              en: dataSet['description.en'],
              de: dataSet['description.de']
            },
            variableIds:
            CleanJSObjectService.removeWhiteSpace(dataSet.variableIds),
            surveyIds: CleanJSObjectService.removeWhiteSpace(dataSet.surveyIds),
            type: {
              en: dataSet['type.en'],
              de: dataSet['type.de']
            },
            subDataSets: subDataSets
          };
        var cleanedDataSetObject = CleanJSObjectService
        .removeEmptyJsonObjects(dataSetObj);
        return new DataSetResource(cleanedDataSetObject);
      };
      var buildSubDataSet = function(subDataSet) {
          var errors = [];
          var error;
          if (!parseInt(subDataSet.numberOfObservations)) {
            error = {
              message: 'data-set-management.log-messages.data-set.sub-' +
              'data-set.number-of-observations-parse-error',
              translationParams: {
                name: subDataSet.name
              }
            };
            errors.push(error);
          }
          if (!parseInt(subDataSet.numberOfAnalyzedVariables)) {
            error = {
              message: 'data-set-management.log-messages.data-set.sub-' +
              'data-set.number-of-analyzed-variables-parse-error',
              translationParams: {
                name: subDataSet.name
              }
            };
            errors.push(error);
          }
          if (errors.length === 0) {
            var subDataSetObj = {
                name: subDataSet.name,
                description: {
                  de: subDataSet['description.de'],
                  en: subDataSet['description.en']
                },
                accessWay: subDataSet.accessWay,
                numberOfObservations:
                parseInt(subDataSet.numberOfObservations),
                numberOfAnalyzedVariables:
                parseInt(subDataSet.numberOfAnalyzedVariables)
              };
            var cleanedDataSetObject = CleanJSObjectService
              .removeEmptyJsonObjects(subDataSetObj);
            return cleanedDataSetObject;
          } else {
            throw errors;
          }
        };
      return {
        buildDataSet: buildDataSet,
        buildSubDataSet: buildSubDataSet
      };
    });
