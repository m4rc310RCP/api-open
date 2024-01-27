package br.com.m4rc310.graphql.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@Data
@ConfigurationProperties("m4rc310.graphql")
public class MGraphQLProperty {
	public static final String ENABLE_GRAPHQL = "m4rc310.graphql.enable";
	
	private boolean enable;
	
	
	private Security security = new Security();
	
	private Constants constants = new Constants();
	
	@Data
	public class Security {		
		private boolean enable;
		private Long expiration;
	}
	
	@Data
	public class Constants{
		private String path;
		private String classname;
	}
	
}
