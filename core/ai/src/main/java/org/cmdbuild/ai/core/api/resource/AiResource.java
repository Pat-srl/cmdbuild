/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.ai.core.api.resource;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 *
 * @author ataboga
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION)
@JsonSubTypes({
    @JsonSubTypes.Type(AiCardResource.class),
    @JsonSubTypes.Type(AiAttachmentResource.class),
    @JsonSubTypes.Type(AiUrlResource.class)
})
public sealed interface AiResource permits AiCardResource, AiAttachmentResource, AiUrlResource {

    public default <A extends AiResourceType> A as(Class<A> classe) {
        return classe.cast(this);
    }
}
