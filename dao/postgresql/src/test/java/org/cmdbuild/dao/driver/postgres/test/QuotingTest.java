package org.cmdbuild.dao.driver.postgres.test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import static org.cmdbuild.dao.postgres.utils.SqlQueryUtils.quoteSqlIdentifier;

public class QuotingTest {

//	static private final Long USELESS_FUNCTION_ID = 1313l;
//	private AttributeTypeService attributeTypeService;
//	private FunctionCallBuilderService functionCallService;
//
//	@Before
//	public void setUp() {
//
////		attributeTypeService = new AttributeTypeServiceImpl();
//		functionCallService = new FunctionCallBuilderServiceImpl();
//
//	}
    @Test
    public void identStringsAreQuoted() {
        assertThat(quoteSqlIdentifier("xy"), is("xy"));
        assertThat(quoteSqlIdentifier("1xy"), is("\"1xy\""));
        assertThat(quoteSqlIdentifier("x1y"), is("x1y"));
        assertThat(quoteSqlIdentifier("x+y"), is("\"x+y\""));
        assertThat(quoteSqlIdentifier("x'y"), is("\"x'y\""));
        assertThat(quoteSqlIdentifier("x\"y"), is("\"x\"\"y\""));
        assertThat(quoteSqlIdentifier("XY"), is("\"XY\""));
        assertThat(quoteSqlIdentifier("X\"Y"), is("\"X\"\"Y\""));
        assertThat(quoteSqlIdentifier("select"), is("\"select\""));
        assertThat(quoteSqlIdentifier("from"), is("\"from\""));
        assertThat(quoteSqlIdentifier("where"), is("\"where\""));
        assertThat(quoteSqlIdentifier("SELECT"), is("\"SELECT\""));
        assertThat(quoteSqlIdentifier("FROM"), is("\"FROM\""));
        assertThat(quoteSqlIdentifier("WHERE"), is("\"WHERE\""));
    }
//
//	@Test
//	public void functionCallsAreQuoted() {
//		List<Object> params = new ArrayList<>();
//		StoredFunctionImpl func = StoredFunctionImpl.builder().withIdentifier(cmIdentifier("func")).withId(USELESS_FUNCTION_ID).withReturnSet(true).withMetadata(new FunctionMetadataImpl(emptyMap()))
//				.accept((b) -> {
//				})
//				.build();
//		assertThat(entryTypeToQuotedSql(functionCallService.create(func), (Object value) -> {
//			params.add(value);
//		}), is("func()"));
//
//	}

//	@Test
//	public void functionCallsAreQuoted2() {
//		List<Object> params = new ArrayList<>();
//		StoredFunctionImpl func = StoredFunctionImpl.builder().withIdentifier(cmIdentifier("func")).withId(USELESS_FUNCTION_ID).withReturnSet(true).withMetadata(new FunctionMetadataImpl(emptyMap()))
//				.accept((b) -> {
//					b.withInputParameter("i1", new IntegerAttributeType());
//					b.withInputParameter("i2", new StringAttributeType());
//					b.withInputParameter("i3", new IntegerAttributeType());
//				})
//				.build();
//		assertThat(entryTypeToQuotedSql(functionCallService.create(func, 42, "s", "24"), (Object value) -> {
//			params.add(value);
//		}), is("func(?,?,?)"));
//		assertThat(params.get(0), is((Object) 42));
//		assertThat(params.get(1), is((Object) "s"));
//		assertThat(params.get(2), is((Object) 24));
//	}
}
