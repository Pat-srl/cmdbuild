/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.schema;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;

/**
 * Represents a schema item data:
 * <ul>
 * <li>{@link Classe};
 * <li>{@link Process};
 * <li>{@link Domain};
 * <li><i>Dms Model</i> (a special type of {@link Classe});
 * <li>{@link LookupType}.
 * </ul>
 *
 * @author afelice
 */
public class CmSchemaItemData {
    
    protected final String name;

    public CmSchemaItemData(String name) {
        this.name = checkNotNull(name);
    }

    public String getName() {
        return name;
    }   
    
    @Override
    public String toString() {
        return "%s{name =< %s >=}".formatted(getClass().getName(), name);
    }
}
