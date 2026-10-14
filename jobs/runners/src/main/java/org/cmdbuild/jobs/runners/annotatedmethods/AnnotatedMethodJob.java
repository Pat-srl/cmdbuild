/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.annotatedmethods;

import static java.lang.String.format;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.sked.SkedJobClusterMode;

public interface AnnotatedMethodJob {

    final String ANNOTATED_METHOD_JOB_TYPE = "cm_service";

    String getBeanName();

    String getMethodName();

    String getCronExpression();

    @Nullable
    String getUser();

    SkedJobClusterMode getClusterMode();

    boolean persistRun();

    default String getCode() {
        return format("%s.%s", getBeanName(), getMethodName());
    }

}
