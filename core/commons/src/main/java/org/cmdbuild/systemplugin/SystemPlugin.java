/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.systemplugin;

import com.google.common.base.Joiner;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;
import java.io.IOException;
import static java.lang.String.format;
import java.nio.file.Path;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.fault.FaultEvent;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import org.cmdbuild.utils.maven.MavenUtils;
import static org.cmdbuild.utils.maven.MavenUtils.mavenNameVersionToFilename;

public interface SystemPlugin {

    String getName();

    String getDescription();

    String getTag();

    String getService();

    String getVersion();

    String getRequiredCoreVersion();

    String getChecksum();

    List<String> getRequiredLibs();

    List<FaultEvent> getHealthCheck();

    default List<String> getRequiredLibFiles() {
        return list(getRequiredLibs()).map(MavenUtils::mavenGavToFilename);
    }

    /**
     *
     * @return getName()-getVersion(), for example plugin-example-1.0.0
     */
    default String getNameVersion() {
        return format("%s-%s", getName(), getVersion());
    }

    default String getFilename() {
        return mavenNameVersionToFilename(getName(), getVersion());
    }

    default boolean isOk() {
        return getHealthCheck().isEmpty();
    }

    default String getHealthCheckMessage() {
        return Joiner.on("; ").join(getHealthCheck().stream().map((event) -> event.getLevel().name() + ": " + event.getMessage()).collect(toList()));//TODO duplicate code !!
    }

    default Map<String, byte[]> getResources(String pathFiles, String... extension) {
        Map<String, byte[]> patches = map();
        try (ScanResult scanResult = new ClassGraph().acceptPackages(format("org.cmdbuild.custom.plugin.%s.%s", firstNotNull(getService(), getName()), pathFiles)).scan()) {
            asList(extension).forEach(e -> scanResult.getResourcesWithExtension(e).forEach(r -> {
                try {
                    byte[] data = r.readCloseable().getByteBuffer().array();
                    patches.put(Path.of(r.getPath()).getFileName().toString(), data);
                } catch (IOException ex) {
                    throw runtime(ex);
                }
            }));
        }
        return patches;
    }
}
