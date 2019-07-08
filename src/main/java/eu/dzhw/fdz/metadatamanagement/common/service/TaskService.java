package eu.dzhw.fdz.metadatamanagement.common.service;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;

import org.apache.commons.lang.NotImplementedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import eu.dzhw.fdz.metadatamanagement.common.domain.Task;
import eu.dzhw.fdz.metadatamanagement.common.domain.Task.TaskState;
import eu.dzhw.fdz.metadatamanagement.common.domain.Task.TaskType;
import eu.dzhw.fdz.metadatamanagement.common.domain.TaskErrorNotification;
import eu.dzhw.fdz.metadatamanagement.common.repository.TaskRepository;
import eu.dzhw.fdz.metadatamanagement.common.rest.errors.ErrorDto;
import eu.dzhw.fdz.metadatamanagement.common.rest.errors.ErrorListDto;
import eu.dzhw.fdz.metadatamanagement.datasetmanagement.exception.TemplateIncompleteException;
import eu.dzhw.fdz.metadatamanagement.mailmanagement.service.MailService;
import eu.dzhw.fdz.metadatamanagement.usermanagement.domain.Authority;
import eu.dzhw.fdz.metadatamanagement.usermanagement.domain.User;
import eu.dzhw.fdz.metadatamanagement.usermanagement.repository.UserRepository;
import eu.dzhw.fdz.metadatamanagement.usermanagement.security.AuthoritiesConstants;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;

/**
 * Service to handle the management of long running tasks.
 *
 * @author tgehrke
 *
 */
@Slf4j
@Service
public class TaskService {
  @Autowired
  private TaskRepository taskRepo;

  @Autowired
  private CounterService counterService;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private MailService mailService;

  @Value("${metadatamanagement.server.instance-index}")
  private Integer instanceId;

  @Value("${metadatamanagement.projectmanagement.email}")
  private String projectManagementEmailSender;

  /**
   * Create a task.
   *
   * @param taskType The kind of task to create. Defined by {@link TaskType}
   * @return the created task.
   */
  public Task createTask(TaskType taskType) {
    String taskId = Long.toString(counterService.getNextSequence("tasks"));
    Task task = Task.builder().state(TaskState.RUNNING).id(taskId).type(taskType).build();
    return taskRepo.insert(task);
  }

  /**
   * Mark the task as errored.
   *
   * @param task the task
   * @param exception the reason for failing.
   */
  public Task handleErrorTask(@NotNull Task task, Exception exception) {
    log.debug("Exception occurred in task {}", task.getId());
    task.setState(TaskState.FAILURE);
    task.setErrorList(createErrorListFromException(exception));
    return taskRepo.save(task);
  }

  /**
   * Handle all {@link TaskErrorNotification}s.
   * 
   * @param errorNotification The details about the error.
   * @param onBehalfUser The {@link User} for whom the task has been executed.
   */
  public void handleErrorNotification(TaskErrorNotification errorNotification, User onBehalfUser) {
    switch (errorNotification.getTaskType()) {
      case DATA_SET_REPORT:
        handleDataSetReportError(errorNotification, onBehalfUser, projectManagementEmailSender);
        break;
      default:
        throw new NotImplementedException("Handling of errors for "
            + errorNotification.getTaskType() + " has not been implemented yet.");
    }
  }

  private void handleDataSetReportError(TaskErrorNotification errorNotification, User onBehalfUser,
      String projectManagementEmailSender) {
    List<User> admins =
        userRepository.findAllByAuthoritiesContaining(new Authority(AuthoritiesConstants.ADMIN));
    mailService.sendDataSetReportErrorMail(onBehalfUser, admins, errorNotification,
        projectManagementEmailSender);
  }

  /**
   * Mark the task as done.
   * 
   * @param task the task id
   * @param resultLocation the location to get the result processed by the task.
   */
  public Task handleTaskDone(@NotNull Task task, String resultLocation) {
    task.setState(TaskState.DONE);
    task.setLocation("/public/files" + resultLocation);
    return taskRepo.save(task);
  }

  private ErrorListDto createErrorListFromException(Exception exception) {
    ErrorListDto errorListDto = new ErrorListDto();
    log.debug("Handling exception", exception);
    if (exception instanceof TemplateException) {
      // The message of the exception is the error message of freemarker.
      // The manually added message for the dto can be translated into i18n strings
      String messageKey = "data-set-management.error.tex-template-error";
      errorListDto.add(new ErrorDto(null, messageKey, exception.getMessage(), null));
    } else if (exception instanceof TemplateIncompleteException) {
      TemplateIncompleteException te = (TemplateIncompleteException) exception;
      // All missing files
      for (String missingFile : te.getMissingFiles()) {
        errorListDto.add(new ErrorDto(null, te.getMessage(), missingFile, null));
      }
    } else {
      String messageKey = "data-set-management.error.io-error";
      errorListDto.add(new ErrorDto(null, messageKey, exception.getMessage(), null));
    }
    return errorListDto;
  }

  /**
   * Delete all completed tasks at 3 am.
   */
  @Scheduled(cron = "0 0 3 * * ?")
  public void deleteCompletedTasks() {
    if (instanceId != 0) {
      return;
    }
    log.info("Starting deletion of completed tasks...");
    LocalDateTime yesterday = LocalDateTime.now().minusDays(14);
    taskRepo.deleteAllByStateAndCreatedDateBefore(TaskState.DONE, yesterday);
    taskRepo.deleteAllByStateAndCreatedDateBefore(TaskState.FAILURE, yesterday);
    log.info("Finished deleting completed tasks.");
  }
}
