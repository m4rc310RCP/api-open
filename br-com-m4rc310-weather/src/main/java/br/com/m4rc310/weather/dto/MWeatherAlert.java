package br.com.m4rc310.weather.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Getter;

/**
 * Instantiates a new m weather location.
 */
@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeatherAlert {
	
	@JsonAlias("sender_name")
	private String sender;
	
	@JsonAlias("event")
	private String event;
	
	@JsonAlias("start")
	private Long dateStart;
	
	@JsonAlias("end")
	private Long dateEnd;
	
	@JsonAlias("description")
	private String description;
	
	@JsonAlias("tags")
	private List<String> tags;
	
}
