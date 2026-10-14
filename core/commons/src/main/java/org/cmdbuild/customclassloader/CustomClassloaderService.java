/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.customclassloader;

import java.util.concurrent.Callable;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isBlank;

public interface CustomClassloaderService {

    ClassLoader getCustomClassLoader(String path);

    <T> T doWithCustomClassLoader(String path, Callable<T> job);

    @Nullable
    default ClassLoader getCustomClassLoaderOrNull(@Nullable String path) {
        return isBlank(path) ? null : getCustomClassLoader(path);
    }
}
