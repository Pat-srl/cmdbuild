/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.annotation.Nullable;
import org.cmdbuild.bim.BimProjectImpl;
import org.cmdbuild.common.beans.CardIdAndClassName;

import static org.apache.commons.lang3.ObjectUtils.firstNonNull;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
public class WsProjectData {

    private final String name, description, importMapping, projectId, ownerClass, ifcFormat;
    private final Boolean active;
    private final Long parentId, ownerCard;

    public WsProjectData(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("importMapping") String importMapping,
            @JsonProperty("projectId") String projectId,
            @JsonProperty("parentId") Long parentId,
            @JsonProperty("ownerClass") String ownerClass,
            @JsonProperty("ifcFormat") String ifcFormat,
            @JsonProperty("ownerCard") Long ownerCard,
            @JsonProperty("active") Boolean active) {
        this.projectId = projectId;
        this.name = checkNotBlank(name);
        this.description = description;
        this.ownerClass = ownerClass;
        this.importMapping = importMapping;
        this.active = firstNonNull(active, true);
        this.parentId = parentId;
        this.ownerCard = ownerCard;
        this.ifcFormat = ifcFormat;
    }

    public BimProjectImpl.BimProjectImplBuilder toBimProject() {
        return BimProjectImpl.builder()
                .withName(name)
                .withDescription(description)
                .withActive(active)
                .withParentId(parentId)
                .withImportMapping(importMapping)
                .withIfcFormat(ifcFormat)
                .withProjectId(projectId);
    }

    @Nullable
    public CardIdAndClassName toOwnerOrNull() {
        if (isNotBlank(ownerClass) && isNotNullAndGtZero(ownerCard)) {
            return card(ownerClass, ownerCard);
        } else {
            return null;
        }

    }
}
