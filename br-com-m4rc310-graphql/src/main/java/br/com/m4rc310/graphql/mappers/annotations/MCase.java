package br.com.m4rc310.graphql.mappers.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The Interface MCase.
 */
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface MCase {
	
	/**
	 * Value.
	 *
	 * @return the case
	 */
	Case value() default Case.NONE;
	
	/**
	 * The Enum Case.
	 */
	public enum Case{
		
		/** The upper. */
		UPPER, 
 /** The lower. */
 LOWER, 
 /** The none. */
 NONE		
	}
}
