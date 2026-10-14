package org.cmdbuild.dms.core.test;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.apache.commons.io.input.NullInputStream;
import org.cmdbuild.auth.login.AuthenticationException;
//import org.cmdbuild.dms_from_core.DocumentService.Metadata;
//import org.cmdbuild.dms_from_core.DmsServicePrivilegedImpl;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.rules.TemporaryFolder;
//import org.cmdbuild.dms_from_core.DocumentService;
public class PrivilegedDmsLogicTest {
	
//	TODO fix this
//
//	@Rule
//	public TemporaryFolder temporaryFolder = new TemporaryFolder();
//
//	private DmsService delegate;
//	private DmsPrivileges dmsPrivileges;
//	private DmsServicePrivilegedImpl underTest;
//
//	@Before
//	public void setUp() throws Exception {
//		delegate = mock(DmsService.class);
//		dmsPrivileges = mock(DmsPrivileges.class);
//		underTest = new DmsServicePrivilegedImpl(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noReadPrivilegeWhenSearching() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).readable(anyString());
//
//		// when
//		underTest.search("foo", 42L);
//	}
//
//	@Test
//	public void readPrivilegeWhenSearching() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).readable(anyString());
//
//		// when
//		underTest.search("foo", 42L);
//
//		// then
//		verify(dmsPrivileges).readable("foo");
//		verify(delegate).search("foo", 42L);
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noReadPrivilegeWhenSearchingSpecificFile() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).readable(anyString());
//
//		// when
//		underTest.search("foo", 42L, "bar");
//	}
//
//	@Test
//	public void readPrivilegeWhenSearchingSpecificFile() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).readable(anyString());
//
//		// when
//		underTest.search("foo", 42L, "bar");
//
//		// then
//		verify(dmsPrivileges).readable("foo");
//		verify(delegate).search("foo", 42L, "bar");
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noWritePrivilegeWhenCreating() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).writable(anyString());
//		final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
//		final Metadata metadata = mock(Metadata.class);
//
//		// when
//		underTest.create("the author", "foo", 42L, inputStream, "the filename", metadata, true);
//	}
//
//	@Test
//	public void writePrivilegeWhenCreating() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).writable(anyString());
//		final InputStream inputStream = new ByteArrayInputStream(new byte[0]);
//		final Metadata metadata = mock(Metadata.class);
//
//		// when
//		underTest.create("the author", "foo", 42L, inputStream, "the filename", metadata, true);
//
//		// then
//		verify(dmsPrivileges).writable("foo");
//		verify(delegate).create("the author", "foo", 42L, inputStream, "the filename", metadata, true);
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noReadPrivilegeWhenDownloading() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).readable(anyString());
//
//		// when
//		underTest.download("the classname", 42L, "the filename", "the version");
//	}
//
//	@Test
//	public void readPrivilegeWhenDownloading() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).readable(anyString());
//
//		// when
//		underTest.download("the classname", 42L, "the filename", "the version");
//
//		// then
//		verify(dmsPrivileges).readable("the classname");
//		verify(delegate).download("the classname", 42L, "the filename", "the version");
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noWritePrivilegeWhenDeleting() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).writable(anyString());
//
//		// when
//		underTest.delete("foo", 42L, "the filename");
//	}
//
//	@Test
//	public void writePrivilegeWhenDeleting() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).writable(anyString());
//
//		// when
//		underTest.delete("foo", 42L, "the filename");
//
//		// then
//		verify(dmsPrivileges).writable("foo");
//		verify(delegate).delete("foo", 42L, "the filename");
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noWritePrivilegeWhenUpdating() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).writable(anyString());
//		final InputStream inputStream = new NullInputStream(42);
//		final Metadata metadata = mock(Metadata.class);
//
//		// when
//		underTest.update("dummy user", "foo", 42L, inputStream, "the filename", metadata, true);
//	}
//
//	@Test
//	public void writePrivilegeWhenUpdatingMetadata() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).writable(anyString());
//		final InputStream inputStream = new NullInputStream(42);
//		final Metadata metadata = mock(Metadata.class);
//
//		// when
//		underTest.update("dummy user", "foo", 42L, inputStream, "the filename", metadata, true);
//
//		// then
//		verify(dmsPrivileges).writable("foo");
//		verify(delegate).update("dummy user", "foo", 42L, inputStream, "the filename", metadata, true);
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noReadPrivilegeOnSourceWhenCopying() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).readable("foo");
//		doReturn(true).when(dmsPrivileges).writable("bar");
//
//		// when
//		try {
//			underTest.copy("foo", 42L, "the filename", "bar", 24L);
//		} catch (final AuthException e) {
//			// then
//			verify(dmsPrivileges).readable("foo");
//
//			throw e;
//		}
//	}
//
//	@Test(expected = AuthException.class)
//	public void noWritePrivilegeOnDestinationWhenCopying() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).readable("foo");
//		doReturn(false).when(dmsPrivileges).writable("bar");
//
//		// when
//		try {
//			underTest.copy("foo", 42L, "the filename", "bar", 24L);
//		} catch (final AuthException e) {
//			// then
//			verify(dmsPrivileges).readable("foo");
//			verify(dmsPrivileges).writable("bar");
//
//			throw e;
//		}
//	}
//
//	@Test
//	public void readPrivilegeOnSourceAndWritePrivilegeOnDestinationWhenCopying() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).readable("foo");
//		doReturn(true).when(dmsPrivileges).writable("bar");
//
//		// when
//		underTest.copy("foo", 42L, "the filename", "bar", 24L);
//
//		// then
//		verify(dmsPrivileges).readable("foo");
//		verify(dmsPrivileges).writable("bar");
//		verify(delegate).copy("foo", 42L, "the filename", "bar", 24L);
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}
//
//	@Test(expected = AuthException.class)
//	public void noReadPrivilegeOnSourceWhenMoving() throws Exception {
//		// given
//		doReturn(false).when(dmsPrivileges).readable("foo");
//		doReturn(true).when(dmsPrivileges).writable("bar");
//
//		// when
//		try {
//			underTest.move("foo", 42L, "the filename", "bar", 24L);
//		} catch (final AuthException e) {
//			// then
//			verify(dmsPrivileges).readable("foo");
//
//			throw e;
//		}
//	}
//
//	@Test(expected = AuthException.class)
//	public void noWritePrivilegeOnDestinationWhenMoving() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).readable("foo");
//		doReturn(false).when(dmsPrivileges).writable("bar");
//
//		// when
//		try {
//			underTest.move("foo", 42L, "the filename", "bar", 24L);
//		} catch (final AuthException e) {
//			// then
//			verify(dmsPrivileges).readable("foo");
//			verify(dmsPrivileges).writable("bar");
//
//			throw e;
//		}
//	}
//
//	@Test
//	public void readPrivilegeOnSourceAndWritePrivilegeOnDestinationWhenMoving() throws Exception {
//		// given
//		doReturn(true).when(dmsPrivileges).readable("foo");
//		doReturn(true).when(dmsPrivileges).writable("bar");
//
//		// when
//		underTest.move("foo", 42L, "the filename", "bar", 24L);
//
//		// then
//		verify(dmsPrivileges).readable("foo");
//		verify(dmsPrivileges).writable("bar");
//		verify(delegate).move("foo", 42L, "the filename", "bar", 24L);
//		verifyNoMoreInteractions(delegate, dmsPrivileges);
//	}

}
