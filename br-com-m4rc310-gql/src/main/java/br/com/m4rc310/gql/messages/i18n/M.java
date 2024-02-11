package br.com.m4rc310.gql.messages.i18n;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

@Component
public class M {

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/**
	 * Gets the string.
	 *
	 * @param input the input
	 * @param args  the args
	 * @return the string
	 */
	public String getString(String input, Object... args) {
		try {
			if (input.contains("${") && input.contains("}")) {
				String regex = "\\$\\{([^}]+)\\}";
				Pattern pattern = Pattern.compile(regex);
				Matcher matcher = pattern.matcher(input);

				while (matcher.find()) {
					String text = matcher.group();
					String stext = text.replace("${", "").replace("}", "");
					stext = messageSource.getMessage(stext, args, Locale.forLanguageTag("pt-BR"));
					input = input.replace(text, stext);
				}
			}
		} catch (Exception e) {}
		return MessageFormat.format(input, args);
	}
}
