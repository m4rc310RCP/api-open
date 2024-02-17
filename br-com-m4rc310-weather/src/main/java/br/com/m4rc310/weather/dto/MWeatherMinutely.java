package br.com.m4rc310.weather.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Instantiates a new m weather minutely.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeatherMinutely {
	
	/** The date. */
	@JsonAlias("dt")
	private Long date;
	
	/** The precipitation. */
	@JsonAlias("precipitation")
	private BigDecimal precipitation;
}
