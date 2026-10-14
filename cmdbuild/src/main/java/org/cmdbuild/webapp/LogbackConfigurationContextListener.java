package org.cmdbuild.webapp;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_AUTOCONFIGURE_PROPERTY;
import static org.cmdbuild.config.CoreConfiguration.CORE_LOGGER_STATIC_LOGBACK_CONFIG_LOCATION;
import org.cmdbuild.config.api.DirectoryService;
import org.cmdbuild.log.LogbackConfigFileHelper;
import org.cmdbuild.log.LogbackConfigFileHelperImpl;
import org.cmdbuild.log.LogbackHelper;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import org.cmdbuild.webapp.services.WebappDirectoryServiceImpl;
import org.slf4j.LoggerFactory;

@WebListener
public class LogbackConfigurationContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        try {
            boolean autoconfigure = toBooleanOrDefault(servletContextEvent.getServletContext().getInitParameter(CORE_LOGGER_AUTOCONFIGURE_PROPERTY), true);
            String staticConfigLocation = servletContextEvent.getServletContext().getInitParameter(CORE_LOGGER_STATIC_LOGBACK_CONFIG_LOCATION);

            if (autoconfigure) {
                DirectoryService directoryService = WebappDirectoryServiceImpl.buildFromServletContext(servletContextEvent.getServletContext()).build();
                LogbackConfigFileHelper helper = new LogbackConfigFileHelperImpl(directoryService);

                String config = helper.getConfigOrDefault();

                LogbackHelper.getInstance().configureLogback(config);
                LoggerFactory.getLogger(getClass()).info("logger ready");
            } else if (isNotBlank(staticConfigLocation)) {
                LogbackHelper.getInstance().configureLogback(staticConfigLocation);
                LoggerFactory.getLogger(getClass()).info("logger ready (using static config source = {})", staticConfigLocation);
            } else {
                LoggerFactory.getLogger(getClass()).info("logger autoconfigure disabled - using existing logger config");
            }
        } catch (Exception ex) {
            System.err.println("error loading logback configuration");
            ex.printStackTrace(System.err);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        //nothing to do
    }

}
