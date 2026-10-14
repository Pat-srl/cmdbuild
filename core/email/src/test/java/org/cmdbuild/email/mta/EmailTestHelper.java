/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package org.cmdbuild.email.mta;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import org.cmdbuild.config.EmailQueueConfiguration;
import org.cmdbuild.debuginfo.InstanceInfoService;
import org.cmdbuild.email.EmailAttachment;
import org.cmdbuild.email.EmailSignatureService;
import org.cmdbuild.email.beans.EmailAccountImpl;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.email.data.EmailRepository;
import static org.cmdbuild.email.mta.TokenEncrypter.getCustomCm3EasyCryptoUtils;
import org.cmdbuild.lock.LockResponse;
import org.cmdbuild.lock.LockService;
import static org.mockito.AdditionalAnswers.returnsFirstArg;
import org.mockito.Matchers;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class EmailTestHelper {

    static final String A_USER_DESTINATION_EMAIL = getCustomCm3EasyCryptoUtils().decryptValue("be60162de3988d23c93147805673bab7a8c4320075a95f6a50c058d20b4d0b7ef86a6a");
    static final String A_KNOWN_IN_REPLY_TO = "<DB6PR03MB2935AE0E87945H56YHE935.eurprd03.prod.outlook.com>";

    static EmailImpl buildTestEmail(String subject, List<String> toAddresses,
            List<EmailAttachment> attachments,
            String inReplyTo) {
        return new EmailImpl.EmailImplBuilder().withToAddresses(toAddresses).withSubject(
                subject).withContent("test").withAttachments(attachments).withInReplyTo(
                inReplyTo).build();
    }

    static EmailImpl buildTestEmail(List<String> toAddresses,
            List<EmailAttachment> attachments,
            String inReplyTo) {
        return buildTestEmail("test", toAddresses, attachments, inReplyTo);
    }

    static EmailAccountImpl mockEmailAccountWithPassword(String passwordJson) {
        return EmailAccountImpl.builder().withPassword(passwordJson).build();
    }

    static EmailQueueConfiguration mockEmailQueueConfiguration() {
        EmailQueueConfiguration mockEmailQueueConfiguration = mock(
                EmailQueueConfiguration.class);
        when(mockEmailQueueConfiguration.getSmtpTimeoutSeconds()).thenReturn(
                null);
        return mockEmailQueueConfiguration;
    }

    static EmailRepository mockEmailRepository() {
        EmailRepository mockEmailRepository = mock(EmailRepository.class);
        when(mockEmailRepository.create(any())).then(returnsFirstArg());
        return mockEmailRepository;
    }

    static class MockInstanceInfoService implements InstanceInfoService {

        public MockInstanceInfoService() {
        }

        @Override
        public String getVersion() {
            return "aVersion";
        }

        @Override
        public String getRevision() {
            return "aRevision";
        }

        @Override
        public String getNodeId() {
            return "aNodeId";
        }

        @Override
        public String getInstanceName() {
            return "aInstanceName";
        }
    } // end MockInstanceInfoService class

    /**
     * Doesn't move received messages at all
     *
     * @return
     */
    static EmailReceiveConfig mockEmailReceiveConfig_DoNothing() {
        EmailReceiveConfig mockEmailReceiveConfig = mock(
                EmailReceiveConfig.class);
        when(mockEmailReceiveConfig.getIncomingFolder()).thenReturn(
                "inbox");
        when(mockEmailReceiveConfig.getReceivedEmailAction()).thenReturn(
                EmailReceivedAction.ERA_DO_NOTHING);
        when(mockEmailReceiveConfig.getCallback()).thenReturn(i -> {
            return EmailProcessedAction.EPA_DO_NOTHING;
        });
        when(mockEmailReceiveConfig.getIncomingFolder()).thenReturn(
                "inbox");
        return mockEmailReceiveConfig;
    }

    static EmailReceiveConfig mockEmailReceiveConfig_MoveReceivedMsgs() {
        EmailReceiveConfig mockEmailReceiveConfig = mockEmailReceiveConfig_DoNothing();
        when(mockEmailReceiveConfig.getReceivedEmailAction()).thenReturn(
                EmailReceivedAction.ERA_MOVE_TO_RECEIVED);
        when(mockEmailReceiveConfig.getReceivedFolder()).thenReturn(
                "_CMDBuildTests_ReceivedEmail");
        when(mockEmailReceiveConfig.getCallback()).thenReturn(i -> {
            return EmailProcessedAction.EPA_MOVE_TO_PROCESSED;
        });
        return mockEmailReceiveConfig;
    }

    static EmailSignatureService mockEmailSignatureService() {
        EmailSignatureService mockEmailSignatureService = mock(
                EmailSignatureService.class);
        when(mockEmailSignatureService.getSignatureHtmlForCurrentUser(anyLong())).thenReturn(
                "<i>aSignatureHtmlForCurrentUser<i>");

        return mockEmailSignatureService;
    }

    static LockService mockLockService() {
        LockService mockLockService = mock(LockService.class);
        LockResponse mockLockResponse = mock(LockResponse.class);
        when(mockLockResponse.isAquired()).thenReturn(true);
        when(mockLockService.aquireLockOrWait(Matchers.anyString()))
                .thenReturn(mockLockResponse);
        return mockLockService;
    }

    static InputStream getResourceInputStream(String filename) {
        return EmailTestHelper.class.getResourceAsStream((new File(filename)).getAbsolutePath());
    }

}
