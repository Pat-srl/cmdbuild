package org.cmdbuild.spring;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

/**
 *
 * @author davide
 * @deprecated remove with 30, replace with full spring integration
 */
@Deprecated
@Component
public class SpringIntegrationUtils {

	@Autowired
	private ApplicationContext applicationContext;

	private static ApplicationContext staticApplicationContext;

	@PostConstruct
	public void init() {
		staticApplicationContext = checkNotNull(applicationContext);
	}

	/**
	 *
	 * @return @deprecated remove with 30, replace with full spring integration
	 */
	@Deprecated
	public static ApplicationContext applicationContext() {
		return staticApplicationContext;
	}

	/**
	 *
	 * @return @deprecated remove with 30, replace with full spring integration
	 */
	@Deprecated
	public static <E> E getLegacyBean(Class<E> clazz) {
		return staticApplicationContext.getBean(clazz);
	}

}
