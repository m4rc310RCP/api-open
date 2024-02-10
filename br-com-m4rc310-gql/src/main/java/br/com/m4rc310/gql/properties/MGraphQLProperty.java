package br.com.m4rc310.gql.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

/**
 * <p>MGraphQLProperty class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Data
@ConfigurationProperties("br.com.m4rc310.gql")
public class MGraphQLProperty {
	public static final String ENABLE_GRAPHQL = "br.com.m4rc310.gql.enable";
	private boolean enable;
	private String path;
	private String classname;
	private Security security = new Security();
	
	@Data
	public class Security{
		private boolean enable;
	}
}
