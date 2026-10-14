/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.etl.loader.EtlTemplateColumnMode;
import static org.cmdbuild.etl.loader.EtlTemplateColumnConfig.AttributeType.AT_RELATION;
import static org.cmdbuild.etl.loader.EtlTemplateColumnMode.ETCM_IGNORE;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface EtlTemplateColumnConfig extends EtlTemplateFieldFormatConfig {

    String getAttributeName();

    @Nullable
    String getColumnName();

    EtlTemplateColumnMode getMode();

    @Nullable
    String getReferenceTemplate();

    @Nullable
    String getDefault();

    @Nullable
    String getExpr();

    ColumnErrorAction getOnErrorAction();

    ColumnOnMissingRefAction getOnMissingRefAction();

    RequiredAttrMode getRequiredAttrMode();

    AttributeType getAttributeType();

    RelationDirection getRelationDirection();

    @JsonIgnore
    default boolean isRelation() {
        return hasType(AT_RELATION);
    }

    @JsonIgnore
    default boolean hasReferenceTemplate() {
        return isNotBlank(getReferenceTemplate());
    }

    default boolean hasType(AttributeType... types) {
        return set(types).contains(getAttributeType());
    }

    default boolean hasMode(EtlTemplateColumnMode mode) {
        return equal(getMode(), checkNotNull(mode));
    }

    @JsonIgnore
    default String getColumnNameOrAttributeName() {
        return firstNotBlank(getColumnName(), getAttributeName());
    }

    @JsonIgnore
    default boolean ignoreColumn() {
        return equal(getMode(), ETCM_IGNORE);
    }

    @JsonIgnore
    default boolean doNotIgnoreColumn() {
        return !ignoreColumn();
    }

    @JsonIgnore
    default boolean hasDefault() {
        return isNotBlank(getDefault());
    }

    @JsonIgnore
    default boolean hasExpr() {
        return isNotBlank(getExpr());
    }

    @JsonIgnore
    default boolean hasColumnName() {
        return isNotBlank(getColumnName());
    }

    default boolean hasRequiredAttrMode(RequiredAttrMode required) {
        return equal(getRequiredAttrMode(), required);
    }

    enum ColumnErrorAction {
        CEA_FAIL, CEA_LOG, CEA_IGNORE
    }

    enum ColumnOnMissingRefAction {
        CMA_ERROR, CMA_CREATE, CMA_IGNORE
    }

    enum RequiredAttrMode {
        RAM_REQUIRED, RAM_AUTO, RAM_NONE
    }

    enum AttributeType {
        AT_DEFAULT, AT_RELATION
    }

}
