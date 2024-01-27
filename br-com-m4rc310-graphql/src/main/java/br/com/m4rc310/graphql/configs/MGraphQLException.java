package br.com.m4rc310.graphql.configs;

import java.util.List;

import graphql.ErrorClassification;
import graphql.GraphQLError;
import graphql.language.SourceLocation;

public class MGraphQLException implements GraphQLError {

	private static final long serialVersionUID = -4816787283988799629L;

	@Override
	public String getMessage() {
		return "---------";
	}

	@Override
	public List<SourceLocation> getLocations() {
		return null;
	}

	@Override
	public ErrorClassification getErrorType() {
		return null;
	}

}
