/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.google.common.base.Objects.equal;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.etl.loader.EtlFileFormat;
import org.cmdbuild.etl.loader.EtlMergeMode;
import org.cmdbuild.etl.loader.EtlTemplateColumnConfig;
import org.cmdbuild.etl.loader.EtlTemplateFieldFormatConfig;
import org.cmdbuild.etl.loader.EtlTemplateType;
import static org.cmdbuild.etl.loader.EtlTemplateConfig.UpdateAttrOnMissingType.UAOMT_ATTRIBUTE;
import static org.cmdbuild.etl.loader.EtlTemplateConfig.UpdateAttrOnMissingType.UAOMT_SCRIPT;
import static org.cmdbuild.etl.loader.EtlTemplateConfig.UpdateAttrOnMissingType.UAOMT_UNKNOWN;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_CLASS;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_DOMAIN;
import static org.cmdbuild.etl.loader.EtlTemplateTarget.ET_PROCESS;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface EtlTemplateConfig extends EtlTemplateFieldFormatConfig {

    EtlTemplateTarget getTargetType();

    String getTargetName();

    List<EtlTemplateColumnConfig> getColumns();

    EtlMergeMode getMergeMode();

    EtlTemplateType getType();

    EtlFileFormat getFileFormat();

    @Nullable
    String getAttributeNameForUpdateAttrOnMissing();

    @Nullable
    String getAttributeValueForUpdateAttrOnMissing();

    @Nullable
    String getAttributeModeForUpdateAttrOnMissing();

    @Nullable
    String getScriptForUpdateAttrOnMissing();

    @Nullable
    List<String> getAvailableAttrsForScriptOnMissing();

    @Nullable
    String getScriptOnCreated();

    @Nullable
    String getScriptOnModified();

    @Nullable
    String getScriptOnUnmodified();

    CmdbFilter getFilter();

    CmdbFilter getExportFilter();

    CmdbFilter getImportFilter();

    CmdbFilter getReferenceFilter();

    @Nullable
    String getCsvSeparator();

    List<String> getImportKeyAttributes();

    boolean getUseHeader();

    boolean getIgnoreColumnOrder();

    boolean getHandleMissingRecordsOnError();

    EnableCreate getEnableCreate();

    @Nullable
    Integer getHeaderRow();

    @Nullable
    Integer getDataRow();

    @Nullable
    Integer getFirstCol();

    @Nullable
    String getSource();

    @Nullable
    String getErrorTemplate();

    @Nullable
    String getNotificationTemplate();

    @Nullable
    String getErrorAccount();

    @Nullable
    String getNotificationAccount();

    @Nullable
    String getCharset();

    @Nullable
    String getCallback();

    EtlTemplateRecordError getOnRecordError();

    default boolean hasFilter() {
        return !getFilter().isNoop();
    }

    default boolean hasReferenceFilter() {
        return !getReferenceFilter().isNoop();
    }

    default boolean hasFormat(EtlFileFormat format) {
        return equal(getFileFormat(), format);
    }

    @JsonIgnore
    default boolean isExportTemplate() {
        return switch (getType()) {
            case ETT_EXPORT, ETT_IMPORT_EXPORT ->
                true;
            default ->
                false;
        };
    }

    @JsonIgnore
    default boolean isImportTemplate() {
        return switch (getType()) {
            case ETT_IMPORT, ETT_IMPORT_EXPORT ->
                true;
            default ->
                false;
        };
    }

    @JsonIgnore
    default boolean getSkipUnknownColumns() {
        return getIgnoreColumnOrder();
    }

    default EtlTemplateColumnConfig getColumnByAttrName(String name) {
        return getColumns().stream().filter(c -> equal(c.getAttributeName(), name)).collect(onlyElement("column not found for attr name = %s", name));
    }

    default boolean hasColumnWithAttrName(String name) {
        return getColumns().stream().filter(c -> equal(c.getAttributeName(), name)).findAny().isPresent();
    }

    default boolean hasNonExprColumnWithAttrName(String name) {
        return getColumns().stream().filter(c -> equal(c.getAttributeName(), name) && !c.hasExpr()).findAny().isPresent();
    }

    default boolean hasMergeMode(EtlMergeMode mode) {
        return equal(mode, getMergeMode());
    }

    default boolean hasMergeMode(EtlMergeMode... modes) {
        return set(modes).contains(getMergeMode());
    }

    default boolean hasTarget(Classe classe) {
        return isTargetClass() && equal(getTargetName(), classe.getName());
    }

    @JsonIgnore
    default boolean isTargetDomain() {
        return equal(getTargetType(), ET_DOMAIN);
    }

    @JsonIgnore
    default boolean isTargetClass() {
        return equal(getTargetType(), ET_CLASS);
    }

    @JsonIgnore
    default boolean isTargetProcess() {
        return equal(getTargetType(), ET_PROCESS);
    }

    @JsonIgnore
    default UpdateAttrOnMissingType getUpdateAttrOnMissingType() {
        if (isNotBlank(getAttributeNameForUpdateAttrOnMissing())) {
            return UAOMT_ATTRIBUTE;
        } else if (isNotBlank(getScriptForUpdateAttrOnMissing())) {
            return UAOMT_SCRIPT;
        } else {
            return UAOMT_UNKNOWN;
        }
    }

    enum EnableCreate {
        EC_TRUE, EC_FALSE, EC_SKIP
    }

    enum EtlTemplateRecordError {
        ETE_FAIL, ETE_LOG_ERROR, ETE_LOG_WARNING
    }

    enum UpdateAttrOnMissingType {
        UAOMT_ATTRIBUTE, UAOMT_SCRIPT, UAOMT_UNKNOWN
    }
}
