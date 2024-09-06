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
public class MWeatherDaily {
	
//	daily.dt Time of the forecasted data, Unix, UTC
	@JsonAlias("dt")
	private Long date;
	
//	daily.sunrise Sunrise time, Unix, UTC. For polar areas in midnight sun and polar night periods this parameter is not returned in the response
	@JsonAlias("sunrise")
	private Long sunrise;
	
//	daily.sunset Sunset time, Unix, UTC. For polar areas in midnight sun and polar night periods this parameter is not returned in the response
	@JsonAlias("sunset")
	private Long sunset;
	
//	daily.moonrise The time of when the moon rises for this day, Unix, UTC
	@JsonAlias("moonrise")
	private Long moonrise;
	
//	daily.moonset The time of when the moon sets for this day, Unix, UTC
	@JsonAlias("moonset")
	private Long moonset;
	
//	daily.moon_phase Moon phase. 0 and 1 are 'new moon', 0.25 is 'first quarter moon', 0.5 is 'full moon' and 0.75 is 'last quarter moon'. The periods in between are called 'waxing crescent', 'waxing gibbous', 'waning gibbous', and 'waning crescent', respectively. Moon phase calculation algorithm: if the moon phase values between the start of the day and the end of the day have a round value (0, 0.25, 0.5, 0.75, 1.0), then this round value is taken, otherwise the average of moon phases for the start of the day and the end of the day is taken
	@JsonAlias("moon_phase")
	private BigDecimal moonPhase;
	
//	summaryHuman-readable description of the weather conditions for the day
//	daily.temp Units – default: kelvin, metric: Celsius, imperial: Fahrenheit. How to change units used
//	daily.temp.morn Morning temperature.
//	daily.temp.day Day temperature.
//	daily.temp.eve Evening temperature.
//	daily.temp.night Night temperature.
//	daily.temp.min Min daily temperature.
//	daily.temp.max Max daily temperature.
//	daily.feels_like This accounts for the human perception of weather. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit. How to change units used
//	daily.feels_like.morn Morning temperature.
//	daily.feels_like.day Day temperature.
//	daily.feels_like.eve Evening temperature.
//	daily.feels_like.night Night temperature.
	
//	daily.pressure Atmospheric pressure on the sea level, hPa
	@JsonAlias("pressure")
	private BigDecimal pressure;
	
//	daily.humidity Humidity, %
	@JsonAlias("humidity")
	private BigDecimal humidity;
	
//	daily.dew_point Atmospheric temperature (varying according to pressure and humidity) below which water droplets begin to condense and dew can form. Units – default: kelvin, metric: Celsius, imperial: Fahrenheit.
	@JsonAlias("dew_point")
	private BigDecimal dewPoint;
	
//	daily.wind_speed Wind speed. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour. How to change units used
	@JsonAlias("wind_speed")
	private BigDecimal windSpeed;
	
//	daily.wind_gust (where available) Wind gust. Units – default: metre/sec, metric: metre/sec, imperial: miles/hour. How to change units used
	@JsonAlias("wind_gust")
	private BigDecimal windGust;
	
//	daily.wind_deg Wind direction, degrees (meteorological)
	@JsonAlias("wind_deg")
	private BigDecimal windDeg;
	
//	daily.clouds Cloudiness, %
	@JsonAlias("clouds")
	private BigDecimal clouds;
	
//	daily.uvi The maximum value of UV index for the day
	@JsonAlias("uvi")
	private BigDecimal uvi;
	
//	daily.pop Probability of precipitation. The values of the parameter vary between 0 and 1, where 0 is equal to 0%, 1 is equal to 100%
	@JsonAlias("pop")
	private BigDecimal pop;
	
//	daily.rain (where available) Precipitation volume, mm. Please note that only mm as units of measurement are available for this parameter
	@JsonAlias("rain")
	private BigDecimal rain;
	
//	daily.snow (where available) Snow volume, mm. Please note that only mm as units of measurement are available for this parameter
	@JsonAlias("snow")
	private BigDecimal snow;
	
//	daily.weather
//	daily.weather.id Weather condition id
//	daily.weather.main Group of weather parameters (Rain, Snow etc.)
//	daily.weather.description Weather condition within the group (full list of weather conditions). Get the output in your language
//	daily.weather.icon Weather icon id. How to get icons
	
	
}
