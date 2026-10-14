/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.log;

import com.fasterxml.jackson.databind.JsonNode;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Maps.uniqueIndex;
import static com.google.common.collect.Streams.stream;
import static com.google.common.io.Files.copy;
import jakarta.activation.DataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import static javax.xml.xpath.XPathConstants.NODE;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.common.log.LoggerConfig;
import org.cmdbuild.common.log.LoggerConfigImpl;
import org.cmdbuild.common.log.LoggerConfigService;
import org.cmdbuild.config.CoreConfiguration;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_CONFIG_PROPERTY;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.config.api.GlobalConfigService;
import org.cmdbuild.minions.MinionComponent;
import org.cmdbuild.minions.MinionHandler;
import org.cmdbuild.minions.MinionHandlerExt;
import org.cmdbuild.minions.MinionHandlerImpl;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_ERROR;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_NOTRUNNING;
import static org.cmdbuild.minions.MinionRuntimeStatus.MRS_READY;
import static org.cmdbuild.utils.encode.CmPackUtils.pack;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.io.CmIoUtils.newDataHandler;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.toImmutableMap;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowConsumer;
import static org.cmdbuild.utils.xml.CmXmlUtils.applyXpath;
import static org.cmdbuild.utils.xml.CmXmlUtils.asList;
import static org.cmdbuild.utils.xml.CmXmlUtils.nodeToString;
import static org.cmdbuild.utils.xml.CmXmlUtils.toDocumentNoNamespace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

@Component
@Primary
public class LogbackConfigServiceImpl implements LoggerConfigService, MinionComponent {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Map<String, LoggerConfig> suggestedLoggers;

    private final LogbackHelper logbackHelper = LogbackHelper.getInstance();

    private final CoreConfiguration configuration;
    private final LogbackConfigFileHelper helper;
    private final DirectoryService directoryService;
    private final GlobalConfigService configService;

    private final MinionHandlerExt minionHandler;

    public LogbackConfigServiceImpl(GlobalConfigService configService, CoreConfiguration config, LogbackConfigFileHelper repository, DirectoryService directoryService) {
        this.helper = checkNotNull(repository);
        this.directoryService = checkNotNull(directoryService);
        this.configuration = checkNotNull(config);
        this.configService = checkNotNull(configService);
        this.minionHandler = MinionHandlerImpl.builder()
                .withName("Log Service")
                .withEnabledChecker(this::isAutoconfigureEnabled)
                .reloadOnConfigs("org.cmdbuild.core.logger")
                .build();

        JsonNode loggersInfo = fromJson(readToString(getClass().getResourceAsStream("loggers_info.json")), JsonNode.class);
        suggestedLoggers = stream(loggersInfo.get("suggested_loggers").elements())
                .map(n -> new LoggerConfigImpl(n.get("category").asText(), n.get("description").asText(), LOGGER_LEVEL_DEFAULT))
                .collect(toImmutableMap(LoggerConfig::getCategory, identity()));
    }

    @Override
    public MinionHandler getMinionHandler() {
        return minionHandler;
    }

    @Override
    public void start() {
        reloadLoggerConfigFromDbIfExistsSafe();
        try {
            if (directoryService.hasConfigDirectory()) {
                Document document = switch (configuration.getLoggerType()) {
                    case LT_DEFAULT ->
                        toDocumentNoNamespace(helper.getDefaultConfig());
                    case LT_STDOUT ->
                        toDocumentNoNamespace(helper.getStdOutConfig());
                };
                getAllLoggerConfig().forEach(l -> setLoggerConfigInDocument(document, l));
                String newConfig = nodeToString(document);
                if (!helper.hasConfigFile() || !equal(getConfigFileContent(), newConfig)) {
                    logger.info("upgrade logger config file");
                    setConfigOnDb(newConfig);
                    setConfigOnFileAndRuntime(newConfig);
                }
            } else {
                logger.debug("skip logger config auto upgrade");
            }
        } catch (Exception ex) {
            logger.error("error upgrading logger config file", ex);
        }
    }

    @Override
    public void stop() {
        minionHandler.setStatus(MRS_NOTRUNNING);//TODO improve this, set status from minion service
    }

    @Override
    public List<File> getActiveLogFiles() {
        return getLogFiles(false);
    }

    @Override
    public List<File> getAllLogFiles() {
        return getLogFiles(true);
    }

    @Override
    public DataHandler downloadLogFile(String fileName) {
        File file = getLogFileBynameOrPath(fileName);
        return newDataHandler(file);
    }

    @Override
    public DataHandler downloadAllLogFiles() {
        return downloadLogFiles(getAllLogFiles());
    }

    @Override
    public DataHandler downloadActiveLogFiles() {
        return downloadLogFiles(getActiveLogFiles());
    }

    @Override
    public List<LoggerConfig> getAllLoggerConfig() {
        logger.debug("getAllLoggerConfig");
        return asList(getCurrentConfigAsDocument().getElementsByTagName("logger")).stream().map((node) -> {
            String category = checkNotBlank(((Element) node).getAttribute("name")),
                    level = checkNotBlank(((Element) node).getAttribute("level")),
                    description = Optional.ofNullable(suggestedLoggers.get(category)).map(LoggerConfig::getDescription).orElse("");
            return new LoggerConfigImpl(category, description, level);
        }).collect(toList());
    }

    @Override
    public List<LoggerConfig> getAllLoggerConfigIncludeUnconfigured() {
        Map<String, LoggerConfig> configuredLoggers = uniqueIndex(getAllLoggerConfig(), LoggerConfig::getCategory);
        return set(suggestedLoggers.keySet()).with(configuredLoggers.keySet()).stream().sorted()
                .map(c -> configuredLoggers.getOrDefault(c, suggestedLoggers.get(c)))
                .collect(toImmutableList());
    }

    @Override
    public void removeLoggerConfig(String category) {
        logger.info("removeLoggerConfig = {}", category);
        Document document = getCurrentConfigAsDocument();
        asList(document.getElementsByTagName("logger")).stream().filter((node) -> equal(((Element) node).getAttribute("name"), category)).forEach((node) -> {
            node.getParentNode().removeChild(node);
        });
        setConfigOnDb(nodeToString(document));
    }

    @Override
    public void setLoggerConfig(LoggerConfig loggerConfig) {
        logger.info("setLoggerConfig = {}", loggerConfig);
        Document document = getCurrentConfigAsDocument();
        setLoggerConfigInDocument(document, loggerConfig);
        setConfigOnDb(nodeToString(document));
    }

    @Override
    public String getConfigFileContent() {
        return helper.getConfigOrDefault();
    }

    private List<File> getLogFiles(boolean includeArchives) {
        List<File> configFiles = includeArchives ? logbackHelper.getAllLogFiles() : logbackHelper.getActiveLogFiles();
        if (directoryService.hasContainerLogDirectory()) {
            File catalinaOut = new File(directoryService.getContainerLogDirectory(), "catalina.out");//TODO improve this
            if (catalinaOut.exists()) {
                configFiles = set(configFiles).with(catalinaOut).stream().sorted().collect(toImmutableList());
            }
        }
        return configFiles;
    }

    private DataHandler downloadLogFiles(List<File> files) {
        File temp = tempFile("logs_", ".zip");
        try (ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(temp))) {
            files.forEach(rethrowConsumer(f -> {
                zip.putNextEntry(new ZipEntry(f.getName()));
                copy(f, zip);
                zip.closeEntry();
            }));
        } catch (IOException ex) {
            throw runtime(ex);
        }
        return newDataHandler(temp);
    }

    private File getLogFileBynameOrPath(String fileNameOrPath) {
        checkNotBlank(fileNameOrPath);
        return getAllLogFiles().stream().filter(f -> equal(f.getName(), fileNameOrPath) || equal(f.getAbsolutePath(), fileNameOrPath)).collect(onlyElement("log file not found for fileName/path =< %s >", fileNameOrPath));
    }

    private boolean isAutoconfigureEnabled() {
        try {
            return configuration.isLogbackAutoconfigurationEnabled() && (!helper.hasConfigFile() || toBooleanOrDefault(applyXpath(getConfigFileContent(), "//*[local-name()='property'][@name='CM_AUTO_UPGRADE_CONFIG']/@value"), true));
        } catch (Exception ex) {
            logger.error("error processing logger config file", ex);
            return true;
        }
    }

    private void setLoggerConfigInDocument(Document document, LoggerConfig loggerConfig) {
        try {
            Element thisLogger = (Element) XPathFactory.newInstance().newXPath().compile(format("//*[local-name()='logger'][@name='%s']", loggerConfig.getCategory())).evaluate(document, NODE);
            if (thisLogger != null) {
                logger.debug("logger config already present, update element = {}", thisLogger);
                thisLogger.setAttribute("level", loggerConfig.getLevel());
            } else {
                logger.debug("logger config not present, insert new logger element before root logger element, for category = {}", loggerConfig.getCategory());
                Element rootLogger = (Element) document.getElementsByTagName("root").item(0);
                Element newLogger = document.createElement("logger");
                newLogger.setAttribute("name", loggerConfig.getCategory());
                newLogger.setAttribute("level", loggerConfig.getLevel());
//                if (isNotBlank(loggerConfig.getModule())) { TODO
//                    newLogger.setAttribute("additivity", "false");
//                }
                rootLogger.getParentNode().insertBefore(newLogger, rootLogger);
                rootLogger.getParentNode().insertBefore(document.createTextNode("\n\n    "), rootLogger);
            }
        } catch (XPathExpressionException ex) {
            throw runtime(ex);
        }

    }

    private Document getCurrentConfigAsDocument() {
        try {
            return toDocumentNoNamespace(getConfigFileContent());
        } catch (Exception ex1) {
            logger.error("error reading current log config, trying to reset config", ex1);
            try {
                return toDocumentNoNamespace(helper.getDefaultConfig());
            } catch (Exception ex2) {
                logger.error("error processing default log config, using fallback config", ex2);
                return toDocumentNoNamespace(helper.getFallbackConfig());
            }
        }
    }

    private void setConfigOnDb(String config) {
        configService.putString(CORE_LOGGER_CONFIG_PROPERTY, pack(config));
    }

    private void setConfigOnFileAndRuntime(String config) {
        logbackHelper.configureLogback(config);
        helper.setConfig(config);

    }

    private void reloadLoggerConfigFromDbIfExistsSafe() {
        try {
            reloadLoggerConfigFromDbIfExists();
            minionHandler.setStatus(MRS_READY);
        } catch (Exception ex) {
            minionHandler.setStatus(MRS_ERROR);
            logbackHelper.configureLogback(helper.getFallbackConfig());
            logger.error("error reloading logger config from db", ex);
        }
    }

    private void reloadLoggerConfigFromDbIfExists() {
        String config = unpackIfPacked(configuration.getLoggerConfig());
        if (isNotBlank(config)) {
            config = helper.setConfigPropertiesInLogbackConfig(config);
            if (!equal(helper.getConfigOrNull(), config)) {
                setConfigOnFileAndRuntime(config);
            }
        }
    }

}
