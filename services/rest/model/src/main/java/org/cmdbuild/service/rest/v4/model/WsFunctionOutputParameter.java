/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName;
import org.cmdbuild.dao.entrytype.attributetype.CardAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.LookupAttributeType;

import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
public class WsFunctionOutputParameter {

    private final String name;
    private final CardAttributeType type;

    public WsFunctionOutputParameter(@JsonProperty("name") String name, @JsonProperty("type") String typeStr, @JsonProperty("fkTarget") String fkTarget, @JsonProperty("lookupType") String lookupType) {
        this.name = checkNotBlank(name);
        AttributeTypeName typeName = AttributeTypeName.valueOf(typeStr.toUpperCase());
        type = switch (typeName) {
            case FOREIGNKEY -> new ForeignKeyAttributeType(fkTarget);
            case LOOKUP -> new LookupAttributeType(lookupType);
            default -> throw unsupported("unsupported param type = %s", typeStr);
        };
    }

    @Override
    public String toString() {
        return "WsFunctionOutputParameter{" + "name=" + name + ", type=" + type + '}';
    }

    public String getName() {
        return this.name;
    }

    public CardAttributeType getType() {
        return this.type;
    }

}
