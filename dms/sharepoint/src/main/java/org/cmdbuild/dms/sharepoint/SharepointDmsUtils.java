/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dms.sharepoint;

import org.cmdbuild.dms.sharepoint.config.SharepointConfiguration;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import com.google.common.net.UrlEscapers;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.http.HttpClientUtils.closeQuietly;

public class SharepointDmsUtils {

    public static final String SHAREPOINT_ENTRY_FILE = "file", SHAREPOINT_ENTRY_NAME = "name";

    public static SharepointGraphApiClient buildSharepointClient(SharepointConfiguration config) {
        SharepointGraphApiClient helper = new SharepointGraphApiClientImpl(config);
        try {
            helper.checkOk();
            return helper;
        } catch (RuntimeException ex) {
            closeQuietly(helper);
            throw ex;
        }
    }

    public static boolean isFile(Map<String, Object> entry) {
        return entry.containsKey(SHAREPOINT_ENTRY_FILE);
    }

    public static boolean isFolder(Map<String, Object> entry) {
        return !isFile(entry);//TODO improve this
    }

    public static String escapeUrlPart(String part) {
        return UrlEscapers.urlPathSegmentEscaper().escape(part);
    }

    public static List<String> splitPath(String path) {
        return Splitter.on("/").omitEmptyStrings().splitToList(checkNotNull(path));
    }
}
