package br.com.m4rc310.weather.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Instantiates a new m weather.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeather {
	
	/** The current. */
	@JsonAlias("current")
	private MWeatherCurrent current;
	
	/** The minutely. */
	@JsonAlias("minutely")
	private List<MWeatherMinutely> minutely;
}
