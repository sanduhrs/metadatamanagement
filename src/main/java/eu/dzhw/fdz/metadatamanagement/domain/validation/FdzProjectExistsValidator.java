package eu.dzhw.fdz.metadatamanagement.domain.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import eu.dzhw.fdz.metadatamanagement.repository.FdzProjectRepository;

/**
 * Validates the name of a fdz project name, if it exists.
 * 
 * @author Daniel Katzberg
 *
 */
public class FdzProjectExistsValidator implements ConstraintValidator<FdzProjectExists, String> {

  @Autowired
  private FdzProjectRepository fdzProjectRepository;

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(FdzProjectExists constraintAnnotation) {}

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
   * javax.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(String fdzProjectName, ConstraintValidatorContext context) {
    
    // No ProjectName, no validation.
    if (StringUtils.isEmpty(fdzProjectName)) {
      return false;
    }

    // Name is set -> validate
    return this.fdzProjectRepository.exists(fdzProjectName);
  }

}
