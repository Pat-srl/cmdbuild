package org.cmdbuild.serializers.cardEditMode;

public class LogicToPersistence {

	private static final String PERSISTENCE_ALLOW_ALL = "create=true,modify=true,clone=true,remove=true";
//
//	@Test
//	public void defaultCardEditModeToPersistence() throws Exception {
//
//		// given
//		final CardEditMode _allowAll = CardEditMode.ALLOW_ALL;
//
//		// when
//		final String persistenceString = LOGIC_TO_PERSISTENCE.apply(_allowAll);
//
//		// then
//		assertTrue(persistenceString.equals(PERSISTENCE_ALLOW_ALL));
//
//	}
//
//	@Test
//	public void nullElementProducesAllowAllString() throws Exception {
//
//		// given
//		final CardEditMode _allowAll = null;
//
//		// when
//		final String _persistenceString = LOGIC_TO_PERSISTENCE.apply(_allowAll);
//
//		// then
//		assertTrue(_persistenceString.equals(PERSISTENCE_ALLOW_ALL));
//	}
//
//	@Test
//	public void serializeDisableClone() throws Exception {
//
//		// given
//		final CardEditMode allowAll = CardEditMode.newInstance().isCloneAllowed(false).build();
//
//		// when
//		final String _persistenceString = LOGIC_TO_PERSISTENCE.apply(allowAll);
//
//		// then
//		assertTrue(_persistenceString.equals("create=true,modify=true,clone=false,remove=true"));
//	}

}
