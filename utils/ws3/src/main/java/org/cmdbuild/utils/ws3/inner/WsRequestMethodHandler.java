/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.emptyToNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.ImmutableList.toImmutableList;
import com.google.common.collect.Iterables;
import static com.google.common.collect.Iterables.getOnlyElement;
import static com.google.common.collect.Iterables.size;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.MultivaluedHashMap;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import static java.util.Arrays.asList;
import static java.util.Arrays.stream;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.apache.cxf.jaxrs.ext.multipart.Attachment;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.getFirstTypeArgOfParametrizedType;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.ws3.api.WsWarningSource;
import static org.cmdbuild.utils.ws3.inner.WsPart.DEFAULT_PART;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import org.cmdbuild.utils.ws3.utils.Ws3FrameworkException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static org.springframework.core.annotation.AnnotationUtils.findAnnotation;

public class WsRequestMethodHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Object service;
    private final Method method;
    private final List<Function<WsRequest, Object>> methodArgMappers;
    private final String contentType;
    private final WsWarningSource warningSource;

    public WsRequestMethodHandler(Object service, Method method, WsWarningSource warningSource) {
        this.warningSource = checkNotNull(warningSource);
        this.service = checkNotNull(service);
        this.method = checkNotNull(method);
        methodArgMappers = buildParamMappers();
        contentType = getResponseContentType();
    }

    public WsResponseHandler handleRequest(WsRequest request) throws Exception {
        try {
            logger.debug("execute req handler method = {}.{}", service.getClass().getName(), method.getName());
            Object response = method.invoke(service, buildMethodArgs(request));
            List<Object> messages = warningSource.getWarningJsonMessages();
            return new WsResponseHandlerImpl(request, response, contentType, messages);
        } catch (InvocationTargetException ex) {
            throw new Ws3Exception(getCause(ex), "error processing ws request = %s", request);
        }
    }

    @Override
    public String toString() {
        return "Ws3RequestMethodHanlder{" + "service=" + service.getClass().getName() + ", method=" + method.getName() + '}';
    }

    private Exception getCause(InvocationTargetException ex) {
        Throwable inner = ex.getCause();
        if (inner instanceof Exception exception) {
            return exception;
        } else {
            return runtime(inner);
        }
    }

    private Object[] buildMethodArgs(WsRequest request) {
        try {
            return methodArgMappers.stream().map((f) -> f.apply(request)).collect(Collectors.toList()).toArray();
        } catch (Exception ex) {
            throw new Ws3FrameworkException(ex, "error building method args for handler = %s", this);
        }
    }

    private List<Function<WsRequest, Object>> buildParamMappers() {
        List<Function<WsRequest, Object>> params = CmCollectionUtils.list(method.getParameters()).stream().map(this::prepareHandlerForMethodParam).collect(ImmutableList.toImmutableList());

        return params;
    }

    private Function<WsRequest, Object> prepareHandlerForMethodParam(Parameter param) {
        try {
            if (hasQueryParamConstructor(param.getType())) {
                return new QueryParamBeanParamProcessor(param);
            } else {
                String defaultValue = findAnnotation(param, DefaultValue.class) == null ? null : findAnnotation(param, DefaultValue.class).value();
                boolean isContext = findAnnotation(param, Context.class) != null,
                        isIterable = Iterable.class.isAssignableFrom(param.getType()),
                        isDataSource = DataSource.class.isAssignableFrom(param.getType()) || DataHandler.class.isAssignableFrom(param.getType())
                        || (isIterable && (DataSource.class.isAssignableFrom(getFirstTypeArgOfParametrizedType(param.getParameterizedType())) || DataHandler.class.isAssignableFrom(getFirstTypeArgOfParametrizedType(param.getParameterizedType()))));
                String name = null, header = null;
                boolean isRequired = false;
                if (findAnnotation(param, QueryParam.class) != null) {
                    name = checkNotBlank(findAnnotation(param, QueryParam.class).value());
                } else if (findAnnotation(param, PathParam.class) != null) {
                    name = checkNotBlank(findAnnotation(param, PathParam.class).value());
                } else if (findAnnotation(param, HeaderParam.class) != null) {
                    header = checkNotBlank(findAnnotation(param, HeaderParam.class).value());
                } else if (findAnnotation(param, FormParam.class) != null) {
                    name = checkNotBlank(findAnnotation(param, FormParam.class).value());
                } else if (findAnnotation(param, org.apache.cxf.jaxrs.ext.multipart.Multipart.class) != null) {
                    name = findAnnotation(param, org.apache.cxf.jaxrs.ext.multipart.Multipart.class).value();
                    isRequired = findAnnotation(param, org.apache.cxf.jaxrs.ext.multipart.Multipart.class).required();
                }

                if (header != null) {
                    return new HeaderParamProcessor(param, header);
                } else if (isContext) {
                    return new ContextParamProcessor(param);
                } else if (name != null) {
                    if (isDataSource) {
                        return new DataSourceParamProcessor(param, name, isRequired);
                    } else {
                        return new NamedParamProcessor(param, name, isRequired, defaultValue);
                    }
                } else {
                    if (isIterable && isDataSource) {
                        return new DataSourceParamProcessor(param, name, isRequired);
                    } else if (isIterable && Attachment.class.isAssignableFrom(getFirstTypeArgOfParametrizedType(param.getParameterizedType()))) {
                        return new AttachmentParamHandler(param);
                    } else {
                        isRequired = findAnnotation(param, Nullable.class) == null;
                        return new PayloadParamHandler(param, isRequired);
                    }
                }
            }
        } catch (Exception ex) {
            throw new Ws3Exception(ex, "error preparing handler for method param = %s", param);
        }
    }

    private static boolean hasQueryParamConstructor(Class type) {
        return type.getConstructors().length == 1 && stream(type.getConstructors()[0].getParameters()).anyMatch(p -> findAnnotation(p, QueryParam.class) != null);
    }

    @Nullable
    private Object convertDataSource(@Nullable DataSource dataSource, Class type) {
        if (dataSource == null || type.isInstance(dataSource)) {
            return dataSource;
        } else if (type.isAssignableFrom(DataHandler.class)) {
            return new DataHandler(dataSource);
        } else {
            throw new Ws3Exception("cannot convert datasource to type = %s", type);
        }
    }

    @Nullable
    private String getResponseContentType() {
        String contentType;
        if (findAnnotation(method, Produces.class) != null) {
            contentType = checkNotBlank(Iterables.getOnlyElement(CmCollectionUtils.list(findAnnotation(method, Produces.class).value())));
        } else if (findAnnotation(service.getClass(), Produces.class) != null) {
            contentType = checkNotBlank(Iterables.getOnlyElement(CmCollectionUtils.list(findAnnotation(service.getClass(), Produces.class).value())));
        } else {
            contentType = MediaType.APPLICATION_JSON;
        }
        return contentType;
    }

    private abstract class ParamHandler implements Function<WsRequest, Object> {

        protected final Parameter param;
        protected final Type type;
        protected final Class classe;
        protected final String name;

        public ParamHandler(Parameter param, @Nullable String name) {
            this.param = checkNotNull(param);
            this.type = param.getParameterizedType();
            this.classe = param.getType();
            this.name = emptyToNull(name);
        }

    }

    private class ContextParamProcessor extends ParamHandler {

        public ContextParamProcessor(Parameter param) {
            super(param, null);
        }

        @Override
        public Object apply(WsRequest r) {
            logger.trace("processing request context with type = {}", param.getType());
            Object inner = r.getInner();
            checkArgument(inner != null && param.getType().isInstance(inner), "error setting context param with type = %s from context = %s", param.getType(), inner);
            return inner;
        }

    }

    private class HeaderParamProcessor extends ParamHandler {

        public HeaderParamProcessor(Parameter param, String name) {
            super(param, checkNotBlank(name));
        }

        @Override
        public Object apply(WsRequest r) {
            logger.trace("processing header param = {} with type = {}", name, type);
            Object value = r.getHeader(name);
            logger.trace("raw value =< {} >", value);
            value = CmConvertUtils.convert(value, type);
            logger.trace("converted value =< {} >", value);
            return value;
        }
    }

    private class NamedParamProcessor extends ParamHandler {

        private final boolean isRequired;
        private final String defaultValue;

        public NamedParamProcessor(Parameter param, String name, boolean isRequired, @Nullable String defaultValue) {
            super(param, checkNotBlank(name));
            this.isRequired = isRequired;
            this.defaultValue = defaultValue;
        }

        @Override
        public Object apply(WsRequest r) {
            Object value = null;
            logger.trace("processing request param = {} with type = {}", name, type);
            for (String n : Splitter.on("|").splitToList(name)) {
                if (Iterable.class.isAssignableFrom(classe) && r instanceof WsRestRequest) {
                    value = ((WsRestRequest) r).getParams(n);
                } else {
                    value = r.getParam(n);
                }
                if (value == null) {
                    DataSource dataSource = r.getPartData(n);
                    if (dataSource != null) {
                        value = readToString(dataSource);
                    }
                }
                if (value != null) {
                    break;
                }
            }
            if (value != null && value instanceof Iterable && size((Iterable) value) == 1 && Number.class.isAssignableFrom(getFirstTypeArgOfParametrizedType(type)) && r.getParam(name).contains(",")) {
                value = r.getParam(name);
            }
            logger.trace("raw value =< {} >", value);
            if (defaultValue != null && value == null) {
                logger.trace("raw value is null, load default =< {} >", defaultValue);
                value = defaultValue;
            }
            value = CmConvertUtils.convert(value, type);
            logger.trace("converted value =< {} >", value);
            if (isRequired) {
                checkNotNull(value, "missing required value for param = %s", name);
            }
            return value;
        }
    }

    private class QueryParamBeanParamProcessor extends ParamHandler {

        private final Constructor constructor;
        private final List<Function<WsRequest, Object>> constructorParamsSuppliers;

        public QueryParamBeanParamProcessor(Parameter param) {
            super(param, null);
            constructor = getOnlyElement(asList(param.getType().getConstructors()));
            constructorParamsSuppliers = asList(constructor.getParameters()).stream().map(WsRequestMethodHandler.this::prepareHandlerForMethodParam).collect(toImmutableList());
        }

        @Override
        public Object apply(WsRequest t) {
            Object[] constructorParams = constructorParamsSuppliers.stream().map(f -> f.apply(t)).collect(toList()).toArray();
            try {
                return constructor.newInstance(constructorParams);
            } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                throw new Ws3Exception(ex, "error building query param bean with constructor = %s", constructor);
            }
        }

    }

    private class DataSourceParamProcessor extends ParamHandler {

        private final boolean isRequired, isIterable;

        public DataSourceParamProcessor(Parameter param, @Nullable String name, boolean isRequired) {
            super(param, name);
            this.isRequired = isRequired;
            isIterable = Iterable.class.isAssignableFrom(param.getType());
            checkArgument(isIterable || isNotBlank(name));
        }

        @Override
        public Object apply(WsRequest r) {
            if (isIterable) {
                return convert(r.getParts().values().stream()
                        .filter(e -> partMatchesName(e.getPartName()))
                        .map(WsPart::getDataSource).map(d -> convertDataSource(d, getFirstTypeArgOfParametrizedType(type))).collect(toImmutableList()), type);
            } else {
                logger.trace("processing request part = {} with type = {}", name, classe);
                DataSource dataSource = r.getPartData(name);
                logger.trace("raw value =< {} >", dataSource);
                if (isRequired) {
                    checkNotNull(dataSource, "missing required rest part = %s", name);
                }
                return convertDataSource(dataSource, classe);
            }
        }

        private boolean partMatchesName(String partName) {
            if (isBlank(name)) {
                return true;
            } else if (name.startsWith("-")) {
                return !equal(partName, name.replaceFirst("^-", "")) && !equal(DEFAULT_PART, partName);
            } else {
                return equal(name, partName) || equal(DEFAULT_PART, partName);
            }
        }

    }

    private class AttachmentParamHandler extends ParamHandler {

        public AttachmentParamHandler(Parameter param) {
            super(param, null);
        }

        @Override
        public Object apply(WsRequest r) {
            logger.trace("processing multipart attachments param");
            return r.getParts().entrySet().stream().map(e -> {
                DataSource dataSource = e.getValue().getDataSource();
                dataSource = newDataSource(dataSource::getInputStream, dataSource.getContentType(), e.getKey());
                return new Attachment(e.getKey(), dataSource, new MultivaluedHashMap<>(e.getValue().getHeaders()));

            }).collect(toImmutableList());
        }

    }

    private class PayloadParamHandler extends ParamHandler {

        private final boolean isRequired;

        public PayloadParamHandler(Parameter param, boolean isRequired) {
            super(param, null);
            this.isRequired = isRequired;
        }

        @Override
        public Object apply(WsRequest r) {
            logger.trace("processing request payload with type = {}", name, param.getType());
            String payload = r.getPayload();
            logger.trace("raw value =< {} >", payload);
            if (isBlank(payload)) {
                checkArgument(!isRequired, "missing required payload content");
                return null;
            } else {
                Object value;
                if (isPrimitiveOrWrapper(param.getType())) {
                    value = convert(payload, param.getType());
                } else {
                    value = fromJson(payload, param.getParameterizedType());
                }
                logger.trace("converted value =< {} >", value);
                return value;
            }
        }
    }
}
