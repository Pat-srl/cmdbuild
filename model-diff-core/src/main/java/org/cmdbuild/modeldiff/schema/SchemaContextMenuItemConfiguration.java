/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

/**
 * Represents all <i>context menu items</i> (used by <i>UI</i>).
 * 
 * <ul>
 * <li>the Javascript component.
 * </ul>
 * 
 * <p>
 * It's not a {@link SchemaItemConfiguration} because it's a bit different: it
 * doesn't work only on map of properties as {@llink ClasseConfiguration}.
 *
 * @author afelice
 */
public class SchemaContextMenuItemConfiguration {

    /** 
     * Components by <code>componentId</code>.
     */ 
    public List<SchemaContextMenuComponentConfiguration> components = list();

    @JsonIgnore
    public Map<String, SchemaContextMenuComponentConfiguration> getComponentsMap() {
        return uniqueIndex(components, SchemaContextMenuComponentConfiguration::getComponentId);
    }
}
