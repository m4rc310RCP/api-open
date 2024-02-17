package br.com.m4rc310.weather.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Instantiates a new m region.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MRegion {
	
	/** The id. */
	@JsonAlias("id")
	private Long id;
	
	/** The acronym. */
	@JsonAlias("sigla")
	private String acronym;
	
	/** The name. */
	@JsonAlias("nome")
	private String name;
}
