/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.apache.commons.lang3.exception.ExceptionUtils;
import static org.cmdbuild.fault.FaultUtils.buildFaultEventMessage;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.fault.FaultLevel.FL_INFO;
import static org.cmdbuild.fault.FaultLevel.FL_WARNING;
import static org.cmdbuild.fault.FaultLevel.FL_ERROR;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;

public class FaultEventImpl implements FaultEvent {

    private final FaultLevel level;
    private final String message, stacktrace;
    private final Map<String, String> meta;

    @JsonCreator
    public FaultEventImpl(@JsonProperty("level") FaultLevel level, @Nullable @JsonProperty("message") String message, @Nullable @JsonProperty("exception") String stackTrace, @Nullable @JsonProperty("meta") Map<String, String> meta) {
        this.level = checkNotNull(level);
        this.message = nullToEmpty(message);
        this.stacktrace = stackTrace;
        this.meta = map(firstNotNull(meta, emptyMap())).immutable();
    }

    public FaultEventImpl(FaultLevel level, @Nullable String message, @Nullable String stackTrace) {
        this(level, message, stackTrace, null);
    }

    public FaultEventImpl(FaultLevel level, @Nullable String message) {
        this(level, message, (String) null);
    }

    public FaultEventImpl(FaultLevel level, @Nullable String message, @Nullable Throwable exception) {
        this(level, buildFaultEventMessage(message, exception), exception == null ? null : ExceptionUtils.getStackTrace(exception));
    }

    @Override
    @JsonIgnore
    public FaultLevel getLevel() {
        return level;
    }

    @JsonProperty("level")
    public String getLevelAsString() {
        return serializeEnum(level);
    }

    @Override
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }

    @Override
    @JsonProperty("exception")
    public String getStacktrace() {
        return stacktrace;
    }

    @Override
    @JsonProperty("meta")
    public Map<String, String> getMeta() {
        return meta;
    }

    @Override
    public String toString() {
        return "FaultEvent{" + "level=" + level + ", message=" + message + '}';
    }

    @Override
    public FaultEvent withMeta(Map<String, String> meta) {
        return new FaultEventImpl(level, message, stacktrace, map(this.meta).with(meta));
    }

    public static FaultEvent warning(String message) {
        return new FaultEventImpl(FL_WARNING, message);
    }

    public static FaultEvent warningWithExc(String message, Throwable ex) {
        return new FaultEventImpl(FL_WARNING, message, ex);    
    }
    
    public static FaultEvent warning(String message, Object... args) {
        return new FaultEventImpl(FL_WARNING, format(message, args));
    }

    public static FaultEvent warning(Throwable ex) {
        return new FaultEventImpl(FL_WARNING, null, ex);
    }

    public static FaultEvent error(String message) {
        return new FaultEventImpl(FL_ERROR, message);
    }

    public static FaultEvent error(String message, Object... args) {
        return new FaultEventImpl(FL_ERROR, format(message, args));
    }

    public static FaultEvent error(Throwable ex) {
        return new FaultEventImpl(FL_ERROR, null, ex);
    }

    public static FaultEvent info(String message) {
        return new FaultEventImpl(FL_INFO, message);
    }

    public static FaultEvent info(String message, Object... args) {
        return new FaultEventImpl(FL_INFO, format(message, args));
    }

    public static FaultEvent info(Throwable ex) {
        return new FaultEventImpl(FL_INFO, null, ex);
    }

}
