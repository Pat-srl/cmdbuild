/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.common.localization.LanguageService;
import org.springframework.stereotype.Component;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author ldare
 */
@Component
public class LanguagesWsCommand {

    private final LanguageService languageService;

    public LanguagesWsCommand(LanguageService languageService) {
        this.languageService = checkNotNull(languageService);
    }

    public Collection<LanguageInfo> doReadLanguages(Boolean activeOnly) {
        return activeOnly ? languageService.getEnabledLanguagesInfo() : languageService.getAllLanguages();
    }
}
