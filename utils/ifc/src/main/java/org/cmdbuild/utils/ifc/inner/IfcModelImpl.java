/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc.inner;

import com.google.common.base.Preconditions;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.lang3.tuple.Pair;
import org.bimserver.emf.IfcModelInterface;
import org.cmdbuild.utils.ifc.IfcEntry;
import org.cmdbuild.utils.ifc.IfcFeature;
import org.cmdbuild.utils.ifc.IfcModel;
import org.cmdbuild.utils.ifc.IfcModelEntriesReport;
import org.cmdbuild.utils.ifc.IfcModelEntryReport;
import org.cmdbuild.utils.ifc.utils.IfcUtils;
import static org.cmdbuild.utils.ifc.utils.IfcUtils.buildJXPathContext;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.eclipse.emf.ecore.EClass;

public class IfcModelImpl implements IfcModel {

    private final IfcModelInterface model;
    private final Supplier<JXPathContext> xpath;

    public IfcModelImpl(IfcModelInterface model) {
        this.model = Preconditions.checkNotNull(model);
        xpath = Suppliers.memoize(() -> buildJXPathContext(IfcModelImpl.this));
    }

    @Override
    public IfcModelInterface getModel() {
        return model;
    }

    @Override
    public JXPathContext xpath() {
        return xpath.get();
    }

    @Override
    public List<IfcEntry> getEntries(String name) {
        EClass eClass = getClassModel(name);
        return model.getAll(eClass).stream().map(IfcEntryImpl::new).collect(ImmutableList.toImmutableList());
    }

    @Override
    public Map<String, IfcFeature> getFeatures(String name) {
        return IfcUtils.getFeatures(getClassModel(name));
    }

    @Override
    public IfcModelEntriesReport getReport() {
        Map<String, IfcModelEntryReport> entries = getModel().getPackageMetaData().getAllClasses().stream()
                .map((e) -> Pair.of(e, getModel().getAll(e))).filter((l) -> !l.getRight().isEmpty())
                .collect(Collectors.toMap((p) -> p.getLeft().getName(), (p) -> new IfcModelEntryReportImpl(p.getLeft().getName(), (long) p.getRight().size())));
        return new IfcModelEntriesReportImpl(entries);
    }

    @Override
    public String toString() {
        return "IfcModel{model = " + model + '}';//TODO
    }

    private static class IfcModelEntryReportImpl implements IfcModelEntryReport {

        private final String name;
        private final long count;

        public IfcModelEntryReportImpl(String name, Long count) {
            this.name = checkNotBlank(name);
            this.count = count;
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public long getCount() {
            return count;
        }
    }

    private static class IfcModelEntriesReportImpl implements IfcModelEntriesReport {

        private final Map<String, IfcModelEntryReport> entries;

        public IfcModelEntriesReportImpl(Map<String, IfcModelEntryReport> entries) {
            this.entries = checkNotNull(entries);
        }

        @Override
        public Map<String, IfcModelEntryReport> getEntries() {
            return entries;
        }
    }

}
