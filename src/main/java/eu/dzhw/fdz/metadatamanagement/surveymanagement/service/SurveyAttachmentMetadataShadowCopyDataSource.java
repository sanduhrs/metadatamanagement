package eu.dzhw.fdz.metadatamanagement.surveymanagement.service;

import com.mongodb.BasicDBObject;
import com.mongodb.client.gridfs.GridFSFindIterable;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import eu.dzhw.fdz.metadatamanagement.common.service.ShadowCopyDataSource;
import eu.dzhw.fdz.metadatamanagement.surveymanagement.domain.SurveyAttachmentMetadata;
import org.bson.Document;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.convert.MongoConverter;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Provides data for creating shadow copies of {@link SurveyAttachmentMetadata}.
 */
@Service
public class SurveyAttachmentMetadataShadowCopyDataSource
    implements ShadowCopyDataSource<SurveyAttachmentMetadata> {

  private GridFsOperations gridFsOperations;

  private GridFS gridFs;

  private MongoTemplate mongoTemplate;

  /**
   * Create a new instance.
   */
  public SurveyAttachmentMetadataShadowCopyDataSource(GridFsOperations gridFsOperations,
                                                      GridFS gridFs,
                                                      MongoTemplate mongoTemplate) {
    this.gridFsOperations = gridFsOperations;
    this.gridFs = gridFs;
    this.mongoTemplate = mongoTemplate;
  }

  @Override
  public Stream<SurveyAttachmentMetadata> getMasters(String dataAcquisitionProjectId) {
    Query query = new Query(GridFsCriteria.whereMetaData("dataAcquisitionProjectId")
        .is(dataAcquisitionProjectId)
        .andOperator(GridFsCriteria.whereFilename()
            .regex(SurveyAttachmentFilenameBuilder.ALL_SURVEY_ATTACHMENTS)
            .andOperator(GridFsCriteria.whereMetaData("shadow")
                .is(false))));

    Iterable<GridFSFile> files = this.gridFsOperations.find(query);
    MongoConverter converter = mongoTemplate.getConverter();

    return StreamSupport.stream(files.spliterator(), false)
        .map(file -> converter.read(SurveyAttachmentMetadata.class, file.getMetadata()));
  }

  @Override
  public SurveyAttachmentMetadata createShadowCopy(SurveyAttachmentMetadata source,
                                                   String version) {

    String derivedId = deriveId(source, version);

    Query query = new Query(GridFsCriteria.whereMetaData("_id").is(derivedId));
    GridFSFile gridFsFile = this.gridFsOperations.findOne(query);
    SurveyAttachmentMetadata copy;

    if (gridFsFile == null) {
      copy = new SurveyAttachmentMetadata();
      BeanUtils.copyProperties(source, copy);
    } else {
      MongoConverter converter = mongoTemplate.getConverter();
      copy = converter.read(SurveyAttachmentMetadata.class, gridFsFile.getMetadata());
      BeanUtils.copyProperties(source, copy, "version");
    }

    copy.setDataAcquisitionProjectId(source.getDataAcquisitionProjectId() + "-" + version);
    copy.setSurveyId(source.getSurveyId() + "-" + version);
    copy.generateId();
    return copy;
  }

  @Override
  public Optional<SurveyAttachmentMetadata> findPredecessorOfShadowCopy(String masterId) {
    Query query = new Query(GridFsCriteria.whereMetaData("masterId")
        .is(masterId)
        .andOperator(GridFsCriteria.whereFilename()
            .regex(SurveyAttachmentFilenameBuilder.ALL_SURVEY_ATTACHMENTS)
            .andOperator(GridFsCriteria.whereMetaData("shadow")
                .is(true)
                .andOperator(GridFsCriteria.whereMetaData("successorId")
                    .is(null)))));

    GridFSFile file = gridFsOperations.findOne(query);
    if (file == null) {
      return Optional.empty();
    } else {
      return Optional.of(mongoTemplate.getConverter().read(SurveyAttachmentMetadata.class,
          file.getMetadata()));
    }
  }

  @Override
  public void updatePredecessor(SurveyAttachmentMetadata predecessor) {
    GridFSDBFile file = gridFs.findOne(SurveyAttachmentFilenameBuilder.buildFileName(
        predecessor.getSurveyId(), predecessor.getFileName()));
    BasicDBObject dbObject = new BasicDBObject((Document) mongoTemplate.getConverter()
        .convertToMongoType(predecessor));
    file.setMetaData(dbObject);
    file.save();
  }

  @Override
  public void saveShadowCopy(SurveyAttachmentMetadata shadowCopy) {
    String originalFilePath = shadowCopy.getMasterId().replaceFirst("/public/files", "");
    GridFSDBFile file = gridFs.findOne(originalFilePath);
    String filename = SurveyAttachmentFilenameBuilder.buildFileName(shadowCopy);

    try (InputStream fIn = file.getInputStream()) {
      gridFsOperations.store(fIn, filename, getContentType(file), shadowCopy);
    } catch (IOException e) {
      throw new RuntimeException("IO error during shadow copy creation of " + shadowCopy.getId(),
          e);
    }
  }

  @Override
  public Stream<SurveyAttachmentMetadata> findShadowCopiesWithDeletedMasters(
      String projectId, String previousVersion) {
    String oldProjectId = projectId + "-" + previousVersion;
    Query query = new Query(GridFsCriteria.whereMetaData("dataAcquisitionProjectId")
        .is(oldProjectId)
        .andOperator(GridFsCriteria.whereFilename()
            .regex(SurveyAttachmentFilenameBuilder.ALL_SURVEY_ATTACHMENTS)
            .andOperator(GridFsCriteria.whereMetaData("shadow")
                .is(true)
                .andOperator(GridFsCriteria.whereMetaData("successorId")
                    .is(null)))));

    GridFSFindIterable gridFsFiles = gridFsOperations.find(query);
    return convertIterableToStream(gridFsFiles);
  }

  private Stream<SurveyAttachmentMetadata> convertIterableToStream(GridFSFindIterable gridFsFiles) {
    MongoConverter converter = mongoTemplate.getConverter();
    return StreamSupport.stream(gridFsFiles.spliterator(), false)
        .map(file -> converter.read(SurveyAttachmentMetadata.class, file.getMetadata()));
  }

  private String deriveId(SurveyAttachmentMetadata source, String version) {
    return String.format("/public/files/surveys/%s-%s/attachments/%s", source.getSurveyId(),
        version, source.getFileName());
  }

  private String getContentType(GridFSDBFile gridFsDbFile) {
    String contentType = gridFsDbFile.getContentType();
    if (StringUtils.hasText(contentType)) {
      return contentType;
    } else {
      return (String) gridFsDbFile.getMetaData().get("_contentType");
    }
  }
}
