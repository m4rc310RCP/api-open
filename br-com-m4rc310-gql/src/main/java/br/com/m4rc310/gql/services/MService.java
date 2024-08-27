package br.com.m4rc310.gql.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.m4rc310.gql.jwt.MGraphQLJwtService;
import br.com.m4rc310.gql.location.dto.DtoGeolocation;

/**
 * <p>
 * MService class.
 * </p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Configuration

public class MService {

	@Autowired
	protected MFluxService flux;

	@Autowired
	protected PasswordEncoder encoder;

	@Autowired
	protected MGraphQLJwtService jwt;

	@Autowired
	private ObjectMapper mapper;

	/** The ibge url. */
	@Value("${br.com.m4rc310.ip-api-url:http://ip-api.com}")
	private String ipApiUrl;

	/**
	 * <p>
	 * convertStreamToString.
	 * </p>
	 *
	 * @param is a {@link java.io.InputStream} object
	 * @return a {@link java.lang.String} object
	 */
	protected static String convertStreamToString(InputStream is) {

		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString();
	}

	/**
	 * <p>
	 * unixTimeStampToDate.
	 * </p>
	 *
	 * @param timeStamp a {@link java.lang.Long} object
	 * @return a {@link java.util.Date} object
	 */
	protected Date unixTimeStampToDate(Long timeStamp) {
		timeStamp = timeStamp * 1000;
		return new Date(timeStamp);
	}

	protected DtoGeolocation getGeolocationFromIp(String ip) throws Exception {
		String suri = String.format("%s/json/%s", ipApiUrl, ip);
		URL uri = new URI(suri).toURL();
		HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
		connection.setRequestProperty("accept", "application/json");

		InputStream responseStream = connection.getInputStream();

		String json = new String(responseStream.readAllBytes(), StandardCharsets.UTF_8);
		DtoGeolocation resp = mapper.readValue(json, DtoGeolocation.class);

		if (resp.getStatus().equals("fail")) {
			throw new UnsupportedOperationException();
		}

		return resp;
	}

}
