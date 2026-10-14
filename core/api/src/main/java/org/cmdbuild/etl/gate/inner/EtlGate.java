/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jakarta.annotation.Nullable;
import static org.cmdbuild.auth.grant.PrivilegeSubject.privilegeId;
import org.cmdbuild.auth.grant.PrivilegeSubjectWithInfo;
import org.cmdbuild.etl.gate.inner.EtlProcessingMode;
import static org.cmdbuild.etl.gate.inner.EtlGateHandler.ETL_HANDLER_CONFIG_TEMPLATES;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_SCRIPT;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmConvertUtils.toListOfStrings;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface EtlGate extends PrivilegeSubjectWithInfo {

    static final String ETL_GATE_CONFIG_SHOW_ON_CLASSES = "showOnClasses", ETL_GATE_KEY = "wy_gate_key";

    @Nullable
    @Override
    @Deprecated
    default Long getId() {
        return null;//TODO
    }

    String getCode();

    @Override
    String getDescription();

    boolean getAllowPublicAccess();

    EtlProcessingMode getProcessingMode();

    Map<String, String> getConfig();

    boolean isEnabled();

    List<EtlGateHandler> getHandlers();

    default boolean hasProcessingMode(EtlProcessingMode mode) {
        return equal(getProcessingMode(), checkNotNull(mode));
    }

    default String getKey() {
        return checkNotBlank(getConfig(ETL_GATE_KEY), "missing etl gate key");
    }

    @Nullable
    default String getConfig(String key) {
        return getConfig().get(checkNotBlank(key));
    }

    @Override
    default String getName() {
        return getCode();
    }

    @Override
    default String getPrivilegeId() {
//        return privilegeId(PS_ETLGATE, getId());
        return privilegeId(PS_ETLGATE, getCode());//TODO check this
    }

    default String getOnlyScript() {
        EtlGateHandler handler = getHandlers().stream().filter(h -> h.isOfType(ETLHT_SCRIPT)).collect(onlyElement("script handler not found for this gate"));
        return handler.getScript();
    }

    default boolean hasSingleHandler() {
        return getHandlers().size() == 1;
    }

    default EtlGateHandler getSingleHandler() {
        return getOnlyElement(getHandlers());
    }

    default String getSingleHandlerType() {
        return getSingleHandler().getType();
    }

    default Set<String> getShowOnClasses() {
        return set(toListOfStrings(getConfig().get(ETL_GATE_CONFIG_SHOW_ON_CLASSES)));
    }

    default Collection<String> getAllTemplates() {
        return set(toListOfStrings(getConfig().get(ETL_HANDLER_CONFIG_TEMPLATES))).accept(s -> getHandlers().stream().flatMap(h -> h.getTemplates().stream()).forEach(s::add));
    }

    default boolean hasSingleHandlerOfType(String... types) {
        return hasSingleHandler() && set(types).contains(getSingleHandlerType());
    }

}
