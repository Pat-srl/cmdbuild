/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.activitylog;

import java.util.Map;

/**
 *
 * @author ataboga
 */
public interface ActivityLogInfo {

    ActivityLogAction getCode();

    Map<String, Object> getMetadata();
}
