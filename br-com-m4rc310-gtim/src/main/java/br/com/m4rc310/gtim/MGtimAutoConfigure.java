package br.com.m4rc310.gtim;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

import br.com.m4rc310.gtim.services.MEanCache;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>
 * MGtimAutoConfigure class.
 * </p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Slf4j
@AutoConfiguration
@EnableWebSecurity
@EnableConfigurationProperties(MGtimProperties.class)
@ConditionalOnProperty(name = MGtimProperties.ENABLE_GTIM, havingValue = "true", matchIfMissing = false)
public class MGtimAutoConfigure {
	
	@Bean(MGtimProperties.ENABLE_GTIM)
	void init() {
		log.info("~> Loading {}...", getClass().getName());
	}
	
	@Bean
	MEanCache loadMEanCache() {
		return new MEanCache();
	}
}
