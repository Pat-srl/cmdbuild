/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.translation;

import org.cmdbuild.common.localization.ContextLanguageHolder;
import static com.google.common.base.Strings.emptyToNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import org.springframework.stereotype.Component;

@Component
public class ContextLanguageHolderImpl implements ContextLanguageHolder {

    private final RequestContextHolder<String> holder;

    public ContextLanguageHolderImpl(RequestContextService requestContextService) {
        holder = requestContextService.createRequestContextHolder();
    }

    @Override
    public String getContextLanguage() {
        return emptyToNull(holder.getOrNull());
    }

    @Override
    public void setContextLanguage(@Nullable String language) {
        holder.set(language);
    }
}
