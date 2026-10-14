/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;

import jakarta.annotation.Nullable;
import java.util.List;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.ai.core.api.AiResponse;
import org.cmdbuild.ai.core.api.resource.AiAttachmentResource;
import org.cmdbuild.ai.core.api.resource.AiCardResource;
import org.cmdbuild.ai.core.api.resource.AiResource;
import org.cmdbuild.ai.core.api.resource.AiUrlResource;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dms.DmsService;
import org.cmdbuild.dms.inner.DocumentInfoAndDetail;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElementOrNull;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.applyOrNull;
import static org.cmdbuild.utils.lang.CmPredicatesUtils.equal;
import org.springframework.stereotype.Component;

/**
 * @author ldare
 */
@Component
public class AiSerializationHelper {

    private final DaoService dao;
    private final DmsService dmsService;

    public AiSerializationHelper(DaoService dao, DmsService dmsService) {
        this.dao = dao;
        this.dmsService = dmsService;
    }

    public CmMapUtils.FluentMap<String, Object> serializeAiResponse(AiResponse aiResponse) {
        return map(
                "response", aiResponse.getResponse(),
                "resources", serializeAiResources(aiResponse.getResources()),
                "durationMillis", String.valueOf(aiResponse.getDurationMillis())
        );
    }

    public List<CmMapUtils.FluentMap<String, Object>> serializeAiResources(List<AiResource> aiResources) {
        List<CmMapUtils.FluentMap<String, Object>> resources = list();
        aiResources.forEach(aiResource -> {
            switch (aiResource) {
                case AiCardResource aiCardResource -> {
                    Classe entryType = getClasseFromTable(aiCardResource.typeName());
                    Card card = getCardDescriptionIfExists(entryType, aiCardResource.id());
                    resources.add(map("_id", aiCardResource.id(),
                            "_type", applyOrNull(entryType, Classe::getName),
                            "description", applyOrNull(card, Card::getDescription)
                    ));
                }
                case AiAttachmentResource aiAttachmentResource -> {
                    DocumentInfoAndDetail attachmentInfo = dmsService.getCardAttachmentById(aiAttachmentResource.documentId());
                    resources.add(map("_id", aiAttachmentResource.id(),
                            "_type", getClasseFromTable(aiAttachmentResource.typeName()),
                            "documentId", aiAttachmentResource.documentId(),
                            "fileName", attachmentInfo.getFileName(),
                            "mimeType", attachmentInfo.getMimeType()));
                }
                case AiUrlResource aiUrlResource -> {
                    if (isNotBlank(aiUrlResource.url())) {
                        resources.add(map("label", aiUrlResource.label(),
                                "url", aiUrlResource.url()
                        ));
                    }
                }
                default ->
                    throw unsupported("unsupported AiResource type =< %s >", aiResource.getClass().getName());
            }
        });
        return resources;
    }

    private Classe getClasseFromTable(String table) {
        return dao.getAllClasses().stream()
                .filter(equal(Classe::getName, table))
                .collect(onlyElementOrNull());
    }

    @Nullable
    private Card getCardDescriptionIfExists(@Nullable Classe type, @Nullable String id) {
        return type == null || isBlank(id) ? null : dao.getCardOrNull(type, Long.parseLong(id));
    }
}
