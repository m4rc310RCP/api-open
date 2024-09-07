package br.com.m4rc310.weather.dto;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeatherHourly {

//	hourly.dt Time of the forecasted data, Unix, UTC
	@JsonAlias("dt")
	private Long date;
	
//	hourly.temp Temperature. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit. How to change units used
	@JsonAlias("temp")
	private BigDecimal temp;
	
//	hourly.feels_like Temperature. This accounts for the human perception of weather. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
	@JsonAlias("feels_like")
	private BigDecimal feelsLike;
	
//	hourly.pressure Atmospheric pressure on the sea level, hPa
	@JsonAlias("pressure")
	private BigDecimal pressure;
	
//	hourly.humidity Humidity, %
	@JsonAlias("humidity")
	private BigDecimal humidity;
	
//	hourly.dew_point Atmospheric temperature (varying according to pressure and humidity) below which water droplets begin to condense and dew can form. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
	@JsonAlias("dew_point")
	private BigDecimal dewPoint;
	
//	hourly.uvi UV index
	@JsonAlias("uvi")
	private BigDecimal uvi;
	
//	hourly.clouds Cloudiness, %
	@JsonAlias("clouds")
	private BigDecimal clouds;
	
//	hourly.visibility Average visibility, metres. The maximum value of the visibility is 10 km
	@JsonAlias("visibility")
	private BigDecimal visibility;
	
//	hourly.wind_speed Wind speed. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour.How to change units used
	@JsonAlias("wind_speed")
	private BigDecimal windSpeed;
	
//	hourly.wind_gust (where available) Wind gust. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour. How to change units used
	@JsonAlias("wind_gust")
	private BigDecimal windGust;
	
//	hourly.wind_deg Wind direction, degrees (meteorological)
	@JsonAlias("wind_deg")
	private BigDecimal windDeg;
	
//	hourly.pop Probability of precipitation. The values of the parameter vary between 0 and 1, where 0 is equal to 0%, 1 is equal to 100%
	@JsonAlias("pop")
	private BigDecimal pop;
	
//	hourly.rain
	@JsonAlias("rain")
	private MWeather1h rain;
	
//	hourly.snow
//	hourly.snow.1h (where available) Precipitation, mm/h. Please note that only mm/h as units of measurement are available for this parameter
	@JsonAlias("snow")
	private MWeather1h snow;
	
//	hourly.weather
	@JsonAlias("weather")
	private MWeatherCurrentWeather weather;
}
