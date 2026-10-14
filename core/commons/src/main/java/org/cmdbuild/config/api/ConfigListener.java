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
import org.cmdbuild.minions.SystemStatus;
import static org.cmdbuild.minions.SystemStatus.SYST_READY;
import static org.cmdbuild.minions.SystemStatus.SYST_READY_RESTART_REQUIRED;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigListener {

    Class value() default Void.class;

    Class[] configs() default {};

    String[] configNamespaces() default {};

    SystemStatus[] requireSystemStatus() default {SYST_READY, SYST_READY_RESTART_REQUIRED};
}
