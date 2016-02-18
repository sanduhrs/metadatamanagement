package eu.dzhw.fdz.metadatamanagement.domain.projections;

import org.springframework.data.rest.core.config.Projection;

import eu.dzhw.fdz.metadatamanagement.domain.DataAcquisitionProject;
import eu.dzhw.fdz.metadatamanagement.domain.I18nString;

/**
 * Projection used to expose all attributes (including ids and versions). Spring Data rest does not
 * expose ids and version per default in the json.
 * 
 * @author René Reitmann
 * @author Daniel Katzberg
 */
@Projection(name = "complete", types = DataAcquisitionProject.class)
public interface CompleteDataAcquisitionProjectProjection
    extends AbstractRdcDomainObjectProjection {
  I18nString getSurveySeries();

  I18nString getPanelName();
}
