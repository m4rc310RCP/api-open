package br.com.m4rc310.graphql.services;

import br.com.m4rc310.graphql.security.MGraphQLJwtService;
import lombok.Data;

@Data
public class MGraphQLService  {
	private MGraphQLJwtService jwt;
}
