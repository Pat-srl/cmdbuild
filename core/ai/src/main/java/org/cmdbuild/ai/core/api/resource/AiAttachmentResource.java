/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai.core.api.resource;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author ataboga
 */
public record AiAttachmentResource(
        @JsonProperty("_type")
        String typeName,
        @JsonProperty("_id")
        String id,
        @JsonProperty("_documentId")
        String documentId) implements AiResource {

}
