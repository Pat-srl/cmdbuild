/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.common.http;

public class HttpConst {

    public final static String CMDBUILD_AUTHORIZATION_HEADER = "CMDBuild-Authorization",
            CMDBUILD_AUTHORIZATION_COOKIE = CMDBUILD_AUTHORIZATION_HEADER,
            CMDBUILD_CLIENT_ID = "CMDBuild-ClientId";
    public final static String CMDBUILD_ACTION_ID_HEADER = "CMDBuild-ActionId";
    public final static String CMDBUILD_REQUEST_ID_HEADER = "CMDBuild-RequestId";
    public final static String CMDBUILD_MAINTENANCE_MODE_PASSTOKEN_HEADER_OR_COOKIE = "CMDBuild-MMpasstoken";

    public final static String MAINTENANCE_MODE_PASSTOKEN_DEFAULT = "cmdbuildwfy";
}
