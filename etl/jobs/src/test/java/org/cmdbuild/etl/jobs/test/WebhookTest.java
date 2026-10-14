/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.etl.jobs.test;

import com.google.common.eventbus.EventBus;
import org.cmdbuild.config.WaterwayConfig;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.email.template.EmailTemplateProcessorService;
import org.cmdbuild.etl.config.WaterwayDescriptorService;
import org.cmdbuild.etl.jobs.WaterwayWebhookTriggerService;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.webhook.WebhookService;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.utils.lang.EventBusUtils;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author ataboga
 */
public class WebhookTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    WaterwayConfig waterwayConfig = mock(WaterwayConfig.class);
    EventBus eventBus = new EventBus(EventBusUtils.logExceptions(logger));
    EventBusService eventBusService = mock(EventBusService.class);

    WaterwayService waterwayService = mock(WaterwayService.class);

    WaterwayDescriptorService waterwayDescriptorService = mock(WaterwayDescriptorService.class);

    WaterwayWebhookTriggerService waterwayWebhookTriggerService;

    @Before
    public void init() {
        when(waterwayConfig.isWebhookEnabled()).thenReturn(Boolean.TRUE);
        when(waterwayService.isEnabled()).thenReturn(Boolean.TRUE);
        when(eventBusService.getCardEventBus()).thenReturn(eventBus);
        when(eventBusService.getWorkflowEventBus()).thenReturn(eventBus);
        when(eventBusService.getDaoEventBus()).thenReturn(eventBus);
        waterwayWebhookTriggerService = new WaterwayWebhookTriggerService(
                mock(RequestContextService.class),
                mock(WaterwayConfig.class),
                waterwayService,
                mock(DaoService.class),
                mock(WaterwayDescriptorService.class),
                mock(WebhookService.class),
                mock(EmailTemplateProcessorService.class),
                eventBusService);
    }

    @Test
    public void testWebhookIsStopped() {
        // assert:
        assertFalse(waterwayWebhookTriggerService.isReady());
    }

    @Test
    public void testWebhookStart() {
        // arrange:
        waterwayWebhookTriggerService.start();

        // assert:
        assertTrue(waterwayWebhookTriggerService.isReady());
    }

    @Test
    public void testWebhookStop() {
        // arrange:
        waterwayWebhookTriggerService.stop();

        // assert:
        assertFalse(waterwayWebhookTriggerService.isReady());
    }

}
