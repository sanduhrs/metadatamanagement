package eu.dzhw.fdz.metadatamanagement.search;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.rest.core.annotation.HandleAfterDelete;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;

import eu.dzhw.fdz.metadatamanagement.domain.Variable;
import eu.dzhw.fdz.metadatamanagement.search.document.VariableSearchDocument;
import eu.dzhw.fdz.metadatamanagement.search.exception.ElasticsearchDocumentDeleteException;
import eu.dzhw.fdz.metadatamanagement.search.exception.ElasticsearchDocumentSaveException;
import eu.dzhw.fdz.metadatamanagement.search.exception.ElasticsearchIoException;
import eu.dzhw.fdz.metadatamanagement.service.enums.ElasticsearchIndices;
import io.searchbox.action.Action;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Bulk;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

/**
 * Data access object for saving variables in elasticsearch.
 * 
 * @author René Reitmann
 * @author Daniel Katzberg
 */
@Component
@RepositoryEventHandler
public class VariableSearchDao {
  /** The type of saved variables in the elasticsearch indices. */
  public static final String TYPE = "variables";

  private final Logger log = LoggerFactory.getLogger(VariableSearchDao.class);

  /**
   * JestClient for the communication with elasticsearch. No use of the spring boot elasticsearch
   * client.
   */
  @Inject
  private JestClient jestClient;

  /**
   * Save the given variable to elasticsearch.
   */
  @HandleAfterSave
  public void index(Variable variable) {
    for (ElasticsearchIndices index : ElasticsearchIndices.values()) {
      VariableSearchDocument variableSearchDocument = new VariableSearchDocument(variable, index);
      index(variableSearchDocument, index.getIndexName());
    }
  }

  private void index(VariableSearchDocument variableSearchDocument,
      String index) {
    JestResult result = execute(new Index.Builder(variableSearchDocument).index(index)
        .type(TYPE)
        .id(variableSearchDocument.getId())
        .build());
    if (!result.isSucceeded()) {
      throw new ElasticsearchDocumentSaveException(index, TYPE,
          variableSearchDocument.getId(), result.getErrorMessage());
    }
  }

  /**
   * Bulk save the given variables to elasticsearch.
   * 
   * @param variables the variables to save
   */
  public void index(List<Variable> variables) {
    if (variables == null || variables.isEmpty()) {
      return;
    }
    for (ElasticsearchIndices index : ElasticsearchIndices.values()) {
      index(variables.stream()
          .map(variable -> new VariableSearchDocument(variable, index))
          .collect(Collectors.toList()), index.getIndexName());
    }
  }

  private void index(List<VariableSearchDocument> variableSearchDocuments, String index) {
    Bulk.Builder builder = new Bulk.Builder().defaultIndex(index)
        .defaultType(TYPE);
    for (VariableSearchDocument variableSearchDocument : variableSearchDocuments) {
      builder
          .addAction(new Index.Builder(variableSearchDocument).id(variableSearchDocument.getId())
          .build());
    }
    Bulk bulk = builder.build();
    JestResult result = execute(bulk);
    if (!result.isSucceeded()) {
      throw new ElasticsearchDocumentSaveException(index, TYPE, result.getErrorMessage());
    }
  }

  /**
   * Return ALL variables stored in the given index.
   * 
   * @return A List of ALL variables in elasticsearch.
   */
  public JsonObject findAll(String index) {
    SearchSourceBuilder queryBuilder = new SearchSourceBuilder();
    queryBuilder.query(QueryBuilders.matchAllQuery());
    return this.findAllByQueryBuilder(queryBuilder, index);
  }

  /**
   * Delete the variable from the index.
   */
  @HandleAfterDelete
  public void delete(Variable variable) {
    for (ElasticsearchIndices index : ElasticsearchIndices.values()) {
      delete(variable, index.getIndexName());
    }
  }
  
  private void delete(Variable variable, String index) {
    JestResult result =
        execute(new Delete.Builder(variable.getId()).index(index)
          .type(TYPE)
          .build());
    if (!result.isSucceeded()) {
      throw new ElasticsearchDocumentDeleteException(index, TYPE,
          variable.getId(), result.getErrorMessage());
    }
  }

  /**
   * This is a find all method by a queryBuilder. The QueryBuilder can include aggregations, filter
   * or complex search queries.
   * 
   * @param queryBuilder A querybuilder with an defined query.
   * @param index the name of a index within elasticseach
   * @return the elasticsearch result as json
   */
  private JsonObject findAllByQueryBuilder(SearchSourceBuilder queryBuilder, String index) {
    Search search =
        new Search.Builder(queryBuilder.toString()).addIndex(index)
          .addType(TYPE)
          .build();
    JestResult result = execute(search);

    if (!result.isSucceeded()) {
      log.warn("Unable to load variable from index " + index + ": "
          + result.getErrorMessage());
      return null;
    }

    return result.getJsonObject();
  }

  /**
   * Execute queries to elasticsearch.
   * 
   * @param action a query for elasticsearch
   * @return the result from elasticsearch by the jest client.
   */
  private JestResult execute(Action<?> action) {
    try {
      return jestClient.execute(action);
    } catch (IOException e) {
      throw new ElasticsearchIoException(e);
    }
  }
}
