/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.auth.session.inner;

import jakarta.annotation.Nullable;
import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.requestcontext.RequestContextService.REQUEST_CONTEXT_SESSION_ID_KEY;
import org.springframework.stereotype.Component;

@Component
public class CurrentSessionHolderImpl implements CurrentSessionHolder {

    private final RequestContextHolder<String> inner;

    public CurrentSessionHolderImpl(RequestContextService requestContextService) {
        inner = requestContextService.createRequestContextHolder(REQUEST_CONTEXT_SESSION_ID_KEY);
    }

    @Override
    @Nullable
    public String getOrNull() {
        return inner.getOrNull();
    }

    @Override
    public String get() {
        return inner.get();
    }

    @Override
    public void set(String value) {
        inner.set(value);
    }

}
