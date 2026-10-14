/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.systemplugin;

import com.google.common.base.Splitter;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import static java.util.Collections.emptySet;
import static java.util.Comparator.comparing;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toUnmodifiableList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import static org.apache.commons.io.FileUtils.readLines;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.minions.MinionStatus.MS_ERROR;
import static org.cmdbuild.minions.MinionStatus.MS_READY;
import static org.cmdbuild.utils.hash.CmHashUtils.hash;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.io.CmPropertyUtils.loadProperties;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.getUnsafe;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.notNull;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import static org.cmdbuild.utils.lang.CmStringUtils.nullToEmpty;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeConsumer;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.unsafeFunction;
import static org.cmdbuild.utils.maven.MavenUtils.getResourceByGavOrNull;
import static org.cmdbuild.utils.maven.MavenUtils.mavenGavToFilename;
import static org.cmdbuild.utils.maven.MavenUtils.mavenNameVersionToFilename;
import static org.cmdbuild.utils.xml.CmXmlUtils.applyXpath;
import static org.cmdbuild.utils.xml.CmXmlUtils.toDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;

public class SystemPluginUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String buildPluginStatusInfoMessage(List<SystemPlugin> plugins) {
        return plugins.isEmpty() ? "" : (list(plugins).sorted(SystemPlugin::getName).map(p -> format("%-28s    %s   %s", p.getDescription(), serializeEnum(p.isOk() ? MS_READY : MS_ERROR), abbreviate(p.getHealthCheckMessage()))).collect(joining("\n")) + "\n\n");
    }

    public static List<SystemPlugin> scanWarFileForPlugins(File warFileOrWebappDir) {
        if (warFileOrWebappDir.isFile()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create(format("jar:%s", warFileOrWebappDir.toPath().toUri())), emptyMap())) {
                Path index = fileSystem.getPath("/WEB-INF/lib/systemlibs.list");
                Set<String> systemLibs = Files.exists(index) ? list(Files.readAllLines(index)).filter(StringUtils::isNotBlank).sorted().toSet() : emptySet();
                return Files.list(fileSystem.getPath("/WEB-INF/lib/"))
                        .filter(p -> p.getFileName().toString().endsWith(".jar") && !systemLibs.contains(p.getFileName().toString()))
                        .map(unsafeFunction(p -> scanFileForPlugin(p.getFileName().toString(), Files.readAllBytes(p)))).filter(notNull()).sorted(comparing(SystemPlugin::getNameVersion))
                        .collect(toUnmodifiableList());
            } catch (IOException ex) {
                throw runtime(ex);
            }
        } else {
            return scanFolderForPlugins(new File(warFileOrWebappDir, "/WEB-INF/lib/"));
        }
    }

    public static void addPluginFilesToWarFileInplace(File warFileOrWebappDir, List<File> plugins) {
        addPluginsToWarFileInplace(warFileOrWebappDir, list(plugins).map(CmIoUtils::toDataSource));
    }

    public static void addPluginsToWarFileInplace(File warFileOrWebappDir, List<DataSource> plugins) {
        Set<String> fileNames = list(plugins).map(DataSource::getName).map(FilenameUtils::getName).toSet();
        list(plugins).stream().map(p -> scanFileForPlugin(p.getName(), toByteArray(p))).filter(notNull()).forEach(p -> {
            LOGGER.debug("processing plugin =< {} >", p.getNameVersion());
            p.getRequiredLibs().forEach(l -> {
                if (!fileNames.contains(mavenGavToFilename(l))) {
                    DataSource lib = getResourceByGavOrNull(l);
                    LOGGER.debug("loading maven lib =< {} >", l);
                    if (lib != null) {
                        LOGGER.debug("add maven lib =< {} >", lib.getName());
                        fileNames.add(lib.getName());
                        plugins.add(lib);
                    } else {
                        LOGGER.warn("missing required lib =< {} > for plugin =< {} > (unable to load from maven)", l, p.getNameVersion());
                    }

                }
            });
        });
        Path basePath = getBasePathPlugin(warFileOrWebappDir);
        plugins.forEach(unsafeConsumer(f -> {
            Path path = Path.of(basePath.toString(), FilenameUtils.getName(f.getName()));
            try (OutputStream outputStream = Files.newOutputStream(path); InputStream inputStream = f.getInputStream()) {
                outputStream.write(inputStream.readAllBytes());
            }
        }));
    }

    public static void removePluginsFromWarFileInplace(File warFileOrWebappDir, List<String> plugins) {
        Map<String, SystemPlugin> map = map(scanWarFileForPlugins(warFileOrWebappDir), SystemPlugin::getName);
        List<String> files = list(plugins).distinct().map(p -> checkNotNull(map.get(p), "plugin not found for name =< %s >", p)).flatMap(p -> list(p.getFilename()).with(p.getRequiredLibFiles()));
        removePluginFilesFromWarFileInplace(warFileOrWebappDir, files);
    }

    public static void removePluginFilesFromWarFileInplace(File warFileOrWebappDir, List<String> pluginFiles) {
        Path basePath = getBasePathPlugin(warFileOrWebappDir);
        pluginFiles.forEach(unsafeConsumer(f -> {
            Path path = Path.of(basePath.toString(), f);
            removePluginFile(path);
        }));
    }

    public static void removePluginFilesFromConfigFolder(File configPluginDir, List<String> pluginFiles) {
        Path basePath = Path.of(configPluginDir.getAbsolutePath());
        pluginFiles.forEach(unsafeConsumer(f -> {
            Path path = Path.of(basePath.toString(), f);
            removePluginFile(path);
        }));
    }

    public static List<SystemPlugin> scanFolderForPlugins(File folder) {
        LOGGER.debug("scan folder = {}", folder.getAbsolutePath());
        File index = new File(folder, "systemlibs.list");
        Set<String> systemLibs = index.exists() ? getUnsafe(() -> list(readLines(index, Charset.defaultCharset())).filter(StringUtils::isNotBlank).sorted().map(l -> {
            LOGGER.trace("load, from index, sys lib =< {} >", l);
            return l;
        }).toSet()) : emptySet();
        if (systemLibs.isEmpty()) {
            LOGGER.warn("system lib index not found for system plugin folder = {}: full folder scan required", folder.getAbsolutePath());
        } else {
            LOGGER.debug("skip {} libs from system lib index", systemLibs.size());
        }
        return scanFilesForPlugins(list(folder.listFiles((d, n) -> n.matches("(?i).+[.]jar") && !systemLibs.contains(n))));
    }

    public static List<SystemPlugin> scanConfigFolderForPlugins(File folder) {
        LOGGER.debug("scan config folder = {}", folder.getAbsolutePath());
        return scanFilesForPlugins(list(folder.listFiles((d, n) -> n.matches("(?i).+[.]jar"))));
    }

    public static List<SystemPlugin> scanFilesForPlugins(Collection<File> files) {
        LOGGER.debug("scan {} files for plugins", files.size());
        return list(files).map(SystemPluginUtils::scanFileForPlugin).filter(Objects::nonNull);
    }

    @Nullable
    public static SystemPlugin scanFileForPlugin(File file) {
        return scanFileForPlugin(file.getAbsolutePath(), toByteArray(file));
    }

    public static boolean hasConfigPluginDirectory(File webappContextFile) {
        String pathPlugins = applyXpath(readToString(webappContextFile), "/Context/Resources/PreResources/@base");
        return isNotBlank(pathPlugins);
    }

    public static File getPluginFolderFromContext(File webappContextFile) {
        return getPluginFolderFromContext(readToString(webappContextFile));
    }

    public static File getPluginFolderFromContext(String webappContext) {
        String pathPlugins = applyXpath(webappContext, "/Context/Resources/PreResources/@base");
        return new File(pathPlugins);
    }

    private static Path getBasePathPlugin(File warFileOrWebappDir) {
        Path basePath;
        if (warFileOrWebappDir.isFile()) {
            try (FileSystem fileSystem = FileSystems.newFileSystem(URI.create(format("jar:%s", warFileOrWebappDir.toPath().toUri())), emptyMap())) {
                basePath = fileSystem.getPath("/WEB-INF/lib/");
            } catch (IOException ex) {
                throw runtime(ex);
            }
        } else {
            basePath = Path.of(warFileOrWebappDir.getAbsolutePath(), "/WEB-INF/lib/");
        }
        return basePath;
    }

    private static void removePluginFile(Path path) throws IOException {
        if (path.toFile().exists()) {
            Files.delete(path);
            LOGGER.info("deleted plugin = {}", path);
        } else {
            LOGGER.info("plugin does not exist = {}", path);
        }
    }

    @Nullable
    private static SystemPlugin scanFileForPlugin(String path, byte[] data) {
        LOGGER.debug("scan file =< {} >", path);
        String filename = FilenameUtils.getName(path);
        try (ZipInputStream in = new ZipInputStream(new ByteArrayInputStream(data))) {
            ZipEntry entry;
            Map<String, String> mapPom = map();
            String pluginPath = null, pomXml = null, pomProperties = null;
            while ((entry = in.getNextEntry()) != null) {
                if (entry.getName().matches("META-INF/maven/.*/pom.xml")) {
                    String xml = readToString(in);
                    Document pom = toDocument(xml);
                    if (toBoolean(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "boolean(/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin'])"))) {
                        pluginPath = FilenameUtils.getPathNoEndSeparator(entry.getName());
                    }
                    mapPom.put(entry.getName(), xml);
                } else if (entry.getName().matches("META-INF/maven/.*/pom.properties")) {
                    String properties = readToString(in);
                    mapPom.put(entry.getName(), properties);
                }
            }
            if (pluginPath != null) {
                pomXml = mapPom.get(format("%s/pom.xml", pluginPath));
                pomProperties = mapPom.get(format("%s/pom.properties", pluginPath));
            }
            if (isNotBlank(pomProperties) && isNotBlank(pomXml)) {
                Document pom = toDocument(pomXml);
                Map<String, String> properties = loadProperties(pomProperties);
                String name = checkNotBlank(properties.get("artifactId")),
                        version = checkNotBlank(properties.get("version")),
                        requiredCoreVersion = applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin.requiredCoreVersion']"),
                        description = nullToEmpty(firstNotBlankOrNull(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:description"), applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:name"))),
                        expectedFilename = mavenNameVersionToFilename(name, version),
                        checksum = hash(data);
                checkArgument(Objects.equals(filename, expectedFilename), "invalid lib filename =< %s > expected filename =< %s >", filename, expectedFilename);
                LOGGER.info("found plugin =< {} >", expectedFilename);
                List<String> libs = Splitter.onPattern("[\\s,;]+").omitEmptyStrings().trimResults().splitToList(nullToEmpty(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin.libs']")));
                LOGGER.debug("plugin libs config = {}", libs);
                libs = list(libs).map(artifactId -> format("%s:%s:%s",
                        checkNotBlank(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), format("/pom:project/pom:dependencies/pom:dependency[pom:artifactId='%s']/pom:groupId", artifactId))),
                        artifactId,
                        checkNotBlank(applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), format("/pom:project/pom:dependencies/pom:dependency[pom:artifactId='%s']/pom:version", artifactId)))));
                LOGGER.info("plugin libs = {}", libs);
                String tagPlugin = applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin.tag']");
                String servicePlugin = applyXpath(pom, map("pom", "http://maven.apache.org/POM/4.0.0"), "/pom:project/pom:properties/*[local-name()='org.cmdbuild.plugin.service']");
                LOGGER.info("plugin '{}'", servicePlugin);
                return SystemPluginImpl.builder()
                        .withName(name)
                        .withDescription(description)
                        .withTag(tagPlugin)
                        .withService(servicePlugin)
                        .withVersion(version)
                        .withRequiredCoreVersion(requiredCoreVersion)
                        .withRequiredLibs(libs)
                        .withChecksum(checksum)
                        .build();
            }
            return null;
        } catch (Exception ex) {
            throw runtime(ex, "error processing plugin file =< %s >", path);
        }
    }
}
