package br.com.m4rc310.gql.messages;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.PropertiesLoaderUtils;
import org.springframework.core.type.filter.RegexPatternTypeFilter;

import br.com.m4rc310.gql.annotations.MConstants;
import br.com.m4rc310.gql.mappers.annotations.MMapper;
import io.leangen.graphql.generator.mapping.TypeMapper;
import lombok.extern.slf4j.Slf4j;

/**
 * <p>MMessageBuilder class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Slf4j
public class MMessageBuilder {

	/** The messages. */
	private final Map<String, Map<String, String>> messages = new HashMap<>();

	/** The lv. */
	private int lv = 0;

	@Value("${br.com.m4rc310.gql.path}")
	private String constPath;
	
	/** The classname. */
	@Value("${br.com.m4rc310.gql.classname:br.com.m4rc310.auth.constants.MConst}")
	private String classname;

	/** The message source. */
	@Autowired
	private MessageSource messageSource;

	/**
	 * <p>Constructor for MMessageBuilder.</p>
	 */
	public MMessageBuilder() {
	}

	/**
	 * Gets the message.
	 *
	 * @param key  the key
	 * @param args the args
	 * @return the message
	 */
	public String getMessage(String key, Object... args) {
		try {
			if (key.contains("${")) {
				key = key.replace("${", "");
				key = key.replace("}", "");

				return messageSource.getMessage(key, args, Locale.forLanguageTag("pt-BR"));
			}
		} catch (Exception e) {
			Pattern pattern = Pattern.compile("\\b\\w+\\.\\w+\\b");
			Matcher matcher = pattern.matcher(key);

			while (matcher.find()) {
				String palavra = matcher.group();

				log.warn("Message not found for {}", palavra);

				appendText(key, palavra);
			}
		}

		return key;
	}

	/**
	 * Append text.
	 *
	 * @param key  the key
	 * @param text the text
	 */
	public void appendText(String key, String text) {

		try {
			String skey = key.substring(0, key.indexOf("."));

			if (!messages.containsKey(skey)) {
				messages.put(skey, new HashMap<>());
			}

			messages.get(skey).put(key, text);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Fix unknow messages.
	 */
	public void fixUnknowMessages() {

		PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(getClass().getClassLoader());

		try {
			Resource[] resources = resolver.getResources("classpath:messages/**/*.properties");

			for (Resource res : resources) {
				Properties properties = PropertiesLoaderUtils.loadProperties(res);

				messages.forEach((key, value) -> {
					value.forEach((skey, svalue) -> {
						log.info("New message registered for key {}", skey);
						svalue = String.format("%s", svalue);
						properties.put(skey, svalue);
					});
				});

				Map<String, Map<String, String>> maps = new TreeMap<>();

				Pattern compile = Pattern.compile("(\\w+)\\..*");

				properties.forEach((key, value) -> {
					String skey = (String) key;
					String svalue = (String) value;
					Matcher matcher = compile.matcher(skey);
					if (matcher.matches()) {
						String ref = skey.substring(0, skey.indexOf("."));
						if (!maps.containsKey(ref)) {
							maps.put(ref, new TreeMap<>());
						}
						maps.get(ref).put(skey, svalue.trim());
					}
				});

				int maxlength = 0;

				for (String key : maps.keySet()) {
					Map<String, String> map = maps.get(key);
					for (String ref : map.keySet()) {
						String ss = String.format("%s=%s\n", ref, map.get(ref));
						if (ss.length() > maxlength) {
							maxlength = ss.length();
						}
					}
				}

				StringBuilder sb = new StringBuilder();

				for (String key : maps.keySet()) {
					String aux = String.format("# ");
					sb.append(String.format("%s%s\n", aux, "=".repeat(maxlength - aux.length())));

					aux = String.format("# %s's   ", key.toUpperCase());
					sb.append(String.format("%s%s\n", aux, "-".repeat(maxlength - aux.length())));

					Map<String, String> map = maps.get(key);
					int ml = 0;
					for (String key2 : map.keySet()) {
						if (key2.length() > ml) {
							ml = key2.length();
						}
					}

					for (String ref : map.keySet()) {
						String sp = " ".repeat(ml - ref.length());
						sb.append(String.format("%s%s = %s\n", ref, sp, map.get(ref).trim()));
					}
				}

				String sfile = String.format("src/main/resources/messages/%s", res.getFilename());

				File file = new File(sfile);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();

				try (BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"))) {
					bufferedWriter.write(sb.toString());
					bufferedWriter.newLine();
					bufferedWriter.flush();
				}

				log.info("Messages stored in {}", file.getAbsolutePath());

				StringBuilder sb2 = new StringBuilder();

				for (String key : maps.keySet()) {
					String skey = key.toUpperCase();
					Map<String, String> map = maps.get(key);
					for (Map.Entry<String, String> entry : map.entrySet()) {
						String k = entry.getKey();
						String variable = String.format("	public static final String %s$%s", skey,
								k.replace(key + ".", "").replace(".", "_"));
						lv = variable.length() > lv ? variable.length() : lv;
					}
				}

				log.info(">> {}", classname);
				
				final ClassPathScanningCandidateComponentProvider provider = new ClassPathScanningCandidateComponentProvider(false);
				provider.addIncludeFilter(new RegexPatternTypeFilter(Pattern.compile(".*")));
				
				final Set<BeanDefinition> classes = provider.findCandidateComponents("br");
				for (BeanDefinition bean : classes) {
					Class<?> clazz = Class.forName(bean.getBeanClassName());
					if (clazz.isAnnotationPresent(MConstants.class)) {
//						Constructor<?> constructor = clazz.getDeclaredConstructor();
//						TypeMapper mapper = (TypeMapper) constructor.newInstance();
//						defaults.prepend(mapper);
//						log.info("~~> Prepend mapper: {}", mapper);
						
						log.info(clazz.getName());
						
					}
				}
				
				

				Class<?> type = Class.forName(classname);
				String saux = String.format("package %s;\n\n\n", type.getPackage().getName());

				sb2.append(saux);
				saux = String.format("public interface %s {\n", type.getSimpleName());

				sb2.append(saux);

				for (String key : maps.keySet()) {
					String skey = key.toUpperCase();
					Map<String, String> map = maps.get(key);

					if ("DESC".equalsIgnoreCase(skey)) {
						// continue;
					}

					map.forEach((k, v) -> {
						String variable = String.format("	public static final String %s$%s", skey,
								k.replace(key + ".", "").replace(".", "_"));
						int rest = lv - variable.length();
						String sp = rest > 0 ? " ".repeat(rest) : "";

						String fieldName = String.format("%s%s = \"${%s}\";", variable, sp, k);
						sb2.append(fieldName).append("\n");
					});
				}

				sb2.append("}\n");

				String spath = String.format("%s/MConst.java", constPath);

				file = new File(spath);
				if (file.exists()) {
					file.delete();
				}
				file.createNewFile();

				try (BufferedWriter bufferedWriter = new BufferedWriter(
						new OutputStreamWriter(new FileOutputStream(file), "ISO-8859-1"))) {
					bufferedWriter.write(sb2.toString());
					bufferedWriter.newLine();
					bufferedWriter.flush();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
