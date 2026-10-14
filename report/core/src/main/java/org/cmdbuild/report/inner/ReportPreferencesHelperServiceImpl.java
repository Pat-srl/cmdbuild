/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.report.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.util.DefaultFormatFactory;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.userconfig.DateAndFormatPreferences;
import org.cmdbuild.userconfig.UserPreferencesService;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

@Component
public class ReportPreferencesHelperServiceImpl implements ReportPreferencesHelperService {

    private final LanguageService languageService;
    private final UserPreferencesService userPreferences;

    public ReportPreferencesHelperServiceImpl(LanguageService languageService, UserPreferencesService userPreferences) {
        this.languageService = checkNotNull(languageService);
        this.userPreferences = checkNotNull(userPreferences);
    }

    @Override
    public Map<String, Object> getUserPreferencesReportParams() {
        return map(JRParameter.REPORT_LOCALE, languageService.getRequestLocale(),
                JRParameter.REPORT_TIME_ZONE, userPreferences.getUserPreferences().getTimezone(),
                JRParameter.REPORT_FORMAT_FACTORY, buildReportFormatFactory()
        /**
         * A {@link net.sf.jasperreports.engine.util.FormatFactory}
         * instance to be used during the report filling process to
         * create instances of <code>java.text.DateFormat</code> to
         * format date text fields and instances of
         * <code>java.text.NumberFormat</code> to format numeric text
         * fields.
         * <p>
         * The value for this parameter is an instance of the
         * {@link net.sf.jasperreports.engine.util.FormatFactory}
         * interface, which is either provided directly by the calling
         * program or created internally by the reporting engine, using
         * the <code>formatFactoryClass</code> attribute of the report
         * template. If this parameter is provided with a value by the
         * report-filling process caller, it takes precedence over the
         * attribute in the report template.
         */
        //	public static final String REPORT_FORMAT_FACTORY = "REPORT_FORMAT_FACTORY";
        );

    }

    private net.sf.jasperreports.engine.util.FormatFactory buildReportFormatFactory() {
        DateAndFormatPreferences pref = userPreferences.getUserPreferences();
        return new DefaultFormatFactory() {

            @Override
            public NumberFormat createNumberFormat(String pattern, Locale locale) {
                NumberFormat format = super.createNumberFormat(pattern, locale);
                if (format != null && format instanceof DecimalFormat) {
                    DecimalFormat decimalFormat = (DecimalFormat) format;
                    DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance();
                    decimalFormatSymbols.setDecimalSeparator(pref.getDecimalSeparator().charAt(0)); //TODO check this (set to dec format symbols)
                    if (pref.hasNumberGroupingSeparator()) {
                        decimalFormatSymbols.setGroupingSeparator(pref.getNumberGroupingSeparator().charAt(0));
                    } else {
                        decimalFormat.setGroupingUsed(false);
                    }
                    decimalFormat.setDecimalFormatSymbols(decimalFormatSymbols);
                }
                return format;
            }
        };
    }

}
