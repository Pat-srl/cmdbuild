/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.cmdbuild.utils.sked.SkedJobClusterMode;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_ALL_NODES;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ScheduledJob {

    String alias() default "";

    /**
     * a cron pattern, like
     * <pre><code>
     * {@code
     * 0/5 * * * * ?
     * }</code></pre>
     *
     * @return
     */
    String value();

    SkedJobClusterMode clusterMode() default RUN_ON_ALL_NODES;

    String user() default "";

    boolean persistRun() default true;

}
