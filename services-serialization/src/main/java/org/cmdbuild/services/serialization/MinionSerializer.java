/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.serialization;


import org.cmdbuild.minions.Minion;
import org.cmdbuild.utils.lang.CmMapUtils;

import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class MinionSerializer {

    public static CmMapUtils.FluentMap<String, Object> serializeServiceStatus(Minion minion) {
        return map("_id", minion.getId(),
                "name", minion.getName(),
                "description", minion.getDescription(),
                "status", serializeEnum(minion.getStatus()),
                "_is_enabled", minion.isEnabled(),
                "_can_start", minion.canStart(),
                "_can_stop", minion.canStop()
        );
    }
}
