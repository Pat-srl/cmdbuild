/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.syscommand;

import static com.google.common.base.Objects.equal;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.sked.SkedJobClusterMode;
import static org.cmdbuild.utils.lang.CmConvertUtils.convert;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumOrDefault;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.sked.SkedJobClusterMode.RUN_ON_ALL_NODES;

public interface SysCommand {

    String getAction();

    Map<String, Object> getData();

    @Nullable
    default Object get(String key) {
        return getData().get(key);
    }

    @Nullable
    default <T> T get(String key, Class<T> type) {
        return convert(getData().get(key), type);
    }

    default String getId() {
        return checkNotBlank(get("id", String.class));
    }

    default SkedJobClusterMode getCommandClusterMode() {
        return parseEnumOrDefault(get("cluster_mode", String.class), RUN_ON_ALL_NODES);
    }

    default boolean runOnAllClusterNodes() {
        return equal(RUN_ON_ALL_NODES, getCommandClusterMode());
    }
}
