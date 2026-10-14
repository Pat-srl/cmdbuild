/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import java.util.TimeZone;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import jakarta.annotation.Nullable;
import org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite;
import static org.cmdbuild.userconfig.UserPreferencesUtils.buildDecimalFormat;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite.POS_DEFAULT;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class DateAndFormatPreferencesImpl implements DateAndFormatPreferences {

    private final DateTimeFormatter dateTimeFormat, dateFormat, timeFormat;
    private final TimeZone timezone;
    private final String decimalSeparator, numberGroupingSeparator, dateTimeFormatPattern, dateFormatPattern, timeFormatPattern, preferredFileCharset, preferredCsvSeparator;
    private final DecimalFormat decimalFormat;
    private final PreferredOfficeSuite preferredOfficeSuite;

    private DateAndFormatPreferencesImpl(DateAndFormatPreferencesImplBuilder builder) {
        this.dateTimeFormatPattern = checkNotBlank(builder.dateTimeFormatPattern);
        this.dateFormatPattern = checkNotBlank(builder.dateFormatPattern);
        this.timeFormatPattern = checkNotBlank(builder.timeFormatPattern);
        this.dateTimeFormat = Optional.ofNullable(builder.dateTimeFormat).orElseGet(() -> DateTimeFormatter.ofPattern(dateTimeFormatPattern));
        this.dateFormat = Optional.ofNullable(builder.dateFormat).orElseGet(() -> DateTimeFormatter.ofPattern(dateFormatPattern));
        this.timeFormat = Optional.ofNullable(builder.timeFormat).orElseGet(() -> DateTimeFormatter.ofPattern(timeFormatPattern));
        this.timezone = checkNotNull(builder.timezone);
        this.decimalSeparator = checkNotBlank(builder.decimalSeparator);
        this.numberGroupingSeparator = emptyToNull(builder.numberGroupingSeparator);
        this.decimalFormat = buildDecimalFormat(decimalSeparator, numberGroupingSeparator);
        this.preferredOfficeSuite = firstNotNull(builder.preferredOfficeSuite, POS_DEFAULT);
        this.preferredFileCharset = firstNotBlank(builder.preferredFileCharset, StandardCharsets.UTF_8.name());
        this.preferredCsvSeparator = firstNotNull(emptyToNull(builder.preferredCsvSeparator), ";");
    }

    @Override
    public DateTimeFormatter getDateTimeFormat() {
        return dateTimeFormat;
    }

    @Override
    public DateTimeFormatter getDateFormat() {
        return dateFormat;
    }

    @Override
    public DateTimeFormatter getTimeFormat() {
        return timeFormat;
    }

    @Override
    public String getDateTimeFormatPattern() {
        return dateTimeFormatPattern;
    }

    @Override
    public String getDateFormatPattern() {
        return dateFormatPattern;
    }

    @Override
    public String getTimeFormatPattern() {
        return timeFormatPattern;
    }

    @Override
    public TimeZone getTimezone() {
        return timezone;
    }

    @Override
    public String getDecimalSeparator() {
        return decimalSeparator;
    }

    @Override
    @Nullable
    public String getNumberGroupingSeparator() {
        return numberGroupingSeparator;
    }

    @Override
    public DecimalFormat getDecimalFormat() {
        return (DecimalFormat) decimalFormat.clone(); //not thread safe
    }

    @Override
    public PreferredOfficeSuite getPreferredOfficeSuite() {
        return preferredOfficeSuite;
    }

    @Override
    public String getPreferredFileCharset() {
        return preferredFileCharset;
    }

    @Override
    public String getPreferredCsvSeparator() {
        return preferredCsvSeparator;
    }

    public static DateAndFormatPreferencesImplBuilder builder() {
        return new DateAndFormatPreferencesImplBuilder();
    }

    public static DateAndFormatPreferencesImplBuilder copyOf(DateAndFormatPreferences source) {
        return new DateAndFormatPreferencesImplBuilder()
                .withDateTimeFormat(source.getDateTimeFormatPattern(), source.getDateTimeFormat())
                .withDateFormat(source.getDateFormatPattern(), source.getDateFormat())
                .withTimeFormat(source.getTimeFormatPattern(), source.getTimeFormat())
                .withTimezone(source.getTimezone())
                .withDecimalSeparator(source.getDecimalSeparator())
                .withNumberGroupingSeparator(source.getNumberGroupingSeparator())
                .withPreferredOfficeSuite(source.getPreferredOfficeSuite())
                .withPreferredFileCharset(source.getPreferredFileCharset())
                .withPreferredCsvSeparator(source.getPreferredCsvSeparator());
    }

    public static class DateAndFormatPreferencesImplBuilder implements Builder<DateAndFormatPreferencesImpl, DateAndFormatPreferencesImplBuilder> {

        private DateTimeFormatter dateTimeFormat;
        private DateTimeFormatter dateFormat;
        private DateTimeFormatter timeFormat;
        private TimeZone timezone;
        private String decimalSeparator, dateTimeFormatPattern, dateFormatPattern, timeFormatPattern;
        private String numberGroupingSeparator, preferredFileCharset, preferredCsvSeparator;
        private PreferredOfficeSuite preferredOfficeSuite;

        public DateAndFormatPreferencesImplBuilder withPreferredCsvSeparator(String preferredCsvSeparator) {
            this.preferredCsvSeparator = preferredCsvSeparator;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withPreferredOfficeSuite(PreferredOfficeSuite preferredOfficeSuite) {
            this.preferredOfficeSuite = preferredOfficeSuite;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withPreferredFileCharset(String preferredFileCharset) {
            this.preferredFileCharset = preferredFileCharset;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withDateFormat(String dateFormatPattern) {
            return this.withDateFormat(dateFormatPattern, null);
        }

        public DateAndFormatPreferencesImplBuilder withTimeFormat(String timeFormatPattern) {
            return this.withTimeFormat(timeFormatPattern, null);
        }

        public DateAndFormatPreferencesImplBuilder withDateTimeFormat(String dateTimeFormatPattern) {
            return this.withDateTimeFormat(dateTimeFormatPattern, null);
        }

        public DateAndFormatPreferencesImplBuilder withDateTimeFormat(String dateTimeFormatPattern, @Nullable DateTimeFormatter dateTimeFormat) {
            this.dateTimeFormatPattern = dateTimeFormatPattern;
            this.dateTimeFormat = dateTimeFormat;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withDateFormat(String dateFormatPattern, @Nullable DateTimeFormatter dateFormat) {
            this.dateFormatPattern = dateFormatPattern;
            this.dateFormat = dateFormat;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withTimeFormat(String timeFormatPattern, @Nullable DateTimeFormatter timeFormat) {
            this.timeFormatPattern = timeFormatPattern;
            this.timeFormat = timeFormat;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withTimezone(TimeZone timezone) {
            this.timezone = timezone;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withTimezone(String timezone) {
            return this.withTimezone(TimeZone.getTimeZone(timezone));
        }

        public DateAndFormatPreferencesImplBuilder withDecimalSeparator(String decimalSeparator) {
            this.decimalSeparator = decimalSeparator;
            return this;
        }

        public DateAndFormatPreferencesImplBuilder withNumberGroupingSeparator(String numberGroupingSeparator) {
            this.numberGroupingSeparator = numberGroupingSeparator;
            return this;
        }

        @Override
        public DateAndFormatPreferencesImpl build() {
            return new DateAndFormatPreferencesImpl(this);
        }

    }
}
