package br.com.m4rc310.weather.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeatherTemp {

//	daily.temp.morn Morning temperature.
	@JsonAlias("morn")
	private BigDecimal morn;

// daily.temp.day Day temperature.
	@JsonAlias("day")
	private BigDecimal day;
	
//	daily.temp.eve Evening temperature.
	@JsonAlias("eve")
	private BigDecimal eve;
	
//	daily.temp.night Night temperature.
	@JsonAlias("night")
	private BigDecimal night;
	
//	daily.temp.min Min daily temperature.
	@JsonAlias("min")
	private BigDecimal min;
	
//	daily.temp.max Max daily temperature.
	@JsonAlias("max")
	private BigDecimal max;
	

}
