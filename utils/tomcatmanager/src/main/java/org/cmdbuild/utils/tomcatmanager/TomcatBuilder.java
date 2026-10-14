/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.tomcatmanager;

import com.google.common.base.Charsets;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Iterables;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.xml.xpath.XPath;
import static javax.xml.xpath.XPathConstants.NODE;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import net.lingala.zip4j.ZipFile;
import net.lingala.zip4j.exception.ZipException;
import net.lingala.zip4j.model.FileHeader;
import org.apache.commons.io.FileUtils;
import static org.apache.commons.io.FileUtils.copyDirectory;
import org.apache.commons.io.IOUtils;
import static org.cmdbuild.utils.io.CmIoUtils.fetchFileWithCache;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.io.CmPlatformUtils.isLinux;
import static org.cmdbuild.utils.io.CmPlatformUtils.isWindows;
import static org.cmdbuild.utils.io.CmZipUtils.unzipToDir;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.trimAndCheckNotBlank;
import static org.cmdbuild.utils.tomcatmanager.TomcatManagerUtils.execSafe;
import static org.cmdbuild.utils.xml.CmXmlUtils.nodeToString;
import static org.cmdbuild.utils.xml.CmXmlUtils.toDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class TomcatBuilder {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final TomcatConfig tomcatConfig;

    public TomcatBuilder(TomcatConfig tomcatConfig) {
        checkNotNull(tomcatConfig);
        this.tomcatConfig = tomcatConfig;
    }

    public void buildTomcat() {
        try {
            checkArgument(!tomcatConfig.getInstallDir().exists() || tomcatConfig.getInstallDir().list().length == 0, "install dir must be empty");
            logger.info("buildTomcat BEGIN");
            unpackToDir();
            prepareTomcatConfig();
            deployWars();
            applyConfigOverlay();
            logger.info("buildTomcat END");
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    public void cleanup() {
        if (containsTomcat(tomcatConfig.getInstallDir())) {
            FileUtils.deleteQuietly(tomcatConfig.getInstallDir());
        }
        if (tomcatConfig.getInstallDir().exists()) {
            logger.warn("unable to clean properly tomcat install dir = {}", tomcatConfig.getInstallDir().getAbsolutePath());
        }
    }

    private static boolean containsTomcat(File dir) {
        return new File(dir, "bin/startup.sh").exists();
    }

    private void unpackToDir() throws IOException, ZipException, InterruptedException {
        String tomcatUrl = checkNotBlank(tomcatConfig.getProperty("tomcat_url")),
                tomcatChecksum = checkNotBlank(tomcatConfig.getProperty("tomcat_checksum"));
        File tomcatFile = fetchFileWithCache(tomcatChecksum, tomcatUrl);
        logger.info("unpacking tomcat {} ({}) to {}", tomcatFile.getName(), FileUtils.byteCountToDisplaySize(tomcatFile.length()), tomcatConfig.getInstallDir().getAbsolutePath());
        ZipFile zipFile = new ZipFile(tomcatFile);
        FileHeader fileHeader = Iterables.find(zipFile.getFileHeaders(), (FileHeader f) -> f.getFileName().matches("apache-tomcat-[0-9.]+/?"));
        File tempUnzipDir = new File(tomcatConfig.getInstallDir().getParentFile(), UUID.randomUUID().toString());
        tempUnzipDir.deleteOnExit();
        zipFile.extractAll(tempUnzipDir.getAbsolutePath());
        tomcatConfig.getInstallDir().delete();
        FileUtils.moveDirectory(new File(tempUnzipDir, fileHeader.getFileName()), tomcatConfig.getInstallDir());
        FileUtils.deleteQuietly(tempUnzipDir);
        checkArgument(containsTomcat(tomcatConfig.getInstallDir()));

        if (isLinux()) {
            String startupDebugScript = IOUtils.toString(getClass().getResourceAsStream("/org/cmdbuild/utils/tomcatmanager/startup_debug.sh"), Charset.defaultCharset());
            startupDebugScript = startupDebugScript.replaceAll("CMDBUILD_DEBUG_PORT", Integer.toString(tomcatConfig.getDebugPort()));
            FileUtils.writeStringToFile(new File(tomcatConfig.getInstallDir(), "bin/startup_debug.sh"), startupDebugScript, Charset.defaultCharset());
            checkArgument(execSafe(tomcatConfig.getInstallDir(), "/bin/bash", "-c", "chmod +x bin/*.sh") == 0);
        }
        logger.info("successfully unpacked tomcat binary distribution to dir = {}", tomcatConfig.getInstallDir().getAbsolutePath());
    }

    private void prepareTomcatConfig() throws IOException, InterruptedException, XPathExpressionException {
        logger.info("configure tomcat ports, http port = {}, shutdown port = {}", tomcatConfig.getHttpPort(), tomcatConfig.getShutodownPort());
        File configFile = new File(tomcatConfig.getInstallDir(), "conf/server.xml");
        Document document = toDocument(readToString(configFile));
        XPath xpath = XPathFactory.newInstance().newXPath();
        ((Element) xpath.evaluate("/*[local-name()='Server']", document, NODE))
                .setAttribute("port", Integer.toString(tomcatConfig.getShutodownPort()));
        ((Element) xpath.evaluate("//*[local-name()='Connector'][@port='8080']", document, NODE))
                .setAttribute("port", Integer.toString(tomcatConfig.getHttpPort()));
        writeToFile(configFile, nodeToString(document));
        if (isLinux()) {
            try (Writer writer = new FileWriter(new File(tomcatConfig.getInstallDir(), "bin/setenv.sh"), true)) {
                logger.info("set catalina pid = {}", tomcatConfig.getCatalinaPidFile().getAbsolutePath());
                writer.write(format("""
                                    CATALINA_PID=\"%s\"
                                    CATALINA_OPTS=\"$CATALINA_OPTS --add-opens=java.base/java.nio=ALL-UNNAMED --add-opens=java.base/sun.reflect.generics.reflectiveObjects=ALL-UNNAMED --add-opens=java.base/java.util.stream=ALL-UNNAMED --add-opens=java.base/jdk.internal.loader=ALL-UNNAMED\"
                                    """, tomcatConfig.getCatalinaPidFile().getAbsolutePath()));
            }
        } else if (isWindows()) {
            try (Writer writer = new FileWriter(new File(tomcatConfig.getInstallDir(), "bin/setenv.bat"), true)) {
                logger.info("set catalina pid = {}", tomcatConfig.getCatalinaPidFile().getAbsolutePath());
                writer.write("\n\nset CATALINA_PID=" + tomcatConfig.getCatalinaPidFile().getAbsolutePath() + "\n\n");
            }
        }
    }

    public void deployWar(String warArtifactAndName) {
        try {
            logger.info("deploy war artifact = {} to tomcat = {}", warArtifactAndName, tomcatConfig.getInstallDir().getAbsolutePath());
            String warFile, webappName;
            Matcher matcher = Pattern.compile("^(.*) +AS +(.*)$").matcher(warArtifactAndName);
            if (matcher.find()) {
                warFile = trimAndCheckNotBlank(matcher.group(1));
                webappName = trimAndCheckNotBlank(matcher.group(2));
            } else {
                warFile = trimAndCheckNotBlank(warArtifactAndName);
                webappName = null;//set from file later
            }
            File file = new File(warFile);
            checkArgument(file.exists(), "invalid war file = {}", warFile);
            if (webappName == null) {
                webappName = trimAndCheckNotBlank(file.getName().replaceFirst("(-[0-9].*)?(.war)?$", ""));
            }
            File targetDir = new File(tomcatConfig.getInstallDir(), "webapps/" + webappName);
            if (file.isDirectory()) {
                copyDirectory(file, targetDir);
            } else {
//				checkArgument(file.getName().endsWith(".war"), "invalid war file = %s", file); TODO validate war file
                unzipToDir(file, targetDir);
            }
            checkArgument(new File(targetDir, "WEB-INF").exists());
            logger.info("successfully deployed war artifact = {} to dir = {}", warArtifactAndName, targetDir);
        } catch (IOException ex) {
            throw runtime(ex);
        }
    }

    private void deployWars() {
        tomcatConfig.getPropertyAsList("tomcat_deploy_artifacts").forEach((warArtifact) -> {
            deployWar(warArtifact);
        });
    }

    private void applyConfigOverlay() throws IOException {
        for (String key : tomcatConfig.getPropertyAsList("tomcat_config_overlay")) {
            String fileName = tomcatConfig.getProperty("tomcat_config_overlay." + key + ".file"),
                    fileContent = tomcatConfig.getProperty("tomcat_config_overlay." + key + ".content");
            File file = new File(tomcatConfig.getInstallDir(), fileName);
            logger.info("adding config overlay for key = {} file = {} content = \n{}", key, file.getAbsolutePath(), fileContent);
            file.getParentFile().mkdirs();
            FileUtils.writeStringToFile(file, fileContent, Charsets.UTF_8);
        }
    }
}
