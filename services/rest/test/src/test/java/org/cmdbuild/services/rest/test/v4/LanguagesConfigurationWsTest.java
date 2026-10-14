package org.cmdbuild.services.rest.test.v4;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.common.localization.LanguageInfo;
import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.service.rest.v4.command.LanguagesConfigurationWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.LanguagesConfigurationWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

import static org.cmdbuild.services.rest.test.common.TestHelper_Check.checkResultList;
import static org.cmdbuild.services.rest.test.common.TestHelper_Variable.*;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.mockito.Mockito.*;

/**
 * @author ldare
 */
public class LanguagesConfigurationWsTest extends WsTestBase {

    private LanguagesConfigurationWs instance;

    private final List<LanguageInfo> languageInfoList;

    public LanguagesConfigurationWsTest() {
        LanguagesConfigurationWsCommand command = new LanguagesConfigurationWsCommand(languageService);
        this.instance = new LanguagesConfigurationWs(command);

        this.languageInfoList = list(
                new LanguageInfoImpl(A_KNOWN_CODE1, "description1"),
                new LanguageInfoImpl(A_KNOWN_CODE2, "description2"),
                new LanguageInfoImpl(A_KNOWN_CODE3, "description3")
        );
    }

    @Test
    public void testGetLoginLanguages() {
        System.out.println("testGetLoginLanguages");

        //arrange:
        when(languageService.getLoginLanguagesInfo()).thenReturn(languageInfoList);

        //act:
        Object resultObject = instance.getLoginLanguages();

        //assert:
        verify(languageService).getLoginLanguagesInfo();
        checkResultList(list(A_KNOWN_CODE1, A_KNOWN_CODE2, A_KNOWN_CODE3), resultObject, "code");
    }

    private static class LanguageInfoImpl implements LanguageInfo {

        private final String code, description;

        @JsonCreator
        public LanguageInfoImpl(@JsonProperty("code") String code, @JsonProperty("description") String description) {
            this.code = checkNotBlank(code);
            this.description = checkNotBlank(description);
        }

        @Override
        public String getCode() {
            return code;
        }

        @Override
        public String getDescription() {
            return description;
        }

    }
}

