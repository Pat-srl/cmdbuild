/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonCreator;
import jakarta.annotation.Nullable;

import java.util.Map;

import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrEmpty;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

/**
 * @author ldare
 */
public class WsAttachmentData {

    private final String category, fileName;
    private final boolean majorVersion;
    private final Map<String, Object> cardValues;

    @JsonCreator
    public WsAttachmentData(Map<String, Object> values) {
        this.category = toStringOrNull(values.get("category"));
        this.fileName = toStringOrNull(values.get("fileName"));
        this.majorVersion = toBooleanOrDefault(values.get("majorVersion"), true);
        this.cardValues = map(values).withoutKeys("category", "fileName", "majorVersion").immutable();
    }

    @Nullable
    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return toStringOrEmpty(cardValues.get(ATTR_DESCRIPTION));
    }

    @Nullable
    public String getFileName() {
        return fileName;
    }

    public boolean getMajorVersion() {
        return majorVersion;
    }

    public Map<String, Object> getCardValues() {
        return cardValues;
    }

}
