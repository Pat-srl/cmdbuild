/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.log;

import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

public interface LoggerConfig {

    String getCategory();

    String getDescription();

    String getLevel();

    @Nullable
    String getModule();//TODO not used yet

    default boolean hasModule() {
        return isNotBlank(getModule());
    }

}
