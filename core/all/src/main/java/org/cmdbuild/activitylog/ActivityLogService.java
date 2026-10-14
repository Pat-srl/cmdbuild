/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.activitylog;

/**
 *
 * @author ataboga
 */
public interface ActivityLogService {

    ActivityLogRecord store(ActivityLogRecord event);
}
