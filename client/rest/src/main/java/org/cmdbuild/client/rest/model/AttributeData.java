/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType;
import org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import org.cmdbuild.dao.entrytype.TextContentSecurity;

public interface AttributeData {

    boolean getActive();

    String getDefaultValue();

    String getDescription();

    String getDomainName();

    String getEditorType();

    String getFilter();

    String getGroup();

    Integer getIndex();

    boolean getInherited();

    Integer getLength();

    String getLookupType();

    Map<String, String> getMetadata();

    String getName();

    Integer getPrecision();

    boolean getRequired();

    Integer getScale();

    boolean getShowInGrid();

    String getTargetClass();

    String getTargetType();

    String getType();

    boolean getUnique();

    AttributePermissionMode getMode();

    String getUnitOfMeasure();

    String getUnitOfMeasureLocation();

    @Nullable
    Boolean getShowThousandsSeparator();

    @Nullable
    Boolean getShowLabel();

    @Nullable
    Boolean getLabelRequired();

    Boolean getPassword();

    default boolean isWritable() {
        return equal(getMode(), AttributePermissionMode.APM_WRITE);
    }

    default boolean isHidden() {
        return equal(getMode(), AttributePermissionMode.APM_HIDDEN);
    }

    String getIpType();

    Integer getClassOrder();

    TextContentSecurity getTextContentSecurity();

    FormulaType getFormulaType();

    String getFormulaCode();

    ShowPassword getShowPassword();
}
