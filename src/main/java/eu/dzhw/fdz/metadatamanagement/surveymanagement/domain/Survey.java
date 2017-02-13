package eu.dzhw.fdz.metadatamanagement.surveymanagement.domain;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.NotEmpty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.google.common.base.MoreObjects;

import eu.dzhw.fdz.metadatamanagement.common.domain.AbstractRdcDomainObject;
import eu.dzhw.fdz.metadatamanagement.common.domain.I18nString;
import eu.dzhw.fdz.metadatamanagement.common.domain.Period;
import eu.dzhw.fdz.metadatamanagement.common.domain.util.Patterns;
import eu.dzhw.fdz.metadatamanagement.common.domain.validation.I18nStringNotEmpty;
import eu.dzhw.fdz.metadatamanagement.common.domain.validation.I18nStringSize;
import eu.dzhw.fdz.metadatamanagement.common.domain.validation.StringLengths;
import eu.dzhw.fdz.metadatamanagement.surveymanagement.domain.validation.ValidSurveyIdName;
import eu.dzhw.fdz.metadatamanagement.surveymanagement.domain.validation.ValidUniqueSurveyNumber;
import net.karneim.pojobuilder.GeneratePojoBuilder;

/**
 * A Survey.
 *
 * @author Daniel Katzberg
 */
@Document(collection = "surveys")
@GeneratePojoBuilder(
    intoPackage = "eu.dzhw.fdz.metadatamanagement.surveymanagement.domain.builders")
@ValidSurveyIdName(message = "survey-management.error.survey.id.valid-survey-id-name")
@ValidUniqueSurveyNumber(message = "survey-management.error"
    + ".survey.unique-survey-number")
public class Survey extends AbstractRdcDomainObject {

  /* Domain Object Attributes */
  @Id
  @NotEmpty(message = "survey-management.error.survey.id.not-empty")
  @Size(max = StringLengths.MEDIUM, message = "survey-management.error.survey.id.size")
  @Pattern(
      regexp = Patterns.GERMAN_ALPHANUMERIC_WITH_UNDERSCORE_AND_MINUS_AND_DOT_AND_EXCLAMATIONMARK,
      message = "survey-management.error.survey.id.pattern")
  private String id;

  @I18nStringSize(max = StringLengths.MEDIUM,
      message = "survey-management.error.survey.title.i18n-string-size")
  private I18nString title;

  @NotNull(message = "survey-management.error.survey.field-period.not-null")
  @Valid
  private Period fieldPeriod;

  /* Foreign Keys */
  @Indexed
  @NotEmpty(message = "survey-management.error.survey.data-acquisition-project.id.not-empty")
  private String dataAcquisitionProjectId;

  @NotNull(message = "survey-management.error.survey.population.not-null")
  @I18nStringNotEmpty(message = "survey-management.error.survey.population.i18n-string-not-empty")
  @I18nStringSize(max = StringLengths.LARGE,
      message = "survey-management.error.survey.population.i18n-string-size")
  private I18nString population;

  @NotNull(message = "survey-management.error.survey.sample.not-null")
  @I18nStringNotEmpty(message = "survey-management.error.survey.sample.i18n-string-not-empty")
  @I18nStringSize(max = StringLengths.LARGE,
      message = "survey-management.error.survey.sample.i18n-string-size")
  private I18nString sample;

  @NotNull(message = "survey-management.error.survey.survey-method.not-null")
  @I18nStringNotEmpty(
      message = "survey-management.error.survey.survey-method.i18n-string-not-empty")
  @I18nStringSize(max = StringLengths.MEDIUM,
      message = "survey-management.error.survey.survey-method.i18n-string-size")
  private I18nString surveyMethod;

  @NotNull(message = "survey-management.error.survey.gross-sample-size.not-null")
  private Integer grossSampleSize;
  
  @NotNull(message = "survey-management.error.survey.sample-size.not-null")
  private Integer sampleSize;
  
  @NotNull(message = "survey-management.error.survey.response-rate.not-null")
  private Double responseRate;
  
  @NotNull(message = "survey-management.error.survey.number.not-null")
  private Integer number;
  
  private String studyId;
  
  /*
   * (non-Javadoc)
   *
   * @see eu.dzhw.fdz.metadatamanagement.domain.AbstractRdcDomainObject#getId()
   */
  @Override
  public String getId() {
    return id;
  }

 
  /*
   * (non-Javadoc)
   * @see eu.dzhw.fdz.metadatamanagement.common.domain.AbstractRdcDomainObject#toString()
   */
  @Override
  public String toString() {
    return MoreObjects.toStringHelper(this)
      .add("super", super.toString())
      .add("id", id)
      .add("title", title)
      .add("fieldPeriod", fieldPeriod)
      .add("dataAcquisitionProjectId", dataAcquisitionProjectId)
      .add("population", population)
      .add("sample", sample)
      .add("surveyMethod", surveyMethod)
      .add("grossSampleSize", grossSampleSize)
      .add("sampleSize", sampleSize)
      .add("responseRate", responseRate)
      .add("number", number)
      .add("studyId", studyId)
      .toString();
  }

  /* GETTER / SETTER */
  public I18nString getTitle() {
    return title;
  }

  public void setTitle(I18nString title) {
    this.title = title;
  }

  public Period getFieldPeriod() {
    return fieldPeriod;
  }

  public void setFieldPeriod(Period fieldPeriod) {
    this.fieldPeriod = fieldPeriod;
  }

  public void setId(String id) {
    this.id = id;
  }

  public I18nString getPopulation() {
    return population;
  }

  public void setPopulation(I18nString population) {
    this.population = population;
  }

  public I18nString getSample() {
    return sample;
  }

  public void setSample(I18nString sample) {
    this.sample = sample;
  }

  public I18nString getSurveyMethod() {
    return surveyMethod;
  }

  public void setSurveyMethod(I18nString surveyMethod) {
    this.surveyMethod = surveyMethod;
  }

  public String getDataAcquisitionProjectId() {
    return dataAcquisitionProjectId;
  }

  public void setDataAcquisitionProjectId(String dataAcquisitionProjectId) {
    this.dataAcquisitionProjectId = dataAcquisitionProjectId;
  }


  public Integer getGrossSampleSize() {
    return grossSampleSize;
  }


  public void setGrossSampleSize(Integer grossSampleSize) {
    this.grossSampleSize = grossSampleSize;
  }


  public Integer getSampleSize() {
    return sampleSize;
  }


  public void setSampleSize(Integer sampleSize) {
    this.sampleSize = sampleSize;
  }


  public Double getResponseRate() {
    return responseRate;
  }


  public void setResponseRate(Double responseRate) {
    this.responseRate = responseRate;
  }


  public Integer getNumber() {
    return number;
  }


  public void setNumber(Integer number) {
    this.number = number;
  }


  public String getStudyId() {
    return studyId;
  }


  public void setStudyId(String studyId) {
    this.studyId = studyId;
  }  
}
