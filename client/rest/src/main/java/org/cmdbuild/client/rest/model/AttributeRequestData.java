/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import jakarta.annotation.Nullable;
import java.util.Map;
import org.cmdbuild.dao.entrytype.AttributeMetadata.FormulaType;
import org.cmdbuild.dao.entrytype.AttributeMetadata.ShowPassword;
import org.cmdbuild.dao.entrytype.AttributePermissionMode;
import org.cmdbuild.dao.entrytype.TextContentSecurity;

public interface AttributeRequestData {

    boolean getActive();

    String getDefaultValue();

    String getDescription();

    String getDomainName();

    String getEditorType();

    String getFilter();

    String getGroup();

    Integer getIndex();

    Integer getLength();

    String getLookupType();

    Map<String, String> getMetadata();

    String getName();

    Integer getPrecision();

    boolean getRequired();

    Integer getScale();

    boolean getShowInGrid();

    String getTargetClass();

    String getDirection();

    String getTargetType();

    String getType();

    TextContentSecurity getTextContentSecurity();

    FormulaType getFormulaType();

    String getFormulaCode();

    Boolean getPassword();

    ShowPassword getShowPassword();

    String getDmsCategory();

    boolean getUnique();

    AttributePermissionMode getMode();

    String getIpType();

    Integer getClassOrder();

    boolean isMasterDetail();

    String getMasterDetailDescription();

    String getUnitOfMeasure();

    String getUnitOfMeasureLocation();

    @Nullable
    Boolean getShowThousandsSeparator();

    @Nullable
    Boolean getShowLabel();

    @Nullable
    Boolean getLabelRequired();

    Boolean getTrimEnabled();
}
