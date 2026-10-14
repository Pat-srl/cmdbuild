/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.api.fluent;

import org.cmdbuild.common.Constants;

public enum WsType {
    BOOLEAN(Constants.Webservices.BOOLEAN_TYPE_NAME), //
    CHAR(Constants.Webservices.CHAR_TYPE_NAME), //
    DATE(Constants.Webservices.DATE_TYPE_NAME), //
    DECIMAL(Constants.Webservices.DECIMAL_TYPE_NAME), //
    DOUBLE(Constants.Webservices.DOUBLE_TYPE_NAME), //
    FOREIGNKEY(Constants.Webservices.FOREIGNKEY_TYPE_NAME), //
    INET(Constants.Webservices.INET_TYPE_NAME), //
    INTEGER(Constants.Webservices.INTEGER_TYPE_NAME), //
    LOOKUP(Constants.Webservices.LOOKUP_TYPE_NAME), //
    REFERENCE(Constants.Webservices.REFERENCE_TYPE_NAME), //
    STRING(Constants.Webservices.STRING_TYPE_NAME), //
    TEXT(Constants.Webservices.TEXT_TYPE_NAME), //
    TIMESTAMP(Constants.Webservices.TIMESTAMP_TYPE_NAME), //
    TIME(Constants.Webservices.TIME_TYPE_NAME), //
    UNKNOWN(Constants.Webservices.UNKNOWN_TYPE_NAME);
    private final String wsTypeName;

    private WsType(String wsTypeName) {
        this.wsTypeName = wsTypeName;
    }

    public static WsType from(String wsTypeName) {
        for (WsType wsType : values()) {
            if (wsType.wsTypeName.equals(wsTypeName)) {
                return wsType;
            }
        }
        return UNKNOWN;
    }

}
