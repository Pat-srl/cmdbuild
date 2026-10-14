/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.log;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import java.io.File;
import static java.lang.String.format;
import static javax.xml.xpath.XPathConstants.NODE;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.javaTmpDir;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import static org.cmdbuild.utils.xml.CmXmlUtils.nodeToString;
import static org.cmdbuild.utils.xml.CmXmlUtils.toDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
public class LogbackConfigFileHelperImpl implements LogbackConfigFileHelper {

    private final static String LOGBACK_CONFIG_FILE_NAME = "logback.xml",
            DEFAULT_LOGBACK_CONFIG_TEMPLATE = checkNotBlank(readToString(LogbackConfigFileHelperImpl.class.getResourceAsStream("/org/cmdbuild/log/logback_default.xml"))),
            STDOUT_LOGBACK_CONFIG_TEMPLATE = checkNotBlank(readToString(LogbackConfigFileHelperImpl.class.getResourceAsStream("/org/cmdbuild/log/logback_stdout.xml"))),
            FALLBACK_LOGBACK_CONFIG = checkNotBlank(readToString(LogbackConfigFileHelperImpl.class.getResourceAsStream("/org/cmdbuild/log/logback_fallback.xml")));

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DirectoryService directoryService;

    public LogbackConfigFileHelperImpl(DirectoryService directoryService) {
        this.directoryService = checkNotNull(directoryService);
    }

    @Override
    public String getConfigOrDefault() {
        return setConfigPropertiesInLogbackConfig(firstNotBlank(getConfigOrNull(), DEFAULT_LOGBACK_CONFIG_TEMPLATE));
    }

    @Override
    public String setConfigPropertiesInLogbackConfig(String logbackConfig) {
        Document document = toDocument(logbackConfig);
        String logname = checkNotBlank(directoryService.getContextName());
        String logDir;
        if (directoryService.hasLogDirectory()) {
            logDir = directoryService.getLogDirectory().getAbsolutePath();
        } else {
            logDir = new File(javaTmpDir(), "cmdbuild_logs_" + randomId(4)).getAbsolutePath();
        }
        setXmlConfigProperty(document, "CM_LOG_DIR", logDir.replace('\\', '/'));
        setXmlConfigProperty(document, "CM_LOG_NAME", logname);
        if (toBooleanOrDefault(System.getProperty("cmdbuild.logger.debug"), false)) {
            enableLogbackDebug(document);
        }
        return nodeToString(document);
    }

    @Override
    public String getDefaultConfig() {
        return setConfigPropertiesInLogbackConfig(DEFAULT_LOGBACK_CONFIG_TEMPLATE);
    }

    @Override
    public String getStdOutConfig() {
        return setConfigPropertiesInLogbackConfig(STDOUT_LOGBACK_CONFIG_TEMPLATE);
    }

    @Override
    public String getFallbackConfig() {
        return FALLBACK_LOGBACK_CONFIG;
    }

    @Override
    @Nullable
    public String getConfigOrNull() {
        if (directoryService.hasConfigDirectory()) {
            File file = getConfigFile();
            if (file.exists()) {
                try {
                    return CmIoUtils.readToString(file);
                } catch (Exception ex) {
                    logger.error("error reading logback config from file = {}", file, ex);
                }
            }
        }
        return null;
    }

    @Override
    public void setConfig(String xmlConfiguration) {
        checkArgument(directoryService.hasConfigDirectory(), "unable to store logback config: config directory is not available");
        File file = getConfigFile();
        if (file.exists()) {
            directoryService.backupFileSafe(file);
        }
        logger.info("update logback config file = {}", file.getAbsolutePath());
        writeToFile(file, xmlConfiguration);
    }

    @Override
    public boolean hasConfigFile() {
        return directoryService.hasConfigDirectory() && getConfigFile().isFile() && isNotBlank(getConfigOrNull());
    }

    private static void setXmlConfigProperty(Document document, String key, String value) {
        try {
            Element element = (Element) XPathFactory.newInstance().newXPath().evaluate(format("/*[local-name()='configuration']/*[local-name()='property'][@name='%s']", key), document, NODE);
            if (element != null) {
                element.setAttribute("value", value);
            }
        } catch (XPathExpressionException ex) {
            throw runtime(ex);
        }
    }

    private static void enableLogbackDebug(Document document) {
        try {
            Element element = (Element) XPathFactory.newInstance().newXPath().evaluate("/*[local-name()='configuration']", document, NODE);
            element.setAttribute("debug", "true");
        } catch (XPathExpressionException ex) {
            throw runtime(ex);
        }
    }

    private File getConfigFile() {
        return new File(directoryService.getConfigDirectory(), LOGBACK_CONFIG_FILE_NAME);
    }
}
