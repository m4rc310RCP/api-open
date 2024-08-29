package br.com.m4rc310.weather.services;

import java.io.InputStream;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.util.UriUtils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.m4rc310.weather.dto.MCity;
import br.com.m4rc310.weather.dto.MCityData;
import br.com.m4rc310.weather.dto.MDistrict;
import br.com.m4rc310.weather.dto.MWeather;
import br.com.m4rc310.weather.dto.MWeatherLocation;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class MWeatherService {

	/** The weather url. */
	@Value("${br.com.m4rc310.weather:http://api.openweathermap.org}")
	private String weatherUrl;

	/** The ibge url. */
	@Value("${br.com.m4rc310.weather.geo.ibge-api-url:https://servicodados.ibge.gov.br}")
	private String ibgeUrl;

	/** The api key. */
	@Value("${WEATHER_APIKEY}")
	private String apiKey;

	/**
	 * Test.
	 *
	 * @return the string
	 */
	public String test() {
		return "MWeatherService running...";
	}

	/**
	 * Find location by name and state.
	 *
	 * @param locationName the location name
	 * @param state        the state
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<MWeatherLocation> findLocationByNameAndState(String locationName, String state) throws Exception {
		String sname = UriUtils.encode(locationName, StandardCharsets.UTF_8.toString());
		String suri = String.format("%s/geo/1.0/direct?q=%s,%s,%s&limit=%d&appid=%s", weatherUrl, sname, state, "BR", 5,
				apiKey);
		
		URL uri = new URI(suri).toURL();
		HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
		connection.setRequestProperty("accept", "application/json");

		InputStream responseStream = connection.getInputStream();
		ObjectMapper mapper = new ObjectMapper();

		return mapper.readValue(responseStream, new TypeReference<List<MWeatherLocation>>() {
		});
	}

	/**
	 * Gets the m weather.
	 *
	 * @param location the location
	 * @return the m weather
	 * @throws Exception the exception
	 */
	public MWeather getMWeather(MWeatherLocation location) throws Exception {
		return getMWeather(location.getLatitude(), location.getLongitude());
	}

	public MWeather getMWeather(BigDecimal lat, BigDecimal lon) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		String json = getMWeatherJSON(lat, lon);
		System.out.println(json);
		return mapper.readValue(json, MWeather.class);
	}

	public String getMWeatherJSON(BigDecimal lat, BigDecimal lon) throws Exception {

		String suri = String.format("%s/data/3.0/onecall?lat=%s&lon=%s&units=metric&lang=pt_br&appid=%s", weatherUrl,
				lat.toPlainString(), lon.toPlainString(), apiKey);

		URL uri = new URI(suri).toURL();
		HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
		connection.setRequestProperty("accept", "application/json");

		InputStream responseStream = connection.getInputStream();
		return new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
	}

	/**
	 * Gets the district from ibge.
	 *
	 * @param ibgeId the ibge id
	 * @return the district from ibge
	 * @throws Exception the exception
	 */
	public MDistrict getDistrictFromIbge(Long ibgeId) throws Exception {
		String suri = String.format("%s/api/v1/localidades/distritos/%d", ibgeUrl, ibgeId);

		URL uri = new URI(suri).toURL();
		HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
		connection.setRequestProperty("accept", "application/json");

		InputStream responseStream = connection.getInputStream();
		ObjectMapper mapper = new ObjectMapper();

		List<MDistrict> list = mapper.readValue(responseStream, new TypeReference<List<MDistrict>>() {
		});

		if (list.isEmpty()) {
			return null;
		}

		return list.get(0);
	}

	public MCity findCity(String name) throws Exception {
		name = normalize(name);
		String suri = String.format("%s/api/v1/localidades/municipios/%s", ibgeUrl, name);
		return get(MCity.class, suri);
	}

	public MDistrict[] listDistrictsFromCity(Long cityCode) {
		try {
			String suri = String.format("%s/api/v1/localidades/municipios/%d/distritos", ibgeUrl, cityCode);
//			return toList(MDistrict.class, suri);
			return toArray(MDistrict.class, suri);
		} catch (Exception e) {
			return null;
		}
	}

	public MCityData getMCityData(String name) {
		try {

			name = normalize(name);
			String suri = String.format("%s/api/v1/localidades/municipios/%s", ibgeUrl, name);
			
			log.info(suri);
			
			URL uri = new URI(suri).toURL();
			HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
			connection.setRequestProperty("accept", "application/json");

			InputStream responseStream = connection.getInputStream();

			String json = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);

			return MCityData.to(json);

		} catch (Exception e) {
			return null;
		}
	}
	
	public MCityData[] listCityData(String name) {
		try {

			name = normalize(name);
			String suri = String.format("%s/api/v1/localidades/municipios/%s", ibgeUrl, name);
			
			URL uri = new URI(suri).toURL();
			HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
			connection.setRequestProperty("accept", "application/json");

			InputStream responseStream = connection.getInputStream();

			String json = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
			
	        ObjectMapper mapper = new ObjectMapper();
	        if (mapper.readTree(json).isArray()) {
	        	return mapper.readValue(responseStream, mapper.getTypeFactory().constructArrayType(MCityData.class));
	        }

			return new MCityData[] { MCityData.to(json)};

		} catch (Exception e) {
			return null;
		}
	}
	

	private String normalize(String value) {
		String sname = Normalizer.normalize(value, Normalizer.Form.NFD);
		sname = sname.replaceAll(" ", "-").toLowerCase();
		return sname.replaceAll("[^a-zA-Z0-9\\\\s-]", "");
	}

	private <T> T get(Class<T> type, String suri) {
		try {
			URL uri = new URI(suri).toURL();
			HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
			connection.setRequestProperty("accept", "application/json");

			InputStream responseStream = connection.getInputStream();
			ObjectMapper mapper = new ObjectMapper();

			return mapper.readValue(responseStream, type);
		} catch (Exception e) {
			// TODO: handle exception
			return null;
		}
	}

	@SuppressWarnings("unused")
	private <T> List<T> toList(Class<T> type, String suri) {
		try {
			URL uri = new URI(suri).toURL();
			HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
			connection.setRequestProperty("accept", "application/json");

			InputStream responseStream = connection.getInputStream();
			ObjectMapper mapper = new ObjectMapper();
			List<T> list = mapper.readValue(responseStream, new TypeReference<List<T>>() {
			});

			return list;
		} catch (Exception e) {
			return Collections.emptyList();
		}
	}

	@SuppressWarnings("unchecked")
	private <T> T[] toArray(Class<T> type, String suri) {
		try {
			URL uri = new URI(suri).toURL();
			HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
			connection.setRequestProperty("accept", "application/json");

			InputStream responseStream = connection.getInputStream();
			ObjectMapper mapper = new ObjectMapper();
			T[] array = mapper.readValue(responseStream, mapper.getTypeFactory().constructArrayType(type));

			return array;
		} catch (Exception e) {
			return (T[]) Array.newInstance(type, 0);
		}
	}

}
