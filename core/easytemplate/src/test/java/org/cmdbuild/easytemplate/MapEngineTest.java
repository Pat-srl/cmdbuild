package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import static com.google.common.base.Functions.forMap;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Maps;
import java.util.Map;

public class MapEngineTest {

	private Map<String, Object> parameterMap;
	private Function<String, Object> engine;

	@Before
	public void setUp() throws Exception {
		parameterMap = Maps.newHashMap();
		engine = forMap(parameterMap, null);
	}

	@Test
	public void evaluatesToNullIfParameterNotPresent() {
		// when
		final Object value = engine.apply("Any Name");

		// then
		assertThat(value, is(nullValue()));
	}

	@Test
	public void evaluatesValuesAsTheyWerePut() {
		// given
		final String aString = "A string";
		final Integer anInteger = Integer.valueOf(42);
		final Long aLong = Long.valueOf(123456789L);
		final Object anObject = new Object();
		parameterMap.put("string", aString);
		parameterMap.put("integer", anInteger);
		parameterMap.put("long", aLong);
		parameterMap.put("object", anObject);

		// when
		final Object stringValue = engine.apply("string");
		final Object integerValue = engine.apply("integer");
		final Object longValue = engine.apply("long");
		final Object objectValue = engine.apply("object");

		// then
		assertThat(stringValue, equalTo((Object) aString));
		assertThat(integerValue, equalTo((Object) anInteger));
		assertThat(longValue, equalTo((Object) aLong));
		assertThat(objectValue, equalTo(anObject));
	}

}
