package org.cmdbuild.auth;

public class OperationUserTest {

//	private AuthenticatedUser mockAuthUser;
//	private PrivilegeContext mockPrivilegeCtx;
//	private CMGroup mockGroup;
//	private UserTenantContext mockUserTenantContext;
//	private static final AuthenticatedUser ANONYMOUS_USER = new AnonymousUser();
//	final SerializablePrivilege po1;
//	private final CMGroup g1;
//	private final CMGroup g2;
//
////	public OperationUserTest() {
////		po1 = new SerializablePrivilege() {
////
////			@Override
////			public String getPrivilegeId() {
////				return "pid";
////			}
////
////			@Override
////			public Long getId() {
////				return new Long(0);
////			}
////
////			@Override
////			public String getName() {
////				return "";
////			}
////
////			@Override
////			public String getDescription() {
////				return "";
////			}
////
////		};
////
////		g1 = GroupImpl.newInstance().withName("g1") //
////				.withPrivilege(new GrantImpl(po1, PrivilegedObjectType.CLASS, AuthorizationConst.READ)) //
////				.withPrivilege(new GrantImpl(new SimplePrivilege())) //
////				.build();
////
////		g2 = GroupImpl.newInstance().withName("g2") //
////				.withPrivilege(new GrantImpl(po1, PrivilegedObjectType.CLASS, AuthorizationConst.WRITE)) //
////				.withPrivilege(new GrantImpl(new SimplePrivilege())) //
////				.build();
////	}
//
//	@Before
//	public void setUp() {
//		mockAuthUser = mock(AuthenticatedUser.class);
//		mockPrivilegeCtx = mock(PrivilegeContext.class);
//		mockGroup = mock(CMGroup.class);
//		mockUserTenantContext = mock(UserTenantContext.class);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void shouldFailCreationIfAuthenticatedUserIsNull() {
//		final OperationUser operationUser = newOperationUser(null, nullPrivilegeContext(), new NullGroup(), minimalAccessUser());
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void shouldFailCreationIfPrivilegeContextIsNull() {
//		final OperationUser operationUser = newOperationUser(ANONYMOUS_USER, null, new NullGroup(), minimalAccessUser());
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void shouldFailCreationIfSelectedGroupIsNull() {
//		final OperationUser operationUser = newOperationUser(ANONYMOUS_USER, nullPrivilegeContext(), null, minimalAccessUser());
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void shouldFailCreationIfUserTenantContextIsNull() {
//		final OperationUser operationUser = newOperationUser(ANONYMOUS_USER, nullPrivilegeContext(), new NullGroup(), null);
//	}
//
//	/*
//	 * CMSecurityManager wrap
//	 */
//	@Test
//	public void forwardsCallsToThePrivilegeContext() {
//		// given
//		final CMPrivilege p = new SimplePrivilege();
//		final OperationUser operationUser = newOperationUser(mockAuthUser, mockPrivilegeCtx, mockGroup, mockUserTenantContext);
//
//		// when
//		operationUser.hasReadAccess(po1);
//		operationUser.hasWriteAccess(po1);
//		operationUser.hasDatabaseDesignerPrivileges();
//		operationUser.hasAdministratorPrivileges();
//		operationUser.hasPrivilege(p);
//		operationUser.hasPrivilege(p, po1);
//
//		// then
//		verify(mockPrivilegeCtx, times(1)).hasReadAccess(po1);
//		verify(mockPrivilegeCtx, times(1)).hasWriteAccess(po1);
//		verify(mockPrivilegeCtx, times(1)).hasDatabaseDesignerPrivileges();
//		verify(mockPrivilegeCtx, times(1)).hasAdministratorPrivileges();
//		verify(mockPrivilegeCtx, times(1)).hasPrivilege(p, po1);
//	}
//
////	/*
////	 * Preferred group
////	 */
////	@Test
////	public void allowsSelectingANullGroup() {
////		// given
////		final OperationUser operationUser = newOperationUser(mockAuthUser, mockPrivilegeCtx, mockGroup, mockUserTenantContext);
////
////		// when
////		operationUser.selectGroup(null);
////
////		// then
////		assertThat(operationUser.getDefaultGroup(), is(nullValue()));
////	}
////
////	@Test
////	public void canSelectAnExistingGroup() {
////		// given
////		when(mockAuthUser.getGroupNames()).thenReturn(groupSet(g1.getName(), g2.getName()));
////		final OperationUser operationUser = newOperationUser(mockAuthUser, mockPrivilegeCtx, mockGroup, mockUserTenantContext);
////
////		// when
////		operationUser.selectGroup(g1);
////
////		// then
////		assertThat(operationUser.getDefaultGroup(), is(g1));
////		assertThat(operationUser.getDefaultGroup().getName(), is(g1.getName()));
////	}
//
//	@Test
//	public void aSingleGroupIsAutomaticallySelected() {
//		// given
//		final OperationUser operationUser = newOperationUser(mockAuthUser, mockPrivilegeCtx, mockGroup, mockUserTenantContext);
//
//		// then
//		assertThat(operationUser.getDefaultGroup(), is(mockGroup));
//	}
//
//	/*
//	 * Utility methods
//	 */
//	private Set<String> groupSet(final String... groupNames) {
//		final Set<String> groups = new HashSet<String>();
//		for (final String g : groupNames) {
//			groups.add(g);
//		}
//		return groups;
//	}
}
