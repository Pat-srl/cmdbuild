package org.cmdbuild.auth;

public class DefaultPrivilegeContextTest {
//
//	private static class SimplePrivilegedObject implements CMPrivilegedObject {
//
//		private final String privilegeId;
//
//		private SimplePrivilegedObject(final String privilegeId) {
//			this.privilegeId = privilegeId;
//		}
//
//		@Override
//		public String getPrivilegeId() {
//			return privilegeId;
//		}
//	}
//
//	private static final CMPrivilegedObject DUMMY_PRIV_OBJECT = new SimplePrivilegedObject("dummy");
//
//	private static final CMPrivilege IMPLIED = new SimplePrivilege();
//	private static final CMPrivilege IMPLYING = new SimplePrivilege() {
//
//		@Override
//		public boolean implies(final CMPrivilege privilege) {
//			return super.implies(privilege) || privilege == IMPLIED;
//		}
//	};
//
//	private final DefaultPrivilegeContextBuilder builder = PrivilegeContextImpl.newBuilderInstance();
//
//	/*
//	 * Builder tests
//	 */
//
//	@Test(expected = NullPointerException.class)
//	public void nullGlobalPrivilegeCannotBeAdded() {
//		builder.withGlobalPrivilege(null);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void nullPrivilegedObjectCannotBeAdded() {
//		builder.withPrivilege(new SimplePrivilege(), null);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void nullPrivilegeCannotBeAdded() {
//		builder.withPrivilege(null, DUMMY_PRIV_OBJECT);
//	}
//
//	@Test
//	public void globalPrivilegesAreRegisteredOnTheGlobalObject() {
//		// given
//		builder.withGlobalPrivilege(IMPLIED);
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// when
//		final List<Grant> privileges = privilegeCtx.getAllPrivileges();
//
//		// then
//		assertThat(privileges.size(), is(1));
//	}
//
//	@Test
//	public void objectPrivilegesAreRegisteredOnThatObject() {
//		// given
//		builder.withPrivilege(IMPLIED, DUMMY_PRIV_OBJECT);
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// when
//		final List<Grant> privileges = privilegeCtx.getAllPrivileges();
//
//		// then
//		assertThat(privileges.size(), is(1));
//		assertThat(privileges.get(0).name, is(DUMMY_PRIV_OBJECT.getPrivilegeId()));
//	}
//
//	@Test
//	public void samePrivilegeIsNotRegisteredTwice() {
//		// given
//		builder.withGlobalPrivilege(IMPLIED);
//		builder.withGlobalPrivilege(IMPLIED);
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertThat(privilegeCtx.getAllPrivileges().size(), is(1));
//	}
//
//	@Test
//	public void differentPrivilegesAreBothRegistered() {
//		// given
//		builder.withGlobalPrivilege(new SimplePrivilege());
//		builder.withGlobalPrivilege(new SimplePrivilege());
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertThat(privilegeCtx.getAllPrivileges().size(), is(2));
//	}
//
//	@Test
//	public void privilegesAreUntouchedIfAlreadyImplied() {
//		// given
//		builder.withGlobalPrivilege(IMPLYING);
//		builder.withGlobalPrivilege(IMPLIED);
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// when
//		final List<Grant> privileges = privilegeCtx.getAllPrivileges();
//
//		// then
//		assertThat(privileges.size(), is(1));
//		assertThat(privileges.get(0).privilege, is(IMPLYING));
//	}
//
//	@Test
//	public void listOfPrivilegesAreMergedAsSinglePrivileges() {
//		// given
//		final CMPrivilegedObject a = new SimplePrivilegedObject("a");
//		final CMPrivilegedObject b = new SimplePrivilegedObject("b");
//		final CMPrivilegedObject c = new SimplePrivilegedObject("c");
//		final CMPrivilegedObject d = new SimplePrivilegedObject("d");
//		final CMPrivilegedObject e = new SimplePrivilegedObject("e");
//
//		builder.withPrivileges(new ArrayList<Grant>() {
//			{
//				add(new Grant(a.getPrivilegeId(), IMPLIED));
//				add(new Grant(b.getPrivilegeId(), IMPLYING));
//				add(new Grant(c.getPrivilegeId(), new SimplePrivilege()));
//				add(new Grant(d.getPrivilegeId(), new SimplePrivilege()));
//			}
//		});
//		builder.withPrivileges(new ArrayList<Grant>() {
//			{
//				add(new Grant(a.getPrivilegeId(), IMPLYING));
//				add(new Grant(b.getPrivilegeId(), IMPLIED));
//				add(new Grant(c.getPrivilegeId(), new SimplePrivilege()));
//				add(new Grant(e.getPrivilegeId(), new SimplePrivilege()));
//			}
//		});
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertThat(privilegeCtx.getPrivilegesFor(a).size(), is(1));
//		assertThat(privilegeCtx.getPrivilegesFor(b).size(), is(1));
//		assertThat(privilegeCtx.getPrivilegesFor(c).size(), is(2));
//		assertThat(privilegeCtx.getPrivilegesFor(d).size(), is(1));
//		assertThat(privilegeCtx.getPrivilegesFor(e).size(), is(1));
//	}
//
//	/*
//	 * DefaultPrivilegeContext tests
//	 */
//
//	@Test
//	public void ifEmptyItHasNoPrivileges() {
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		assertThat(privilegeCtx.getAllPrivileges().size(), is(0));
//		assertFalse(privilegeCtx.hasAdministratorPrivileges());
//		assertFalse(privilegeCtx.hasDatabaseDesignerPrivileges());
//	}
//
//	@Test
//	public void globalPrivilegesAreAppliedToEveryObject() {
//		// given
//		builder.withGlobalPrivilege(IMPLIED);
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertTrue(privilegeCtx.hasPrivilege(IMPLIED));
//		assertTrue(privilegeCtx.hasPrivilege(IMPLIED, DUMMY_PRIV_OBJECT));
//	}
//
//	@Test
//	public void objectPrivilegesAreNotAppliedGlobally() {
//		// given
//		builder.withPrivilege(IMPLIED, DUMMY_PRIV_OBJECT);
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertFalse(privilegeCtx.hasPrivilege(IMPLIED));
//		assertTrue(privilegeCtx.hasPrivilege(IMPLIED, DUMMY_PRIV_OBJECT));
//	}
//
//	@Test
//	public void withGodPrivilegesYouCanDoEverything() {
//		// given
//		builder.withGlobalPrivilege(AuthorizationConst.GOD);
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertTrue(privilegeCtx.hasPrivilege(AuthorizationConst.ADMINISTRATOR));
//		assertTrue(privilegeCtx.hasPrivilege(AuthorizationConst.DATABASE_DESIGNER));
//		assertTrue(privilegeCtx.hasReadAccess(DUMMY_PRIV_OBJECT));
//		assertTrue(privilegeCtx.hasWriteAccess(DUMMY_PRIV_OBJECT));
//	}
//
//	@Test
//	public void readPrivilegeGrantsReadAccess() {
//		// given
//		builder.withPrivilege(AuthorizationConst.READ, DUMMY_PRIV_OBJECT);
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertTrue(privilegeCtx.hasReadAccess(DUMMY_PRIV_OBJECT));
//		assertFalse(privilegeCtx.hasWriteAccess(DUMMY_PRIV_OBJECT));
//	}
//
//	@Test
//	public void writePrivilegeGrantsReadAndWriteAccess() {
//		// given
//		builder.withPrivilege(AuthorizationConst.WRITE, DUMMY_PRIV_OBJECT);
//
//		// when
//		final PrivilegeContextImpl privilegeCtx = builder.build();
//
//		// then
//		assertTrue(privilegeCtx.hasReadAccess(DUMMY_PRIV_OBJECT));
//		assertTrue(privilegeCtx.hasWriteAccess(DUMMY_PRIV_OBJECT));
//	}
//
//	@Test
//	public void shouldHaveCorrectPrivilegesWhenPrivilegeContextBuiltFromGroups() {
//		// given
//		final CMGroup group = mock(CMGroup.class);
//		final CMPrivilegedObject a = new SimplePrivilegedObject("a");
//		final CMPrivilegedObject b = new SimplePrivilegedObject("b");
//		final List<Grant> privileges = new ArrayList<>();
//		privileges.add(new Grant(a.getPrivilegeId(), AuthorizationConst.READ));
//		privileges.add(new Grant(b.getPrivilegeId(), AuthorizationConst.WRITE));
//		when(group.getAllPrivileges()).thenReturn(privileges);
//		when(group.getName()).thenReturn("myGroup");
////		final DefaultPrivilegeContextFactory ctxFactory = new DefaultPrivilegeContextFactory();
//
//		// when
//		final PrivilegeContext privilegeCtx = PrivilegeContextImpl.newBuilderInstance().withGroups(group).build();
//
//		// then
//		assertTrue(privilegeCtx.hasReadAccess(a));
//		assertFalse(privilegeCtx.hasWriteAccess(a));
//		assertTrue(privilegeCtx.hasReadAccess(b));
//		assertTrue(privilegeCtx.hasWriteAccess(b));
//	}

}
