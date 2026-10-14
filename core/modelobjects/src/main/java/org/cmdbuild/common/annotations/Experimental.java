package org.cmdbuild.common.annotations;

/**
 * Used for experimental features that must be used with attention until they
 * will be standardized.
 */
public @interface Experimental {

	String value() default "";

}
