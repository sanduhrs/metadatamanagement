/**
 * 
 */
package eu.dzhw.fdz.metadatamanagement.common.unittesthelper.util;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import eu.dzhw.fdz.metadatamanagement.common.domain.DataTypes;
import eu.dzhw.fdz.metadatamanagement.common.domain.Release;
import eu.dzhw.fdz.metadatamanagement.common.domain.ScaleLevels;
import eu.dzhw.fdz.metadatamanagement.common.domain.builders.I18nStringBuilder;
import eu.dzhw.fdz.metadatamanagement.common.domain.builders.PeriodBuilder;
import eu.dzhw.fdz.metadatamanagement.common.domain.builders.ReleaseBuilder;
import eu.dzhw.fdz.metadatamanagement.concept.domain.builders.ConceptBuilder;
import eu.dzhw.fdz.metadatamanagement.conceptmanagement.domain.Concept;
import eu.dzhw.fdz.metadatamanagement.datasetmanagement.domain.DataSet;
import eu.dzhw.fdz.metadatamanagement.datasetmanagement.domain.builders.DataSetBuilder;
import eu.dzhw.fdz.metadatamanagement.domain.BibliographicalReference;
import eu.dzhw.fdz.metadatamanagement.domain.RuleExpressionLanguages;
import eu.dzhw.fdz.metadatamanagement.domain.builders.BibliographicalReferenceBuilder;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.DataAcquisitionProject;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.domain.builders.DataAcquisitionProjectBuilder;
import eu.dzhw.fdz.metadatamanagement.questionmanagement.domain.AtomicQuestion;
import eu.dzhw.fdz.metadatamanagement.questionmanagement.domain.AtomicQuestionTypes;
import eu.dzhw.fdz.metadatamanagement.questionmanagement.domain.Questionnaire;
import eu.dzhw.fdz.metadatamanagement.questionmanagement.domain.builders.AtomicQuestionBuilder;
import eu.dzhw.fdz.metadatamanagement.questionmanagement.domain.builders.QuestionnaireBuilder;
import eu.dzhw.fdz.metadatamanagement.surveymanagement.domain.Survey;
import eu.dzhw.fdz.metadatamanagement.surveymanagement.domain.builders.SurveyBuilder;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.AccessWays;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.FilterDetails;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.FilterExpressionLanguages;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.GenerationDetails;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.Statistics;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.Value;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.Variable;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.builders.FilterDetailsBuilder;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.builders.GenerationDetailsBuilder;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.builders.StatisticsBuilder;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.builders.ValueBuilder;
import eu.dzhw.fdz.metadatamanagement.variablemanagement.domain.builders.VariableBuilder;

/**
 * @author Daniel Katzberg
 *
 */
public class UnitTestCreateDomainObjectUtils {

  private UnitTestCreateDomainObjectUtils() {}

  public static DataAcquisitionProject buildDataAcquisitionProject() {

    List<Release> releases = new ArrayList<>();
    releases.add(buildRelease());

    return new DataAcquisitionProjectBuilder().withId("testProject")
      .withSurveySeries(new I18nStringBuilder().build())
      .withPanelName(new I18nStringBuilder().withDe("German PanelName")
        .withEn("English PanelName")
        .build())
      .withReleases(releases)
      .build();
  }

  public static Survey buildSurvey(String projectId) {
    return new SurveyBuilder().withId(projectId + "-sy1")
      .withDataAcquisitionProjectId(projectId)
      .withFieldPeriod(new PeriodBuilder().withStart(LocalDate.now())
        .withEnd(LocalDate.now())
        .build())
      .withTitle(new I18nStringBuilder().withDe("Titel")
        .withEn("title")
        .build())
      .withQuestionnaireId("QuestionnaireId")
      .build();
  }

  public static DataSet buildDataSet(String projectId, String surveyId) {
    List<String> variableIds = new ArrayList<>();
    variableIds.add("Id001");
    variableIds.add("Id002");
    variableIds.add("Id003");
    List<String> surveyIds = new ArrayList<>();
    variableIds.add(surveyId);
    return new DataSetBuilder().withSurveyIds(surveyIds)
      .withDataAcquisitionProjectId(projectId)
      .withId(projectId + "-ds1")
      .withVariableIds(variableIds)
      .withDescription(new I18nStringBuilder().withDe("De Beschreibung")
        .withEn("En Description")
        .build())
      .build();
  }

  public static AtomicQuestion buildAtomicQuestion(String projectId, String questionnaireId,
      String variableId) {
    return new AtomicQuestionBuilder().withCompositeQuestionName("CompositeQuestioName")
      .withDataAcquisitionProjectId(projectId)
      .withFootnote(new I18nStringBuilder().withDe("De Fussnote")
        .withEn("En Fussnote")
        .build())
      .withId("testAtomicQuestion")
      .withInstruction(new I18nStringBuilder().withDe("De Instruktion")
        .withEn("En Instruction")
        .build())
      .withIntroduction(new I18nStringBuilder().withDe("De Einführung")
        .withEn("En Introduction")
        .build())
      .withName("Name")
      .withQuestionnaireId(questionnaireId)
      .withQuestionText(new I18nStringBuilder().withDe("De Frage")
        .withEn("En Question")
        .build())
      .withSectionHeader(new I18nStringBuilder().withDe("De Kapitelüberschrift")
        .withEn("En Section header")
        .build())
      .withType(new I18nStringBuilder().withDe(AtomicQuestionTypes.OPEN.getDe())
          .withEn(AtomicQuestionTypes.OPEN.getEn())
          .build())
      .withVariableId(variableId).withId(projectId + "-" + variableId)
      .build();
  }

  public static Variable buildVariable(String projectId, String surveyId) {

    // Prepare Variable
    List<String> accessWays = new ArrayList<>();
    accessWays.add(AccessWays.CUF);
    accessWays.add(AccessWays.REMOTE);
    accessWays.add(AccessWays.SUF);
    List<String> withSameVariablesInPanel = new ArrayList<>();
    List<Value> withValues = new ArrayList<>();
    withValues.add(buildValueBuilder());
    String name = "name";

    // Create Variable
    return new VariableBuilder().withId(projectId + "-" + name)
      .withDataType(DataTypes.NUMERIC)
      .withScaleLevel(ScaleLevels.CONTINOUS)
      .withDataAcquisitionProjectId(projectId)
      .withSurveyId(surveyId)
      .withLabel(new I18nStringBuilder().withDe("label")
        .withEn("label")
        .build())
      .withName(name)
      .withAccessWays(accessWays)
      .withDescription(new I18nStringBuilder().withDe("De Beschreibung")
        .withEn("En Description")
        .build())
      .withFilterDetails(new FilterDetailsBuilder()
          .withFilterDescription(new I18nStringBuilder().withDe("De Filterbeschreibung")
            .withEn("En Filter Description")
            .build())
          .withFilterExpressionLanguage(FilterExpressionLanguages.STATA)
          .withFilterExpression("Filter Expression").build())      
      .withSameVariablesInPanel(withSameVariablesInPanel)
      .withValues(withValues)
      .withConceptId("ConceptId001")
      .withStatistics(buildStatistics())
      .withGenerationDetails(buildGenerationDetails())
      .build();
  }

  public static Concept buildConcept() {
    return new ConceptBuilder().withDescription(new I18nStringBuilder().withDe("De Beschreibung")
      .withEn("En Description")
      .build())
      .withId("ConceptId001")
      .withName(new I18nStringBuilder().withDe("Deutscher Name")
        .withEn("English name")
        .build())
      .build();
  }

  public static Questionnaire buildQuestionnaire(String projectId) {
    return new QuestionnaireBuilder().withDataAcquisitionProjectId(projectId)
      .withId("testQuestionnaire")
      .withSurveyId("testSurveyId")
      .build();
  }

  public static Statistics buildStatistics() {
    return new StatisticsBuilder().withFirstQuartile(70.0)
      .withHighWhisker(130.0)
      .withKurtosis(234.0)
      .withLowWhisker(30.0)
      .withMaximum(140.0)
      .withMeanValue(87.5)
      .withMedian(90.0)
      .withMinimum(0.0)
      .withSkewness(123.0)
      .withStandardDeviation(40.0)
      .withThirdQuartile(110.0)
      .build();
  }

  public static GenerationDetails buildGenerationDetails() {
    return new GenerationDetailsBuilder()
      .withDescription(new I18nStringBuilder().withDe("De Beschreibung")
        .withEn("En Description")
        .build())
      .withRule("Rule 123 to 234")
      .withRuleExpressionLanguage(RuleExpressionLanguages.R)
      .build();
  }

  public static Value buildValueBuilder() {
    return new ValueBuilder().withAbsoluteFrequency(123)
      .withCode(1234)
      .withIsAMissing(false)
      .withLabel(new I18nStringBuilder().withDe("De Label")
        .withEn("En Lable")
        .build())
      .withRelativeFrequency(43.78)
      .build();
  }

  public static Release buildRelease() {
    return new ReleaseBuilder().withDoi("A Test Doi")
      .withNotes(new I18nStringBuilder().withDe("Eine Notiz für die Version 1.0")
        .withEn("A notice for the version 1.0.")
        .build())
      .withVersion("1.0")
      .withDate(ZonedDateTime.now())
      .build();
  }

  public static BibliographicalReference buildBibliographicalReference() {
    return new BibliographicalReferenceBuilder().withAuthor("Max Mustermann")
      .withBookTitle("Ein Testbuch fuer Testzwecke")
      .withChapter("Kapitel 1 - Der Test")
      .withEdition("Edition 1")
      .withEditor("Mara Mustermann")
      .withHowPublished("Gar nicht.")
      .withId("Reference001")
      .withInstitution("Institution Testbeispiel")
      .withJournal("Testjournal")
      .withNote("Eine Notiz")
      .withNumber("12 Number")
      .withOrganization("Testorganisation")
      .withPages("123")
      .withPublicationYear(2016)
      .withPublisher("Testverlag")
      .withSchool("Testschule")
      .withSeries("Eine Testserie")
      .withTitle("Der Titel vom Testbuch")
      .withType("Testtype")
      .withVolume("Volume 1")
      .build();
  }
  
  public static FilterDetails buildFilterDetails() {
    return new FilterDetailsBuilder()
        .withFilterDescription(new I18nStringBuilder()
            .withDe("Eine Filterbeschreibung.")
            .withEn("A filter description.")
            .build())
        .withFilterExpression("A Filter Expression")
        .withFilterExpressionLanguage(FilterExpressionLanguages.STATA)
        .build();
  }

}
