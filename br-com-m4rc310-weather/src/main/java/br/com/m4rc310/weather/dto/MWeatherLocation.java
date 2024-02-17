package br.com.m4rc310.weather.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Instantiates a new m weather location.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeatherLocation {
	
	/** The name. */
	@JsonAlias("name")
	private String name;
	
	/** The state. */
	@JsonAlias("state")
	private String state;
	
	/** The latitude. */
	@JsonAlias("lat")
	private BigDecimal latitude;
	
	/** The longitude. */
	@JsonAlias("lon")
	private BigDecimal longitude;
}
