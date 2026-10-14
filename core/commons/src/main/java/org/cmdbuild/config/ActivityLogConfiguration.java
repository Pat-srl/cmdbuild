/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.config;

import java.util.List;
import java.util.Map;
import org.cmdbuild.activitylog.ActivityLogAction;

/**
 *
 * @author ataboga
 */
public interface ActivityLogConfiguration {

    Map<String, List<ActivityLogAction>> getEntries();
}
