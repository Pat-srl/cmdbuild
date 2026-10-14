/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.common.reflect.Reflection;
import jakarta.annotation.Nullable;
import java.io.IOException;
import static java.lang.String.format;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import static java.util.Arrays.asList;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.apache.commons.lang3.builder.Builder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.cmdbuild.api.SchemaCollectorApi;
import org.cmdbuild.client.rest.api.AttachmentApi;
import org.cmdbuild.client.rest.api.AuditApi;
import org.cmdbuild.client.rest.api.CalendarApi;
import org.cmdbuild.client.rest.api.CardApi;
import org.cmdbuild.client.rest.api.ClassApi;
import org.cmdbuild.client.rest.api.CustomComponentApi;
import org.cmdbuild.client.rest.api.DomainApi;
import org.cmdbuild.client.rest.api.EmailApi;
import org.cmdbuild.client.rest.api.EtlApi;
import org.cmdbuild.client.rest.api.GeoserverLayerApi;
import org.cmdbuild.client.rest.api.LoginApi;
import org.cmdbuild.client.rest.api.LookupApi;
import org.cmdbuild.client.rest.api.MenuApi;
import org.cmdbuild.client.rest.api.RelationApi;
import org.cmdbuild.client.rest.api.ReportApi;
import org.cmdbuild.client.rest.api.SessionApi;
import org.cmdbuild.client.rest.api.SystemApi;
import org.cmdbuild.client.rest.api.TranslationApi;
import org.cmdbuild.client.rest.api.UploadApi;
import org.cmdbuild.client.rest.api.UserApi;
import org.cmdbuild.client.rest.api.WokflowApi;
import org.cmdbuild.client.rest.core.InnerRestClient;
import org.cmdbuild.client.rest.core.RestClientException;
import org.cmdbuild.client.rest.core.RestWsClient;
import org.cmdbuild.client.rest.impl.AttachmentApiImpl;
import org.cmdbuild.client.rest.impl.AuditApiImpl;
import org.cmdbuild.client.rest.impl.CalendarApiImpl;
import org.cmdbuild.client.rest.impl.CardApiImpl;
import org.cmdbuild.client.rest.impl.ClasseApiImpl;
import org.cmdbuild.client.rest.impl.CustomPageApiImpl;
import org.cmdbuild.client.rest.impl.DomainApiImpl;
import org.cmdbuild.client.rest.impl.EmailApiImpl;
import org.cmdbuild.client.rest.impl.EtlApiImpl;
import org.cmdbuild.client.rest.impl.GeoserverLayerApiImpl;
import org.cmdbuild.client.rest.impl.LoginApiImpl;
import org.cmdbuild.client.rest.impl.LookupApiImpl;
import org.cmdbuild.client.rest.impl.MenuApiImpl;
import org.cmdbuild.client.rest.impl.RelationApiImpl;
import org.cmdbuild.client.rest.impl.ReportApiImpl;
import org.cmdbuild.client.rest.impl.SchemaCollectorApiImpl;
import org.cmdbuild.client.rest.impl.SessionApiImpl;
import org.cmdbuild.client.rest.impl.SystemApiImpl;
import org.cmdbuild.client.rest.impl.TranslationApiImpl;
import org.cmdbuild.client.rest.impl.UploadApiImpl;
import org.cmdbuild.client.rest.impl.UserApiImpl;
import org.cmdbuild.client.rest.impl.WorkflowApiImpl;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_ADMIN;
import static org.cmdbuild.service.rest.common.utils.WsSerializationAttrs.VIEW_MODE_HEADER_PARAM;
import static org.cmdbuild.utils.io.CmNetUtils.getInsecureSslContext;
import org.cmdbuild.utils.io.StreamProgressEvent;
import org.cmdbuild.utils.io.StreamProgressListener;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RestClientImpl implements RestClient, RestWsClient, InnerRestClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private String sessionToken, actionId;
    protected final String serverUrl;
    private CloseableHttpClient httpClient;
    private final Map<String, String> customHeaders = map();
    private final EventBus eventBus = new EventBus(logExceptions(logger));
    private boolean allowInsecureSsl = false;

    protected RestClientImpl(String serverUrl) {
        this.serverUrl = checkNotBlank(serverUrl).replaceFirst("/+$", "");
    }

    @Override
    public String getBaseUrl() {
        return serverUrl;
    }

    @Override
    public void addUploadProgressListener(StreamProgressListener listener) {
        checkNotNull(listener);
        eventBus.register(new Object() {

            @Subscribe
            public void handleUploadProgressEvent(StreamProgressEvent event) {
                listener.handleStreamProgressEvent(event);
            }
        });
    }

    @Override
    public EventBus getEventBus() {
        return eventBus;
    }

    private void init() {
        HttpClientBuilder builder = HttpClients.custom()
                .setDefaultHeaders(mapOf(String.class, String.class).with(VIEW_MODE_HEADER_PARAM, VIEW_MODE_ADMIN).with(customHeaders).entrySet().stream().map((h) -> new BasicHeader(h.getKey(), h.getValue())).collect(toList()));
        if (allowInsecureSsl) {
            builder.setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(getInsecureSslContext());
        }
        httpClient = builder.build();
    }

    @Override
    public void allowInsecureSsl(boolean allowInsecureSsl) {
        this.allowInsecureSsl = allowInsecureSsl;
    }

    @Override
    public void close() {
        if (httpClient != null) {
            try {
                httpClient.close();
            } catch (IOException ex) {
                throw runtime(ex);
            } finally {
                httpClient = null;
            }
        }
    }

    @Override
    public CloseableHttpClient getHttpClient() {
        if (httpClient == null) {
            init();
        }
        return httpClient;
    }

    @Override
    public void setHeader(String key, String value) {
        customHeaders.put(key, value);
        close();
    }

    @Override
    public String getSessionToken() {
        return checkNotBlank(sessionToken, "session token not set");
    }

    @Override
    public void setSessionToken(String sessionToken) {
        this.sessionToken = checkNotNull(trimToNull(sessionToken));
    }

    @Override
    public String getServerUrl() {
        return checkNotBlank(serverUrl, "server url not set");
    }

    @Override
    public LoginApi login() {
        return proxy(LoginApi.class, new LoginApiImpl(this));
    }

    @Override
    public WokflowApi workflow() {
        return proxy(WokflowApi.class, new WorkflowApiImpl(this));
    }

    @Override
    public SessionApi session() {
        return proxy(SessionApi.class, new SessionApiImpl(this));
    }

    @Override
    public CardApi card() {
        return proxy(CardApi.class, new CardApiImpl(this));
    }

    @Override
    public DomainApi domain() {
        return proxy(DomainApi.class, new DomainApiImpl(this));
    }

    @Override
    public RelationApi relation() {
        return proxy(RelationApi.class, new RelationApiImpl(this));
    }

    @Override
    public UserApi user() {
        return proxy(UserApi.class, new UserApiImpl(this));
    }

    @Override
    public ClassApi classe() {
        return proxy(ClassApi.class, new ClasseApiImpl(this));
    }

    @Override
    public AttachmentApi attachment() {
        return proxy(AttachmentApi.class, new AttachmentApiImpl(this));
    }

    @Override
    public SystemApi system() {
        return proxy(SystemApi.class, new SystemApiImpl(this));
    }

    @Override
    public EtlApi etl() {
        return proxy(EtlApi.class, new EtlApiImpl(this));
    }

    @Override
    public MenuApi menu() {
        return proxy(MenuApi.class, new MenuApiImpl(this));
    }

    @Override
    public AuditApi audit() {
        return proxy(AuditApi.class, new AuditApiImpl(this));
    }

    @Override
    public LookupApi lookup() {
        return proxy(LookupApi.class, new LookupApiImpl(this));
    }

    @Override
    public UploadApi uploads() {
        return proxy(UploadApi.class, new UploadApiImpl(this));
    }

    @Override
    public ReportApi report() {
        return proxy(ReportApi.class, new ReportApiImpl(this));
    }

    @Override
    public CustomComponentApi customComponent() {
        return proxy(CustomComponentApi.class, new CustomPageApiImpl(this));
    }

    @Override
    public EmailApi email() {
        return proxy(EmailApi.class, new EmailApiImpl(this));
    }

    @Override
    public CalendarApi calendar() {
        return proxy(CalendarApi.class, new CalendarApiImpl(this));
    }

    @Override
    public GeoserverLayerApi geoserverLayer() {
        return proxy(GeoserverLayerApi.class, new GeoserverLayerApiImpl(this));
    }

    @Override
    public TranslationApi translation() {
        return proxy(TranslationApi.class, new TranslationApiImpl(this));
    }

    @Override
    public SchemaCollectorApi schemaCollector() {
        return proxy(SchemaCollectorApi.class, new SchemaCollectorApiImpl(this));
    }

    @Override
    public InnerRestClient inner() {
        return this;
    }

    public static RestClientBuilder builder() {
        return new RestClientBuilder();
    }

    public static RestClient build(String host, int port) {
        return build(format("http://%s:%s", host, port));
    }

    public static RestClient build(String url) {
        return builder().withServerUrl(url).build();
    }

    @Override
    public void setActionId(@Nullable String actionId) {
        this.actionId = trimToNull(actionId);
    }

    @Override
    public String getActionId() {
        return actionId;
    }

    public static class RestClientBuilder implements Builder<RestClient> {

        protected String serverUrl;

        public RestClientBuilder withServerUrl(String serverUrl) {
            this.serverUrl = serverUrl;
            return this;
        }

        @Override
        public RestClient build() {
            return new RestClientImpl(serverUrl);
        }

    }

    protected <T> T proxy(Class<T> type, T service) {
        return Reflection.newProxy(type, new ExceptionWrappingInvocationHandler(service));
    }

    private class ExceptionWrappingInvocationHandler<T> implements InvocationHandler {

        private final T service;

        public ExceptionWrappingInvocationHandler(T service) {
            this.service = checkNotNull(service);
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            try {
                return method.invoke(service, args);
            } catch (InvocationTargetException ex) {
                Throwable innerException = ex.getCause();
                String serviceDescription = getOnlyElement(asList(proxy.getClass().getInterfaces())).getSimpleName().replaceFirst("Service$", "").toLowerCase();
                String methodName = method.getName();
                throw new RestClientException(format("error calling rest ws method %s.%s", serviceDescription, methodName), innerException);
            }
        }
    }
}
