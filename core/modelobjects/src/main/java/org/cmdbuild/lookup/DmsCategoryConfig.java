/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.lookup;

import java.util.Set;
import jakarta.annotation.Nullable;

public interface DmsCategoryConfig { //TODO move this to other package/module ??

    final String DMS_MODEL_CLASS = "cm_dms_modelClass",
            DMS_ALLOWED_EXTENSIONS = "cm_dms_allowed_extensions",
            DMS_CHECK_COUNT = "cm_dms_check_count",
            DMS_CHECK_COUNT_NUMBER = "cm_dms_check_count_number",
            ALLOWED_EXTENSIONS_ALL = "*",
            DMS_MAX_FILE_SIZE = "cm_dms_max_file_size";

    Set<String> getDmsAllowedExtensions();
    
    Integer getMaxFileSize();

    @Nullable
    DmsAttachmentCountCheck getDmsCheckCount();

    @Nullable
    Integer getDmsCheckCountNumber();

}
