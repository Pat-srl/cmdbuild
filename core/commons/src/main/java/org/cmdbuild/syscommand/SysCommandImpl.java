/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.syscommand;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.emptyMap;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SysCommandImpl implements SysCommand {

    private final String action;
    private final Map<String, Object> params;

    public SysCommandImpl(String action) {
        this(action, emptyMap());
    }

    public SysCommandImpl(String action, Map<String, Object> params) {
        this.action = checkNotBlank(action);
        this.params = map(checkNotNull(params)).immutable();
    }

    @Override
    public String getAction() {
        return action;
    }

    @Override
    public Map<String, Object> getData() {
        return params;
    }

    @Override
    public String toString() {
        return "SysCommand{" + "action=" + action + '}';
    }

}
