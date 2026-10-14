/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import jakarta.annotation.Nullable;
import org.apache.commons.jxpath.JXPathContext;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.fromDynaBeanValue;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNull;

public interface XpathQuery {

    JXPathContext xpath();

    default IfcEntry queryEntry(String xpath) {
        return getOnlyElement(queryEntries(xpath), null);
    }

    @Nullable
    default Object queryValue(String xpath) {
        return getOnlyElement(query(xpath), null);
    }

    default List<IfcEntry> queryEntries(String xpath) {
        List list = query(xpath);
        list.forEach(e -> checkArgument(e instanceof IfcEntry, "found non-entry element =< %s > for xpath query =< %s >", e, xpath));
        return list;
    }

    default List query(String xpath) {
        return (List) fromDynaBeanValue(xpath().selectNodes(xpath));
    }

    @Nullable
    default String queryString(String xpath) {
        return toStringOrNull(queryValue(xpath));
    }

}
