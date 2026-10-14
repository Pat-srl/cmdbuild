/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.annotatedmethods;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.sked.SkedJobClusterMode;

public class AnnotatedMethodJobImpl implements AnnotatedMethodJob {

    private final String beanName, methodName, cronExpression,user;
    private final SkedJobClusterMode clusterMode;
    private final boolean persistRun;

    public AnnotatedMethodJobImpl(String beanName, String methodName, String cronExpression, SkedJobClusterMode clusterMode, Boolean persistRun,String user) {
        this.beanName = checkNotBlank(beanName);
        this.methodName = checkNotBlank(methodName);
        this.cronExpression = checkNotBlank(cronExpression);
        this.clusterMode = checkNotNull(clusterMode);
        this.persistRun = persistRun;
        this.user = user;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    @Override
    public String getMethodName() {
        return methodName;
    }

    @Override
    public String getCronExpression() {
        return cronExpression;
    }

    @Override
    public SkedJobClusterMode getClusterMode() {
        return clusterMode;
    }

    @Override
    public boolean persistRun() {
        return persistRun;
    }

    @Override
    @Nullable
    public String getUser() {
        return user;
    }

    @Override
    public String toString() {
        return "AnnotatedMethodJobImpl{" + "beanName=" + beanName + ", methodName=" + methodName + ", cronExpression=" + cronExpression + '}';
    }

}
