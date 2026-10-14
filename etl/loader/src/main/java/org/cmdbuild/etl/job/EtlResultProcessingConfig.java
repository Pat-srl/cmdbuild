/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import java.time.Duration;
import jakarta.annotation.Nullable;

public interface EtlResultProcessingConfig {

    boolean onWarningThrowError();

    boolean onErrorRetry();

    boolean onFailThrowSuccess();

    @Nullable
    String onSuccessNotify();

    @Nullable
    String onWarningNotify();

    @Nullable
    String onWarningForward();

    @Nullable
    String onErrorNotify();

    @Nullable
    String onErrorForwad();

    @Nullable
    String onFailNotify();

    @Nullable
    String onSuccessForward();

    @Nullable
    String onFailForward();

    Duration getRetryDelay();

    int getRetryCount();
}
