/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.classpath;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.concurrent.Callable;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.LambdaExceptionUtils.Runnable_WithExceptions;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClasspathUtils {

    public static ClassLoader buildClassloaderWithoutClass(ClassLoader classLoader, String classNameToRemove) {
        checkNotBlank(classNameToRemove);
        String classResourceToRemove = classNameToRemove.replaceAll("[.]", "/") + ".class";
        if (containsClass(classLoader, classNameToRemove)) {
            classLoader = new ClassLoader(classLoader) {
                @Override
                public InputStream getResourceAsStream(String name) {
                    if (classResourceToRemove.equals(name)) {
                        return null;
                    } else {
                        return super.getResourceAsStream(name);
                    }
                }

                @Override
                public Enumeration<URL> getResources(String name) throws IOException {
                    if (classResourceToRemove.equals(name)) {
                        return Collections.enumeration(Collections.emptyList());
                    } else {
                        return super.getResources(name);
                    }
                }

                @Override
                public URL getResource(String name) {
                    if (classResourceToRemove.equals(name)) {
                        return null;
                    } else {
                        return super.getResource(name);
                    }
                }

                @Override
                public Class<?> loadClass(String name) throws ClassNotFoundException {
                    if (classNameToRemove.equals(name)) {
                        throw new ClassNotFoundException("this class has been stripped from this class loader");
                    } else {
                        return super.loadClass(name);
                    }
                }

            };
        }
        return classLoader;
    }

    public static boolean containsClass(ClassLoader classLoader, String className) {
        try {
            checkNotNull(classLoader.loadClass(className));
            return true;
        } catch (ClassNotFoundException ex) {
            return false;
        }
    }

    public static ClassLoader buildClassloaderWithJarOverride(Collection<File> jars) {
        return buildClassloaderWithJarOverride(jars, null);
    }

    public static ClassLoader buildClassloaderWithJarOverride(Collection<File> jars, @Nullable ClassLoader parent) {
        return new ClasspathHandler(jars, firstNotNull(parent, Thread.currentThread().getContextClassLoader())).getClassLoader();
    }

    public static void doWithCustomClassLoader(ClassLoader classLoader, Runnable_WithExceptions job) {
        doWithCustomClassLoader(classLoader, () -> {
            job.run();
            return null;
        });
    }

    public static <T> T doWithCustomClassLoader(ClassLoader classLoader, Callable<T> job) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        Thread.currentThread().setContextClassLoader(classLoader);
        try {
            return job.call();
        } catch (Exception ex) {
            throw runtime(ex);
        } finally {
            Thread.currentThread().setContextClassLoader(contextClassLoader);
        }
    }
//    public static ClassLoader mergeClassLoaders(Collection<ClassLoader> classLoaders) {
//        checkArgument(!classLoaders.isEmpty());
//        if (classLoaders.size() == 1) {
//            return getOnlyElement(classLoaders);
//        } else {
//            Iterator<ClassLoader> iterator = list(classLoaders).reverse().iterator();
//            ClassLoader classLoader = iterator.next();
//            while (iterator.hasNext()) {
//                classLoader = new NestedClassLoader(iterator.next(), classLoader);
//            }
//            return classLoader;
//        }
//    }

    private static class ClasspathHandler {

        private final Logger logger = LoggerFactory.getLogger(getClass());

        private final ClassLoader parentClassLoader, customClassLoader;

        public ClasspathHandler(Collection<File> extraJars, ClassLoader parentClassLoader) {
            try {
                this.parentClassLoader = checkNotNull(parentClassLoader);
                extraJars.forEach(f -> checkArgument(f.exists() && f.isFile(), "invalid jar file name = %s", f));
                URL[] urls = extraJars.stream().map(rethrowFunction(f -> f.toURI().toURL())).collect(toList()).toArray(new URL[]{});
//                extraJarsLoader = new URLClassLoader(, null);
                logger.debug("loading custom classes from jars = {}", extraJars);
                customClassLoader = new CustomClassLoader(urls, parentClassLoader);
            } catch (MalformedURLException ex) {
                throw runtime(ex);
            }
        }

        public ClassLoader getClassLoader() {
            return customClassLoader;
        }

        private class CustomClassLoader extends URLClassLoader {

            public CustomClassLoader(URL[] urls, ClassLoader parent) {
                super(urls, parent);
            }

            @Override
            public Class<?> loadClass(String name) throws ClassNotFoundException {
                logger.debug("load class = {}", name);

                synchronized (getClassLoadingLock(name)) {
                    Class classe = findLoadedClass(name);
                    if (classe == null) {
                        try {
                            logger.trace("try to load class = {} from custom jars", name);
                            classe = findClass(name);
                            logger.trace("loaded class = {} from custom jars", classe);
                            return classe;
                        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
                            logger.trace("class not found in custom jars for name = {}", name);
                        }
                        logger.trace("try to load class = {} from parent classloader", name);
                        classe = parentClassLoader.loadClass(name);
                        logger.trace("loaded class = {} from parent classloader", classe);
                        return classe;
                    } else {
                        return classe;
                    }
                }
            }
        }
    }
//
//    private static class NestedClassLoader extends URLClassLoader {
//
//        private final Logger logger = LoggerFactory.getLogger(getClass());
//
//        private final ClassLoader parent, delegate;
//
//        public NestedClassLoader(ClassLoader delegate, ClassLoader parent) {
//            super(new URL[]{}, parent);
//            this.delegate = checkNotNull(delegate);
//            this.parent = checkNotNull(parent);
//        }
//
//        @Override
//        public Class<?> loadClass(String name) throws ClassNotFoundException {
//            logger.debug("load class = {}", name);
//
//            synchronized (getClassLoadingLock(name)) {
//                Class classe = findLoadedClass(name);
//                if (classe == null) {
//                    try {
//                        logger.trace("try to load class = {} from delegate classloader", name);
//                        classe = delegate.loadClass(name);
//                        logger.trace("loaded class = {} from delegate classloader", classe);
//                        return classe;
//                    } catch (ClassNotFoundException | NoClassDefFoundError ex) {
//                        logger.trace("class not found in delegate classloader for name = {}", name);
//                    }
//                    logger.trace("try to load class = {} from parent classloader", name);
//                    classe = parent.loadClass(name);
//                    logger.trace("loaded class = {} from parent classloader", classe);
//                    return classe;
//                } else {
//                    return classe;
//                }
//            }
//        }
//    }
}
