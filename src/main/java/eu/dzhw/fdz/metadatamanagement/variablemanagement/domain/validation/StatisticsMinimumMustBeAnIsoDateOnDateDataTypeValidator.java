package eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.validation;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.DataTypes;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.Variable;

/**
 * Validates the statistics of a variable. It checks the minimum has a numeric string, if the
 * variable has a numeric data type.
 * 
 * @author Daniel Katzberg
 *
 */
public class StatisticsMinimumMustBeAnIsoDateOnDateDataTypeValidator implements
    ConstraintValidator<StatisticsMinimumMustBeAnIsoDateOnDateDataType, Variable> {

  /*
   * (non-Javadoc)
   * @see javax.validation.ConstraintValidator#initialize(java.lang.annotation.Annotation)
   */
  @Override
  public void initialize(
      StatisticsMinimumMustBeAnIsoDateOnDateDataType constraintAnnotation) {}

  /*
   * (non-Javadoc)
   * 
   * @see javax.validation.ConstraintValidator#isValid(java.lang.Object,
   * javax.validation.ConstraintValidatorContext)
   */
  @Override
  public boolean isValid(Variable variable, ConstraintValidatorContext context) {

    if (variable == null) {
      return true;
    }

    if (variable.getDataType() == null) {
      return true;
    }

    if (variable.getDistribution() == null) {
      return true;
    }

    if (variable.getDistribution().getStatistics() == null) {
      return true;
    }

    if (variable.getDataType().equals(DataTypes.DATE)) {
      //check for an iso standard
      String minimum = variable.getDistribution().getStatistics().getMinimum();

      try {
        LocalDateTime.parse(minimum);
        return true;
      } catch (DateTimeParseException dtpe) {
        return false; // not parsable
      }
      
    }

    // no date, everything is okay.
    return true;
  }

}