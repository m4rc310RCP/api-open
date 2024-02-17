package br.com.m4rc310.weather.autoconfigure;


import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import br.com.m4rc310.weather.services.MWeatherService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AutoConfiguration
@EnableConfigurationProperties(MWeatherAutoConfiguration.MWeatherProperty.class)
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnProperty(name = "br.com.m4rc310.weather.enable", havingValue = "true", matchIfMissing = false)
public class MWeatherAutoConfiguration {
	
	@Bean("status-weather")
	void status() {
		log.info("~> Module '{}' has been loaded.", "br.com.m4rc310.weather");
	}
	
	
	/**
	 * Load M weather service.
	 *
	 * @return the m weather service
	 */
	@Bean
	MWeatherService loadMWeatherService() {
		return new MWeatherService();
	}
	
	/**
	 * Instantiates a new m weather property.
	 */
	@Data
	@ConfigurationProperties("br.com.m4rc310.weather")
	public class MWeatherProperty {
		
		/** The enable. */
		private boolean enable;
	}
}
