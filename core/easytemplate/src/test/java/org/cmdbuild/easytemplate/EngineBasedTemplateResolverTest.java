package org.cmdbuild.easytemplate;

import com.google.common.base.Function;
import static com.google.common.base.Functions.forMap;
import java.util.HashMap;
import java.util.Map;
import org.cmdbuild.easytemplate.EasytemplateProcessorImpl;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import org.junit.Test;

public class EngineBasedTemplateResolverTest {

    private static Function<String, Object> engineWithParam(final String name, final Object value) {
        final Map<String, Object> map = new HashMap<>();
        map.put(name, value);
        return forMap(map, null);
    }

    @Test
    public void nullStringResolvedAsNull() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder().build();

        // when
        final String value = tr.processExpression(null);

        // then
        assertThat(value, is(nullValue()));
    }

    @Test
    public void resolvesAsNullWhenExpressionEvaluationWasUnsuccessful() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder() //
                .withResolver(engineWithParam("param", "value"), "e1") //
                .build();

        // when
        final String value = tr.processExpression("{e1:missing}");

        // then
//		assertThat(value, equalTo("null"));
        assertThat(value, equalTo(""));
    }

    @Test
    public void multipleEnginesCanBeUsedWithinSameExpression() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder() //
                .withResolver(engineWithParam("stringParam", "string param"), "e1") //
                .withResolver(engineWithParam("integerParam", 42), "e2") //
                .build();

        // when
        final String value = tr.processExpression("{e1:stringParam} -> {e2:integerParam}");

        // then
        assertThat(value, equalTo("string param -> 42"));
    }

    @Test
    public void variableNamesCanContainSpacesAndSpecialCharactersButNotCurlyBraces() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder() //
                .withResolver(engineWithParam("this can BE a (variable]", "42"), "e1") //
                .build();

        // when
        final String value = tr.processExpression("{e1:this can BE a (variable]}");

        // then
        assertThat(value, equalTo("42"));
    }

    @Test
    public void expressionWithNoTemplatesIsNotChanged() throws Exception {
        // given
        final String template = "A simple string";
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder().build();

        // when
        final String value = tr.processExpression(template);

        // then
        assertThat(value, equalTo(template));
    }

    @Test
    public void nonTemplateTextWithinExpressionIsNotChanged() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder() //
                .withResolver(engineWithParam("param", 42), "e1") //
                .build();

        // when
        final String value = tr.processExpression("foo {e1:param} bar");

        // then
        assertThat(value, equalTo("foo 42 bar"));
    }

    @Test
    public void templateCanBeTheVariableOfAnotherTemplate() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder() //
                .withResolver(engineWithParam("value", 42), "e0") //
                .withResolver(engineWithParam("param", "value"), "e1") //
                .build();

        // when
        final String value = tr.processExpression("foo {e0:{e1:param}} bar");

        // then
        assertThat(value, equalTo("foo 42 bar"));
    }

    @Test
    public void backslashesAndDollarSignsCanBeUsed() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder() //
                .withResolver(engineWithParam("contains_backslash", "foo \\ bar"), "e0") //
                .withResolver(engineWithParam("contains_dollar_sign", "baz$"), "e1") //
                .build();

        // when
        final String value = tr.processExpression("{e0:contains_backslash} {e1:contains_dollar_sign}");

        // then
        assertThat(value, equalTo("foo \\ bar baz$"));
    }

    @Test
    public void jsonTemplateResolved() throws Exception {
        // given
        final EasytemplateProcessorImpl tr = EasytemplateProcessorImpl.builder() //
                .withResolver(engineWithParam("foo", "this is the output"), "e0") //
                .build();

        // when
        final String value = tr.processExpression("{\"a key\": \"a value\", \"another key\": \"{e0:foo}\"}");

        // then
        assertThat(value, equalTo("{\"a key\": \"a value\", \"another key\": \"this is the output\"}"));
    }

}
