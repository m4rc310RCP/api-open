package br.com.m4rc310.gql.services;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * <p>MService class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Configuration

public class MService {

	@Autowired
	protected MFluxService flux;

	/**
	 * <p>convertStreamToString.</p>
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
	 * <p>unixTimeStampToDate.</p>
	 *
	 * @param timeStamp a {@link java.lang.Long} object
	 * @return a {@link java.util.Date} object
	 */
	protected Date unixTimeStampToDate(Long timeStamp) {
		timeStamp = timeStamp * 1000;
		return new Date(timeStamp);
	}

}
