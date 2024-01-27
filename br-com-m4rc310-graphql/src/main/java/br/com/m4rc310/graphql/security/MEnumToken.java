package br.com.m4rc310.graphql.security;

/**
 * The Enum MEnumToken.
 */
public enum MEnumToken {
	
	/** The test. */
	TEST("Test"),
	
	/** The basic. */
	BASIC("Basic"),
	
	/** The bearer. */
	BEARER("Bearer");
	

	/** The description. */
	private String description;

	/**
	 * Instantiates a new m enum token.
	 *
	 * @param description the description
	 */
	private MEnumToken(String description) {
		this.description = description;
	}
	
	/**
	 * Gets the description.
	 *
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
}
