/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.webapp.filters;

import freemarker.template.Template;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import static org.apache.commons.lang3.StringUtils.EMPTY;
import org.cmdbuild.common.localization.LanguageService;
import static org.cmdbuild.easytemplate.FtlUtils.getDefaultConfiguration;
import static org.cmdbuild.easytemplate.FtlUtils.processToString;
import static org.cmdbuild.translation.TranslationSection.TS_UICUSTOM;
import org.cmdbuild.translation.TranslationService;
import static org.cmdbuild.utils.json.CmJsonUtils.toPrettyJson;
import static org.cmdbuild.utils.lang.CmInlineUtils.recursiveUnflattenMaps;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;

/**
 *
 * @author ataboga
 */
@Configuration
public class UiCustomLocalesFilter implements Filter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final LanguageService languageService;
    private final TranslationService translationService;

    private final Template template;

    public UiCustomLocalesFilter(LanguageService languageService, TranslationService translationService) throws IOException {
        this.languageService = checkNotNull(languageService);
        this.translationService = checkNotNull(translationService);

        this.template = new Template("custom_locales_template.js.ftl", new InputStreamReader(getClass().getResourceAsStream("/org/cmdbuild/webapp/filters/custom_locales_template.js.ftl")), getDefaultConfiguration());
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.debug("ui custom locales filter doFilter BEGIN");
        try {
            Map<String, Object> data = map("singleton", true, "localization", languageService.getContextLanguage());
            Map<String, String> translations = map(translationService.getTranslationsBySectionAndCurrentUser(TS_UICUSTOM)).mapKeys(k -> k.replaceAll("^uicustom[.]|[.]description$", EMPTY));
            String customLocales = toPrettyJson(recursiveUnflattenMaps(map(data).with(translations)));

            byte[] responseData = processToString(template, map("customLocales", customLocales)).getBytes(StandardCharsets.UTF_8);

            response.setContentType("application/javascript");
            response.setContentLength(responseData.length);
            response.getOutputStream().write(responseData);

            logger.debug("ui custom locales filter doFilter END");
        } catch (Exception ex) {
            logger.error("error in ui custom locales filter", ex);
            throw ex;
        }
    }
}
