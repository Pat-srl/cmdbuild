/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.services;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Supplier;
import java.io.File;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import jakarta.annotation.Nullable;
import jakarta.servlet.ServletContext;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.config.api.DirectoryServiceImpl;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

@Component
public class WebappDirectoryServiceImpl extends DirectoryServiceImpl {

    public WebappDirectoryServiceImpl(ServletContext servletContext) {
        super(buildFromServletContext(servletContext));
        logger.info("cmdbuild webapp dir = {}", webappDirectory);
        logger.info("cmdbuild config dir = {}", configDirectory);
        logger.info("container dir = {}", containerDirectory);
        logger.info("log dir = {}", logDirectory);
    }

    public static DirectoryServiceImplBuilder buildFromServletContext(ServletContext servletContext) {
        return builder()
                .withContainerDirectory(getFileOrNullIfError("catalina base dir", () -> new File(checkNotBlank(System.getProperty("catalina.base")))))
                .withConfigDirectory(getFileOrNullIfError("cmdbuild config dir", () -> getConfigDirectory(servletContext)))
                .withLogDirectory(getFileOrNullIfError("cmdbuild log dir", () -> getLogsDirectory(servletContext)))
                .withWebappDirectory(getFileOrNullIfError("cmdbuild webapp dir", () -> getWebappRoot(servletContext)));
    }

    @Nullable
    private static File getFileOrNullIfError(String info, Supplier<File> supplier) {
        File file;
        try {
            file = supplier.get();
        } catch (Exception ex) {
            LoggerFactory.getLogger(WebappDirectoryServiceImpl.class).error("error configuring {}", info, ex);
            file = null;
        }
        return file;
    }

    public static File getConfigDirectory(ServletContext servletContext) {
        String cmdbuildContext = getContextNameFromWebappName(getWebappName(servletContext));
        if (isNotBlank(servletContext.getInitParameter("configLocation"))) {//legacy param value 
            return prepareConfigDir(servletContext.getInitParameter("configLocation"));
        }
        if (isNotBlank(servletContext.getInitParameter("org.cmdbuild.config.location"))) {//new param value
            return prepareConfigDir(servletContext.getInitParameter("org.cmdbuild.config.location"));
        }
        File configDir = new File(format("%s/conf/%s", System.getProperty("catalina.base"), cmdbuildContext)),
                webInfConf = new File(getServletContextRootPath(servletContext) + "/WEB-INF/conf");
        if (!configDir.isDirectory() && webInfConf.isDirectory() && stream(webInfConf.listFiles()).anyMatch(f -> isConfigFile(f))) {
            return webInfConf;
        }
        return prepareConfigDir(configDir.getAbsolutePath());
    }

    @Nullable
    private static File getLogsDirectory(ServletContext servletContext) {
        if (isNotBlank(servletContext.getInitParameter("org.cmdbuild.logs.location"))) {
            return new File(servletContext.getInitParameter("org.cmdbuild.logs.location"));
        } else {
            return null;
        }
    }

    public static File getConfigFile(ServletContext servletContext, String fileName) {
        return new File(getConfigDirectory(servletContext), fileName);
    }

    public static String getServletContextRootPath(ServletContext servletContext) {
        return servletContext.getRealPath("/");
    }

    public static File getWebappRoot(ServletContext servletContext) {
        return new File(getServletContextRootPath(servletContext));
    }

    public static String getWebappName(ServletContext servletContext) {
        return getWebappRoot(servletContext).getName();
    }

    private static boolean isConfigFile(File file) {
        return file.getName().endsWith(".conf");
    }

    private static File prepareConfigDir(String path) {
        try {
            checkNotBlank(path);
            File file = new File(path);
            file.mkdirs();
            checkArgument(file.isDirectory(), "invalid config dir =< %s >", path);
            return file;
        } catch (Exception ex) {
            throw runtime(ex, "error preparing config dir =< %s >", path);
        }
    }

}
