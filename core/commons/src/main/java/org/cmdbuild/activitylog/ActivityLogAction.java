/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.activitylog;

import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;

/**
 *
 * @author ataboga
 */
public enum ActivityLogAction {

    ALA_CARD_CREATE('C'), ALA_CARD_READ('R'), ALA_CARD_UPDATE('U'), ALA_CARD_DELETE('D'),
    ALA_CARDS_LIST('L');

    private final char code;

    ActivityLogAction(char code) {
        this.code = code;
    }

    public char getCode() {
        return code;
    }

    public String getActionName() {
        return format("action_%s", serializeEnum(this));
    }
}
