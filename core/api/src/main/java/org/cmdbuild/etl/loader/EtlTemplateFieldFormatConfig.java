/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.loader;

import jakarta.annotation.Nullable;
import org.cmdbuild.etl.loader.EtlTemplateDateTimeMode;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;

public interface EtlTemplateFieldFormatConfig {

    @Nullable
    String getClasspath();

    @Nullable
    EtlTemplateDateTimeMode getDateTimeMode();

    @Nullable
    String getDateFormat();

    @Nullable
    String getTimeFormat();

    @Nullable
    String getDateTimeFormat();

    @Nullable
    String getDecimalSeparator();

    @Nullable
    String getThousandsSeparator();

    default boolean hasDateFormat() {
        return isNotBlank(getDateFormat());
    }

    default boolean hasTimeFormat() {
        return isNotBlank(getTimeFormat());
    }

    default boolean hasDateTimeFormat() {
        return isNotBlank(getDateTimeFormat());
    }

    default boolean hasDecimalSeparator() {
        return isNotBlank(getDecimalSeparator());
    }

    default boolean hasThousandsSeparator() {
        return getThousandsSeparator() != null; //allow '' and ' ' here
    }
}
