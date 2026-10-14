/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import java.util.List;
import java.util.Map;
import org.bimserver.emf.IfcModelInterface;
import org.cmdbuild.utils.ifc.inner.IfcMapper;
import org.eclipse.emf.ecore.EClass;

public interface IfcModel extends XpathQuery {

    final String RECORD_SOURCE_ENTRY = "_ifc_record_source_entry";

    IfcModelInterface getModel();

    List<IfcEntry> getEntries(String name);

    Map<String, IfcFeature> getFeatures(String name);

    IfcModelEntriesReport getReport();

    default List<String> getClasses() {
        return getModel().getPackageMetaData().getAllClasses().stream().map(EClass::getName).collect(toImmutableList());
    }

    default List<String> getAvailableClasses() {
        return getReport().getEntries().values().stream().map(IfcModelEntryReport::getName).collect(toImmutableList());
    }

    default EClass getClassModel(String name) {
        return checkNotNull(getModel().getPackageMetaData().getEClass(name), "class not found for name =< %s >", name);
    }

    default List<Map<String, Object>> extractRecords(String xpathSelector, Map<String, String> attributeFromXpathMapping) {
        return new IfcMapper(this).extractRecords(xpathSelector, attributeFromXpathMapping);
    }

}
