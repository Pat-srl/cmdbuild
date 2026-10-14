package org.cmdbuild.config;

import jakarta.annotation.Nullable;

public interface EmailQueueConfiguration {

    boolean isQueueEnabled();

    long getQueueTime();

    int getMaxErrors();

    int getMinRetryDelaySeconds();

    int getMaxRetryDelaySeconds();

    double getRetryDelayIncrement();

    String getDefaultEmailAccountCode();

    @Nullable
    Integer getSmtpTimeoutSeconds();

    @Nullable
    Integer getImapTimeoutSeconds();
}
