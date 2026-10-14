/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.userconfig;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.emptyToNull;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;
import java.util.TimeZone;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.config.PreferencesConfiguration;
import org.cmdbuild.config.PreferencesConfiguration.PreferredOfficeSuite;
import org.cmdbuild.config.UiConfiguration;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DATETIME_FORMAT_EXTJS;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIMEZONE;
import static org.cmdbuild.utils.date.CmDateUtils.systemTimeZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DATE_FORMAT_EXTJS;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_DECIMAL_SEPARATOR;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_PREFERRED_FILE_CHARSET;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_PREFERRED_CSV_SEPARATOR;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_PREFERRED_OFFICE_SUITE;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_THOUSANDS_SEPARATOR;
import static org.cmdbuild.userconfig.UserConfigConst.USER_CONFIG_TIME_FORMAT_EXTJS;
import static org.cmdbuild.utils.date.ExtjsDateUtils.extjsDateTimeFormatToJavaDateTimeFormat;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrEmpty;

@Component
public class UserPreferencesServiceImpl implements UserPreferencesService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final UiConfiguration uiConfiguration;
    private final CoreConfiguration coreConfiguration;
    private final UserConfigService userConfigService;
    private final PreferencesConfiguration preferencesConfiguration;

    public UserPreferencesServiceImpl(UiConfiguration uiConfiguration, CoreConfiguration coreConfiguration, UserConfigService userConfigService, PreferencesConfiguration preferencesConfiguration) {
        this.uiConfiguration = checkNotNull(uiConfiguration);
        this.coreConfiguration = checkNotNull(coreConfiguration);
        this.userConfigService = checkNotNull(userConfigService);
        this.preferencesConfiguration = checkNotNull(preferencesConfiguration);
    }

    @Override
    public DateAndFormatPreferences getUserPreferences() {
        Map<String, String> config = userConfigService.getForCurrentUsername();
        TimeZone timezone = Optional.ofNullable(trimToNull(config.get(USER_CONFIG_TIMEZONE))).map(value -> {
            try {
                return TimeZone.getTimeZone(value);
            } catch (Exception ex) {
                logger.error(marker(), "invalid user time zone value =< {} >", value, ex);
                return null;
            }
        }).or(() -> Optional.ofNullable(emptyToNull(coreConfiguration.getTimezone())).map(TimeZone::getTimeZone)).orElse(systemTimeZone());
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME,
                dateFormatter = DateTimeFormatter.ISO_DATE,
                timeFormatter = DateTimeFormatter.ISO_TIME;
        String dateTimeFormatPattern = "yyyy-MM-dd'T'HH:mm:ssZ",
                dateFormatPattern = "yyyy-MM-dd",
                timeFormatPattern = "HH:mm";
        String extjsUserDateFormat = firstNotBlankOrEmpty(config.get(USER_CONFIG_DATE_FORMAT_EXTJS), uiConfiguration.getDateFormat()),
                extjsUserTimeFormat = firstNotBlankOrEmpty(config.get(USER_CONFIG_TIME_FORMAT_EXTJS), uiConfiguration.getTimeFormat());
        try {
            String userDateFormat = extjsDateTimeFormatToJavaDateTimeFormat(extjsUserDateFormat);
            dateFormatter = DateTimeFormatter.ofPattern(userDateFormat);
            dateFormatPattern = userDateFormat;
        } catch (Exception ex) {
            logger.error(marker(), "invalid user date format value =< {} >", extjsUserDateFormat, ex);
        }
        try {
            String userTimeFormat = extjsDateTimeFormatToJavaDateTimeFormat(extjsUserTimeFormat);
            timeFormatter = DateTimeFormatter.ofPattern(userTimeFormat);
            timeFormatPattern = userTimeFormat;
        } catch (Exception ex) {
            logger.error(marker(), "invalid user time format value =< {} >", extjsUserTimeFormat, ex);
        }
        try {
            String userDateTimeFormat = isNotBlank(config.get(USER_CONFIG_DATETIME_FORMAT_EXTJS))
                    ? extjsDateTimeFormatToJavaDateTimeFormat(config.get(USER_CONFIG_DATETIME_FORMAT_EXTJS))
                    : format("%s %s", extjsDateTimeFormatToJavaDateTimeFormat(extjsUserDateFormat), extjsDateTimeFormatToJavaDateTimeFormat(extjsUserTimeFormat));//TODO single datetime format config override
            dateTimeFormatter = DateTimeFormatter.ofPattern(userDateTimeFormat);
            dateTimeFormatPattern = userDateTimeFormat;
        } catch (Exception ex) {
            logger.error(marker(), "invalid user date time format value ", ex);
        }
        String decimalSeparator = uiConfiguration.getDecimalsSeparator(),
                userDecSep = config.get(USER_CONFIG_DECIMAL_SEPARATOR);
        try {
            if (isNotBlank(userDecSep)) {
                userDecSep = userDecSep.trim();
                checkArgument(userDecSep.length() == 1);
                decimalSeparator = userDecSep;
            }
        } catch (Exception ex) {
            logger.error(marker(), "invalid user decimal separator value =< {} >", userDecSep, ex);
        }
        String thousandsSeparator = uiConfiguration.getThousandsSeparator(),
                userThoSep = config.get(USER_CONFIG_THOUSANDS_SEPARATOR);
        try {
            if (userThoSep != null) { // allow '' and ' ' as valid values here ( '' means no separator )
                thousandsSeparator = userThoSep;
            }
        } catch (Exception ex) {
            logger.error(marker(), "invalid user thousands separator value =< {} >", userThoSep, ex);
        }

        PreferredOfficeSuite preferredOfficeSuite = preferencesConfiguration.getPreferredOfficeSuite();
        try {
            preferredOfficeSuite = parseEnumOrDefault(config.get(USER_CONFIG_PREFERRED_OFFICE_SUITE), preferencesConfiguration.getPreferredOfficeSuite());
        } catch (Exception ex) {
            logger.error(marker(), "invalid preferred user office suite value =< {} >", config.get(USER_CONFIG_PREFERRED_OFFICE_SUITE), ex);
        }

        String preferredFileCharset = preferencesConfiguration.getPreferredFileCharset();
        try {
            preferredFileCharset = Charset.forName(firstNotBlank(config.get(USER_CONFIG_PREFERRED_FILE_CHARSET), preferencesConfiguration.getPreferredFileCharset())).name();
        } catch (Exception ex) {
            logger.error(marker(), "invalid preferred file charset value =< {} >", config.get(USER_CONFIG_PREFERRED_FILE_CHARSET), ex);
        }

        String preferredCsvSeparator = preferencesConfiguration.getPreferredCsvSeparator();
        try {
            preferredCsvSeparator = firstNotNull(config.get(USER_CONFIG_PREFERRED_CSV_SEPARATOR), preferencesConfiguration.getPreferredCsvSeparator());
        } catch (Exception ex) {
            logger.error(marker(), "invalid preferred csv separator value =< {} >", config.get(USER_CONFIG_PREFERRED_CSV_SEPARATOR), ex);
        }

        return DateAndFormatPreferencesImpl.builder()
                .withTimezone(timezone)
                .withDateFormat(dateFormatPattern, dateFormatter)
                .withTimeFormat(timeFormatPattern, timeFormatter)
                .withDateTimeFormat(dateTimeFormatPattern, dateTimeFormatter)
                .withDecimalSeparator(decimalSeparator)
                .withNumberGroupingSeparator(thousandsSeparator)
                .withPreferredOfficeSuite(preferredOfficeSuite)
                .withPreferredFileCharset(preferredFileCharset)
                .withPreferredCsvSeparator(preferredCsvSeparator)
                .build();
    }

    @Override
    public UserPrefHelper getUserPreferencesHelper() {
        return new UserPrefHelperImpl(getUserPreferences());
    }

}
