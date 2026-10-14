package org.cmdbuild.api.fluent;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;

import org.junit.Before;
import org.junit.Test;

public class LookupQueryTest {

	private FluentApi api;
	private FluentApiExecutor executor;

	@Before
	public void setUp() throws Exception {
		executor = mock(FluentApiExecutor.class);
		api = new ExecutorBasedFluentApi(executor);
	}

	@Test
	public void allLookupsByType() throws Exception {
		// given
		final Lookup first = new ApiLookupImpl(1l);
		final Lookup second = new ApiLookupImpl(2l);
		final Iterable<Lookup> _elements = Arrays.asList(first, second);
		when(executor.fetch(any(QueryAllLookup.class))) //
				.thenReturn(_elements);

		// when
		final QueryAllLookup queryLookup = api.queryLookup("foo");
		final Iterable<Lookup> elements = queryLookup //
				.fetch();

		// then
		verify(executor).fetch(queryLookup);
		assertThat(elements, containsInAnyOrder(first, second));
	}

	@Test
	public void singleLookupByTypeAndId() throws Exception {
		// given
		final Lookup first = new ApiLookupImpl(1l);
		when(executor.fetch(any(QuerySingleLookup.class))) //
				.thenReturn(first);

		// when
		final QuerySingleLookup queryLookup = api.queryLookup("foo") //
				.elementWithId(123);
		final Lookup element = queryLookup //
				.fetch();

		// then
		verify(executor).fetch(queryLookup);
		assertThat(element, equalTo(first));
	}

}
