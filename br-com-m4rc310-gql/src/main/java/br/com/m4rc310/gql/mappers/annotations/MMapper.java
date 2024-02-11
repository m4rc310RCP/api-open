package br.com.m4rc310.gql.mappers.annotations;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.ElementType.TYPE_PARAMETER;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface MMapper.
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Retention(RUNTIME)
@Target({ TYPE, TYPE_PARAMETER, TYPE_USE })
public @interface MMapper {

}
