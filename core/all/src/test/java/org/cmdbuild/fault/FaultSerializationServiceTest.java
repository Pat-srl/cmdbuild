/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.fault;

import java.util.List;
import java.util.Map;
import org.cmdbuild.translation.ObjectTranslationService;
import static org.cmdbuild.utils.lang.CmExceptionUtils.parseErrorCode;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author ataboga
 */
public class FaultSerializationServiceTest {

    @Test
    public void faultSerializationTest1() {
        FaultEvent errorEvent = FaultEventImpl.error(parseErrorCode("100", "this is the %s to %s", "error", "format"));
        ObjectTranslationService mockObjectTranslationService = mock(ObjectTranslationService.class);
        when(mockObjectTranslationService.translateByCode("100", "this is the %s to %s")).thenReturn("questo è l'%s da %s");
        FaultSerializationService faultSerializationService = new FaultSerializationServiceImpl(mockObjectTranslationService);
        List<Map<String, Object>> errorToJsonMessages = faultSerializationService.errorToJsonMessages(errorEvent);

        assertEquals("ERROR", errorToJsonMessages.get(0).get("level"));
        assertEquals(true, errorToJsonMessages.get(0).get("show_user"));
        assertEquals("this is the error to format", errorToJsonMessages.get(0).get("message"));
        assertEquals("questo &egrave; l'error da format", errorToJsonMessages.get(0).get("_message_translation"));
        assertEquals("100", errorToJsonMessages.get(0).get("code"));
    }

    @Test
    public void faultSerializationTest2() {
        FaultEvent errorEvent = FaultEventImpl.error(parseErrorCode("100", "this is the error"));
        ObjectTranslationService mockObjectTranslationService = mock(ObjectTranslationService.class);
        when(mockObjectTranslationService.translateByCode("100", "this is the error")).thenReturn("questo è l'errore");
        FaultSerializationService faultSerializationService = new FaultSerializationServiceImpl(mockObjectTranslationService);
        List<Map<String, Object>> errorToJsonMessages = faultSerializationService.errorToJsonMessages(errorEvent);

        assertEquals("ERROR", errorToJsonMessages.get(0).get("level"));
        assertEquals(true, errorToJsonMessages.get(0).get("show_user"));
        assertEquals("this is the error", errorToJsonMessages.get(0).get("message"));
        assertEquals("questo è l'errore", errorToJsonMessages.get(0).get("_message_translation"));
        assertEquals("100", errorToJsonMessages.get(0).get("code"));
    }

    @Test
    public void faultSerializationTest3() {
        FaultEvent errorEvent = FaultEventImpl.error("CM 100: this is the error");
        ObjectTranslationService mockObjectTranslationService = mock(ObjectTranslationService.class);
        when(mockObjectTranslationService.translateByCode("100", "this is the error")).thenReturn("questo è l'errore");
        FaultSerializationService faultSerializationService = new FaultSerializationServiceImpl(mockObjectTranslationService);
        List<Map<String, Object>> errorToJsonMessages = faultSerializationService.errorToJsonMessages(errorEvent);

        assertEquals("ERROR", errorToJsonMessages.get(0).get("level"));
        assertEquals(true, errorToJsonMessages.get(0).get("show_user"));
        assertEquals("this is the error", errorToJsonMessages.get(0).get("message"));
        assertEquals("questo è l'errore", errorToJsonMessages.get(0).get("_message_translation"));
        assertEquals("100", errorToJsonMessages.get(0).get("code"));
    }
}
