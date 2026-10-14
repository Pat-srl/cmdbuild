/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.function;

import static java.lang.String.format;
import java.util.Collection;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.dao.entrytype.EntryTypeMetadata;
import org.cmdbuild.dao.entrytype.TextContentSecurity;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface FunctionMetadata extends EntryTypeMetadata {

    static final String CATEGORIES = "cm_categories",
            MASTERTABLE = "cm_mastertable",//TODO check this
            SOURCE = "cm_source",
            TAGS = "cm_tags";

    Collection<StoredFunctionCategory> getCategories();

    Set<String> getTags();

    String getMasterTable();

    @Nullable
    String getSource();

    default boolean isCached() {
        return toBooleanOrDefault(getCustomMetadata().get("cached"), false);//TODO improve this
    }

    default boolean isScheduled() {
        return isNotBlank(getScheduledJobExpr());
    }

    @Nullable
    default String getScheduledJobExpr() {
        return getCustomMetadata().get("scheduled");//TODO improve this
    }

    default boolean isBaseDsp(String attrName) {
        return toBooleanOrDefault(getCustomMetadata().get(format("attribute.%s.basedsp", checkNotBlank(attrName))), true);
    }

    default String getDescription(String attrName) {
        checkNotBlank(attrName);
        return org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull(trimToNull(getCustomMetadata().get(format("attribute.%s.description", attrName))), attrName);
    }

    @Nullable
    default TextContentSecurity getTextContentSecurity(String attrName) {
        return parseEnumOrNull(getCustomMetadata().get(format("attribute.%s.text_content_security", checkNotBlank(attrName))), TextContentSecurity.class);
    }

    @Nullable
    default Integer getAttrScale(String attrName) {
        return toIntegerOrNull(getCustomMetadata().get(format("attribute.%s.scale", checkNotBlank(attrName))));
    }

    @Nullable
    default Integer getAttrPrecision(String attrName) {
        return toIntegerOrNull(getCustomMetadata().get(format("attribute.%s.precision", checkNotBlank(attrName))));
    }

    @Nullable
    default Integer getAttrLength(String attrName) {
        return toIntegerOrNull(getCustomMetadata().get(format("attribute.%s.length", checkNotBlank(attrName))));
    }
}
