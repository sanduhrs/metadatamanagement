package eu.dzhw.fdz.metadatamanagement.questionmanagement.service;

import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import eu.dzhw.fdz.metadatamanagement.AbstractTest;
import eu.dzhw.fdz.metadatamanagement.common.service.ShadowCopyService;
import eu.dzhw.fdz.metadatamanagement.common.unittesthelper.util.UnitTestCreateDomainObjectUtils;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.DataAcquisitionProject;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.Release;
import eu.dzhw.fdz.metadatamanagement.questionmanagement.domain.QuestionImageMetadata;
import eu.dzhw.fdz.metadatamanagement.usermanagement.security.AuthoritiesConstants;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsCriteria;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.security.test.context.support.WithMockUser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

@WithMockUser(authorities = AuthoritiesConstants.PUBLISHER)
public class QuestionImageMetadataShadowCopyDataSourceTest extends AbstractTest {

  private static final String PROJECT_ID = "issue1991";

  private static final String QUESTION_ID = "que-" + PROJECT_ID + "-ins1-1.1$";

  @Autowired
  private GridFsOperations gridFsOperations;

  @Autowired
  private GridFS gridFs;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private QuestionImageMetadataShadowCopyDataSource shadowCopyDataSource;

  @Autowired
  private ShadowCopyService<QuestionImageMetadata> shadowCopyService;

  private DataAcquisitionProject dataAcquisitionProject;

  private Release release;

  @Before
  public void setup() {
    release = new Release("1.0.0", LocalDateTime.now());

    dataAcquisitionProject = UnitTestCreateDomainObjectUtils.buildDataAcquisitionProject();
    dataAcquisitionProject.setId(PROJECT_ID);
    dataAcquisitionProject.setRelease(release);
  }

  @After
  public void teardown() {
    Query query = new Query(GridFsCriteria.whereFilename().regex("^/questions"));
    this.gridFsOperations.delete(query);
  }

  @Test
  public void createShadowCopy() throws Exception {
    QuestionImageMetadata master = UnitTestCreateDomainObjectUtils
        .buildQuestionImageMetadata(PROJECT_ID, QUESTION_ID);
    master.generateId();
    master.setMasterId(master.getId());
    createTestFileForAttachment(master);

    shadowCopyService.createShadowCopies(dataAcquisitionProject,
        null, shadowCopyDataSource);

    GridFSFile gridFsFile = gridFsOperations.findOne(new Query(GridFsCriteria
        .whereMetaData("dataAcquisitionProjectId").is(PROJECT_ID + "-1.0.0")
        .andOperator(GridFsCriteria.whereMetaData("shadow").is(true))));

    QuestionImageMetadata metaData = mongoTemplate.getConverter()
        .read(QuestionImageMetadata.class, gridFsFile.getMetadata());

    assertThat(gridFsFile, notNullValue());
    assertThat(metaData, notNullValue());
    assertThat(metaData.getId(), equalTo("/public/files/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG"));
    assertThat(metaData.getDataAcquisitionProjectId(), equalTo(PROJECT_ID + "-1.0.0"));
    assertThat(metaData.isShadow(), equalTo(true));
    assertThat(gridFsFile.getFilename(), equalTo("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG"));

    List<String> expectedFiles = new ArrayList<>();
    expectedFiles.add("/questions/que-" + PROJECT_ID + "-ins1-1.1$/images/TestFileName.PNG");
    expectedFiles.add("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG");
    assertExpectedFilesExistence(expectedFiles);

    GridFSDBFile shadowCopy = gridFs.findOne("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG");
    assertThat(shadowCopy.getMetaData().get("_contentType"), equalTo("image/png"));
  }

  @Test
  public void createShadowCopyWithSameReleaseVersion() throws Exception {
    QuestionImageMetadata master = UnitTestCreateDomainObjectUtils
        .buildQuestionImageMetadata(PROJECT_ID, QUESTION_ID);
    master.generateId();
    master.setMasterId(master.getId());
    createTestFileForAttachment(master);
    QuestionImageMetadata shadow = createShadow(master, "1.0.0");
    createTestFileForAttachment(shadow);

    shadowCopyService.createShadowCopies(dataAcquisitionProject, "1.0.0",
        shadowCopyDataSource);

    List<DBObject> files = new ArrayList<>();
    gridFs.getFileList().iterator().forEachRemaining(files::add);

    assertThat(files.size(), equalTo(2));

    Query shadowQuery = new Query(GridFsCriteria.whereFilename().is("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG"));
    GridFSFile shadowFile = gridFsOperations.findOne(shadowQuery);

    assertThat(shadowFile, notNullValue());

    QuestionImageMetadata metadata = mongoTemplate.getConverter()
        .read(QuestionImageMetadata.class, shadowFile.getMetadata());

    assertThat(metadata.getSuccessorId(), nullValue());
    assertThat(shadowFile.getMetadata().get("_contentType"), equalTo("image/png"));
  }

  @Test
  public void createShadowCopyLinkPredecessorToSuccessor() throws Exception {
    QuestionImageMetadata master = UnitTestCreateDomainObjectUtils
        .buildQuestionImageMetadata(PROJECT_ID, QUESTION_ID);
    master.generateId();
    master.setMasterId(master.getId());
    createTestFileForAttachment(master);

    QuestionImageMetadata shadow = createShadow(master, "1.0.0");
    createTestFileForAttachment(shadow);
    release.setVersion("1.0.1");

    shadowCopyService.createShadowCopies(dataAcquisitionProject, "1.0.0",
        shadowCopyDataSource);

    GridFSFile gridFsFile = gridFsOperations.findOne(new Query(GridFsCriteria
        .whereMetaData("dataAcquisitionProjectId").is(PROJECT_ID + "-1.0.0")
        .andOperator(GridFsCriteria.whereMetaData("shadow").is(true))));

    QuestionImageMetadata metadata = mongoTemplate.getConverter()
        .read(QuestionImageMetadata.class, gridFsFile.getMetadata());

    assertThat(metadata, notNullValue());
    assertThat(metadata.getSuccessorId(), equalTo("/public/files/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.1/images/TestFileName.PNG"));

    List<String> expectedFiles = new ArrayList<>();
    expectedFiles.add("/questions/que-" + PROJECT_ID + "-ins1-1.1$/images/TestFileName.PNG");
    expectedFiles.add("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG");
    expectedFiles.add("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.1/images/TestFileName.PNG");
    assertExpectedFilesExistence(expectedFiles);

    GridFSDBFile predecessor = gridFs.findOne("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG");
    assertThat(predecessor.getMetaData().get("_contentType"), equalTo("image/png"));
  }

  @Test
  public void createShadowCopyWithDeletedMaster() throws Exception {
    QuestionImageMetadata master = UnitTestCreateDomainObjectUtils
        .buildQuestionImageMetadata(PROJECT_ID, QUESTION_ID);

    QuestionImageMetadata shadow = createShadow(master, "1.0.0");
    createTestFileForAttachment(shadow);
    release.setVersion("1.0.1");

    shadowCopyService.createShadowCopies(dataAcquisitionProject, "1.0.0",
        shadowCopyDataSource);

    Query shadowQuery = new Query(GridFsCriteria.whereFilename().is("/questions/que-" + PROJECT_ID + "-ins1-1.1$-1.0.0/images/TestFileName.PNG"));
    GridFSFile shadowFile = gridFsOperations.findOne(shadowQuery);

    assertThat(shadowFile, notNullValue());

    QuestionImageMetadata metadata = mongoTemplate.getConverter()
        .read(QuestionImageMetadata.class, shadowFile.getMetadata());

    assertThat(metadata.getSuccessorId(), equalTo("DELETED"));
  }

  private QuestionImageMetadata createShadow(QuestionImageMetadata master, String version) {

    QuestionImageMetadata shadow = new QuestionImageMetadata();
    BeanUtils.copyProperties(master, shadow);
    shadow.setQuestionId(master.getQuestionId() + "-" + version);
    shadow.setDataAcquisitionProjectId(shadow.getDataAcquisitionProjectId() + "-" + version);
    shadow.generateId();
    return shadow;
  }

  private void createTestFileForAttachment(QuestionImageMetadata metadata)
      throws Exception {

    InputStream is = new ByteArrayInputStream("Test".getBytes(StandardCharsets.UTF_8));
    String filename = String.format("/questions/%s/images/%s", metadata.getQuestionId(), metadata.getFileName());
    gridFsOperations.store(is, filename, "image/png", metadata);
    is.close();
  }

  private void assertExpectedFilesExistence(List<String> expectedFiles) {
    Iterator<DBObject> it = gridFs.getFileList().iterator();
    List<String> fileNames = new ArrayList<>();
    while (it.hasNext()) {
      fileNames.add((String) it.next().get("filename"));
    }
    assertThat(fileNames.size(), equalTo(expectedFiles.size()));
    assertThat(fileNames, containsInAnyOrder(expectedFiles.toArray()));
  }
}