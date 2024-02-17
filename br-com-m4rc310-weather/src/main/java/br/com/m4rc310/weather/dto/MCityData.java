package br.com.m4rc310.weather.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor(staticName = "to")
public class MCityData {
	private String json;
}
