package org.cmdbuild.data.store.session;


//TODO default session store is no more; write test for new session service 
public class DefaultSessionStoreTest {
//
//	private Store<Session> delegate;
//	private DefaultSessionStore underTest;
//
//	@Before
//	public void setUp() {
//		underTest = new DefaultSessionStore(delegate);
//	}
//
//	@Test
//	public void noUserReturnedWhenBothAreMissing() throws Exception {
//		// given
//		final Session session = mock(Session.class);
//
//		// when
//		final OperationUser response = underTest.selectUserOrImpersonated(session);
//
//		// then
//		verify(session).getImpersonated();
//		verify(session).getUser();
//		verifyNoMoreInteractions(session);
//
//		assertThat(response, nullValue());
//	}
//
//	@Test
//	public void mainUserReturnedWhenImpersonatedIsMissing() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		final Session session = mock(Session.class);
//		doReturn(operationUser) //
//				.when(session).getUser();
//
//		// when
//		final OperationUser response = underTest.selectUserOrImpersonated(session);
//
//		// then
//		verify(session).getImpersonated();
//		verify(session).getUser();
//		verifyNoMoreInteractions(session);
//
//		assertThat(response, equalTo(operationUser));
//	}
//
//	@Test
//	public void impersonatedUserReturnedIfPresent() throws Exception {
//		// given
//		final AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
//		final OperationUser operationUser = newOperationUser(authenticatedUser, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//		final Session session = mock(Session.class);
//		doReturn(operationUser) //
//				.when(session).getImpersonated();
//
//		// when
//		final OperationUser response = underTest.selectUserOrImpersonated(session);
//
//		// then
//		verify(session).getImpersonated();
//		verifyNoMoreInteractions(session);
//
//		assertThat(response, equalTo(operationUser));
//	}

}
