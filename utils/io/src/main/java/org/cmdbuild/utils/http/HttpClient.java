/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.http;

import java.io.IOException;
import static java.lang.String.format;
import java.nio.charset.Charset;
import java.util.function.Function;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import static org.apache.http.entity.ContentType.MULTIPART_FORM_DATA;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.cmdbuild.utils.io.BigByteArray;
import org.cmdbuild.utils.io.CmHttpRequestException;
import static org.cmdbuild.utils.io.CmIoUtils.toBigByteArray;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ldare
 */
public class HttpClient implements AutoCloseable {

    private final ExtServiceConfiguration configuration;

    private final Logger LOGGER = LoggerFactory.getLogger(getClass());
    private final PoolingHttpClientConnectionManager connectionManager = new PoolingHttpClientConnectionManager();

    public HttpClient(ExtServiceConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * Builds an authenticated HTTP request for the given configuration, path
     * and HTTP method.
     *
     * @param <T>
     * @param path the API path
     * @param http a function to create the HTTP request
     * @return the configured {@link HttpUriRequest}
     */
    public <T extends HttpUriRequest> T buildHttpRequest(String path, Function<String, T> http) {
        T request = http.apply(configuration.getUrl() + path);
        String auth = format("%s:%s", configuration.getUsername(),
                configuration.getPassword());
        byte[] encodedAuth = Base64.encodeBase64(auth.getBytes(Charset.forName("US-ASCII")));
        String authHeader = format("Basic %s", new String(encodedAuth));
        request.setHeader("Authorization", authHeader);
        return request;
    }

    /**
     * Returns a {@link CloseableHttpClient} using the connection manager.
     *
     * @return the HTTP client
     */
    private CloseableHttpClient getHttpClient() {
        return HttpClients.custom().setConnectionManager(connectionManager).build();
    }

    /**
     * Checks the status of the remote service by sending an HTTP GET request.
     * Throws a runtime exception if the service is not available.
     */
    public void checkServiceStatus() {
        try {
            HttpGet request = buildHttpRequest("/status", HttpGet::new);
            CloseableHttpResponse response = getHttpClient().execute(request);
            HttpClientUtils.checkStatusAndClose(response);
        } catch (Exception ex) {
            LOGGER.error("Error checking service status");
            // TODO THROW ERROR
        }
    }

    /**
     * Sends the resource to the remote conversion service via HTTP POST and
     * returns the result.
     *
     * @param file the file to be converted as a {@link BigByteArray}
     * @param path the API path for conversion
     * @return the converted file as a {@link BigByteArray}
     * @throws CmHttpRequestException if the HTTP request fails
     */
    public BigByteArray postConversion(BigByteArray file, String path) throws CmHttpRequestException {
        try {
            HttpPost request = buildHttpRequest(path, HttpPost::new);
            final MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
            entityBuilder.setMode(HttpMultipartMode.RFC6532);
            // below could break, last param was file.ifc, might be needed to specify file extension?
            entityBuilder.addBinaryBody("file", file.toInputStream(), MULTIPART_FORM_DATA, "file");
            request.setEntity(entityBuilder.build());
            CloseableHttpResponse response = getHttpClient().execute(request);
            HttpClientUtils.checkStatus(response);
            return toBigByteArray(response.getEntity().getContent());
        } catch (IOException ex) {
            LOGGER.error("Error executing request", ex);
            throw runtime("conversion failed =< %s >", ex.getMessage());
        }
    }

    /**
     * Closes the HTTP connection manager and releases resources.
     *
     * @throws Exception if an error occurs during closing
     */
    @Override
    public void close() throws Exception {
        connectionManager.close();
    }
}
