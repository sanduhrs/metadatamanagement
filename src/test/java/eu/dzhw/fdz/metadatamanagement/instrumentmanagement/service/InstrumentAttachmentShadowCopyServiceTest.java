package eu.dzhw.fdz.metadatamanagement.instrumentmanagement.service;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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

import com.mongodb.DBObject;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;

import eu.dzhw.fdz.metadatamanagement.AbstractTest;
import eu.dzhw.fdz.metadatamanagement.common.unittesthelper.util.UnitTestCreateDomainObjectUtils;
import eu.dzhw.fdz.metadatamanagement.instrumentmanagement.domain.InstrumentAttachmentMetadata;
import eu.dzhw.fdz.metadatamanagement.instrumentmanagement.service.helper.InstrumentAttachmentFilenameBuilder;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.DataAcquisitionProject;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.Release;
import eu.dzhw.fdz.metadatamanagement.usermanagement.security.AuthoritiesConstants;

@WithMockUser(authorities = AuthoritiesConstants.PUBLISHER)
public class InstrumentAttachmentShadowCopyServiceTest extends AbstractTest {

  private static final String PROJECT_ID = "issue1991";

  @Autowired
  private GridFsOperations gridFsOperations;

  @Autowired
  private GridFS gridFs;

  @Autowired
  private MongoTemplate mongoTemplate;

  @Autowired
  private InstrumentAttachmentShadowCopyService shadowCopyService;

  private DataAcquisitionProject dataAcquisitionProject;

  private Release release;

  @Before
  public void setup() {
    release = new Release("1.0.0", LocalDateTime.now(), null);

    dataAcquisitionProject = UnitTestCreateDomainObjectUtils.buildDataAcquisitionProject();
    dataAcquisitionProject.setId(PROJECT_ID);
    dataAcquisitionProject.setRelease(release);
  }

  @After
  public void teardown() {
    Query query = new Query(GridFsCriteria.whereFilename().regex("^/instruments"));
    this.gridFsOperations.delete(query);
  }

  @Test
  public void createShadowCopy() throws Exception {
    InstrumentAttachmentMetadata master =
        UnitTestCreateDomainObjectUtils.buildInstrumentAttachmentMetadata(PROJECT_ID, 1);
    master.generateId();
    master.setMasterId(master.getId());
    createTestFileForAttachment(master);

    shadowCopyService.createShadowCopies(dataAcquisitionProject.getId(),
        dataAcquisitionProject.getRelease().getVersion(), null);

    GridFSFile gridFsFile = gridFsOperations.findOne(
        new Query(GridFsCriteria.whereMetaData("dataAcquisitionProjectId").is(PROJECT_ID + "-1.0.0")
            .andOperator(GridFsCriteria.whereMetaData("shadow").is(true))));

    InstrumentAttachmentMetadata metaData = mongoTemplate.getConverter()
        .read(InstrumentAttachmentMetadata.class, gridFsFile.getMetadata());

    assertThat(gridFsFile, notNullValue());
    assertThat(metaData, notNullValue());
    assertThat(metaData.getId(), equalTo(
        "/public/files/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt"));
    assertThat(metaData.getDataAcquisitionProjectId(), equalTo(PROJECT_ID + "-1.0.0"));
    assertThat(metaData.isShadow(), equalTo(true));
    assertThat(gridFsFile.getFilename(),
        equalTo("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt"));

    List<String> expectedFiles = new ArrayList<>();
    expectedFiles.add("/instruments/ins-" + PROJECT_ID + "-ins1$/attachments/filename.txt");
    expectedFiles.add("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt");
    assertExpectedFilesExistence(expectedFiles);

    GridFSDBFile shadowCopy =
        gridFs.findOne("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt");
    assertThat(shadowCopy.getMetaData().get("_contentType"), equalTo("text/plain"));
  }

  @Test
  public void createShadowCopyWithSameReleaseVersion() throws Exception {
    InstrumentAttachmentMetadata master =
        UnitTestCreateDomainObjectUtils.buildInstrumentAttachmentMetadata(PROJECT_ID, 1);
    master.generateId();
    master.setMasterId(master.getId());
    createTestFileForAttachment(master);
    InstrumentAttachmentMetadata shadow = createShadow(master, "1.0.0");
    createTestFileForAttachment(shadow);

    shadowCopyService.createShadowCopies(dataAcquisitionProject.getId(),
        dataAcquisitionProject.getRelease().getVersion(), "1.0.0");

    List<DBObject> files = new ArrayList<>();
    gridFs.getFileList().iterator().forEachRemaining(files::add);

    assertThat(files.size(), equalTo(2));

    Query shadowQuery = new Query(GridFsCriteria.whereFilename()
        .is("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt"));
    GridFSFile shadowFile = gridFsOperations.findOne(shadowQuery);

    assertThat(shadowFile, notNullValue());

    InstrumentAttachmentMetadata metadata = mongoTemplate.getConverter()
        .read(InstrumentAttachmentMetadata.class, shadowFile.getMetadata());

    assertThat(metadata.getSuccessorId(), nullValue());

    GridFSDBFile shadowCopy =
        gridFs.findOne("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt");
    assertThat(shadowCopy.getMetaData().get("_contentType"), equalTo("text/plain"));
  }

  @Test
  public void createShadowCopyLinkPredecessorToSuccessor() throws Exception {
    InstrumentAttachmentMetadata master =
        UnitTestCreateDomainObjectUtils.buildInstrumentAttachmentMetadata(PROJECT_ID, 1);
    master.generateId();
    master.setMasterId(master.getId());
    createTestFileForAttachment(master);

    InstrumentAttachmentMetadata shadow = createShadow(master, "1.0.0");
    createTestFileForAttachment(shadow);
    release.setVersion("1.0.1");

    shadowCopyService.createShadowCopies(dataAcquisitionProject.getId(),
        dataAcquisitionProject.getRelease().getVersion(), "1.0.0");

    GridFSFile gridFsFile = gridFsOperations.findOne(
        new Query(GridFsCriteria.whereMetaData("dataAcquisitionProjectId").is(PROJECT_ID + "-1.0.0")
            .andOperator(GridFsCriteria.whereMetaData("shadow").is(true))));

    InstrumentAttachmentMetadata metadata = mongoTemplate.getConverter()
        .read(InstrumentAttachmentMetadata.class, gridFsFile.getMetadata());

    assertThat(metadata, notNullValue());
    assertThat(metadata.getSuccessorId(), equalTo(
        "/public/files/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.1/attachments/filename.txt"));

    List<String> expectedFiles = new ArrayList<>();
    expectedFiles.add("/instruments/ins-" + PROJECT_ID + "-ins1$/attachments/filename.txt");
    expectedFiles.add("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt");
    expectedFiles.add("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.1/attachments/filename.txt");
    assertExpectedFilesExistence(expectedFiles);

    GridFSDBFile predecessor =
        gridFs.findOne("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt");
    assertThat(predecessor.getMetaData().get("_contentType"), equalTo("text/plain"));
  }

  @Test
  public void createShadowCopyWithDeletedMaster() throws Exception {
    InstrumentAttachmentMetadata master =
        UnitTestCreateDomainObjectUtils.buildInstrumentAttachmentMetadata(PROJECT_ID, 1);

    InstrumentAttachmentMetadata shadow = createShadow(master, "1.0.0");
    createTestFileForAttachment(shadow);
    release.setVersion("1.0.1");

    shadowCopyService.createShadowCopies(dataAcquisitionProject.getId(),
        dataAcquisitionProject.getRelease().getVersion(), "1.0.0");

    Query shadowQuery = new Query(GridFsCriteria.whereFilename()
        .is("/instruments/ins-" + PROJECT_ID + "-ins1$-1.0.0/attachments/filename.txt"));
    GridFSFile shadowFile = gridFsOperations.findOne(shadowQuery);

    assertThat(shadowFile, notNullValue());

    InstrumentAttachmentMetadata metadata = mongoTemplate.getConverter()
        .read(InstrumentAttachmentMetadata.class, shadowFile.getMetadata());

    assertThat(metadata.getSuccessorId(), equalTo("DELETED"));
  }

  private InstrumentAttachmentMetadata createShadow(InstrumentAttachmentMetadata master,
      String version) {

    InstrumentAttachmentMetadata shadow = new InstrumentAttachmentMetadata();
    BeanUtils.copyProperties(master, shadow);
    shadow.setInstrumentId(master.getInstrumentId() + "-" + version);
    shadow.setDataAcquisitionProjectId(shadow.getDataAcquisitionProjectId() + "-" + version);
    shadow.generateId();
    return shadow;
  }

  private void createTestFileForAttachment(InstrumentAttachmentMetadata metadata) throws Exception {

    InputStream is = new ByteArrayInputStream("Test".getBytes(StandardCharsets.UTF_8));
    String filename = InstrumentAttachmentFilenameBuilder.buildFileName(metadata);
    gridFsOperations.store(is, filename, "text/plain", metadata);
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
