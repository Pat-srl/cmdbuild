/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents.utils;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;
import com.google.javascript.jscomp.CompilerOptions;
import static com.google.javascript.jscomp.CompilerOptions.LanguageMode.ECMASCRIPT_2017;
import com.google.javascript.jscomp.JSError;
import com.google.javascript.jscomp.Result;
import com.google.javascript.jscomp.SourceFile;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.String.format;
import java.lang.invoke.MethodHandles;
import java.util.Collection;
import java.util.Collections;
import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.compress.utils.SeekableInMemoryByteChannel;
import org.apache.commons.io.FilenameUtils;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.commons.lang3.tuple.Pair;
import org.cmdbuild.ui.TargetDevice;
import static org.cmdbuild.ui.TargetDevice.TD_DEFAULT;
import static org.cmdbuild.ui.TargetDevice.TD_MOBILE;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.UiComponentInfoImpl.UiComponentInfoImplBuilder;
import org.cmdbuild.uicomponents.UiComponentVersionInfoImpl;
import org.cmdbuild.uicomponents.custompage.CustomPageException;
import org.cmdbuild.uicomponents.data.UiComponentData;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UiComponentUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static String getAdminCustomPageCode(String pluginName) {
        return format("pluginadmincustompage_%s", pluginName);
    }

    public static Map<TargetDevice, byte[]> normalizeComponentData(Map<TargetDevice, byte[]> data) {
        return normalizeComponentData(data.values());
    }

    public static Map<TargetDevice, byte[]> normalizeComponentData(Collection<byte[]> data) {
        return new ZipFileProcessor(data).getData();
    }

    public static UiComponentInfo toComponentInfo(UiComponentData data) {
        return UiComponentInfoImpl.builder()
                .accept(parseExtComponentData(data.getData()))
                .withId(data.getId())
                .withActive(data.getActive())
                .withDescription(data.getDescription())
                .withLastUpdated(data.getLastUpdated())
                .withType(data.getType())
                .withVersions(list(data.getTargetDevices()).map(UiComponentVersionInfoImpl::new))
                .withIconFont(data.getIconFont())
                .build();
    }

    public static String getCodeFromExtComponentData(Collection<byte[]> data) {
        return new ZipFileProcessor(data).getCode();
    }

    public static byte[] getComponentFile(Object component, byte[] zipData, String path, boolean compressJs) {
        try (ZipFile zipFile = ZipFile.builder().setSeekableByteChannel(new SeekableInMemoryByteChannel(zipData)).get()) {
            ZipArchiveEntry entry = checkNotNull(zipFile.getEntry(path), "entry not found for path =< %s >", path);
            byte[] data = toByteArray(zipFile.getInputStream(entry));
            if (path.endsWith(".js") && compressJs) {
                try {
                    data = uglifyJs(data);
                } catch (Exception ex) {
                    LOGGER.warn("error minifying file =< {} > from component = {}", path, component, ex);
                }
            }
            return data;
        } catch (Exception ex) {
            throw new CustomPageException(ex, "error processing component data for component = %s path = %s", component, path);
        }
    }

    public static byte[] uglifyJs(byte[] data) {
        CompilerOptions options = new CompilerOptions();
        options.setLanguageOut(ECMASCRIPT_2017);
        com.google.javascript.jscomp.Compiler compiler = new com.google.javascript.jscomp.Compiler();
        SourceFile sourceFile = SourceFile.fromCode("file.js", new String(data));
        Result result = compiler.compile(emptyList(), singletonList(sourceFile), options);
        checkArgument(result.success, "error compressing javascript: %s", result.errors.stream().map(JSError::toString).collect(joining(", ")));
        return compiler.toSource().getBytes();
    }

    public static Consumer<UiComponentInfoImplBuilder> parseExtComponentData(Map<TargetDevice, byte[]> data) {
        return b -> parseExtComponentData(data, b);
    }

    public static void checkUglifyJs(UiComponentData uiComponent, boolean compressJs) {
        if (compressJs) {
            new ExtractZippedJsProcessor(uiComponent.getData()).getData().forEach((fileName, uiFile) -> {
                if (fileName.endsWith(".js")) {
                    try {
                        LOGGER.debug("checking js {}", fileName);
                        uglifyJs(uiFile);
                    } catch (Exception ex) {
                        throw new CustomPageException(ex, "error minifying file =< %s > from component = %s", uiFile, uiComponent.getName());
                    }
                }
            });
        }
    }

    private static void parseExtComponentData(Map<TargetDevice, byte[]> data, UiComponentInfoImplBuilder builder) {
        new ZipFileProcessor(data.values()).toComponentInfo(builder);
    }

    private static class ZipFileProcessor {

        private final List<byte[]> inputFiles;
        private final Map<TargetDevice, byte[]> data = map();
        private String cpName, alias, mainExtClass;

        public ZipFileProcessor(Collection<byte[]> inputFiles) {
            this.inputFiles = checkNotEmpty(ImmutableList.copyOf(inputFiles));
            this.inputFiles.forEach(f -> new ZipFileHelper().processFile(f));
        }

        private class ZipFileHelper {

            private TargetDevice targetDevice;
            private String typeExpr, thisCpName, thisAlias, thisMainExtClass, dirPrefix;
            private boolean foundMainFile = false;

            private void processFile(byte[] inputFile) {
                try (ZipFile zipFile = ZipFile.builder().setSeekableByteChannel(new SeekableInMemoryByteChannel(inputFile)).get()) { //TODO zip file processing
                    List<Pair<String, byte[]>> files = list();
                    Collections.list(zipFile.getEntries()).stream().filter(not(ZipArchiveEntry::isDirectory)).forEach((entry) -> {
                        try {
                            byte[] file = toByteArray(zipFile.getInputStream(entry));
                            String normalizedFile = normalizeFile(file);
                            if (normalizedFile.matches(".*mixins:\\[[^\\]]*['\"]((CMDBuildUI|CMDBuildMobile)\\.(mixins.CustomPage|mixins.AdminCustomPage|mixins.ContextMenuComponent|view.widgets.Mixin))['\"].*")) {
                                checkArgument(!foundMainFile, "duplicate main file found in component data");
                                foundMainFile = true;

                                Matcher matcher = Pattern.compile("Ext.define[(]['\"](CMDBuildUI|CMDBuildMobile)\\.(view\\.(custompages|administration.custompages|contextmenucomponents|widgets.custom)\\.([^.]+)\\.[^.'\"]+)['\"]").matcher(normalizedFile);
                                checkArgument(matcher.find(), "unable to find component id tag");
                                typeExpr = checkNotBlank(matcher.group(1));
                                thisMainExtClass = checkNotBlank(matcher.group(2));
                                thisCpName = checkNotBlank(matcher.group(4));

                                matcher = Pattern.compile("alias:['\"]([^'\"]+)['\"]").matcher(normalizedFile);
                                checkArgument(matcher.find(), "unable to find alias tag");
                                thisAlias = checkNotBlank(matcher.group(1));

                                dirPrefix = FilenameUtils.getPath(entry.getName());

                                targetDevice = checkNotNull((TargetDevice) map("CMDBuildUI", TD_DEFAULT, "CMDBuildMobile", TD_MOBILE).get(typeExpr), "invalid class =< %s >", typeExpr);
                            }
                            files.add(Pair.of(entry.getName(), file));
                        } catch (Exception ex) {
                            throw new CustomPageException(ex, "error processing component file = %s", entry.getName());
                        }
                    });
                    checkArgument(foundMainFile, "main file not found in component data");

                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    try (ZipOutputStream zip = new ZipOutputStream(byteArrayOutputStream)) {
                        files.stream().sorted(Ordering.natural().onResultOf(Pair::getLeft)).forEach((p) -> {
                            try {
                                String name = p.getLeft();
                                byte[] file = p.getRight();
                                if (isNotBlank(dirPrefix)) {
                                    checkArgument(name.startsWith(dirPrefix));
                                    name = name.replaceFirst(Pattern.quote(dirPrefix), "");
                                }
                                ZipEntry entry = new ZipEntry(name);
                                zip.putNextEntry(entry);
                                zip.write(file);
                                zip.closeEntry();
                            } catch (IOException ex) {
                                throw runtime(ex);
                            }
                        });
                    }

                    data.put(targetDevice, byteArrayOutputStream.toByteArray());

                    checkArgument(isBlank(cpName) || equal(thisCpName, cpName), "invalid main ext class, expected =< %s > but found =< %s >", cpName, thisCpName);
                    checkArgument(isBlank(alias) || equal(thisAlias, alias), "invalid main ext class, expected =< %s > but found =< %s >", alias, thisAlias);
                    checkArgument(isBlank(mainExtClass) || equal(thisMainExtClass, mainExtClass), "invalid main ext class, expected =< %s > but found =< %s >", mainExtClass, thisMainExtClass);

                    cpName = thisCpName;
                    alias = thisAlias;
                    mainExtClass = thisMainExtClass;
                } catch (IOException ex) {
                    throw new CustomPageException(ex, "error processing component data");
                }
            }
        }

        public Map<TargetDevice, byte[]> getData() {
            return data;
        }

        public void toComponentInfo(UiComponentInfoImplBuilder builder) {
            builder.withName(cpName).withDescription(cpName).withExtjsAlias(alias).withExtjsComponentId(mainExtClass);
        }

        public String getCode() {
            return cpName;
        }

        private String normalizeFile(byte[] file) {
            return new String(file).replaceAll("[\n\r\t ]", "");
        }

    }

    private static class ExtractZippedJsProcessor {

        private final Map<TargetDevice, byte[]> inputFiles;
        private final Map<String, byte[]> data = map();

        public ExtractZippedJsProcessor(Map<TargetDevice, byte[]> inputFiles) {
            this.inputFiles = checkNotEmpty(inputFiles);
            this.inputFiles.forEach((fileName, byteArray) -> new ExtractZippedJsHelper().processFile(byteArray));
        }

        private class ExtractZippedJsHelper {

            private void processFile(byte[] inputFile) {
                try (ZipFile zipFile = ZipFile.builder().setSeekableByteChannel(new SeekableInMemoryByteChannel(inputFile)).get()) {
                    Collections.list(zipFile.getEntries()).stream().filter(not(ZipArchiveEntry::isDirectory)).forEach((entry) -> {
                        try {
                            byte[] file = toByteArray(zipFile.getInputStream(entry));
                            data.put(entry.getName(), file);
                        } catch (Exception ex) {
                            throw new CustomPageException(ex, "error processing component file = %s", entry.getName());
                        }
                    });
                } catch (IOException ex) {
                    throw new CustomPageException(ex, "error processing component data");
                }
            }
        }

        public Map<String, byte[]> getData() {
            return data;
        }

    }
}
