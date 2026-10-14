/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.regex.Pattern;
import org.cmdbuild.auth.login.AuthRequestInfo;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;

public class AuthRequestInfoImpl implements AuthRequestInfo {

    private final HttpServletRequest request;
    private MultipartHttpServletRequest multipartRequest;

//    private final Supplier<String> payloadSupplier;
    public AuthRequestInfoImpl(HttpServletRequest request) {
        this.request = checkNotNull(request);
//        payloadSupplier = Suppliers.memoize(() -> {
//            try {
//                return IOUtils.toString(this.request.getReader());
//            } catch (Exception ex) {
//                throw runtime(ex);
//            }
//        });
    }

    @Override
    public String getRequestUrl() {
        return request.getRequestURL().toString();
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    @Nullable
    public String getHeader(String name) {
        return request.getHeader(name);
    }

    @Override
    @Nullable
    public String getParameter(String name) {
        return request.getParameter(name);
    }

    @Override
    public String getRequestPath() {
        return request.getRequestURI().replaceFirst(Pattern.quote(request.getContextPath()), "");
    }

    @Override
    @Nullable
    public byte[] getMultipartParameter(String key) {
        if (multipartRequest == null) {
            StandardServletMultipartResolver commonsMultipartResolver = new StandardServletMultipartResolver();
            if (commonsMultipartResolver.isMultipart(request)) {
                multipartRequest = commonsMultipartResolver.resolveMultipart(request);
            }
        }
        if (multipartRequest != null) {
            MultipartFile file = multipartRequest.getFile(key);
            try {
                return file == null ? null : file.getBytes();
            } catch (IOException ex) {
                throw runtime(ex);
            }
        } else {
            return null;
        }
    }

    @Override
    public boolean hasParameter(String key) {
        return request.getParameterMap().containsKey(key);
    }

    @Override
    public <T> T getInner() {
        return (T) request;
    }

}
