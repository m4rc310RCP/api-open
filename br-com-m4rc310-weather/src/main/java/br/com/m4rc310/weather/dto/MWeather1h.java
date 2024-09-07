package br.com.m4rc310.weather.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeather1h {
	
//	hourly.rain.1h (where available) Precipitation, mm/h. Please note that only mm/h as units of measurement are available for this parameter
	@JsonAlias("1h")
	private BigDecimal amount;
}
