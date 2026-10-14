/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package org.cmdbuild.chat;

import static java.lang.String.format;
import java.util.Objects;
import org.cmdbuild.auth.session.SessionService;
import static org.cmdbuild.auth.user.LoginUserImpl.ANONYMOUS_LOGIN_USER;
import org.cmdbuild.auth.user.OperationUserImpl;
import org.cmdbuild.auth.user.OperationUserSupplier;
import static org.cmdbuild.chat.ChatMessageSourceType.CMST_SYSTEM;
import static org.cmdbuild.email.Email.NOTIFICATION_PROVIDER_CHAT;
import org.cmdbuild.email.beans.EmailImpl;
import org.cmdbuild.notification.NotificationCommonData;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentMatcher;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 *
 * @author afelice
 */
public class ChatNotificationProviderTest {

    private final static String A_USER = "aUser";
    private final static String A_USERNAME = "aUserName";

    private final static String A_SUBJECT = "<Subject>";
    private final static String A_CONTENT = "<Content>";

    private final ChatService chatService = mock(ChatService.class);
    private final OperationUserSupplier operationUserSupplier = mock(OperationUserSupplier.class);
    private final SessionService sessionService = mock(SessionService.class);

    private ChatNotificationProvider instance;

    @Before
    public void setUp() {
        when(operationUserSupplier.getUsername()).thenReturn(A_USERNAME);
        when(operationUserSupplier.getUser()).thenReturn(OperationUserImpl.builder().withAuthenticatedUser(ANONYMOUS_LOGIN_USER).build());
        instance = new ChatNotificationProvider(chatService, operationUserSupplier, sessionService);
    }

    /**
     * Test of sendNotification method, of class ChatNotificationProvider.
     */
    @Test
    public void testSendNotification() {
        System.out.println("sendNotification_singleTarget");

        // arrange:
        NotificationCommonData notificationToSend = buildNotificationChat(A_USER);
        ChatMessageData expNotificationToSend = buildChatMessageData(A_USER);

        // act:
        instance.sendNotification(notificationToSend);

        // assert:
        verify(chatService, times(1)).sendMessageAs(
                matchChatMessageData(expNotificationToSend),
                eq(CMST_SYSTEM),
                eq(A_USERNAME),
                anyString(),
                anyList());
    }

    private static EmailImpl buildNotificationChat(final String to) {
        return EmailImpl.builder()
                .withNotificationProvider(NOTIFICATION_PROVIDER_CHAT)
                .withSubject(A_SUBJECT)
                .withContent(A_CONTENT)
                .withTo(to)
                .build();
    }

    private static ChatMessageDataImpl buildChatMessageData(final String target) {
        return ChatMessageDataImpl.builder()
                .withSubject(A_SUBJECT)
                .withContent(A_CONTENT)
                .withTarget(target)
                .build();
    }

    private static ChatMessageData matchChatMessageData(ChatMessageData chatMessageData) {
        return argThat(new ChatMessageDataMatcher(chatMessageData));
    }

} // end ChatNotificationProviderTest class

class ChatMessageDataMatcher extends ArgumentMatcher<ChatMessageData> {

    private final ChatMessageData left;

    ChatMessageDataMatcher(ChatMessageData left) {
        this.left = left;
    }

    @Override
    public boolean matches(Object obj) {
        ChatMessageData right = (ChatMessageData) obj;
        return Objects.equals(left.getTarget(), right.getTarget())
                && Objects.equals(left.getSubject(), right.getSubject())
                && Objects.equals(left.getContent(), right.getContent());
    }

    @Override
    public String toString() {
        return format("%s{target =< %s >, subject =< %s >, content =< %s >}",
                ChatMessageData.class.getName(),
                left.getTarget(), left.getSubject(), left.getContent());
    }

    @Override
    public void describeTo(Description description) {
        description.appendText(toString());
    }

} // end ChatMessageDataMatcher class
