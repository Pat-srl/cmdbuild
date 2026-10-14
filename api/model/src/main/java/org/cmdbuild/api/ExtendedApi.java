/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import org.cmdbuild.api.fluent.FluentApi;
import org.cmdbuild.api.fluent.MailApi;
import org.slf4j.LoggerFactory;

public interface ExtendedApi extends FluentApi, ExtendedApiMethods, MailApi {

    ApiUser getCurrentUser();

    ApiRole getRole(String name);

    SystemApi getSystemApi();

    default SystemApi system() {
        return getSystemApi();
    }

    EtlApi etl();

    UtilsApi utils();

    /**
     * Release of notification APIs resources.
     *
     * @return
     */
    ReleasableNotificationApi notification();

    default String test() {
        LoggerFactory.getLogger(getClass()).info("cmdb api test OK");
        return "cmdb api READY";
    }
}
