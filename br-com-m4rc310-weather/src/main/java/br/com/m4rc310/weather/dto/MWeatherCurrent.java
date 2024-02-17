package br.com.m4rc310.weather.dto;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

/**
 * Instantiates a new m weather current.
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class MWeatherCurrent {
	
	/** The date weather. */
	@JsonAlias("dt")
	private Long dateWeather;
	
	/** The date sun rise. */
	@JsonAlias("sunrise")
	private Long dateSunRise;
	
	/** The date sun set. */
	@JsonAlias("sunset")
	private Long dateSunSet;
	
	/** The temperature. */
	@JsonAlias("temp")
	private BigDecimal temperature;
	
	/** The feels like. */
	@JsonAlias("feels_like")
	private BigDecimal feelsLike;
	
	/** The pressure. */
	@JsonAlias("pressure")
	private BigDecimal pressure;
	
	/** The humidity. */
	@JsonAlias("humidity")
	private BigDecimal humidity;
	
	/** The dew point. */
	@JsonAlias("dew_point")
	private BigDecimal dewPoint;
	
	/** The uvi. */
	@JsonAlias("uvi")
	private BigDecimal uvi;
	
	/** The clouds. */
	@JsonAlias("clouds")
	private BigDecimal clouds;
	
	/** The visibility. */
	@JsonAlias("visibility")
	private BigDecimal visibility;
	
	/** The speed wind. */
	@JsonAlias("wind_speed")
	private BigDecimal speedWind;
	
	/** The degrees wind. */
	@JsonAlias("wind_deg")
	private BigDecimal degreesWind;
	
	/** The rain. */
	@JsonAlias("rain")
	private Rain rain;
	
	/** The weather. */
	@JsonAlias("weather")
	private List<MWeatherCurrentWeather> weather;
	
	/**
	 * Instantiates a new rain.
	 */
	@Data
	public class Rain {
		
		/** The precipitation. */
		@JsonAlias("1h")
		private BigDecimal precipitation;
	}
}
