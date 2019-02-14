package eu.dzhw.fdz.metadatamanagement.projectmanagement.service;

import com.github.zafarkhaja.semver.Version;
import eu.dzhw.fdz.metadatamanagement.common.config.MetadataManagementProperties;
import eu.dzhw.fdz.metadatamanagement.common.service.ShadowCopyService;
import eu.dzhw.fdz.metadatamanagement.mailmanagement.service.MailService;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.AssigneeGroup;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.DataAcquisitionProject;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.ProjectReleasedEvent;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.Release;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.repository.DataAcquisitionProjectRepository;
import eu.dzhw.fdz.metadatamanagement.usermanagement.domain.User;
import eu.dzhw.fdz.metadatamanagement.usermanagement.repository.UserRepository;
import eu.dzhw.fdz.metadatamanagement.usermanagement.security.AuthoritiesConstants;
import eu.dzhw.fdz.metadatamanagement.usermanagement.security.SecurityUtils;
import eu.dzhw.fdz.metadatamanagement.usermanagement.security.UserInformationProvider;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.data.rest.core.annotation.HandleAfterSave;
import org.springframework.data.rest.core.annotation.HandleBeforeSave;
import org.springframework.data.rest.core.annotation.RepositoryEventHandler;
import org.springframework.data.rest.core.event.AfterDeleteEvent;
import org.springframework.data.rest.core.event.AfterSaveEvent;
import org.springframework.data.rest.core.event.BeforeDeleteEvent;
import org.springframework.data.rest.core.event.BeforeSaveEvent;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Service class for the Data Acquisition Project. Handles calls to the mongo db.
 * 
 * @author Daniel Katzberg
 */
@Service
@RepositoryEventHandler
public class DataAcquisitionProjectService {

  private static final Version PUBLIC_RELEASE_VERSION = Version.valueOf("1.0.0");

  private DataAcquisitionProjectRepository acquisitionProjectRepository;

  private ApplicationEventPublisher eventPublisher;

  private UserInformationProvider userInformationProvider;

  private DataAcquisitionProjectChangesProvider changesProvider;

  private UserRepository userRepository;

  private MailService mailService;

  private MetadataManagementProperties metadataManagementProperties;

  private DataAcquisitionProjectShadowCopyDataSource dataAcquisitionProjectShadowCopyDataSource;

  private ShadowCopyService<DataAcquisitionProject> shadowCopyService;

  private DataAcquisitionProjectVersionsService dataAcquisitionProjectVersionsService;

  /**
   * Creates a new {@link DataAcquisitionProjectService} instance.
   */
  public DataAcquisitionProjectService(DataAcquisitionProjectRepository dataAcquisitionProjectRepo,
      ApplicationEventPublisher applicationEventPublisher,
      UserInformationProvider userInformationProvider,
      DataAcquisitionProjectChangesProvider changesProvider, UserRepository userRepository,
      MailService mailService, MetadataManagementProperties metadataManagementProperties,
      DataAcquisitionProjectShadowCopyDataSource dataAcquisitionProjectShadowCopyDataSource,
      ShadowCopyService<DataAcquisitionProject> shadowCopyService,
      DataAcquisitionProjectVersionsService dataAcquisitionProjectVersionsService) {
    this.acquisitionProjectRepository = dataAcquisitionProjectRepo;
    this.eventPublisher = applicationEventPublisher;
    this.userInformationProvider = userInformationProvider;
    this.changesProvider = changesProvider;
    this.userRepository = userRepository;
    this.mailService = mailService;
    this.metadataManagementProperties = metadataManagementProperties;
    this.dataAcquisitionProjectShadowCopyDataSource =
        dataAcquisitionProjectShadowCopyDataSource;
    this.shadowCopyService = shadowCopyService;
    this.dataAcquisitionProjectVersionsService = dataAcquisitionProjectVersionsService;
  }

  /**
   * Saves a Data Acquisition Project.
   */
  public DataAcquisitionProject saveDataAcquisitionProject(
      DataAcquisitionProject dataAcquisitionProject) {
    this.eventPublisher.publishEvent(new BeforeSaveEvent(dataAcquisitionProject));
    DataAcquisitionProject saved = this.acquisitionProjectRepository.save(dataAcquisitionProject);
    this.eventPublisher.publishEvent(new AfterSaveEvent(dataAcquisitionProject));
    return saved;
  }

  /**
   * Deletes a Data Acquisition Project, it it hasn't been released before.
   * 
   * @param dataAcquisitionProject A representation of the Data Acquisition Project.
   * @return True = The Project is deleted. False = The Project is not deleted.
   */
  public boolean deleteDataAcquisitionProject(DataAcquisitionProject dataAcquisitionProject) {

    // just delete project, if it has not been released before.
    if (!dataAcquisitionProject.getHasBeenReleasedBefore()) {
      this.eventPublisher.publishEvent(new BeforeDeleteEvent(dataAcquisitionProject));
      this.acquisitionProjectRepository.delete(dataAcquisitionProject);
      this.eventPublisher.publishEvent(new AfterDeleteEvent(dataAcquisitionProject));
    }

    return !dataAcquisitionProject.getHasBeenReleasedBefore();
  }

  /**
   * Searches for {@link DataAcquisitionProject} items for the given id. The result may be limited
   * if the current user is not an admin or publisher.
   * 
   * @param projectId Project id
   * @return A list of {@link DataAcquisitionProject}
   */
  public List<DataAcquisitionProject> findByIdLikeOrderByIdAsc(String projectId) {
    String loginName = userInformationProvider.getUserLogin();

    if (isAdmin() || isPublisher()) {
      return acquisitionProjectRepository.findByIdLikeOrderByIdAsc(projectId);
    } else {
      return acquisitionProjectRepository.findAllByIdLikeAndPublisherIdOrderByIdAsc(projectId,
          loginName);
    }
  }

  /**
   * Searches for a {@link DataAcquisitionProject} with the given id. The result depends on the
   * user's role. Publishers and administrators find everything (provided the project with the given
   * id exists). Data Providers only find those projects where they are added as a data provider.
   * Anonymous users may access all projects.
   * 
   * @param projectId Project id
   * @return Optional of {@link DataAcquisitionProject}, might contain {@code null} if the project
   *         doesn't exist or if the user has insufficient access rights.
   */
  public Optional<DataAcquisitionProject> findDataAcquisitionProjectById(String projectId) {
    String loginName = userInformationProvider.getUserLogin();

    if (isAdmin() || isPublisher()) {
      return acquisitionProjectRepository.findById(projectId);
    } else if (isDataProvider()) {
      return acquisitionProjectRepository.findByProjectIdAndDataProviderId(projectId, loginName);
    } else {
      return acquisitionProjectRepository.findById(projectId);
    }
  }

  private boolean isAdmin() {
    return userInformationProvider.isUserInRole(AuthoritiesConstants.ADMIN);
  }

  private boolean isPublisher() {
    return userInformationProvider.isUserInRole(AuthoritiesConstants.PUBLISHER);
  }

  private boolean isDataProvider() {
    return userInformationProvider.isUserInRole(AuthoritiesConstants.DATA_PROVIDER);
  }

  @HandleBeforeSave
  void onHandleBeforeSave(DataAcquisitionProject newDataAcquisitionProject) {
    Optional<DataAcquisitionProject> oldProject =
        acquisitionProjectRepository.findById(newDataAcquisitionProject.getId());
    changesProvider.put(oldProject.orElse(null), newDataAcquisitionProject);
  }

  @HandleAfterSave
  void onHandleAfterSave(DataAcquisitionProject newDataAcquisitionProject) {
    final String projectId = newDataAcquisitionProject.getId();
    sendPublishersDataProvidersChangedMails(projectId);
    sendAssigneeGroupChangedMails(newDataAcquisitionProject);

    if (isPublicProjectRelease(newDataAcquisitionProject.getId())) {
      Release previousRelease = dataAcquisitionProjectVersionsService
          .findPreviousRelease(newDataAcquisitionProject.getId(),
              newDataAcquisitionProject.getRelease());
      String previousVersion;

      if (previousRelease != null) {
        previousVersion = previousRelease.getVersion();
      } else {
        previousVersion = null;
      }
      this.eventPublisher.publishEvent(new ProjectReleasedEvent(this, newDataAcquisitionProject,
          previousVersion));
    }
  }

  @EventListener
  void onProjectReleaseEvent(ProjectReleasedEvent projectReleasedEvent) {
    shadowCopyService.createShadowCopies(projectReleasedEvent.getDataAcquisitionProject(),
        projectReleasedEvent
            .getPreviousReleaseVersion(), dataAcquisitionProjectShadowCopyDataSource);
  }

  private void sendAssigneeGroupChangedMails(DataAcquisitionProject newDataAcquisitionProject) {
    String projectId = newDataAcquisitionProject.getId();
    String sender = metadataManagementProperties.getProjectManagement().getEmail();
    if (changesProvider.hasAssigneeGroupChanged(projectId)) {
      if (isProjectForcefullyReassignedByPublisher(projectId)) {
        List<String> dataProviders = changesProvider.getOldDataAcquisitionProject(projectId)
            .getConfiguration().getDataProviders();
        List<User> users = userRepository.findAllByLoginIn(new HashSet<>(dataProviders));
        mailService.sendDataProviderAccessRevokedMail(users, projectId,
            newDataAcquisitionProject.getLastAssigneeGroupMessage(), sender);
      } else {
        AssigneeGroup assigneeGroup = changesProvider.getNewAssigneeGroup(projectId);
        Set<String> userNames;

        switch (assigneeGroup) {
          case DATA_PROVIDER:
            List<String> dataProviders =
                newDataAcquisitionProject.getConfiguration().getDataProviders();
            userNames =
                dataProviders != null ? new HashSet<>(dataProviders) : Collections.emptySet();
            break;
          case PUBLISHER:
            userNames = new HashSet<>(newDataAcquisitionProject.getConfiguration().getPublishers());
            break;
          default:
            throw new IllegalStateException("Unknown assignee group " + assigneeGroup);
        }

        if (!userNames.isEmpty()) {
          List<User> users = userRepository.findAllByLoginIn(userNames);
          mailService.sendAssigneeGroupChangedMail(users, projectId,
              newDataAcquisitionProject.getLastAssigneeGroupMessage(), sender);
        }
      }
    }
  }

  private boolean isProjectForcefullyReassignedByPublisher(String projectId) {

    DataAcquisitionProject oldProject = changesProvider.getOldDataAcquisitionProject(projectId);
    DataAcquisitionProject newProject = changesProvider.getNewDataAcquisitionProject(projectId);

    String loginName = SecurityUtils.getCurrentUserLogin();
    boolean isAssignedPublisher = oldProject.getConfiguration().getPublishers().contains(loginName);

    return isAssignedPublisher && oldProject.getAssigneeGroup() == AssigneeGroup.DATA_PROVIDER
        && newProject.getAssigneeGroup() == AssigneeGroup.PUBLISHER;
  }

  private void sendPublishersDataProvidersChangedMails(String projectId) {
    List<String> addedPublishers = changesProvider.getAddedPublisherUserNamesList(projectId);
    List<String> removedPublishers = changesProvider.getRemovedPublisherUserNamesList(projectId);

    List<String> addedDataProviders = changesProvider.getAddedDataProviderUserNamesList(projectId);
    List<String> removedDataProviders =
        changesProvider.getRemovedDataProviderUserNamesList(projectId);

    UserFetchResult users = fetchUsersForUserNames(addedPublishers, removedPublishers,
        addedDataProviders, removedDataProviders);

    String sender = metadataManagementProperties.getProjectManagement().getEmail();

    mailService.sendPublishersAddedMail(users.addedPublisherUsers, projectId, sender);
    mailService.sendPublisherRemovedMail(users.removedPublisherUsers, projectId, sender);
    mailService.sendDataProviderAddedMail(users.addedDataProviderUsers, projectId, sender);
    mailService.sendDataProviderRemovedMail(users.removedDataProviderUsers, projectId, sender);

  }

  private UserFetchResult fetchUsersForUserNames(List<String> addedPublishers,
      List<String> removedPublishers, List<String> addedDataProviders,
      List<String> removedDataProviders) {
    Set<String> userLoginNames = new HashSet<>(addedPublishers);
    userLoginNames.addAll(removedPublishers);
    userLoginNames.addAll(addedDataProviders);
    userLoginNames.addAll(removedDataProviders);

    List<User> users = userRepository.findAllByLoginIn(userLoginNames);

    List<User> addedPublisherUsers = filterUsersByUserNames(users, addedPublishers);
    List<User> removedPublisherUsers = filterUsersByUserNames(users, removedPublishers);
    List<User> addedDataProviderUsers = filterUsersByUserNames(users, addedDataProviders);
    List<User> removedDataProviderUsers = filterUsersByUserNames(users, removedDataProviders);

    return new UserFetchResult(addedPublisherUsers, removedPublisherUsers, addedDataProviderUsers,
        removedDataProviderUsers);
  }

  private List<User> filterUsersByUserNames(List<User> users, List<String> userNames) {
    return users.stream().filter(u -> userNames.contains(u.getLogin()))
        .collect(Collectors.toList());
  }

  /**
   * Encapsulates repository query result for added or removed publishers and data providers.
   */
  private static class UserFetchResult {
    private List<User> addedPublisherUsers;
    private List<User> removedPublisherUsers;
    private List<User> addedDataProviderUsers;
    private List<User> removedDataProviderUsers;

    UserFetchResult(List<User> addedPublisherUsers, List<User> removedPublisherUsers,
        List<User> addedDataProviderUsers, List<User> removedDataProviderUsers) {
      this.addedPublisherUsers = addedPublisherUsers;
      this.removedPublisherUsers = removedPublisherUsers;
      this.addedDataProviderUsers = addedDataProviderUsers;
      this.removedDataProviderUsers = removedDataProviderUsers;
    }
  }

  private boolean isPublicProjectRelease(String dataAcquisitionProjectId) {
    DataAcquisitionProject oldProject =
        changesProvider.getOldDataAcquisitionProject(dataAcquisitionProjectId);

    DataAcquisitionProject newProject =
        changesProvider.getNewDataAcquisitionProject(dataAcquisitionProjectId);

    if (oldProject != null && newProject != null) {
      Release oldRelease = oldProject.getRelease();
      Release newRelease = newProject.getRelease();

      if (oldRelease == null && newRelease != null) {
        return Version.valueOf(newRelease.getVersion())
            .greaterThanOrEqualTo(PUBLIC_RELEASE_VERSION);
      } else {
        return false;
      }
    } else {
      return false;
    }
  }
}
