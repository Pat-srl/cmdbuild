package org.cmdbuild.service.rest.v2.providers;

import static java.lang.String.format;
import static java.net.URLEncoder.encode;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import jakarta.activation.DataHandler;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerResponseContext;
import jakarta.ws.rs.container.ContainerResponseFilter;
import jakarta.ws.rs.ext.Provider;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlankOrNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ContentDispositionHeaderService implements ContainerResponseFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext) throws IOException {
        Object entity = responseContext.getEntity();
        if (entity instanceof DataHandler) {
            DataHandler dataHandler = (DataHandler) entity;
            String filename = firstNotBlank(dataHandler.getName(), "file");
            String contentDisposition = getContentDisposition(requestContext);
            responseContext.getHeaders().putSingle("Content-Disposition", format("%s; filename=\"%s\"", contentDisposition, encodeFileName(filename)));
            String contentType = firstNotBlankOrNull(requestContext.getUriInfo().getQueryParameters().getFirst("_contenttype"), dataHandler.getContentType());
            if (isNotBlank(contentType)) {
                responseContext.getHeaders().putSingle("Content-Type", contentType);
            }
        }
    }

    private String getContentDisposition(ContainerRequestContext requestContext) {
        if (toBooleanOrDefault(requestContext.getUriInfo().getQueryParameters().getFirst("_download"), false) == true) {
            return "attachment";
        } else {
            return "inline";
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
