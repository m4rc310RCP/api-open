package br.com.m4rc310.weather.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Instantiates a new m weather current weather.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeatherCurrentWeather {
	
	/** The id. */
	@JsonAlias("id")
	private Long id;
	
	/** The main. */
	@JsonAlias("main")
	private String main;
	
	/** The description. */
	@JsonAlias("description")
	private String description;
	
	/** The icon. */
	@JsonAlias("icon")
	private String icon;
}
