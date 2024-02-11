package br.com.m4rc310.gql.mappers;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.lang.reflect.ParameterizedType;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import br.com.m4rc310.gql.mappers.MCoercingUtils.MFunction;
import graphql.schema.Coercing;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLOutputType;
import graphql.schema.GraphQLScalarType;
import io.leangen.graphql.generator.mapping.TypeMapper;
import io.leangen.graphql.generator.mapping.TypeMappingEnvironment;

/**
 * The Class MGraphQLScalarType.
 *
 * @param <T> the generic type
 * @author marcelo
 * @version $Id: $Id
 */
public abstract class MGraphQLScalarType<T extends Annotation> implements TypeMapper{
	
	/** The Constant maps. */
	private static final Map<String, GraphQLScalarType> maps = new HashMap<>();
	
	/** The graph QL scalar type. */
	private GraphQLScalarType graphQLScalarType;
	

	/**
	 * {@inheritDoc}
	 *
	 * To graph QL type.
	 */
	@Override
	public GraphQLOutputType toGraphQLType(AnnotatedType javaType, Set<Class<? extends TypeMapper>> mappersToSkip,
			TypeMappingEnvironment env) {
		return graphQLScalarType;
	}

	/**
	 * {@inheritDoc}
	 *
	 * To graph QL input type.
	 */
	@Override
	public GraphQLInputType toGraphQLInputType(AnnotatedType javaType, Set<Class<? extends TypeMapper>> mappersToSkip,
			TypeMappingEnvironment env) {
		return graphQLScalarType;
	}

	/**
	 * {@inheritDoc}
	 *
	 * Supports.
	 */
	@Override
	public boolean supports(AnnotatedElement element, AnnotatedType type) {
		
		Class<T> annotationType = getAnnotationType();
		
		boolean supported = element.isAnnotationPresent(annotationType);
		if (supported) {
			T annotation = element.getAnnotation(annotationType);
			graphQLScalarType = init(element, type, annotation);
		}
		return supported;
	}
	
	/**
	 * Gets the annotation type.
	 *
	 * @return the annotation type
	 */
	@SuppressWarnings("unchecked")
	public Class<T> getAnnotationType (){
		return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
	}
	
	
	/**
	 * Inits the.
	 *
	 * @param element    the element
	 * @param type       the type
	 * @param annotation the annotation
	 * @return the graph QL scalar type
	 */
	public abstract GraphQLScalarType init(AnnotatedElement element, AnnotatedType type, T annotation);
	
	/**
	 * Gets the string.
	 *
	 * @param pattern the pattern
	 * @param args    the args
	 * @return the string
	 */
	public String getString(String pattern, Object... args) {
		return MessageFormat.format(pattern, args);
	}
	
	/**
	 * Gets the.
	 *
	 * @param key      the key
	 * @param coercing the coercing
	 * @return the graph QL scalar type
	 */
	public static GraphQLScalarType get(String key, Coercing<?, ?> coercing) {
		return get(key, key, coercing);
	}
	
	/**
	 * Gets the.
	 *
	 * @param key         the key
	 * @param description the description
	 * @param coercing    the coercing
	 * @return the graph QL scalar type
	 */
	public static GraphQLScalarType get(String key, String description, Coercing<?, ?> coercing) {
		
		if (maps.containsKey(key)) {
			return maps.get(key);
		}

		GraphQLScalarType ret = GraphQLScalarType.newScalar().name(key).description(description).coercing(coercing).build();

		maps.put(key, ret);

		return ret;
	}
	
	/**
	 * Hash string.
	 *
	 * @param s the s
	 * @return the string
	 */
	protected String hashString(String s) {
		int hash = 7;
		for (int i = 0; i < s.length(); i++) {
			hash = hash * 31 + s.charAt(i);
		}

		hash = Math.abs(hash);

		return String.format("%010d", hash);
	}
	
	
	
	/**
	 * Gets the.
	 *
	 * @param key the key
	 * @return the graph QL scalar type
	 */
	public GraphQLScalarType get(String key) {
		return maps.get(key);
	}
	
	/**
	 * Checks if is contains graph QL scalar type.
	 *
	 * @param key the key
	 * @return true, if is contains graph QL scalar type
	 */
	public boolean isContainsGraphQLScalarType(String key) {
		return maps.containsKey(key);
	}

	/**
	 * Gets the coercing.
	 *
	 * @param <O>        the generic type
	 * @param type       the type
	 * @param fromString the from string
	 * @param toString   the to string
	 * @return the coercing
	 */
	public <O> Coercing<O, String> getCoercing(Class<O> type, MFunction<String, O> fromString,
			MFunction<O, String> toString) {
		return MCoercingUtils.get(type, fromString, toString);
	}
	
}
