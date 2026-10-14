/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.plugin.config.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 * @author ataboga
 */
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface PluginConfigValue {

    final static String NULL = "NULL_DEFAULT_VALUE";

    final static String TRUE = "true", FALSE = "false";

    final static String PRIVATE = "private", PUBLIC = "public";

    String key() default NULL;

    String name() default "";

    String description() default "";

    String defaultValue() default NULL;

    String access() default PRIVATE;

    String hidden() default FALSE;

    String mandatory() default TRUE;

    String password() default FALSE;
}
