/**
 * 
 */
package eu.dzhw.fdz.metadatamanagement.data.variablemanagement.documents;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;

import eu.dzhw.fdz.metadatamanagement.data.common.documents.DateRange;
import eu.dzhw.fdz.metadatamanagement.data.variablemanagement.documents.validation.VariableDocumentValidator;
import eu.dzhw.fdz.metadatamanagement.web.AbstractWebTest;

/**
 * @author Daniel Katzberg
 *
 */
public class VariableDocumentTest extends AbstractWebTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(VariableDocumentTest.class);

  @Autowired
  private Validator validator;

  @Autowired
  private VariableDocumentValidator variableDocumentValidator;

  @Test
  public void testEmptyInValidVariableDocument() {

    // Arrange
    VariableDocument variable = new VariableDocument();
    variable.setId("");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variable);

    // Assert
    assertEquals(3, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testEmptyInValidVariable()" + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertThat(variableVialation.getMessageTemplate(),
          anyOf(equalTo("{org.hibernate.validator.constraints.NotEmpty.message}"),
              equalTo("{org.hibernate.validator.constraints.NotBlank.message}")));
    }
  }

  @Test
  public void testEmptyValidVariableDocument() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testInvalidID() {

    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("This ID is not okay.");
    variableDocument.setName("This name is okay.");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidID()" + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.Pattern.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testValidIDWithSigns() {

    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("This-ID_is-okay");
    variableDocument.setName("This name is okay.");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testInvalidName() {

    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsTooLongAndThrowAnException.");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidName()" + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.Size.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testInvalidLabel() {

    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setLabel("ThisLabelIsNotOkay.ItIsTooLongAndThrowsAnException."
        + "ButTheLabelLengthIsVeryLong.ItNeedsManyWordsForTheException.");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidLabel()" + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.Size.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testValidLabel() {

    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setLabel("ThisLabelIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testValidDataField() {
    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setDataType("string");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testInvalidDataField() {
    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setDataType("sTrinGIsNotOkay");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidDataField() " + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals(
          "{eu.dzhw.fdz.metadatamanagement.data.variablemanagement."
              + "documents.validation.validdatatype.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testValidScaleLevel() {

    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setScaleLevel("ordinal");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testInvalidScaleLevel() {
    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setScaleLevel("oRdiNalIsNotOkay");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidDataField() " + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals(
          "{eu.dzhw.fdz.metadatamanagement.data.variablemanagement."
              + "documents.validation.validscalelevel.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testNotNullScaleLevelAtNumericDataType() {
    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setDataType("numeric");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act
    Errors errors = new BeanPropertyBindingResult(variableDocument, "variableDocument");
    variableDocumentValidator.validate(variableDocument, errors);

    // Assert
    assertThat(errors.getFieldError("scaleLevel").getCode(),
        is(VariableDocumentValidator.MANDATORY_SCALE_LEVEL_MESSAGE_CODE));

  }

  @Test
  public void testInvalidLabelAtAnswerOption() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    AnswerOption answerOption = new AnswerOption();
    answerOption
        .setLabel("AddAExtraLabelForAnTestValidationError.AddAExtraLabelForAnTestValidationError.");
    List<AnswerOption> answerOptions = new ArrayList<>();
    answerOptions.add(answerOption);
    variableDocument.setAnswerOptions(answerOptions);

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Arrange
    assertEquals(2, variableViolations.size());
    Iterator<ConstraintViolation<VariableDocument>> ite = variableViolations.iterator();

    // both messages found (it is not possible to expect an exact order of error messages
    boolean foundSize = false;
    boolean foundNotNull = false;
    while (ite.hasNext()) {
      String msgTemplate = ite.next().getMessageTemplate();

      LOGGER.debug("testInvalidLabelAtAnswerOption() " + msgTemplate);

      if (msgTemplate.equals("{javax.validation.constraints.Size.message}") && !foundSize) {
        foundSize = true;
      }
      if (msgTemplate.equals("{javax.validation.constraints.NotNull.message}") && !foundNotNull) {
        foundNotNull = true;
      }
    }
    assertEquals(true, foundSize);
    assertEquals(true, foundNotNull);
  }

  @Test
  public void testInvalidCodeAtAnswerOption() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    AnswerOption answerOption = new AnswerOption();
    answerOption.setLabel("This label is okay.");
    answerOption.setCode(null);
    List<AnswerOption> answerOptions = new ArrayList<>();
    answerOptions.add(answerOption);
    variableDocument.setAnswerOptions(answerOptions);

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidLabelAtAnswerOption() " + variableVialation.getMessageTemplate()
          + " -> " + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.NotNull.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testValidAnswerOption() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    AnswerOption answerOption = new AnswerOption();
    answerOption.setCode(5);
    answerOption.setLabel("Label is okay.");
    List<AnswerOption> answerOptions = new ArrayList<>();
    answerOptions.add(answerOption);
    variableDocument.setAnswerOptions(answerOptions);

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
    assertEquals("Label is okay.", variableDocument.getAnswerOptions().get(0).getLabel());
    assertEquals(Integer.valueOf(5), variableDocument.getAnswerOptions().get(0).getCode());
  }

  @Test
  public void testInvalidAnswerOptionWithANullCode() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    AnswerOption answerOption = new AnswerOption();
    answerOption.setCode(5);
    answerOption.setLabel("Label is okay.");
    AnswerOption answerOption1 = new AnswerOption();
    answerOption1.setCode(null);
    answerOption1.setLabel("Label is okay.");
    AnswerOption answerOption2 = new AnswerOption();
    answerOption2.setCode(2);
    answerOption2.setLabel("Label is okay.");
    List<AnswerOption> answerOptions = new ArrayList<>();
    answerOptions.add(answerOption);
    answerOptions.add(answerOption1);
    answerOptions.add(answerOption2);
    variableDocument.setAnswerOptions(answerOptions);

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidLabelAtAnswerOption() " + variableVialation.getMessageTemplate()
          + " -> " + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.NotNull.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testInvalidVariableDocumentSurveyWithEmptyFields() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    VariableSurvey variableSurvey = new VariableSurvey();
    variableDocument.setVariableSurvey(variableSurvey);

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(5, variableViolations.size());
    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidVariableDocumentSurveyWithEmptyFields()"
          + variableVialation.getMessageTemplate() + " -> " + variableVialation.getMessage());

      assertThat(variableVialation.getMessageTemplate(),
          anyOf(equalTo("{org.hibernate.validator.constraints.NotBlank.message}"),
              equalTo("{eu.dzhw.fdz.metadatamanagement.data.variablemanagement."
                  + "documents.validation.uniquevariablealias.message}"),
          equalTo("{javax.validation.constraints.NotNull.message}")));

    }
  }

  @Test
  public void testInvalidVariableDocumentSurveyWithInvalidAlias() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    VariableSurvey variableSurvey = new VariableSurvey();
    variableDocument.setVariableSurvey(variableSurvey);
    variableDocument.getVariableSurvey().setSurveyId("AliasIsOkay.");
    variableDocument.getVariableSurvey().setTitle("TitleIsOkay.");
    variableDocument.getVariableSurvey()
        .setVariableAlias("ThisAliasIsTooLong.ItWillThrowAnException");
    variableSurvey.setSurveyPeriod(new DateRange());

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());

    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidVariableDocumentSurveyWithInvalidSurveyId()"
          + variableVialation.getMessageTemplate() + " -> " + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.Size.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testInvalidVariableDocumentSurveyWithInvalidSurveyId() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    VariableSurvey variableSurvey = new VariableSurvey();
    variableDocument.setVariableSurvey(variableSurvey);
    variableDocument.getVariableSurvey().setSurveyId("SurveyIdIsTooLong.ItWillThrowAnException");
    variableDocument.getVariableSurvey().setTitle("TitleIsOkay.");
    variableDocument.getVariableSurvey().setVariableAlias(variableDocument.getName());
    variableDocument.getVariableSurvey().setSurveyPeriod(new DateRange());

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());

    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidVariableDocumentSurveyWithInvalidSurveyId()"
          + variableVialation.getMessageTemplate() + " -> " + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.Size.message}",
          variableVialation.getMessageTemplate());
    }
  }


  @Test
  public void testInvalidVariableDocumentSurveyWithInvalidTitle() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    VariableSurvey variableSurvey = new VariableSurvey();
    variableDocument.setVariableSurvey(variableSurvey);
    variableDocument.getVariableSurvey().setVariableAlias(variableDocument.getName());
    variableDocument.getVariableSurvey().setSurveyId("SurveyIdIsOkay.");
    variableDocument.getVariableSurvey().setTitle("TitleIsNotOkay.TheTitleIsTooLong.");
    variableDocument.getVariableSurvey().setSurveyPeriod(new DateRange());

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());

    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("variableSurveyTest()" + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals("{javax.validation.constraints.Size.message}",
          variableVialation.getMessageTemplate());
    }
  }


  @Test
  public void testValidVariableDocumentSurvey() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");
    VariableSurvey variableSurvey = new VariableSurvey();
    variableDocument.setVariableSurvey(variableSurvey);
    variableDocument.getVariableSurvey().setSurveyId("SurveyIdIsOkay.");
    variableDocument.getVariableSurvey().setTitle("TitleIsOkay.");
    variableDocument.getVariableSurvey().setVariableAlias(variableDocument.getName());
    variableDocument.getVariableSurvey().setSurveyPeriod(new DateRange());

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testInvalidDateRange() {

    // Assert
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");

    VariableSurvey variableSurvey = new VariableSurvey();
    variableSurvey.setSurveyId("SurveyIdIsOkay.");
    variableSurvey.setTitle("TitleIsOkay.");
    variableSurvey.setVariableAlias(variableDocument.getName());

    DateRange surveyPeriod = new DateRange();
    surveyPeriod.setStartDate(LocalDate.now());
    surveyPeriod.setEndDate(surveyPeriod.getStartDate().minusDays(2));
    variableSurvey.setSurveyPeriod(surveyPeriod);

    variableDocument.setVariableSurvey(variableSurvey);

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    // assertEquals(1, variableViolations.size());

    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("testInvalidDateRange()" + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals(
          "{eu.dzhw.fdz.metadatamanagement.data.variablemanagement."
              + "common.validation.validdaterange.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testValidDateRange() {

    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");

    VariableSurvey variableSurvey = new VariableSurvey();
    variableSurvey.setSurveyId("SurveyIdIsOkay.");
    variableSurvey.setTitle("TitleIsOkay.");
    variableSurvey.setVariableAlias(variableDocument.getName());

    DateRange surveyPeriod = new DateRange();
    surveyPeriod.setStartDate(LocalDate.now());
    surveyPeriod.setEndDate(surveyPeriod.getStartDate().plusDays(2));
    variableSurvey.setSurveyPeriod(surveyPeriod);

    variableDocument.setVariableSurvey(variableSurvey);

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testEmptyDateRangeValidator() {

    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ID");
    variableDocument.setName("name");
    variableDocument.setQuestion("DefaultQuestion?");
    variableDocument.setVariableSurvey(new VariableSurvey());
    variableDocument.getVariableSurvey().setSurveyId("ID_Survey");
    variableDocument.getVariableSurvey().setTitle("TitleIsOkay.");
    variableDocument.getVariableSurvey().setVariableAlias(variableDocument.getName());
    variableDocument.getVariableSurvey().setSurveyPeriod(new DateRange());

    // set a date, but without a start and end date
    variableDocument.getVariableSurvey().setSurveyPeriod(new DateRange());


    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // everything okay
    assertEquals(0, variableViolations.size());
  }

  @Test
  public void testVariableDocumentToString() {
    // Arrange
    VariableDocument variableDocument = new VariableDocument();

    // Act

    // Assert
    assertEquals("VariableDocument [variableSurvey=null, name=null, dataType=null, "
        + "label=null, scaleLevel=null, answerOptions=null]", variableDocument.toString());
  }

  @Test
  public void testVariableDocumentWithSurvayToString() {
    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setVariableSurvey(new VariableSurvey());
    variableDocument.setAnswerOptions(new ArrayList<>());

    // Act

    // Assert
    assertEquals(
        "VariableDocument [variableSurvey=VariableSurvey [surveyId=null, title=null, surveyPeriod=null, "
            + "variableAlias=null], name=null, dataType=null, label=null, scaleLevel=null, answerOptions=[]]",
        variableDocument.toString());
  }

  @Test
  public void testVariableDocumentWithSurveyAndEmptyDateRangeToString() {
    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setVariableSurvey(new VariableSurvey());
    variableDocument.setAnswerOptions(new ArrayList<>());
    variableDocument.getVariableSurvey().setSurveyPeriod(new DateRange());

    // Act

    // Assert
    assertEquals(
        "VariableDocument [variableSurvey=VariableSurvey [surveyId=null, title=null, "
            + "surveyPeriod=DateRange [startDate=null, endDate=null], variableAlias=null], name=null, "
            + "dataType=null, label=null, scaleLevel=null, answerOptions=[]]",
        variableDocument.toString());
  }


  @Test
  public void testVariableDocumentWithSurveyAndFilledDateRangeToString() {
    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setVariableSurvey(new VariableSurvey());
    variableDocument.setAnswerOptions(new ArrayList<>());
    variableDocument.getVariableSurvey().setSurveyPeriod(new DateRange());
    variableDocument.getVariableSurvey().getSurveyPeriod().setStartDate(LocalDate.of(2015, 1, 1));
    variableDocument.getVariableSurvey().getSurveyPeriod().setEndDate(LocalDate.of(2015, 2, 1));

    // Act

    // Assert
    assertEquals(
        "VariableDocument [variableSurvey=VariableSurvey [surveyId=null, title=null, "
            + "surveyPeriod=DateRange [startDate=2015-01-01, endDate=2015-02-01], variableAlias=null], "
            + "name=null, dataType=null, label=null, scaleLevel=null, answerOptions=[]]",
        variableDocument.toString());
  }

  @Test
  public void testInvalidVariableDocumentWithEmptyQuest() {
    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");

    // Act
    Set<ConstraintViolation<VariableDocument>> variableViolations =
        this.validator.validate(variableDocument);

    // Assert
    assertEquals(1, variableViolations.size());

    for (ConstraintViolation<VariableDocument> variableVialation : variableViolations) {

      LOGGER.debug("variableSurveyTest()" + variableVialation.getMessageTemplate() + " -> "
          + variableVialation.getMessage());

      assertEquals("{org.hibernate.validator.constraints.NotBlank.message}",
          variableVialation.getMessageTemplate());
    }
  }

  @Test
  public void testHashCode() {
    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");

    // Act

    // Assert
    assertEquals(-923525232, variableDocument.hashCode());
  }

  @Test
  public void testEquals() {
    // Arrange
    VariableDocument variableDocument = new VariableDocument();
    variableDocument.setId("ThisIDisOkay");
    variableDocument.setName("ThisNameIsOkay.");
    variableDocument.setQuestion("DefaultQuestion?");

    VariableDocument variableDocument2 = new VariableDocument();
    VariableDocument variableDocument3 = new VariableDocument();

    List<AnswerOption> answerOptions = new ArrayList<>();
    List<AnswerOption> answerOptions2 = new ArrayList<>();
    AnswerOption answerOption = new AnswerOption();
    answerOption.setCode(1);
    answerOption.setLabel("Label");
    answerOptions2.add(answerOption);

    VariableSurvey variableSurvey = new VariableSurvey();
    variableSurvey.setSurveyId("1");
    VariableSurvey variableSurvey2 = new VariableSurvey();
    variableSurvey.setSurveyId("2");


    // Act
    boolean checkNull = variableDocument.equals(null);
    boolean checkDifferentClass = variableDocument.equals(new Object());
    boolean checkDifferentVariableDocument = variableDocument.equals(variableDocument2);
    boolean checkDifferentVariableDocumentWithNullName =
        variableDocument3.equals(variableDocument2);
    variableDocument.setAnswerOptions(answerOptions);
    boolean checkAnswerOptionsOther = variableDocument2.equals(variableDocument);
    variableDocument2.setAnswerOptions(answerOptions);
    boolean checkAnswerOptionsOtherBoth = variableDocument2.equals(variableDocument);
    variableDocument2.setAnswerOptions(answerOptions2);
    boolean checkAnswerOptionsOtherBothDifferent = variableDocument2.equals(variableDocument);
    variableDocument.setDataType("string");
    variableDocument2.setAnswerOptions(null);
    variableDocument.setAnswerOptions(null);
    boolean checkDifferentDataType = variableDocument2.equals(variableDocument);
    variableDocument2.setDataType("string");
    boolean checkDifferentDataTypeBoth = variableDocument2.equals(variableDocument);
    variableDocument2.setDataType("numeric");
    boolean checkDifferentDataTypeBothDifferent = variableDocument2.equals(variableDocument);
    variableDocument.setDataType(null);
    variableDocument2.setDataType(null);
    variableDocument.setLabel("Label 1");
    boolean checkLabelOther = variableDocument2.equals(variableDocument);
    variableDocument2.setLabel("Label 2");
    boolean checkLabelBoth = variableDocument2.equals(variableDocument);
    variableDocument2.setLabel("Label 1");
    boolean checkLabelBothSame = variableDocument2.equals(variableDocument);
    variableDocument.setLabel(null);
    variableDocument2.setLabel(null);
    variableDocument2.setName("Another Name");
    boolean checkDifferentName = variableDocument2.equals(variableDocument);
    variableDocument2.setName(variableDocument.getName());
    boolean checkSameName = variableDocument2.equals(variableDocument);
    variableDocument2.setName(null);
    variableDocument.setName(null);
    variableDocument.setQuestion("DefaultQuestion?");
    boolean checkQuestionOther = variableDocument2.equals(variableDocument);
    variableDocument2.setQuestion("AnotherDefaultQuestion?");
    boolean checkQuestionBoth = variableDocument2.equals(variableDocument);
    variableDocument2.setQuestion(variableDocument.getQuestion());
    boolean checkQuestionBothSame = variableDocument2.equals(variableDocument);
    variableDocument.setQuestion(null);
    variableDocument2.setQuestion(null);
    variableDocument.setScaleLevel("nominal");
    boolean checkScaleLevelOther = variableDocument2.equals(variableDocument);
    variableDocument2.setScaleLevel("metric");
    boolean checkScaleLevelBoth = variableDocument2.equals(variableDocument);
    variableDocument2.setScaleLevel(variableDocument.getScaleLevel());
    boolean checkScaleLevelBothSame = variableDocument2.equals(variableDocument);
    variableDocument.setScaleLevel(null);
    variableDocument2.setScaleLevel(null);
    variableDocument.setVariableSurvey(variableSurvey);
    boolean checkVariableSurveyOther = variableDocument2.equals(variableDocument);
    variableDocument2.setVariableSurvey(variableSurvey2);
    boolean checkVariableSurveyBoth = variableDocument2.equals(variableDocument);
    variableDocument2.setVariableSurvey(variableSurvey);
    boolean checkVariableSurveyBothSame = variableDocument2.equals(variableDocument);

    // Assert
    assertEquals(false, checkNull);
    assertEquals(false, checkDifferentClass);
    assertEquals(false, checkDifferentVariableDocument);
    assertEquals(true, checkDifferentVariableDocumentWithNullName);
    assertEquals(false, checkAnswerOptionsOther);
    assertEquals(false, checkAnswerOptionsOtherBoth);
    assertEquals(false, checkAnswerOptionsOtherBothDifferent);
    assertEquals(false, checkDifferentDataType);
    assertEquals(false, checkDifferentDataTypeBoth);
    assertEquals(false, checkDifferentDataTypeBothDifferent);
    assertEquals(false, checkLabelOther);
    assertEquals(false, checkLabelBoth);
    assertEquals(false, checkLabelBothSame);
    assertEquals(false, checkDifferentName);
    assertEquals(false, checkSameName);
    assertEquals(false, checkQuestionOther);
    assertEquals(false, checkQuestionBoth);
    assertEquals(true, checkQuestionBothSame);
    assertEquals(false, checkScaleLevelOther);
    assertEquals(false, checkScaleLevelBoth);
    assertEquals(true, checkScaleLevelBothSame);
    assertEquals(false, checkVariableSurveyOther);
    assertEquals(false, checkVariableSurveyBoth);
    assertEquals(true, checkVariableSurveyBothSame);
  }
}
