package br.com.m4rc310.weather.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Instantiates a new m city.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MCity {
	
	/** The id. */
	@JsonAlias("id")
	private Long id;
	
	/** The name. */
	@JsonAlias("nome")
	private String name;
	
	

}
