/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.fault;

import com.google.common.base.Preconditions;
import org.cmdbuild.utils.lang.CmPreconditions;

public class FaultMessageImpl implements FaultMessage {

    private final FaultLevel level;
    private final String message;
    private final String code;
    private final boolean showUser;

    public FaultMessageImpl(FaultLevel level, String message, String code, boolean showUser) {
        this.level = Preconditions.checkNotNull(level);
        this.message = CmPreconditions.checkNotBlank(message);
        this.code = code;
        this.showUser = showUser;
    }

    @Override
    public FaultLevel getLevel() {
        return level;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public boolean showUser() {
        return showUser;
    }

}
