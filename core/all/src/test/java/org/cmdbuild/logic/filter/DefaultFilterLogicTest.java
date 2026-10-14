package org.cmdbuild.logic.filter;

public class DefaultFilterLogicTest { //TODO
//
//	private CardFilterRepository store;
//	private Converter converter;
//	private OperationUserSupplier userStore;
//	private CardFilterServiceImpl defaultFilterLogic;
//
//	private AuthenticatedUser authenticatedUser;
//	private PrivilegeContext privilegeContext;
//	private UserTenantContext userTenantContext;
//
//	@Before
//	public void setUp() throws Exception {
//		store = mock(CardFilterRepository.class);
//		converter = mock(Converter.class);
//		userStore = mock(OperationUserSupplier.class);
//		defaultFilterLogic = new CardFilterServiceImpl() {
//			{
//				this.store = DefaultFilterLogicTest.this.store;
//				this.userStore = DefaultFilterLogicTest.this.userStore;
//				this.converter = DefaultFilterLogicTest.this.converter;
////					init();
//			}
//		};
//
//		authenticatedUser = mock(AuthenticatedUser.class);
//		privilegeContext = mock(PrivilegeContext.class);
//		userTenantContext = mock(UserTenantContext.class);
//		final CMGroup selectedGroup = newProxy(CMGroup.class, unsupported("method not supported"));
//		final OperationUser operationUser = newOperationUser(authenticatedUser, privilegeContext, selectedGroup, userTenantContext);
//		doReturn(operationUser) //
//				.when(userStore).getUser();
//
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void filterCannotBeCreatedWhenNameIsNull() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//
//		// when
//		defaultFilterLogic.create(input);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void filterCannotBeCreatedWhenNameIsEmpty() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		doReturn(EMPTY) //
//				.when(input).getName();
//
//		// when
//		defaultFilterLogic.create(input);
//	}
//
//	@Test(expected = IllegalArgumentException.class)
//	public void filterCannotBeCreatedWhenNameIsBlank() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		doReturn(" ") //
//				.when(input).getName();
//
//		// when
//		defaultFilterLogic.create(input);
//	}
//
//	@Test
//	public void filterCreated() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		doReturn("filter name") //
//				.when(input).getName();
//		final StoreFilter convertedForStore = mock(StoreFilter.class);
//		final CardFilter convertedForOutput = mock(CardFilter.class);
//		final StoreFilter created = mock(StoreFilter.class);
//		doReturn(42L) //
//				.when(store).create(any(StoreFilter.class));
//		doReturn(created) //
//				.when(store).read(anyLong());
//		doReturn(convertedForStore) //
//				.when(converter).logicToStore(any(CardFilter.class));
//		doReturn(convertedForOutput) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//
//		// when
//		final CardFilter output = defaultFilterLogic.create(input);
//
//		// then
//		assertThat(output, equalTo(convertedForOutput));
//
//		verify(converter).logicToStore(eq(input));
//		verify(store).create(eq(convertedForStore));
//		verify(store).read(eq(42L));
//		verify(converter).storeToLogic(eq(created));
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//	}
//
//	@Test
//	public void onlySomeAttributesCanBeChangedDuringUpdate() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		final StoreFilter convertedForStore = CardFilterImpl.newFilter() //
//				.withId(12L) //
//				.withName("foo") //
//				.withDescription("foo description") //
//				.withClassName("foo classname") //
//				.withConfiguration("foo value") //
//				.thatIsShared(true) //
//				.withUserId(34L) //
//				.build();
//		doReturn(convertedForStore) //
//				.when(converter).logicToStore(any(CardFilter.class));
//		final StoreFilter alreadyStored = CardFilterImpl.newFilter() //
//				.withId(56L) //
//				.withName("bar") //
//				.withDescription("bar description") //
//				.withClassName("bar classname") //
//				.withConfiguration("bar value") //
//				.thatIsShared(false) //
//				.withUserId(78L) //
//				.build();
//		doReturn(alreadyStored) //
//				.when(store).read(anyLong());
//
//		// when
//		defaultFilterLogic.update(input);
//
//		// then
//		final ArgumentCaptor<StoreFilter> captor = ArgumentCaptor.forClass(StoreFilter.class);
//		verify(converter).logicToStore(eq(input));
//		verify(store).read(eq(12L));
//		verify(store).update(captor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//
//		final StoreFilter captured = captor.getValue();
//		assertThat(captured.getId(), equalTo(alreadyStored.getId()));
//		assertThat(captured.getName(), equalTo(convertedForStore.getName()));
//		assertThat(captured.getDescription(), equalTo(convertedForStore.getDescription()));
//		assertThat(captured.getClassName(), equalTo(convertedForStore.getClassName()));
//		assertThat(captured.getConfiguration(), equalTo(convertedForStore.getConfiguration()));
//		assertThat(captured.isShared(), equalTo(alreadyStored.isShared()));
//		assertThat(captured.getUserId(), equalTo(alreadyStored.getUserId()));
//	}
//
//	@Test
//	public void filterDeleted() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		final StoreFilter convertedForStore = mock(StoreFilter.class);
//		doReturn(convertedForStore) //
//				.when(converter).logicToStore(any(CardFilter.class));
//
//		// when
//		defaultFilterLogic.delete(input);
//
//		// then
//		verify(converter).logicToStore(eq(input));
//		verify(store).delete(eq(convertedForStore));
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//	}
//
//	@Test
//	public void filtersForCurrentUserAreReaded_SharedAndNonSharedAreReturned() throws Exception {
//		// given
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(new PagedElements<StoreFilter>(asList(first, second), 123)) //
//				.when(store).readNonSharedFilters(anyString(), anyLong(), anyInt(), anyInt());
//		doReturn(empty()) //
//				.when(store).readSharedFilters(anyString(), anyInt(), anyInt());
//		final CardFilter _first = mock(CardFilter.class);
//		final CardFilter _second = mock(CardFilter.class);
//		doReturn(_first).doReturn(_second) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//		doReturn(42L) //
//				.when(authenticatedUser).getId();
//
//		// when
//		final PagedElements<CardFilter> output = defaultFilterLogic.readForCurrentUser("a classname");
//
//		// then
//		assertThat(output.elements(), containsInAnyOrder(_first, _second));
//		assertThat(output.totalSize(), equalTo(0));
//
//		final ArgumentCaptor<StoreFilter> captor = ArgumentCaptor.forClass(StoreFilter.class);
//
//		verify(userStore).getUser();
//		verify(authenticatedUser).getId();
//		verify(store).readNonSharedFilters(eq("a classname"), eq(42L), eq(0), eq(MAX_VALUE));
//		verify(store).readSharedFilters(eq("a classname"), eq(0), eq(MAX_VALUE));
//		verify(converter, times(2)).storeToLogic(captor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//
//		assertThat(captor.getAllValues().get(0), equalTo(first));
//		assertThat(captor.getAllValues().get(1), equalTo(second));
//	}
//
//	@Test
//	public void filtersForCurrentUserAreReaded_OnlySharedFiltersAreReturned_UserHasAdministratorPrivileges()
//			throws Exception {
//		// given
//		doReturn(empty()) //
//				.when(store).readNonSharedFilters(anyString(), anyLong(), anyInt(), anyInt());
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(new PagedElements<StoreFilter>(asList(first, second), 123)) //
//				.when(store).readSharedFilters(anyString(), anyInt(), anyInt());
//		final CardFilter _first = mock(CardFilter.class);
//		final CardFilter _second = mock(CardFilter.class);
//		doReturn(_first).doReturn(_second) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//		doReturn(42L) //
//				.when(authenticatedUser).getId();
//		doReturn(true) //
//				.when(privilegeContext).hasAdministratorPrivileges();
//
//		// when
//		final PagedElements<CardFilter> output = defaultFilterLogic.readForCurrentUser("a classname");
//
//		// then
//		assertThat(output.elements(), containsInAnyOrder(_first, _second));
//		assertThat(output.totalSize(), equalTo(0));
//
//		final ArgumentCaptor<StoreFilter> captor = ArgumentCaptor.forClass(StoreFilter.class);
//
//		verify(userStore).getUser();
//		verify(authenticatedUser).getId();
//		verify(store).readNonSharedFilters(eq("a classname"), eq(42L), eq(0), eq(MAX_VALUE));
//		verify(store).readSharedFilters(eq("a classname"), eq(0), eq(MAX_VALUE));
//		verify(privilegeContext, times(2)).hasAdministratorPrivileges();
//		verify(converter, times(2)).storeToLogic(captor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//
//		assertThat(captor.getAllValues().get(0), equalTo(first));
//		assertThat(captor.getAllValues().get(1), equalTo(second));
//	}
//
//	@Test
//	public void filtersForCurrentUserAreReaded_OnlySharedFiltersAreReturned_UserHasNotAdministratorPrivilegesButReadAccess()
//			throws Exception {
//		// given
//		doReturn(empty()) //
//				.when(store).readNonSharedFilters(anyString(), anyLong(), anyInt(), anyInt());
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(new PagedElements<StoreFilter>(asList(first, second), 123)) //
//				.when(store).readSharedFilters(anyString(), anyInt(), anyInt());
//		final CardFilter _first = mock(CardFilter.class);
//		final CardFilter _second = mock(CardFilter.class);
//		doReturn(_first).doReturn(_second) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//		doReturn(42L) //
//				.when(authenticatedUser).getId();
//		doReturn(false) //
//				.when(privilegeContext).hasAdministratorPrivileges();
//		doReturn(true) //
//				.when(privilegeContext).hasReadAccess(any(StoreFilter.class));
//
//		// when
//		final PagedElements<CardFilter> output = defaultFilterLogic.readForCurrentUser("a classname");
//
//		// then
//		assertThat(output.elements(), containsInAnyOrder(_first, _second));
//		assertThat(output.totalSize(), equalTo(0));
//
//		final ArgumentCaptor<CMPrivilegedObject> privilegeContextCaptor
//				= ArgumentCaptor.forClass(CMPrivilegedObject.class);
//		final ArgumentCaptor<StoreFilter> converterCaptor = ArgumentCaptor.forClass(StoreFilter.class);
//
//		verify(userStore).getUser();
//		verify(authenticatedUser).getId();
//		verify(store).readNonSharedFilters(eq("a classname"), eq(42L), eq(0), eq(MAX_VALUE));
//		verify(store).readSharedFilters(eq("a classname"), eq(0), eq(MAX_VALUE));
//		verify(privilegeContext, times(2)).hasAdministratorPrivileges();
//		verify(privilegeContext, times(2)).hasReadAccess(privilegeContextCaptor.capture());
//		verify(converter, times(2)).storeToLogic(converterCaptor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//
//		assertThat(privilegeContextCaptor.getAllValues().get(0), equalTo(CMPrivilegedObject.class.cast(first)));
//		assertThat(privilegeContextCaptor.getAllValues().get(1), equalTo(CMPrivilegedObject.class.cast(second)));
//
//		assertThat(converterCaptor.getAllValues().get(0), equalTo(first));
//		assertThat(converterCaptor.getAllValues().get(1), equalTo(second));
//	}
//
//	@Test
//	public void allSharedFiltersAreReaded() throws Exception {
//		// given
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(new PagedElements<StoreFilter>(asList(first, second), 42)) //
//				.when(store).readSharedFilters(anyString(), anyInt(), anyInt());
//		final CardFilter _first = mock(CardFilter.class);
//		final CardFilter _second = mock(CardFilter.class);
//		doReturn(_first).doReturn(_second) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//
//		// when
//		final PagedElements<CardFilter> output = defaultFilterLogic.readShared("foo", 123, 456);
//
//		// then
//		assertThat(output.elements(), containsInAnyOrder(_first, _second));
//		assertThat(output.totalSize(), equalTo(42));
//
//		final ArgumentCaptor<StoreFilter> captor = ArgumentCaptor.forClass(StoreFilter.class);
//
//		verify(store).readSharedFilters(eq("foo"), eq(123), eq(456));
//		verify(converter, times(2)).storeToLogic(captor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//
//		assertThat(captor.getAllValues().get(0), equalTo(first));
//		assertThat(captor.getAllValues().get(1), equalTo(second));
//	}
//
//	@Test
//	public void allNonSharedFiltersAreReaded() throws Exception {
//		// given
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(new PagedElements<StoreFilter>(asList(first, second), 42)) //
//				.when(store).readNonSharedFilters(anyString(), anyLong(), anyInt(), anyInt());
//		final CardFilter _first = mock(CardFilter.class);
//		final CardFilter _second = mock(CardFilter.class);
//		doReturn(_first).doReturn(_second) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//
//		// when
//		final PagedElements<CardFilter> output = defaultFilterLogic.readNotShared("a classname", 123, 456);
//
//		// then
//		assertThat(output.elements(), containsInAnyOrder(_first, _second));
//		assertThat(output.totalSize(), equalTo(42));
//
//		final ArgumentCaptor<StoreFilter> captor = ArgumentCaptor.forClass(StoreFilter.class);
//
//		verify(store).readNonSharedFilters(eq("a classname"), isNull(Long.class), eq(123), eq(456));
//		verify(converter, times(2)).storeToLogic(captor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//
//		assertThat(captor.getAllValues().get(0), equalTo(first));
//		assertThat(captor.getAllValues().get(1), equalTo(second));
//	}
//
//	@Test
//	public void allDefaultFiltersAreReadedWithSpecificGroup() throws Exception {
//		// given
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(asList(first, second)) //
//				.when(store).read(anyString(), anyString());
//		final CardFilter _first = mock(CardFilter.class);
//		final CardFilter _second = mock(CardFilter.class);
//		doReturn(_first).doReturn(_second) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//
//		// when
//		final Iterable<CardFilter> output = newArrayList(defaultFilterLogic.getDefaults("a classname", "a group"));
//
//		// then
//		assertThat(size(output), equalTo(2));
//		assertThat(output, containsInAnyOrder(_first, _second));
//
//		final ArgumentCaptor<StoreFilter> captor = ArgumentCaptor.forClass(StoreFilter.class);
//
//		verify(store).read(eq("a classname"), eq("a group"));
//		verify(converter, times(2)).storeToLogic(captor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//
//		assertThat(captor.getAllValues().get(0), equalTo(first));
//		assertThat(captor.getAllValues().get(1), equalTo(second));
//	}
//
//	@Test
//	public void allDefaultFiltersAreReadedWithNoGroup() throws Exception {
//		// given
//		final CMGroup selectedGroup = mock(CMGroup.class);
//		doReturn("a group") //
//				.when(selectedGroup).getName();
//		final OperationUser operationUser = newOperationUser(authenticatedUser, privilegeContext, selectedGroup, userTenantContext);
//		doReturn(operationUser) //
//				.when(userStore).getUser();
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(asList(first, second)) //
//				.when(store).read(anyString(), anyString());
//		final CardFilter _first = mock(CardFilter.class);
//		final CardFilter _second = mock(CardFilter.class);
//		doReturn(_first).doReturn(_second) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//
//		// when
//		final Iterable<CardFilter> output = newArrayList(defaultFilterLogic.getDefaults("a classname", null));
//
//		// then
//		assertThat(size(output), equalTo(2));
//		assertThat(output, containsInAnyOrder(_first, _second));
//
//		final ArgumentCaptor<StoreFilter> captor = ArgumentCaptor.forClass(StoreFilter.class);
//
//		verify(userStore).getUser();
//		verify(selectedGroup).getName();
//		verify(store).read(eq("a classname"), eq("a group"));
//		verify(converter, times(2)).storeToLogic(captor.capture());
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext, selectedGroup);
//
//		assertThat(captor.getAllValues().get(0), equalTo(first));
//		assertThat(captor.getAllValues().get(1), equalTo(second));
//	}
//
//	@Test
//	public void defaultFilterIsSettedForGroups() throws Exception {
//		// given
//		final StoreFilter stored = mock(StoreFilter.class);
//		doReturn(stored) //
//				.when(store).read(anyLong());
//		doReturn(asList("foo", "baz")) //
//				.when(store).joinedGroups(anyLong());
//
//		// when
//		defaultFilterLogic.setDefaultGroups(1L, asList("foo", "bar"));
//
//		// then
//		verify(store).read(eq(1L));
//		verify(store).joinedGroups(eq(1L));
//		verify(store).disjoin(eq("baz"), eq(asList(stored)));
//		verify(store).join(eq("bar"), eq(asList(stored)));
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//	}
//
//	@Test
//	public void defaultGroupIsSettedForFilters() throws Exception {
//		// given
//		doReturn(asList(1L, 3L)) //
//				.when(store).joinedFilters(anyString());
//		final StoreFilter first = mock(StoreFilter.class);
//		final StoreFilter second = mock(StoreFilter.class);
//		doReturn(first).doReturn(second) //
//				.when(store).read(anyLong());
//
//		// when
//		defaultFilterLogic.setDefaultsForGroup("group", asList(1L, 2L));
//
//		// then
//		verify(store).joinedFilters("group");
//		verify(store).read(eq(3L));
//		verify(store).disjoin(eq("group"), eq(asList(first)));
//		verify(store).read(eq(2L));
//		verify(store).join(eq("group"), eq(asList(second)));
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//	}
//
//	@Test
//	public void getGroupsWhichTheSpecifiedFilterIsDefault() throws Exception {
//		// given
//		doReturn(asList("foo", "bar", "baz")) //
//				.when(store).joinedGroups(anyLong());
//
//		// when
//		final Iterable<String> output = defaultFilterLogic.getGroups(42L);
//
//		// then
//		assertThat(output, containsInAnyOrder("foo", "bar", "baz"));
//
//		verify(store).joinedGroups(eq(42L));
//		verifyNoMoreInteractions(store);
//	}
//
//	@Test(expected = NullPointerException.class)
//	public void filterReadWithMissingId() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		doReturn(null) //
//				.when(input).getId();
//
//		// when
//		try {
//			defaultFilterLogic.read(input);
//		} catch (final NullPointerException e) {
//			verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//			throw e;
//		}
//	}
//
//	@Test
//	public void filterReadWhenMissing() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		doReturn(42L) //
//				.when(input).getId();
//		doThrow(NoSuchElementException.class) //
//				.when(store).read(anyLong());
//
//		// when
//		final Optional<CardFilter> output = defaultFilterLogic.read(input);
//
//		// then
//		assertThat(output.isPresent(), equalTo(false));
//
//		verify(store).read(eq(42L));
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//	}
//
//	@Test
//	public void filterRead() throws Exception {
//		// given
//		final CardFilter input = mock(CardFilter.class);
//		doReturn(42L) //
//				.when(input).getId();
//		final StoreFilter read = mock(StoreFilter.class);
//		doReturn(read) //
//				.when(store).read(anyLong());
//		final CardFilter convertedForOutput = mock(CardFilter.class);
//		doReturn(convertedForOutput) //
//				.when(converter).storeToLogic(any(StoreFilter.class));
//
//		// when
//		final Optional<CardFilter> output = defaultFilterLogic.read(input);
//
//		// then
//		assertThat(output.isPresent(), equalTo(true));
//		assertThat(output.get(), equalTo(convertedForOutput));
//
//		verify(store).read(eq(42L));
//		verify(converter).storeToLogic(eq(read));
//		verifyNoMoreInteractions(store, converter, userStore, authenticatedUser, privilegeContext);
//	}

}
