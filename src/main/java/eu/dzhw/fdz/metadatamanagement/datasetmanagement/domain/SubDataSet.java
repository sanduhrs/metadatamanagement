package eu.dzhw.fdz.metadatamanagement.datasetmanagement.domain;

import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;

import eu.dzhw.fdz.metadatamanagement.common.domain.I18nString;
import eu.dzhw.fdz.metadatamanagement.common.domain.validation.I18nStringNotEmpty;
import eu.dzhw.fdz.metadatamanagement.common.domain.validation.I18nStringSize;
import eu.dzhw.fdz.metadatamanagement.common.domain.validation.StringLengths;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.validation.ValidAccessWays;
import net.karneim.pojobuilder.GeneratePojoBuilder;

/**
 * Domain object of the SubDataSet.
 *
 */
@GeneratePojoBuilder(
    intoPackage = "eu.dzhw.fdz.metadatamanagement.datasetmanagement.domain.builders")
public class SubDataSet {
  
  @NotNull(message = "data-set-management.error.sub-data-set.name.not-null")
  @NotEmpty(message = "data-set-management.error.sub-data-set.name.not-empty")
  @Size(max = StringLengths.SMALL,
      message = "data-set-management.error.sub-data-set.name.size")
  private String name;
  
  private int numberOfObservations;
  
  private int numberOfAnalyzableVariables;
  
  @NotNull(message = "data-set-management.error.sub-data-set.access-ways.not-null")
  @NotEmpty(message = "data-set-management.error.sub-data-set.access-ways.not-empty")
  @ValidAccessWays(
      message = "data-set-management.error.sub-data-set.access-ways.valid-access-ways")
  private List<String> accessWays;
  
  @NotNull(message = "data-set-management.error.sub-data-set.description.not-null")
  @I18nStringSize(max = StringLengths.MEDIUM,
      message = "data-set-management.error.sub-data-set.description.i18n-string-size")
  @I18nStringNotEmpty(
      message = "data-set-management.error.sub-data-set.description.i18n-string-not-empty")
  private I18nString description;
  
  public String getName() {
    return name;
  }
  
  public void setName(String name) {
    this.name = name;
  }
  
  public int getNumberOfObservations() {
    return numberOfObservations;
  }
  
  public void setNumberOfObservations(int numberOfObservations) {
    this.numberOfObservations = numberOfObservations;
  }
  
  public int getNumberOfAnalyzableVariables() {
    return numberOfAnalyzableVariables;
  }
  
  public void setNumberOfAnalyzableVariables(int numberOfAnalyzableVariables) {
    this.numberOfAnalyzableVariables = numberOfAnalyzableVariables;
  }
  
  public List<String> getAccessWays() {
    return accessWays;
  }
  
  public void setAccessWays(List<String> accessWays) {
    this.accessWays = accessWays;
  }
  
  public I18nString getDescription() {
    return description;
  }
  
  public void setDescription(I18nString description) {
    this.description = description;
  }

  @Override
  public String toString() {
    return "SubDataSet [name=" + name + ", numberOfObservations=" + numberOfObservations
        + ", numberOfAnalyzableVariables=" + numberOfAnalyzableVariables + ", accessWays="
        + accessWays + ", description=" + description + "]";
  }
}
