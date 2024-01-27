package br.com.m4rc310.graphql.exceptions;

/**
 * The Class MException.
 */
public class MException extends Exception {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The code. */
	private final int code;
	
	/**
	 * Instantiates a new m exception.
	 *
	 * @param code    the code
	 * @param message the message
	 */
	public MException(int code, String message) {
		super(message);
		this.code = code;
	}
	
	/**
	 * Gets the.
	 *
	 * @param code    the code
	 * @param message the message
	 * @return the m exception
	 */
	public static MException get(int code, String message) {
		return new MException(code, message);
	}
	
	
	/**
	 * Gets the code.
	 *
	 * @return the code
	 */
	public int getCode() {
		return code;
	}
}
