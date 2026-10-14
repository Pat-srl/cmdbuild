/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static com.google.common.collect.Iterables.getOnlyElement;
import static java.util.Collections.singletonList;
import java.util.List;
import org.cmdbuild.dao.entrytype.Attribute;

public interface AttributeRepository {

    Attribute createAttribute(Attribute attribute);

    List<Attribute> updateAttributes(List<Attribute> attributes);

    void deleteAttribute(Attribute attribute);

    default Attribute updateAttribute(Attribute attribute) {
        return getOnlyElement(updateAttributes(singletonList(attribute)));
    }
}
