/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.json.JsonBean;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonBean(FaultEventImpl.class)
public interface FaultEvent extends FaultEventBase {

    @Nullable
    String getStacktrace();

    Map<String, String> getMeta();

    FaultEvent withMeta(Map<String, String> meta);

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(checkNotBlank(key));
    }

    @JsonIgnore
    default String getUserMessage() {
        return FaultUtils.getUserMessage(this);
    }

    default FaultEvent withMeta(String... meta) {
        return withMeta(map(meta));
    }
}
