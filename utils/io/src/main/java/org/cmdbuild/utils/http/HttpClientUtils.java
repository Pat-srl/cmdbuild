/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.http;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import java.lang.invoke.MethodHandles;
import java.util.Optional;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.util.EntityUtils;
import org.cmdbuild.utils.io.CmHttpRequestException;
import static org.cmdbuild.utils.io.CmIoUtils.readToString;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HttpClientUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    public static void closeQuietly(@Nullable AutoCloseable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ex) {
            LOGGER.warn("error closing resource = {}", closeable, ex);
        }
    }

    public static void checkStatus(CloseableHttpResponse response) throws CmHttpRequestException {
        checkStatus(response, false);
    }

    public static void checkStatusAndClose(CloseableHttpResponse response) throws CmHttpRequestException {
        checkStatus(response, false);
        closeQuietly(response);
    }

    public static String checkStatusAndReadResponse(CloseableHttpResponse response) throws CmHttpRequestException {
        String message = checkStatus(response, true);
        closeQuietly(response);
        return message;
    }

    @Nullable
    private static String checkStatus(CloseableHttpResponse response, boolean alwaysReadResponse) throws CmHttpRequestException {
        String message = null;
        try {
            HttpEntity entity = response.getEntity();
            boolean isOk = Integer.toString(response.getStatusLine().getStatusCode()).matches("[23]0.");
            if (entity != null && (alwaysReadResponse || !isOk)) {
                message = readToString(entity.getContent(), Optional.ofNullable(entity.getContentEncoding()).map(Header::getValue).orElse(null));
                EntityUtils.consumeQuietly(entity);
                LOGGER.debug("response status =< {} > message = \n\n{}\n", response.getStatusLine(), message);
            } else {
                LOGGER.debug("response status =< {} >", response.getStatusLine());
            }
            checkArgument(isOk, "response error = %s : < %s >", response.getStatusLine().toString(), firstNotBlank(abbreviate(message), "<no message>"));
            return message;
        } catch (Exception ex) {
            closeQuietly(response);
            throw new CmHttpRequestException(ex, response.getStatusLine(), message);
        }
    }
}
