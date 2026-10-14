/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.job;

import java.time.Duration;
import java.util.Map;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.date.CmDateUtils.toDuration;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;

public class EtlResultProcessingConfigImpl implements EtlResultProcessingConfig {

    private final boolean onWarningThrowError, onErrorRetry, onFailThrowSuccess;
    private final String onSuccessNotify, onSuccessForward, onWarningNotify, onWarningForward, onErrorNotify, onErrorForwad, onFailNotify, onFailForward;
    private final Duration retryDelay;
    private final int retryCount;

    public EtlResultProcessingConfigImpl(Map<String, String> config) {
        onWarningThrowError = toBooleanOrDefault(config.get("on_warning_throwError"), false);
        onErrorRetry = toBooleanOrDefault(config.get("on_error_retry"), false);
        onFailThrowSuccess = toBooleanOrDefault(config.get("on_fail_throwSuccess"), false);
        onSuccessNotify = firstNotBlankOrNull(config.get("on_success_notify"), config.get("infoTemplate"));
        onSuccessForward = firstNotBlankOrNull(config.get("on_success_forward"), config.get("forward"));
        onWarningNotify = config.get("on_warning_notify");
        onWarningForward = config.get("on_warning_forward");
        onErrorNotify = firstNotBlankOrNull(config.get("on_error_notify"), config.get("errorTemplate"));
        onErrorForwad = config.get("on_error_forward");
        onFailNotify = config.get("on_fail_notify");
        onFailForward = config.get("on_fail_forward");
        retryDelay = toDuration(firstNotBlank(config.get("on_error_retry_delay"), "PT10M"));
        retryCount = toIntegerOrDefault(config.get("on_error_retry_count"), 5);
    }

    @Override
    public boolean onWarningThrowError() {
        return onWarningThrowError;
    }

    @Override
    public boolean onErrorRetry() {
        return onErrorRetry;
    }

    @Override
    public boolean onFailThrowSuccess() {
        return onFailThrowSuccess;
    }

    @Nullable
    @Override
    public String onSuccessNotify() {
        return onSuccessNotify;
    }

    @Nullable
    @Override
    public String onSuccessForward() {
        return onSuccessForward;
    }

    @Nullable
    @Override
    public String onWarningNotify() {
        return onWarningNotify;
    }

    @Nullable
    @Override
    public String onWarningForward() {
        return onWarningForward;
    }

    @Nullable
    @Override
    public String onErrorNotify() {
        return onErrorNotify;
    }

    @Nullable
    @Override
    public String onErrorForwad() {
        return onErrorForwad;
    }

    @Nullable
    @Override
    public String onFailNotify() {
        return onFailNotify;
    }

    @Override
    @Nullable
    public String onFailForward() {
        return onFailForward;
    }

    @Override
    public Duration getRetryDelay() {
        return retryDelay;
    }

    @Override
    public int getRetryCount() {
        return retryCount;
    }

}
