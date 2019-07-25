package eu.dzhw.fdz.metadatamanagement.datasetmanagement.service;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import eu.dzhw.fdz.metadatamanagement.common.service.ShadowCopyHelper;
import eu.dzhw.fdz.metadatamanagement.datasetmanagement.domain.DataSet;
import eu.dzhw.fdz.metadatamanagement.datasetmanagement.service.helper.DataSetShadowCopyDataSource;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.service.ShadowCopyQueueItemService;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.service.ShadowCopyingStartedEvent;

/**
 * Service which generates shadow copies of all dataSets of a project, when the project has been
 * released.
 * 
 * @author René Reitmann
 */
@Service
public class DataSetShadowCopyService extends ShadowCopyHelper<DataSet> {
  public DataSetShadowCopyService(DataSetShadowCopyDataSource dataSetShadowCopyDataSource) {
    super(dataSetShadowCopyDataSource);
  }

  /**
   * Create shadow copies of current master dataSets on project release.
   * 
   * @param shadowCopyingStartedEvent Emitted by {@link ShadowCopyQueueItemService}
   */
  @EventListener
  public void onShadowCopyingStarted(ShadowCopyingStartedEvent shadowCopyingStartedEvent) {
    super.createShadowCopies(shadowCopyingStartedEvent.getDataAcquisitionProjectId(),
        shadowCopyingStartedEvent.getReleaseVersion(),
        shadowCopyingStartedEvent.getPreviousReleaseVersion());
  }
}
