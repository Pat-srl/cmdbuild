/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.beans.Card;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;
import org.cmdbuild.modeldiff.core.CmSerializationHelper;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.mapOf;

/**
 * Represents a <i>diff</i>, a changed {@link Card}, on modified <i>data</i>.
 *
 * @author afelice
 */
public class GeneratedDiffData_ChangedCard {

    @JsonProperty(CmSerializationHelper.ATTR_ID_SERIALIZATION)
    public Long id;

    @JsonProperty(CmSerializationHelper.ATTR_IDCLASS_SERIALIZATION)
    public String classeName;

    @JsonProperty(ATTR_DESCRIPTION)
    public String description;

    /**
     * Typically contains synthesized attributes, like <i>document metadata</i>.
     */
    @JsonProperty("added")
    public Map<String, Object> addedAttribs = map();

    @JsonProperty("changed")
    public List<GeneratedDiffData_CardAttribute> changedAttribs = list();

    /**
     * Typically contains synthesized attributes, like <i>document metadata</i>.
     */
    @JsonProperty("removed")
    public Map<String, Object> removedAttribs = map();

    @JsonProperty("unchanged")
    public Map<String, Object> unchangedAttribs = map();

    public Map<String, Object> getNewAttributeValues() {
        return mapOf(String.class, Object.class).accept(m -> {
            changedAttribs.forEach(a -> {
                m.put(a.attribName, a.newValue);
            });
        });
    }

    /**
     * Returns changed plus added (synthesized) attributes.
     *
     * @return
     */
    public Map<String, Object> getAllNewAttributeValues() {
        return map(getNewAttributeValues()).with(addedAttribs);
    }

    public Map<String, Object> getOldAttributeValues() {
        return mapOf(String.class, Object.class).accept(m -> {
            changedAttribs.forEach(a -> {
                m.put(a.attribName, a.oldValue);
            });
        });
    }

    /**
     * Returns changed plus removed (synthesized) attributes.
     *
     * @return
     */
    public Map<String, Object> getAllOldAttributeValues() {
        return mapOf(String.class, Object.class)
                .with(getOldAttributeValues())
                .with(removedAttribs);
    }

    public Map<String, Object> getCumulativeAttributeValues() {
        return mapOf(String.class, Object.class)
                .skipNullValues()
                .with(removedAttribs)
                .with(getNewAttributeValues())
                .with(getOldAttributeValues())
                .with(addedAttribs);
    }
}
