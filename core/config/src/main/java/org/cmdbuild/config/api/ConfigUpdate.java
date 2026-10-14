/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.base.Objects.equal;
import static org.cmdbuild.config.api.ConfigUpdate.ConfigUpdateMode.UM_DELETE;
import static org.cmdbuild.config.api.ConfigUpdate.ConfigUpdateMode.UM_UPDATE;
import org.cmdbuild.config.utils.ConfigUtils;

public interface ConfigUpdate {

    String getKey();

    ConfigUpdateMode getMode();

    default String getNamespace() {
        return ConfigUtils.getNamespace(getKey());
    }

    default boolean hasCmNamespace() {
        return ConfigUtils.hasCmNamespace(getKey());
    }

    default boolean hasCustomNamespace() {
        return !hasCmNamespace();
    }

    default boolean isDelete() {
        return equal(getMode(), UM_DELETE);
    }

    default boolean isUpdate() {
        return equal(getMode(), UM_UPDATE);
    }

    enum ConfigUpdateMode {
        UM_UPDATE, UM_DELETE
    }
}
