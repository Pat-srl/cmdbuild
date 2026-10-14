/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.classpath;

import java.io.File;
import java.io.InputStream;
import static java.lang.String.format;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.UUID;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.io.CmIoUtils.copy;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;

public class MoreClasspathUtils {

    public static ClassLoader buildClassloaderWithJarOverride(InputStream... sources) {
        return buildClassloaderWithJarOverride(null, sources);
    }

    public static ClassLoader buildClassloaderWithJarOverride(@Nullable ClassLoader parent, InputStream... sources) {
        File dir = tempDir();
        List<File> jars = stream(sources).map(s -> {
            File file = new File(dir, format("%s.jar", UUID.randomUUID().toString().toLowerCase().replaceAll("[^a-z0-9]", "")));
            copy(s, file);
            return file;
        }).collect(toList());
        return ClasspathUtils.buildClassloaderWithJarOverride(jars, parent);
    }
}
