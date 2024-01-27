package br.com.m4rc310.graphql.mappers.customs;

import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.AnnotatedType;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.m4rc310.graphql.mappers.MGraphQLScalarType;
import br.com.m4rc310.graphql.mappers.annotations.MDate;
import br.com.m4rc310.graphql.mappers.annotations.MMapper;
import graphql.schema.Coercing;
import graphql.schema.GraphQLScalarType;

/**
 * The Class MDateMapper.
 */
@MMapper
public class MDateMapper extends MGraphQLScalarType<MDate> {

	/**
	 * Inits the.
	 *
	 * @param element    the element
	 * @param type       the type
	 * @param annotation the annotation
	 * @return the graph QL scalar type
	 */
	@Override
	public GraphQLScalarType init(AnnotatedElement element, AnnotatedType type, MDate annotation) {

		final String format = annotation.value();
		
		if (annotation.unixFormat()) {
			String skey = String.format("DateUnix_%s", hashString(format));
			Coercing<Long, String> coercing = getCoercing(Long.class, 
					sdate -> dateToUnix(sdate, format), unix -> unixToString(unix, format));
			return get(skey, getString("Date format: {0}", format), coercing);
		}else {
			String skey = String.format("Date_%s", hashString(format));
			Coercing<Date, String> coercing = getCoercing(Date.class, 
					sdate -> stringToDate(sdate, format), date -> dateToString(date, format));
			return get(skey, getString("Date format: {0}", format), coercing);
		}
	}

	/**
	 * Date to string.
	 *
	 * @param date   the date
	 * @param format the format
	 * @return the string
	 */
	private String dateToString(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	/**
	 * String to date.
	 *
	 * @param sdate  the sdate
	 * @param format the format
	 * @return the date
	 */
	private Date stringToDate(String sdate, String format) {
		DateFormat df = new SimpleDateFormat(format);
		try {
			return df.parse(sdate);
		} catch (Exception e) {
			throw new UnsupportedOperationException(e.getMessage());
		}
	}

	/**
	 * Unix to string.
	 *
	 * @param unix   the unix
	 * @param format the format
	 * @return the string
	 */
	private String unixToString(long unix, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(new Date(unix * 1000));
	}

	/**
	 * Date to unix.
	 *
	 * @param sdate  the sdate
	 * @param format the format
	 * @return the long
	 */
	private long dateToUnix(String sdate, String format) {

		DateFormat df = new SimpleDateFormat(format);
		try {
			long time = df.parse(sdate).getTime();
			if (time > 0) {
				time = time / 1000;
			}
			return time;
		} catch (Exception e) {
			throw new UnsupportedOperationException(e.getMessage());
		}
	}

}
