package br.com.m4rc310.graphql.mappers;

import graphql.language.StringValue;
import graphql.schema.Coercing;
import graphql.schema.CoercingParseLiteralException;
import graphql.schema.CoercingParseValueException;
import graphql.schema.CoercingSerializeException;

/**
 * The Class MCoercingUtils.
 */
public class MCoercingUtils {
	
	/**
	 * Gets the.
	 *
	 * @param <T>        the generic type
	 * @param type       the type
	 * @param fromString the from string
	 * @param toString   the to string
	 * @return the coercing
	 */
	public static <T> Coercing<T, String> get(Class<T> type, MFunction<String, T> fromString,
			MFunction<T, String> toString) {
		return new Coercing<T, String>() {

			@Override
			@SuppressWarnings("unchecked")
			public String serialize(Object dataFetcherResult) throws CoercingSerializeException {
				if (type.isInstance(dataFetcherResult)) {
					try {
						return toString.apply((T) dataFetcherResult);
					} catch (Exception e) {
						throw new CoercingSerializeException(e.getMessage());
					}
				}
				throw new CoercingSerializeException("Value " + dataFetcherResult + " could not be serialized");
			}

			@Override
			public T parseValue(Object input) throws CoercingParseValueException {
				if (type.isInstance(input)) {
					return type.cast(input);
				}
				throw new CoercingSerializeException("Could not be parseValue");
			}

			@Override
			public T parseLiteral(Object input) throws CoercingParseLiteralException {

				try {
					if (input instanceof StringValue) {
						return fromString.apply(((StringValue) input).getValue());
					}

					if (input instanceof String) {
						return fromString.apply((String) input);
					}

					throw new CoercingSerializeException("Could not be parseValue");
				} catch (Exception e) {
					throw new CoercingSerializeException(e);
				}
			}
		};
	}

	/**
	 * The Interface MFunction.
	 *
	 * @param <T> the generic type
	 * @param <R> the generic type
	 */
	@FunctionalInterface
	public interface MFunction<T, R> {
		
		/**
		 * Apply.
		 *
		 * @param t the t
		 * @return the r
		 * @throws Exception the exception
		 */
		R apply(T t) throws Exception;
	}
}
