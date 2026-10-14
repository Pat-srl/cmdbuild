/*
 * CMDBuild has been developed and is managed by Tecnoteca srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.lang;

import java.util.List;
import org.apache.commons.lang3.tuple.Pair;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.isParamError;
import static org.cmdbuild.utils.lang.CmExceptionUtils.parseError;
import static org.cmdbuild.utils.lang.CmExceptionUtils.serializeError;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class CmExceptionUtilsTest {

    @Test
    public void parseErrorTest1() {
        String error = parseError("this is the %s to %s", "error", "format");

        assertTrue(isParamError(error));
        Pair<String, List<String>> serializedError = serializeError(error);

        assertEquals("this is the %s to %s", serializedError.getKey());
        assertEquals(list("error", "format"), serializedError.getValue());
    }

    @Test
    public void parseErrorTest2() {
        String error = parseError("this is the an error");

        assertTrue(isParamError(error));
        Pair<String, List<String>> serializedError = serializeError(error);

        assertEquals("this is the an error", serializedError.getKey());
        assertEquals(list(), serializedError.getValue());
    }
}
