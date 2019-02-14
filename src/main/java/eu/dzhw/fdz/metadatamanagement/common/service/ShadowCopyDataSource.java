package eu.dzhw.fdz.metadatamanagement.common.service;

import java.util.Optional;
import java.util.stream.Stream;

import eu.dzhw.fdz.metadatamanagement.common.domain.AbstractShadowableRdcDomainObject;

/**
 * Provides domain objects for {@link ShadowCopyService} which are used to create shadow copies.
 * 
 * @param <T> The domain object to be copied.
 */
public interface ShadowCopyDataSource<T extends AbstractShadowableRdcDomainObject> {

  /**
   * Returns the master objects which are used to derive shadow copies from. Consumers are expected
   * to close the stream.
   * @param dataAcquisitionProjectId Project id used to find master objects
   * @return List of master objects
   */
  Stream<T> getMasters(String dataAcquisitionProjectId);

  /**
   * Create a copy of the given object.
   * @param source       The object source to copy
   * @param version The version to use as part of the id for the copied object
   * @return Copied object
   */
  T createShadowCopy(T source, String version);

  /**
   * Find the predecessor of the given shadow copy.
   * @param masterId Master id of the domain object
   * @return Predecessor of the given shadow copy if existing
   */
  Optional<T> findPredecessorOfShadowCopy(String masterId);

  /**
   * Update an existing predecessor shadow copy.
   * @param predecessor predecessor
   * @return Persisted predecessor
   */
  void updatePredecessor(T predecessor);

  /**
   * Save the (new) shadow copy.
   * @param shadowCopy New shadow copy to save
   * @return Persisted shadow copy
   */
  void saveShadowCopy(T shadowCopy);

  /**
   * Find shadow copies where the master has been deleted between the last and the current project
   * release.
   * @param projectId Project id
   * @param previousVersion Version of the previous project release
   * @return Stream of shadow copies with deleted masters
   */
  Stream<T> findShadowCopiesWithDeletedMasters(String projectId, String previousVersion);
}
