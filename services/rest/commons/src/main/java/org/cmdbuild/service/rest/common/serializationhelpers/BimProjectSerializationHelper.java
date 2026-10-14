/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.common.serializationhelpers;


import org.cmdbuild.bim.BimProject;
import org.cmdbuild.bim.BimProjectExt;
import org.cmdbuild.utils.lang.CmMapUtils;

import static org.cmdbuild.utils.date.CmDateUtils.toIsoDateTime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * @author ldare
 */
public class BimProjectSerializationHelper {

    public static CmMapUtils.FluentMap<String, Object> serializeProject(BimProject p) {
        return map(
                "_id", p.getId(),
                "parentId", p.getParentId(),
                "name", p.getName(),
                "description", p.getDescription(),
                "lastCheckin", toIsoDateTime(p.getLastCheckin()),
                "projectId", p.getProjectId(),
                "active", p.isActive(),
                "_can_convert", p.getXktFile() == null
        );
    }

    public static Object serializeProjectAndObject(BimProjectExt projectAndObject) {
        return serializeProject(projectAndObject).accept(m -> {
            if (projectAndObject.hasOwner()) {
                m.put(
                        "ownerClass", projectAndObject.getOwner().getClassName(),
                        "ownerCard", projectAndObject.getOwner().getId());
            }
        });
    }
}
