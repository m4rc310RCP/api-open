package br.com.m4rc310.gql.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * <p>MEnumToken class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@AllArgsConstructor
public enum MEnumToken {
	TEST("Test"), BASIC("Basic"), BEARER("Bearer");

	@Getter
	private String description;
}
