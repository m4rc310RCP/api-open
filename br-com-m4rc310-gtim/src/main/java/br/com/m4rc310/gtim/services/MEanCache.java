package br.com.m4rc310.gtim.services;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.EnableCaching;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import br.com.m4rc310.gtim.dto.MProduct;
import io.leangen.graphql.annotations.GraphQLContext;

/**
 * The Class MEanCache.
 */
@EnableCaching
public class MEanCache {
	
	/** The url. */
	@Value("${br.com.m4rc310.gtim.services.url:http://www.eanpictures.com.br:9000}")
	private String url;

	/**
	 * Gets the product cache.
	 *
	 * @param ean the ean
	 * @return the product cache
	 */
	@Cacheable("ean")
	public MProduct getProductCache(Long ean) {
		try {
			String suri = url;
			suri = String.format("%s/api/desc/%d", suri, ean);

			URL uri = new URI(suri).toURL();
			HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
			connection.setRequestProperty("accept", "application/json");
			
			InputStream responseStream = connection.getInputStream();
			ObjectMapper mapper = new ObjectMapper();
			
			
			MProduct product = mapper.readValue(responseStream, new TypeReference<MProduct>() {});
			if (product.getStatus() != 200) {
				return null;
			}

			product.setEan(ean);

			return product;
		} catch (Exception e) {
			return null;
		}
	}
	
	/**
	 * Gets the image.
	 *
	 * @param product the product
	 * @return the image
	 */
	@Cacheable(value = "ean_image", key = "#product?.ean")
	public String getImage(@GraphQLContext MProduct product) {
		try {
			
			String uri = url;
			uri = String.format("%s/api/gtin/%d", uri, product.getEan());
			
			java.net.URL url = new URI(uri).toURL();
			InputStream is = url.openStream();
			byte[] bytes = IOUtils.toByteArray(is);
			return Base64.encodeBase64String(bytes);
		} catch (Exception e) {
			
			return null;
		}
	}

	/**
	 * Reset product cache.
	 *
	 * @param ean the ean
	 */
	@CacheEvict({"ean", "ean_image"})
	public void resetProductCache(Long ean) {
	}

}
