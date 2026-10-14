/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.concat;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.reflect.Reflection.newProxy;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import static java.util.Collections.emptyList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import java.util.stream.IntStream;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmExceptionUtils.inner;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.LambdaExceptionUtils.rethrowFunction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmReflectionUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static Iterable<Field> getAllFields(Class type) {
        if (type.equals(Object.class)) {
            return emptyList();
        } else {
            return concat(getAllFields(type.getSuperclass()), asList(type.getDeclaredFields()));
        }
    }

    public static <T> T wrapProxy(Class<T> iface, T inner, ProxyWrapper proxyWrapper) {
        return newProxy(iface, (Object proxy, Method method, Object[] args) -> {
            proxyWrapper.beforeMethodInvocation(method, args);
            try {
                Object response = method.invoke(inner, args);
                response = proxyWrapper.afterSuccessfullMethodInvocation(method, args, response);
                return response;
            } catch (Throwable error) {
                proxyWrapper.afterFailedMethodInvocation(method, args, error);
                throw error;
            } finally {
                proxyWrapper.afterMethodInvocation(method, args);
            }
        });
    }

    public static <T> T executeMethod(Method method) {
        checkNotNull(method, "method is null");
        try {
            return (T) method.invoke(null);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw runtime(inner(ex));
        }
    }

    public static <T> T executeMethod(Object instance, String methodName, Object... args) {
        return executeMethod(instance, methodName, asList(args));
    }

    public static <T> T executeMethod(Object instance, String methodName, List<Object> args) {
        Method method;
        try {
            if (args.isEmpty()) {
                method = instance.getClass().getMethod(methodName);
            } else {
                List<Method> methods = stream(instance.getClass().getMethods())
                        .map(rethrowFunction(m -> instance.getClass().getMethod(m.getName(), m.getParameterTypes()))).distinct()
                        .filter(m -> equal(m.getName(), methodName) && m.getParameterCount() == args.size()).collect(toList());
                if (methods.size() <= 1) {
                    method = getOnlyElement(methods);
                } else {
                    method = methods.stream().filter(m -> IntStream.range(0, args.size()).allMatch(i -> args.get(i) == null || m.getParameterTypes()[i].isInstance(args.get(i)))).collect(onlyElement("method not found"));
                }
            }
        } catch (NoSuchMethodException | SecurityException ex) {
            throw runtime(inner(ex), "error retrieving method for instance = %s with name = %s args = %s", instance, methodName, args);
        } catch (Exception ex) {
            throw runtime(ex, "error retrieving method for instance = %s with name = %s args = %s", instance, methodName, args);
        }
        return executeMethod(instance, method, args);
    }

    public static <T> T executeMethod(Object instance, Method method, Object... args) {
        return executeMethod(instance, method, asList(args));
    }

    public static <T> T executeMethod(Object instance, Method method, List<Object> args) {
        checkNotNull(method, "method is null");
        checkNotNull(instance, "instance is null");
        try {
            if (!method.canAccess(instance)) {
                method.setAccessible(true);
            }
            args = list(args).mapWithIndex((i, v) -> (v == null || method.getParameterTypes()[i].isInstance(v)) ? v : convert(v, method.getParameterTypes()[i]));
            return (T) method.invoke(instance, args.toArray());
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
            throw runtime(inner(ex), "error executing method = %s with args = %s on instance = %s", method, args, instance);
        }
    }

    public static <T> T newInstance(Class<T> classe) {
        try {
            return (T) classe.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException ex) {
            throw runtime(inner(ex), "error constructing instance of class = %s", classe);
        }
    }

    public static boolean existsOnClasspath(String className) {
        checkNotBlank(className, "className is blank");
        try {
            Class.forName(className);
            return true;
        } catch (Exception ex) {
            LOGGER.warn("class not found for class name = {} : {}", className, ex.toString());
            return false;
        }
    }
}
