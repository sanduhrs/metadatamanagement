package eu.dzhw.fdz.metadatamanagement.web.questionmanagement;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.hateoas.mvc.ResourceAssemblerSupport;
import org.springframework.stereotype.Component;

import eu.dzhw.fdz.metadatamanagement.data.questionmanagement.documents.QuestionDocument;
import eu.dzhw.fdz.metadatamanagement.web.questionmanagement.search.QuestionSearchController;

/**
 * Convert a {@link QuestionDocument} into a {@link QuestionResource}.
 * 
 * @author Daniel Katzberg
 *
 */
@Component
public class QuestionResourceAssembler
    extends ResourceAssemblerSupport<QuestionDocument, QuestionResource> {

  public QuestionResourceAssembler() {
    //TODO Change to Details Controller
    super(QuestionSearchController.class, QuestionResource.class);
  }

  /*
   * (non-Javadoc)
   * @see org.springframework.hateoas.ResourceAssembler#toResource(java.lang.Object)
   */
  @Override
  public QuestionResource toResource(QuestionDocument questionDocument) {
    QuestionResource resource;
    resource = createResourceWithId(questionDocument.getId(), questionDocument,
        LocaleContextHolder.getLocale().getLanguage());
    return resource;
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * org.springframework.hateoas.mvc.ResourceAssemblerSupport#instantiateResource(java.lang.Object)
   */
  @Override
  protected QuestionResource instantiateResource(QuestionDocument questionDocument) {
    return new QuestionResource(questionDocument);
  }

}
