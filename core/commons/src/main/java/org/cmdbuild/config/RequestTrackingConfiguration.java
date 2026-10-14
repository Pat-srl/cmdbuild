/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config;

import static com.google.common.base.Objects.equal;
import jakarta.annotation.Nullable;
import static org.cmdbuild.config.RequestTrackingConfiguration.LogTrackingMode.LTM_NEVER;

public interface RequestTrackingConfiguration {

    boolean isRequestTrackingEnabled();

    boolean includeRequestPayload();

    boolean includeResponsePayload();

    boolean includeTcpDump();
   
    boolean includeHeaders();

    boolean filterPayload();

    int getMaxPayloadLength();

    /**
     * return a regex to be used to set request paths to track (all paths that
     * matches this regex will be tracked, and others will be not); null will
     * track everything.
     *
     * includes will be precessed before excludes (as in: track_this_request =
     * request_is_included and not(request_is_excluded) )
     *
     * default to "^/services"
     *
     * @return
     */
    @Nullable
    String getRegexForPathsToInclude();

    /**
     * return regex for request paths to exclude from tracking (all paths that
     * matches this request will not be tracked).
     *
     * excludes will be precessed after includes (as in: track_this_request =
     * request_is_included and not(request_is_excluded) )
     *
     * default to null	*
     *
     * @return
     */
    @Nullable
    String getRegexForPathsToExclude();

    LogTrackingMode getLogTrackingMode();

    default boolean enableLogTracking() {
        return !equal(LTM_NEVER, getLogTrackingMode());
    }

    default boolean trimPayload() {
        return getMaxPayloadLength() > 0;
    }

    enum LogTrackingMode {
        LTM_NEVER, LTM_ON_ERROR, LTM_ALWAYS
    }

}
