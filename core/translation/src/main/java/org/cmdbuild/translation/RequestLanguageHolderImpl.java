/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import static com.google.common.base.Strings.emptyToNull;
import com.google.common.eventbus.EventBus;
import jakarta.annotation.Nullable;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class RequestLanguageHolderImpl implements RequestLanguageHolder {

    private final RequestContextHolder<String> holder;
    private final EventBus eventBus;

    public RequestLanguageHolderImpl(RequestContextService requestContextService, EventBusService eventBusService) {
        holder = requestContextService.createRequestContextHolder();
        eventBus = eventBusService.getContextEventBus();
    }

    @Override
    public boolean hasRequestLanguage() {
        return holder.hasContent();
    }

    @Override
    public void setRequestLanguage(String lang) {
        holder.set(checkNotBlank(lang));
        eventBus.post(RequestLanguageSetEvent.INSTANCE);
    }

    @Override
    public String getRequestLanguage() {
        return checkNotBlank(holder.get());
    }

    @Override
    @Nullable
    public String getRequestLanguageOrNull() {
        return emptyToNull(holder.getOrNull());
    }
}
