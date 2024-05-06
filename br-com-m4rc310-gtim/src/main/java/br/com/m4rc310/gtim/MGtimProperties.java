package br.com.m4rc310.gtim;

import org.springframework.boot.context.properties.ConfigurationProperties;


import lombok.Data;

/**
 * <p>MGtimProperties class.</p>
 *
 * @author marcelo
 * @version $Id: $Id
 */
@Data
@ConfigurationProperties("br.com.m4rc310.gtim")
public class MGtimProperties {
	public static final String ENABLE_GTIM = "br.com.m4rc310.gtim.enable";
}
