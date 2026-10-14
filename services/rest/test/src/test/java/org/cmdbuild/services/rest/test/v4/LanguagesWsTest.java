package org.cmdbuild.services.rest.test.v4;

import org.cmdbuild.common.localization.LanguageService;
import org.cmdbuild.service.rest.v4.command.LanguagesWsCommand;
import org.cmdbuild.service.rest.v4.endpoint.LanguagesWs;
import org.cmdbuild.services.rest.test.common.WsTestBase;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * @author ldare
 */
public class LanguagesWsTest extends WsTestBase {

    private LanguagesWs instance;

    public LanguagesWsTest() {
        LanguagesWsCommand command = new LanguagesWsCommand(languageService);
        this.instance = new LanguagesWs(command);
    }

    @Test
    public void testReadLanguages() {
        System.out.println("testReadLanguages");

        //arrange:

        //act:
        Object resultObject = instance.readLanguages(false);

        //assert:
    }
}

