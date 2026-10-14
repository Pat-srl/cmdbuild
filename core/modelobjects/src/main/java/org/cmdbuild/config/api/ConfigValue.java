/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import static org.cmdbuild.config.api.ConfigCategory.CC_DEFAULT;
import static org.cmdbuild.config.api.ConfigLocation.CL_DEFAULT;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigValue {

    final static String NULL = "NULL_DEFAULT_VALUE";

    final static String TRUE = "true", FALSE = "false";

    String value() default NULL;

    String key() default NULL;

    String description() default "";

    String defaultValue() default NULL;

    boolean isProtected() default false;

    boolean experimental() default false;

    ConfigLocation location() default CL_DEFAULT;

    ConfigCategory category() default CC_DEFAULT;

    String modular() default "";
}
