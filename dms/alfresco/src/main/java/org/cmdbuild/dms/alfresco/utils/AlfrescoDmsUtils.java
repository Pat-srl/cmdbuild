package org.cmdbuild.dms.alfresco.utils;

import static org.cmdbuild.utils.encode.CmEncodeUtils.decodeString;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class AlfrescoDmsUtils {

    public static String decodeDocumentId(String encodedDocumentId) {
        return decodeString(checkNotBlank(encodedDocumentId, "empty document id")).split(";")[0];
    }

    public static String decodeDocumentVersion(String encodedDocumentId) {
        return decodeString(checkNotBlank(encodedDocumentId, "empty document id")).split(";")[1];
    }
}
