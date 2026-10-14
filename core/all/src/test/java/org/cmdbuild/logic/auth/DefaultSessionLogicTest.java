package org.cmdbuild.logic.auth;

/**
 * TODO remove, replace with tests for new session service
 * @author davide
 */
public class DefaultSessionLogicTest {

//	private static class DummyException extends RuntimeException {
//
//		private static final long serialVersionUID = 1L;
//
//	}
//
//	private AuthenticationLogic authenticationLogic;
////	private CurrentSessionStore currentSessionStore;
//	private OperationUserSupplier currentUserStore;
//	private SessionStore sessionStore;
//	private TokenGenerator tokenGenerator;
//	private Predicate<OperationUser> canImpersonate;
//	private DefaultSessionLogic underTest;
//
//	@Before
//	public void setUp() throws Exception {
//		authenticationLogic = mock(AuthenticationLogic.class);
////		currentSessionStore = mock(CurrentSessionStore.class);
//		currentUserStore = mock(OperationUserSupplier.class);
//		sessionStore = mock(SessionStore.class);
//		tokenGenerator = mock(TokenGenerator.class);
//		canImpersonate = mock(Predicate.class);
//		underTest = new DefaultSessionLogic(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//	}
//
//	@Test(expected = DummyException.class)
//	public void logicThrowsExceptionDuringCreationWithLogin() throws Exception {
//		// given
//		final LoginDTO login = LoginDTO.newInstance() //
//				.build();
//		doThrow(new DummyException()) //
//				.when(authenticationLogic).login(any(LoginDTO.class), any(OperationUserSupplier.class));
//
//		try {
//			// when
//			underTest.create(login);
//		} finally {
//			// then
//			verify(authenticationLogic).login(eq(login), any(OperationUserSupplier.class));
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//		}
//	}
//
//	@Test
//	public void createWithLogin() throws Exception {
//		// given
//		final LoginDTO login = LoginDTO.newInstance() //
//				.build();
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		doReturn("foo") //
//				.when(authenticatedUser).getUsername();
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		doAnswer((Answer<Response>) (final InvocationOnMock invocation) -> {
//			final OperationUserSupplier userStore = OperationUserSupplier.class.cast(invocation.getArguments()[1]);
//			userStore.setUser(operationUser);
//			return newProxy(Response.class, unsupported("should not be used"));
//		}) //
//				.when(authenticationLogic).login(any(LoginDTO.class), any(OperationUserSupplier.class));
//		doReturn("bar") //
//				.when(tokenGenerator).generate(anyString());
//
//		// when
//		final String response = underTest.create(login);
//
//		// then
//		verify(authenticationLogic).login(eq(login), any(OperationUserSupplier.class));
//		verify(tokenGenerator).generate(eq("foo"));
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).create(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session captured = captor.getValue();
//		assertThat(captured.getIdentifier(), equalTo("bar"));
//		assertThat(captured.getUser(), equalTo(operationUser));
//		assertThat(captured.getImpersonated(), nullValue(OperationUser.class));
//
//		assertThat(response, equalTo("bar"));
//	}
//
//	@Test(expected = DummyException.class)
//	public void logicThrowsExceptionDuringCreationWithClientRequest() throws Exception {
//		// given
//		final ClientRequest request = mock(ClientRequest.class);
//		final SessionLogic.SessionCreateCallback callback = mock(SessionLogic.SessionCreateCallback.class);
//		doThrow(new DummyException()) //
//				.when(authenticationLogic).login(any(ClientRequest.class), any(OperationUserSupplier.class));
//
//		try {
//			// when
//			underTest.create(request, callback);
//		} finally {
//			// then
//			verify(authenticationLogic).login(eq(request), any(OperationUserSupplier.class));
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//		}
//	}
//
//	@Test
//	public void createWithClientRequest() throws Exception {
//		// given
//		final ClientRequest request = mock(ClientRequest.class);
//		final SessionLogic.SessionCreateCallback callback = mock(SessionLogic.SessionCreateCallback.class);
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		doReturn("foo") //
//				.when(authenticatedUser).getUsername();
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		final ClientAuthenticationResponse expectedResponse = mock(ClientAuthenticationResponse.class);
//		doAnswer(new Answer<ClientAuthenticationResponse>() {
//
//			@Override
//			public ClientAuthenticationResponse answer(final InvocationOnMock invocation) throws Throwable {
//				final OperationUserSupplier userStore = OperationUserSupplier.class.cast(invocation.getArguments()[1]);
//				userStore.setUser(operationUser);
//				return expectedResponse;
//			}
//
//		}) //
//				.when(authenticationLogic).login(any(ClientRequest.class), any(OperationUserSupplier.class));
//		doReturn("bar") //
//				.when(tokenGenerator).generate(anyString());
//
//		// when
//		final ClientAuthenticationResponse response = underTest.create(request, callback);
//
//		// then
//		verify(authenticationLogic).login(eq(request), any(OperationUserSupplier.class));
//		verify(tokenGenerator).generate(eq("foo"));
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).create(captor.capture());
//		verify(callback).sessionCreated(eq("bar"));
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session captured = captor.getValue();
//		assertThat(captured.getIdentifier(), equalTo("bar"));
//		assertThat(captured.getUser(), equalTo(operationUser));
//		assertThat(captured.getImpersonated(), nullValue(OperationUser.class));
//
//		assertThat(response, equalTo(expectedResponse));
//	}
//
//	@Test(expected = DummyException.class)
//	public void unexpectedErrorDuringExistenceCheck() throws Exception {
//		// given
//		doThrow(new DummyException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		// when
//		try {
//			underTest.exists("foo");
//		} finally {
//			final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//			verify(sessionStore).read(captor.capture());
//
//			final Session captured = captor.getValue();
//			assertThat(captured.getIdentifier(), equalTo("foo"));
//		}
//	}
//
//	@Test
//	public void notExistingSession() throws Exception {
//		// given
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		// when
//		final boolean response = underTest.exists("foo");
//
//		// then
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session captured = captor.getValue();
//		assertThat(captured.getIdentifier(), equalTo("foo"));
//
//		assertThat(response, equalTo(false));
//	}
//
//	@Test
//	public void existingSession() throws Exception {
//		// given
//		final Session session = mock(Session.class);
//		doReturn(session) //
//				.when(sessionStore).read(any(Storable.class));
//
//		// when
//		final boolean response = underTest.exists("foo");
//
//		// then
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session captured = captor.getValue();
//		assertThat(captured.getIdentifier(), equalTo("foo"));
//
//		assertThat(response, equalTo(true));
//	}
//
//	@Test(expected = NoSuchElementException.class)
//	public void sessionNotFoundDuringUpdate() throws Exception {
//		// given
//		final LoginDTO login = LoginDTO.newInstance() //
//				.build();
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		try {
//			// when
//			underTest.update("foo", login);
//		} finally {
//			// then
//			final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//			verify(sessionStore).read(captor.capture());
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//
//			final Session captured = captor.getValue();
//			assertThat(captured.getIdentifier(), equalTo("foo"));
//		}
//	}
//
//	@Test(expected = DummyException.class)
//	public void logicThrowsExceptionDuringUpdate() throws Exception {
//		// given
//		final LoginDTO login = LoginDTO.newInstance() //
//				.build();
//		final Session session = mock(Session.class);
//		doReturn(session) //
//				.when(sessionStore).read(any(Storable.class));
//		doThrow(new DummyException()) //
//				.when(authenticationLogic).login(any(LoginDTO.class), any(OperationUserSupplier.class));
//
//		try {
//			// when
//			underTest.update("foo", login);
//		} finally {
//			// then
//			final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//			verify(sessionStore).read(captor.capture());
//			verify(authenticationLogic).login(eq(login), any(OperationUserSupplier.class));
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//
//			final Session captured = captor.getValue();
//			assertThat(captured.getIdentifier(), equalTo("foo"));
//		}
//	}
//
//	@Test
//	public void update() throws Exception {
//		// given
//		final LoginDTO login = LoginDTO.newInstance() //
//				.build();
//
//		final Session session = mock(Session.class);
//		doReturn("bar") //
//				.when(session).getIdentifier();
//		doReturn(session) //
//				.when(sessionStore).read(any(Storable.class));
//
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		doReturn("foo") //
//				.when(authenticatedUser).getUsername();
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		doAnswer((Answer<Response>) (final InvocationOnMock invocation) -> {
//			final OperationUserSupplier userStore = OperationUserSupplier.class.cast(invocation.getArguments()[1]);
//			userStore.setUser(operationUser);
//			return newProxy(Response.class, unsupported("should not be used"));
//		}) //
//				.when(authenticationLogic).login(any(LoginDTO.class), any(OperationUserSupplier.class));
//		doReturn("bar") //
//				.when(tokenGenerator).generate(anyString());
//
//		// when
//		underTest.update("foo", login);
//
//		// then
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(captor.capture());
//		verify(authenticationLogic).login(eq(login), any(OperationUserSupplier.class));
//		verify(sessionStore).update(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session capturedAtRead = captor.getAllValues().get(0);
//		assertThat(capturedAtRead.getIdentifier(), equalTo("foo"));
//
//		final Session capturedAtUpdate = captor.getAllValues().get(1);
//		assertThat(capturedAtUpdate.getIdentifier(), equalTo("bar"));
//		assertThat(capturedAtUpdate.getUser(), equalTo(operationUser));
//		assertThat(capturedAtUpdate.getImpersonated(), nullValue(OperationUser.class));
//	}
//
//	@Test(expected = NoSuchElementException.class)
//	public void sessionNotFoundDuringDelete() throws Exception {
//		// given
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).delete(any(Storable.class));
//
//		try {
//			// when
//			underTest.delete("foo");
//		} finally {
//			// then
//			final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//			verify(sessionStore).delete(captor.capture());
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//
//			final Session captured = captor.getValue();
//			assertThat(captured.getIdentifier(), equalTo("foo"));
//		}
//	}
//
//	@Test
//	public void delete() throws Exception {
//		// when
//		underTest.delete("foo");
//
//		// then
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).delete(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session captured = captor.getValue();
//		assertThat(captured.getIdentifier(), equalTo("foo"));
//	}
//
//	@Test(expected = NoSuchElementException.class)
//	public void sessionNotFoundDuringImpersonate() throws Exception {
//		// given
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		try {
//			// when
//			underTest.impersonate("foo", "user");
//		} finally {
//			// then
//			final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//			verify(sessionStore).read(captor.capture());
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//
//			final Session captured = captor.getValue();
//			assertThat(captured.getIdentifier(), equalTo("foo"));
//		}
//	}
//
//	@Test(expected = IllegalStateException.class)
//	public void cannotImpersonate() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		final Session session = mock(Session.class);
//		doReturn(operationUser) //
//				.when(session).getUser();
//		doReturn(session) //
//				.when(sessionStore).read(any(Storable.class));
//		doReturn(false) //
//				.when(canImpersonate).test(any(OperationUser.class));
//
//		try {
//			// when
//			underTest.impersonate("foo", "user");
//		} finally {
//			// then
//			final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//			verify(sessionStore).read(captor.capture());
//			verify(canImpersonate).test(eq(operationUser));
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//
//			final Session captured = captor.getValue();
//			assertThat(captured.getIdentifier(), equalTo("foo"));
//		}
//	}
//
//	@Test
//	public void impersonate() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		final Session session = mock(Session.class);
//		doReturn("bar") //
//				.when(session).getIdentifier();
//		doReturn(operationUser) //
//				.when(session).getUser();
//		doReturn(session) //
//				.when(sessionStore).read(any(Storable.class));
//		doReturn(true) //
//				.when(canImpersonate).test(any(OperationUser.class));
//		final OperationUser anotherOperationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		doAnswer((Answer<Response>) (final InvocationOnMock invocation) -> {
//			final OperationUserSupplier userStore = OperationUserSupplier.class.cast(invocation.getArguments()[1]);
//			userStore.setUser(anotherOperationUser);
//			return newProxy(Response.class, unsupported("should not be used"));
//		}) //
//				.when(authenticationLogic).login(any(LoginDTO.class), any(OperationUserSupplier.class));
//
//		// when
//		underTest.impersonate("foo", "user");
//
//		// then
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(captor.capture());
//		verify(canImpersonate).test(eq(operationUser));
//		verify(authenticationLogic).login(eq(LoginDTO.newInstance() //
//				.withLoginString("user") //
//				.withNoPasswordRequired() //
//				.build()), any(OperationUserSupplier.class));
//		verify(sessionStore).update(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session capturedAtRead = captor.getAllValues().get(0);
//		assertThat(capturedAtRead.getIdentifier(), equalTo("foo"));
//
//		final Session capturedAtUpdate = captor.getAllValues().get(1);
//		assertThat(capturedAtUpdate.getIdentifier(), equalTo("bar"));
//		assertThat(capturedAtUpdate.getUser(), equalTo(operationUser));
//		assertThat(capturedAtUpdate.getImpersonated(), equalTo(anotherOperationUser));
//	}
//
//	@Test(expected = NoSuchElementException.class)
//	public void sessionNotFoundWhenRollingBackImpersonateProcess() throws Exception {
//		// given
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		try {
//			// when
//			underTest.impersonate("foo", null);
//		} finally {
//			// then
//			final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//			verify(sessionStore).read(captor.capture());
//			verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//					tokenGenerator, canImpersonate);
//
//			final Session captured = captor.getValue();
//			assertThat(captured.getIdentifier(), equalTo("foo"));
//		}
//	}
//
//	@Test
//	public void rollingBackImpersonateProcess() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		doReturn("foo") //
//				.when(authenticatedUser).getUsername();
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		final Session session = mock(Session.class);
//		doReturn("bar") //
//				.when(session).getIdentifier();
//		doReturn(operationUser) //
//				.when(session).getUser();
//		doReturn(session) //
//				.when(sessionStore).read(any(Storable.class));
//
//		// when
//		underTest.impersonate("foo", null);
//
//		// then
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(captor.capture());
//		verify(sessionStore).update(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session capturedAtRead = captor.getAllValues().get(0);
//		assertThat(capturedAtRead.getIdentifier(), equalTo("foo"));
//
//		final Session capturedAtUpdate = captor.getAllValues().get(1);
//		assertThat(capturedAtUpdate.getIdentifier(), equalTo("bar"));
//		assertThat(capturedAtUpdate.getUser(), equalTo(operationUser));
//		assertThat(capturedAtUpdate.getImpersonated(), nullValue(OperationUser.class));
//	}
//
//	@Test
//	public void getCurrent() throws Exception {
//		// given
//		doReturn("foo") //
//				.when(currentSessionStore).get();
//
//		// when
//		final String response = underTest.getCurrent();
//
//		// then
//		verify(currentSessionStore).get();
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		assertThat(response, equalTo("foo"));
//	}
//
//	@Test
//	public void setCurrentToAnonymousWhenSessionIdIsNull() throws Exception {
//		// given
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		// when
//		underTest.setCurrent(null);
//
//		// then
//		verify(currentSessionStore).set(eq(null));
//		verify(currentUserStore).setUser(eq(null));
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//	}
//
//	@Test
//	public void setCurrentToAnonymousWhenSessionIdIsNotFound() throws Exception {
//		// given
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		// when
//		underTest.setCurrent("foo");
//
//		// then
//		verify(currentSessionStore).set(eq("foo"));
//		final ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(sessionCaptor.capture());
//		final ArgumentCaptor<OperationUser> userCaptor = ArgumentCaptor.forClass(OperationUser.class);
//		verify(currentUserStore).setUser(userCaptor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session capturedSession = sessionCaptor.getValue();
//		assertThat(capturedSession.getIdentifier(), equalTo("foo"));
//
//		final OperationUser capturedUser = userCaptor.getValue();
//		assertThat(capturedUser.isValid(), equalTo(false));
//		assertThat(capturedUser.getAuthenticatedUser(), instanceOf(AnonymousUser.class));
//		assertThat(capturedUser.getPrivilegeContext(), instanceOf(NullPrivilegeContext.class));
//		assertThat(capturedUser.getDefaultGroup(), instanceOf(NullGroup.class));
//	}
//
//	@Test
//	public void setCurrent() throws Exception {
//		// given
//		final Session session = mock(Session.class);
//		doReturn(session) //
//				.when(sessionStore).read(any(Storable.class));
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		doReturn(operationUser) //
//				.when(sessionStore).selectUserOrImpersonated(any(Session.class));
//
//		// when
//		underTest.setCurrent("foo");
//
//		// then
//		verify(currentSessionStore).set(eq("foo"));
//		final ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(sessionCaptor.capture());
//		verify(sessionStore).selectUserOrImpersonated(sessionCaptor.capture());
//		final ArgumentCaptor<OperationUser> userCaptor = ArgumentCaptor.forClass(OperationUser.class);
//		verify(currentUserStore).setUser(userCaptor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session capturedAtRead = sessionCaptor.getAllValues().get(0);
//		assertThat(capturedAtRead.getIdentifier(), equalTo("foo"));
//
//		final Session capturedAtSelectUser = sessionCaptor.getAllValues().get(1);
//		assertThat(capturedAtSelectUser, equalTo(session));
//
//		final OperationUser capturedUser = userCaptor.getValue();
//		assertThat(capturedUser, equalTo(operationUser));
//	}
//
//	@Test
//	public void notValidUserWhenSessionIdIsNull() throws Exception {
//		// when
//		final boolean response = underTest.isValidUser(null);
//
//		// then
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		assertThat(response, equalTo(false));
//	}
//
//	@Test
//	public void notValidUserWhenSessionIdIsNotFound() throws Exception {
//		// given
//		doThrow(new NoSuchElementException()) //
//				.when(sessionStore).read(any(Storable.class));
//
//		// when
//		final boolean response = underTest.isValidUser("foo");
//
//		// then
//		final ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(sessionCaptor.capture());
//
//		final Session captured = sessionCaptor.getValue();
//		assertThat(captured.getIdentifier(), equalTo("foo"));
//
//		assertThat(response, equalTo(false));
//	}
//
//	@Test
//	public void notValidUserWhenUserSaysSo() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final Session session = mock(Session.class);
//		doReturn(session) //
//				.when(sessionStore).read(any(Session.class));
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		doReturn(operationUser) //
//				.when(sessionStore).selectUserOrImpersonated(any(Session.class));
//
//		// when
//		final boolean response = underTest.isValidUser("foo");
//
//		// then
//		final ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(sessionCaptor.capture());
//		verify(sessionStore).selectUserOrImpersonated(sessionCaptor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session capturedAtRead = sessionCaptor.getAllValues().get(0);
//		assertThat(capturedAtRead.getIdentifier(), equalTo("foo"));
//
//		final Session capturedAtGetUser = sessionCaptor.getAllValues().get(1);
//		assertThat(capturedAtGetUser, equalTo(session));
//
//		assertThat(response, equalTo(false));
//	}
//
//	@Test
//	public void validUserWhenUserSaysSo() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final Session session = mock(Session.class);
//		doReturn(session) //
//				.when(sessionStore).read(any(Session.class));
//		final CMGroup group = mock(CMGroup.class);
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), group, minimalAccessUser());
//		doReturn(operationUser) //
//				.when(sessionStore).selectUserOrImpersonated(any(Session.class));
//
//		// when
//		final boolean response = underTest.isValidUser("foo");
//
//		// then
//		final ArgumentCaptor<Session> sessionCaptor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).read(sessionCaptor.capture());
//		verify(sessionStore).selectUserOrImpersonated(sessionCaptor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate);
//
//		final Session capturedAtRead = sessionCaptor.getAllValues().get(0);
//		assertThat(capturedAtRead.getIdentifier(), equalTo("foo"));
//
//		final Session capturedAtGetUser = sessionCaptor.getAllValues().get(1);
//		assertThat(capturedAtGetUser, equalTo(session));
//
//		assertThat(response, equalTo(true));
//	}
//
//	@Test
//	public void settingUser() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final PrivilegeContext privilegeContext = mock(PrivilegeContext.class);
//		final CMGroup group = mock(CMGroup.class);
//		final OperationUser operationUser = newOperationUser(authenticatedUser, privilegeContext, group, minimalAccessUser());
//
//		// when
//		underTest.setUser("foo", operationUser); //
//
//		// then
//		final ArgumentCaptor<Session> captor = ArgumentCaptor.forClass(Session.class);
//		verify(sessionStore).update(captor.capture());
//		verifyNoMoreInteractions(authenticationLogic, currentSessionStore, currentUserStore, sessionStore,
//				tokenGenerator, canImpersonate, authenticatedUser, privilegeContext, group);
//
//		final Session captured = captor.getValue();
//		assertThat(captured.getIdentifier(), equalTo("foo"));
//		assertThat(captured.getUser(), equalTo(operationUser));
//	}

}
