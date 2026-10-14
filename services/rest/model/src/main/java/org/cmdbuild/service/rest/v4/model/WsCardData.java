/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.Preconditions;
import java.util.Map;
import org.cmdbuild.utils.lang.CmMapUtils;

public class WsCardData {

    private final Map<String, Object> values;

    @JsonCreator
    public WsCardData(Map<String, Object> values) {
        this.values = CmMapUtils.map(Preconditions.checkNotNull(values)).immutable();
    }

    public Map<String, Object> getValues() {
        return values;
    }

}
