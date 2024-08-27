package br.com.m4rc310.gql.location.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName="from")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DtoGeolocation implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@JsonAlias("query")
	private String query;
	
	@JsonAlias("status")
	private String status;
	
	@JsonAlias("country")
	private String country;

	@JsonAlias("countryCode")
	private String countryCode;

	@JsonAlias("region")
	private String region;
	
	@JsonAlias("regionName")
	private String regionName;
	
	@JsonAlias("city")
	private String city;
	
	@JsonAlias("zip")
	private String zip;
	
	@JsonAlias("lat")
	private BigDecimal latitude;
	
	@JsonAlias("lon")
	private BigDecimal longitude;
	
	@JsonAlias("timezone")
	private String timezone;
	
	@JsonAlias("isp")
	private String isp;
	
	@JsonAlias("org")
	private String org;
	
	@JsonAlias("as")
	private String as;
}
