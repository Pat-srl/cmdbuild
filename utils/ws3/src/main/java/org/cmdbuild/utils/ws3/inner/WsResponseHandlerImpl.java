/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ws3.inner;

import com.fasterxml.jackson.core.JsonProcessingException;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import static com.google.common.collect.Iterables.getOnlyElement;
import jakarta.activation.DataHandler;
import jakarta.activation.DataSource;
import static jakarta.ws.rs.core.MediaType.APPLICATION_JSON;
import static jakarta.ws.rs.core.MediaType.TEXT_HTML;
import static jakarta.ws.rs.core.MediaType.TEXT_PLAIN;
import static jakarta.ws.rs.core.MediaType.TEXT_XML;
import jakarta.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import static java.lang.String.format;
import static java.net.URLEncoder.encode;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.io.IOUtils;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.io.CmIoUtils.newDataSource;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.utils.lang.CmConvertUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.getClassOfNullable;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringNotBlank;
import org.cmdbuild.utils.lang.LambdaExceptionUtils.Consumer_WithExceptions;
import org.cmdbuild.utils.ws3.utils.Ws3Exception;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WsResponseHandlerImpl implements WsResponseHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final String suggestedContentType;
    private final Object data;
    private final WsRequest request;
    private final List<Object> messages;

    public WsResponseHandlerImpl(WsRequest request, @Nullable Object data, @Nullable String suggestedContentType, List<Object> messages) {
        this.data = data;
        this.request = checkNotNull(request);
        this.suggestedContentType = suggestedContentType;
        this.messages = ImmutableList.copyOf(messages);
    }

    @Override
    public WsResponsePrinter prepareResponse() {
        return new ResponseBuilderHelper();
    }

    private class ResponseBuilderHelper implements WsResponsePrinter {

        private String contentType = suggestedContentType, contentDisposition;
        private int status = 200;
        private Long contentLength;
        private Object entity;
        private Consumer_WithExceptions<OutputStream, IOException> streamConsumer;
        private Consumer_WithExceptions<Writer, IOException> writerConsumer;
        private Response wsResponse;
        private String charset;
        private String filename;
        private final Map<String, String> headers = map();

        private final String HEADER_XSS_CSP_KEY = "Content-Security-Policy", HEADER_XSS_CSP_VALUE = "default-src 'self';";

        public ResponseBuilderHelper() {
            init();
        }

        private void init() {
            try {
                if (data instanceof Response response) {
                    wsResponse = response;
                    wsResponse.getHeaders().forEach((k, v) -> {
                        headers.put(k, toStringNotBlank(getOnlyElement(v)));
                    });
                    //TODO handle other ws response features
                    entity = wsResponse.getEntity();
                    if (wsResponse.getMediaType() != null) {
                        contentType = wsResponse.getMediaType().toString();
                    }
                } else {
                    entity = data;
                }

                if (entity instanceof DataHandler dataHandler) {
                    entity = CmIoUtils.toDataSource(dataHandler);
                }
                if (entity == null) {
                    status = 204;
                } else if (entity instanceof DataSource dataSource) {
                    headers.put(HEADER_XSS_CSP_KEY, HEADER_XSS_CSP_VALUE);
                    filename = firstNotBlank(dataSource.getName(), "file");
                    String disposition;
                    if (toBooleanOrDefault(request.getParam("_download"), false)) {
                        disposition = "attachment";
                    } else {
                        disposition = "inline";
                    }
                    contentDisposition = format("%s; filename=\"%s\"", disposition, encodeFileName(filename));
                    contentType = firstNotBlankOrNull(dataSource.getContentType(), contentType);
                    contentLength = CmIoUtils.countBytes(dataSource);
                    streamConsumer = (r) -> IOUtils.copy(dataSource.getInputStream(), r);
                } else {
                    switch (contentType) {
                        case APPLICATION_JSON -> {
                            attachMessagesToEntity();
                            byte[] json;
                            if (entity instanceof String string) {
                                json = string.getBytes(StandardCharsets.UTF_8);
                            } else {
                                json = CmJsonUtils.getObjectMapper().writeValueAsBytes(entity);
                            }
                            charset = StandardCharsets.UTF_8.name();
                            streamConsumer = (r) -> r.write(json);
                        }
                        case TEXT_PLAIN, TEXT_XML, TEXT_HTML -> {
                            String text = CmStringUtils.toStringOrEmpty(entity);
                            writerConsumer = (r) -> r.write(text);
                        }
                        default -> {
                            byte[] data = CmConvertUtils.convert(entity, byte[].class);
                            contentLength = (long) data.length;
                            streamConsumer = (r) -> r.write(data);
                        }
                    }
                }
                if (wsResponse != null) {
                    status = wsResponse.getStatus();
                }

                if (request.hasParam("_contenttype")) {
                    contentType = request.getParam("_contenttype");
                }

            } catch (JsonProcessingException ex) {
                throw new Ws3Exception(ex, "error processing ws response");
            }
        }

        @Override
        public void printResponse(HttpServletResponse response) throws IOException {
            getResponseHeaders().forEach(response::setHeader);
            response.setStatus(status);
            if (contentLength != null) {
                response.setContentLengthLong(contentLength);
            }
            if (contentType != null) {
                response.setContentType(contentType);
            }
            if (charset != null) {
                response.setCharacterEncoding(charset);
            }
            if (writerConsumer != null) {
                writerConsumer.accept(response.getWriter());
            } else if (streamConsumer != null) {
                streamConsumer.accept(response.getOutputStream());
            }
        }

        @Override
        public Map<String, String> getResponseHeaders() {
            return map(headers).accept(m -> {
                if (contentDisposition != null) {
                    m.put("Content-Disposition", contentDisposition);
                }
            });
        }

        @Override
        public String getResponseAsString() {
            try {
                if (streamConsumer != null) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    streamConsumer.accept(out);
                    return charset == null ? new String(out.toByteArray()) : new String(out.toByteArray(), charset);//TODO improve charset processing, read from content type (?)
                } else {
                    StringWriter writer = new StringWriter();
                    writerConsumer.accept(writer);
                    return writer.toString();
                }
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }

        @Override
        public DataSource getResponseAsDataSource() {
            try {
                if (streamConsumer != null) {
                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    streamConsumer.accept(out);
                    return newDataSource(out.toByteArray(), contentType, charset, filename);
                } else {
                    StringWriter writer = new StringWriter();
                    writerConsumer.accept(writer);
                    return newDataSource(writer.toString(), contentType, charset, filename);
                }
            } catch (IOException ex) {
                throw runtime(ex);
            }
        }

        private void attachMessagesToEntity() {
            if (!messages.isEmpty() && isJsonResponse()) {
                if (entity instanceof Map map) {
                    if (toBooleanOrDefault(map.get("success"), false)) {
                        entity = map(map).with("messages", list().accept(l -> {
                            if (map.containsKey("messages")) {
                                l.addAll((Collection) map.get("messages"));
                            }
                        }).with(messages));
                    }
                } else {
                    logger.warn("tracked error/warning events, but unable to attach them to response entity of type = {}", getClassOfNullable(entity).getName());
                    //TODO handle other kind of response beans ? 
                }
            }
        }

        private boolean isJsonResponse() {
            return equal(contentType, APPLICATION_JSON);
        }

    }

    private String encodeFileName(String name) {
        try {
            checkNotBlank(name);
            return encode(name, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            logger.error("error encoding name", e);
            return name;
        }
    }

}
