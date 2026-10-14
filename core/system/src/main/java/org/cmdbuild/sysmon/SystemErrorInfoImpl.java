/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static java.lang.String.format;
import java.time.ZonedDateTime;
import org.cmdbuild.fault.FaultLevel;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SystemErrorInfoImpl implements SystemErrorInfo {

    private final FaultLevel level;
    private final ZonedDateTime timestamp;
    private final String message, category, source;

    private SystemErrorInfoImpl(SystemErrorInfoImplBuilder builder) {
        this.level = checkNotNull(builder.level);
        this.timestamp = checkNotNull(builder.timestamp);
        this.message = nullToEmpty(builder.message);
        this.category = checkNotBlank(builder.category);
        this.source = nullToEmpty(builder.source);
    }

    @Override
    public FaultLevel getLevel() {
        return level;
    }

    @Override
    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCategory() {
        return category;
    }

    @Override
    public String getSource() {
        return source;
    }

    public static SystemErrorInfoImplBuilder builder() {
        return new SystemErrorInfoImplBuilder();
    }

    public static SystemErrorInfoImplBuilder copyOf(SystemErrorInfo source) {
        return new SystemErrorInfoImplBuilder()
                .withLevel(source.getLevel())
                .withTimestamp(source.getTimestamp())
                .withMessage(source.getMessage())
                .withCategory(source.getCategory())
                .withSource(source.getSource());
    }

    public static class SystemErrorInfoImplBuilder implements Builder<SystemErrorInfoImpl, SystemErrorInfoImplBuilder> {

        private FaultLevel level;
        private ZonedDateTime timestamp;
        private String message;
        private String category;
        private String source;

        public SystemErrorInfoImplBuilder withLevel(FaultLevel level) {
            this.level = level;
            return this;
        }

        public SystemErrorInfoImplBuilder withTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public SystemErrorInfoImplBuilder withMessage(String message) {
            this.message = message;
            return this;
        }

        public SystemErrorInfoImplBuilder withCategory(String category) {
            this.category = category;
            return this;
        }

        public SystemErrorInfoImplBuilder withSource(String format, Object... args) {
            return this.withSource(format(format, args));
        }

        public SystemErrorInfoImplBuilder withSource(String source) {
            this.source = source;
            return this;
        }

        @Override
        public SystemErrorInfoImpl build() {
            return new SystemErrorInfoImpl(this);
        }

    }
}
