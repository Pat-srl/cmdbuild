/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.easytemplate;

import static com.google.common.base.Strings.nullToEmpty;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.invoke.MethodHandles;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FtlUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static boolean isFtlTemplate(String template) {
        return Pattern.compile(("(?s).*[<\\[]#?ftl[\\s>\\]]")).matcher(nullToEmpty(template)).find();
    }

    public static String prepareFtlTemplateFixHeaderIfRequired(String template) {
        template = nullToEmpty(template).replaceFirst("(?s)^(.*)([<\\[]#?ftl[^>\\]]+[>\\]])", "$2$1");
        return template;
    }

    public static String processToString(Template template, Object data) {
        try {
            Writer writer = new StringWriter();
            template.process(data, writer);
            String output = writer.toString();
            LOGGER.trace("ftl template output = \n\n{}\n", output);
            return output;
        } catch (TemplateException | IOException ex) {
            throw runtime(ex);
        }
    }

    public static Configuration getDefaultConfiguration() {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
        configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
        configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        configuration.setLogTemplateExceptions(false);
        configuration.setWrapUncheckedExceptions(true);
        return configuration;
    }

}
