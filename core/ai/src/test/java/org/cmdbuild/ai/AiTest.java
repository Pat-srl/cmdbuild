/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai;

import java.util.List;
import org.cmdbuild.ai.core.api.AiResponse;
import org.cmdbuild.ai.core.api.resource.AiAttachmentResource;
import org.cmdbuild.ai.core.api.resource.AiCardResource;
import org.cmdbuild.ai.core.api.resource.AiUrlResource;
import static org.cmdbuild.utils.json.CmJsonUtils.prettifyJsonSafe;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author ataboga
 */
public class AiTest {

    @Test
    public void responseTest() {
        AiResponse response = new AiResponse(
                "this is the response",
                List.of(
                        new AiCardResource("InternalEmployee", "1234"),
                        new AiAttachmentResource("ExternalEmployee", "2345", "sd435fdb5bhsirfhb345r4325r"),
                        new AiUrlResource("testpage", "https://www.cmdbuild.org")
                ),
                1000
        );
        String json = prettifyJsonSafe(toJson(response));
        String toCompare = """
                    {
                      "response" : "this is the response",
                      "resources" : [ {
                        "_type" : "InternalEmployee",
                        "_id" : "1234"
                      }, {
                        "_type" : "ExternalEmployee",
                        "_id" : "2345",
                        "_documentId" : "sd435fdb5bhsirfhb345r4325r"
                      }, {
                        "label" : "testpage",
                        "url" : "https://www.cmdbuild.org"
                      } ],
                      "durationMillis" : 1000
                    }
                    """;
        assertEquals(toCompare.trim(), json.trim());
    }
}
