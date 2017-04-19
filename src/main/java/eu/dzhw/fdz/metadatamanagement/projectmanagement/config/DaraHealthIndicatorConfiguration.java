package eu.dzhw.fdz.metadatamanagement.projectmanagement.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import eu.dzhw.fdz.metadatamanagement.projectmanagement.dao.DaraDao;
import eu.dzhw.fdz.metadatamanagement.projectmanagement.dao.DaraHealthIndicator;

/**
 * Add additional {@link HealthIndicator} of Dara for spring boot actuator.
 * 
 * @author Daniel Katzberg
 *
 */
@Configuration
public class DaraHealthIndicatorConfiguration {
  
  @Autowired
  private DaraDao daraDao;

  /**
   * Adds a {@link DaraHealthIndicator} to the application context.
   * 
   * @return Dara Health Indicator.
   */
  @Bean
  public DaraHealthIndicator daraHealthIndicator() {
    return new DaraHealthIndicator(this.daraDao);
  }
  
}