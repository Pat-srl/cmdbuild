package org.cmdbuild.services.errors.management;

//import static org.apache.commons.lang3.SystemUtils.LINE_SEPARATOR;
//import static org.hamcrest.Matchers.equalTo;
//import static org.hamcrest.Matchers.instanceOf;
//import static org.junit.Assert.assertThat;
//import static org.mockito.Matchers.any;
//import static org.mockito.Matchers.eq;
//import static org.mockito.Mockito.doReturn;
//import static org.mockito.Mockito.doThrow;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//
//import org.cmdbuild.dao.entry.CMCard;
//import org.cmdbuild.dao.entry.CMCard.CMCardDefinition;
//import org.cmdbuild.dao.entrytype.Classe ;
//import org.cmdbuild.exception.ORMException;
//import org.junit.Before;
//import org.junit.Test;
//import org.cmdbuild.dao.view.DataView;

public class CustomExceptionHandlerDataViewTest {
	
	
//TODO restore these tests
	
	
//	@SuppressWarnings("serial")
//	private static class DummyException extends RuntimeException {
//
//	}
//
//	private DataView delegate;
////	private CustomExceptionHandlerDataView underTest;
//
//	@Before
//	public void setUp() throws Exception {
//		delegate = mock(DataView.class);
//		underTest = new CustomExceptionHandlerDataView(delegate);
//	}
//
//	@Test
//	public void whenCreatingNewCardsExceptionsContainingSpecificMessageAreTranslated() throws Exception {
//		// given
//		final CMCardDefinition cardDefinition = mock(CMCardDefinition.class);
//		doThrow(customMessage("  don't try this at home\t ")) //
//				.when(cardDefinition).save();
//		doReturn(cardDefinition) //
//				.when(delegate).createCardFor(any(CMClass.class));
//		final Classe target = mock(CMClass.class);
//
//		// when
//		final CMCardDefinition newCard = underTest.createCardFor(target);
//		try {
//			newCard.save();
//		} catch (final Exception e) {
//			assertThat(e, instanceOf(ORMException.class));
//			final ORMException ormException = ORMException.class.cast(e);
//			assertThat(ormException.getExceptionParameters().length, equalTo(1));
//			assertThat(ormException.getExceptionParameters()[0], equalTo("don't try this at home"));
//		}
//
//		// then
//		verify(delegate).createCardFor(eq(target));
//	}
//
//	@Test(expected = DummyException.class)
//	public void whenCreatingNewCardsExceptionsNowContainingSpecificMessageAreHandledNormally() throws Exception {
//		// given
//		doThrow(new DummyException()) //
//				.when(delegate).createCardFor(any(CMClass.class));
//		final Classe target = mock(CMClass.class);
//		final CMCardDefinition newCard = underTest.createCardFor(target);
//
//		// when
//		newCard.save();
//	}
//
//	@Test
//	public void whenUpdatingExistingCardsExceptionsContainingSpecificMessageAreTranslated() throws Exception {
//		// given
//		final CMCardDefinition cardDefinition = mock(CMCardDefinition.class);
//		doThrow(customMessage("  don't try this at home\t ")) //
//				.when(cardDefinition).save();
//		doReturn(cardDefinition) //
//				.when(delegate).update(any(CMCard.class));
//		final CMCard target = mock(CMCard.class);
//
//		// when
//		final CMCardDefinition existingCard = underTest.update(target);
//		try {
//			existingCard.save();
//		} catch (final Exception e) {
//			assertThat(e, instanceOf(ORMException.class));
//			final ORMException ormException = ORMException.class.cast(e);
//			assertThat(ormException.getExceptionParameters().length, equalTo(1));
//			assertThat(ormException.getExceptionParameters()[0], equalTo("don't try this at home"));
//		}
//
//		// then
//		verify(delegate).update(eq(target));
//	}
//
//	@Test(expected = DummyException.class)
//	public void whenUpdatingExistingCardsExceptionsNowContainingSpecificMessageAreHandledNormally() throws Exception {
//		// given
//		doThrow(new DummyException()) //
//				.when(delegate).update(any(CMCard.class));
//		final CMCard target = mock(CMCard.class);
//		final CMCardDefinition existingCard = underTest.update(target);
//
//		// when
//		existingCard.save();
//	}
//
//	@Test
//	public void whenDeletingExistingCardsExceptionsContainingSpecificMessageAreTranslated() throws Exception {
//		// given
//		doThrow(customMessage("  don't try this at home\t ")) //
//				.when(delegate).delete(any(CMCard.class));
//		final CMCard target = mock(CMCard.class);
//
//		// when
//		try {
//			underTest.delete(target);
//		} catch (final Exception e) {
//			assertThat(e, instanceOf(ORMException.class));
//			final ORMException ormException = ORMException.class.cast(e);
//			assertThat(ormException.getExceptionParameters().length, equalTo(1));
//			assertThat(ormException.getExceptionParameters()[0], equalTo("don't try this at home"));
//		}
//
//		// then
//		verify(delegate).delete(eq(target));
//	}
//
//	@Test(expected = DummyException.class)
//	public void whenDeletingExistingCardsExceptionsNowContainingSpecificMessageAreHandledNormally() throws Exception {
//		// given
//		doThrow(new DummyException()) //
//				.when(delegate).delete(any(CMCard.class));
//		final CMCard target = mock(CMCard.class);
//
//		// when
//		underTest.delete(target);
//	}
//
//	@Test
//	public void onlyFirstLineOfMessageIsReturned() throws Exception {
//		// given
//		doThrow(customMessage("  don't try this at home\t " + LINE_SEPARATOR + " blah blah blah")) //
//				.when(delegate).delete(any(CMCard.class));
//		final CMCard target = mock(CMCard.class);
//
//		// when
//		try {
//			underTest.delete(target);
//		} catch (final Exception e) {
//			assertThat(e, instanceOf(ORMException.class));
//			final ORMException ormException = ORMException.class.cast(e);
//			assertThat(ormException.getExceptionParameters().length, equalTo(1));
//			assertThat(ormException.getExceptionParameters()[0], equalTo("don't try this at home"));
//		}
//	}
//
//	/*
//	 * Utilities
//	 */
//
//	private static Throwable customMessage(final String message) {
//		return new RuntimeException("   this will be ignored \t" + "CM_CUSTOM_EXCEPTION:" + message);
//	}

}
