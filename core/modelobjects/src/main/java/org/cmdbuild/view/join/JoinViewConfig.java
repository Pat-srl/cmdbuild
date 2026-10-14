/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.view.join;

import static com.google.common.base.Objects.equal;
import java.util.List;
import org.cmdbuild.data.filter.CmdbFilter;
import org.cmdbuild.data.filter.CmdbSorter;
import org.cmdbuild.utils.json.JsonBean;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonBean(JoinViewConfigImpl.class)
public interface JoinViewConfig {

    String getMasterClass();

    String getMasterClassAlias();

    CmdbFilter getFilter();

    CmdbSorter getSorter();

    List<JoinElement> getJoinElements();

    List<JoinAttribute> getAttributes();

    List<JoinAttributeGroup> getAttributeGroups();

    JoinViewPrivilegeMode getPrivilegeMode();

    String getIconFont();

    default JoinAttribute getAttribute(String name) {
        checkNotBlank(name);
        return getAttributes().stream().filter(a -> equal(a.getName(), name)).collect(onlyElement("attribute config not found for name =< %s >", name));
    }

}
