/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.data;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.core.LookupConfiguration;
import org.cmdbuild.modeldiff.core.LookupValueConfiguration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Represent all model configurations
 * <ul>
 * <li>{@link ClasseConfiguration};
 * <li>{@link ProcessConfiguration};
 * <li>{@link ViewConfiguration};
 * <li>{@link LookupConfiguration};
 * </ul>
 * needed on mobile offline mode. * @author afelice
 */
public class ModelConfiguration {

    public String id;
    public String name;

    public List<ClasseConfiguration> classes = list();
    public List<ProcessConfiguration> processes = list();
    public List<ViewConfiguration> views = list();

    /**
     * (lookupTypeName, {@link LookupConfiguration})
     */
    public Map<String, LookupConfiguration> lookups = map();

    // DMS
    public List<ClasseConfiguration> dmsModels = list();
    public Map<String, LookupConfiguration> dmsCategoryLookups = map();

    @JsonCreator
    public ModelConfiguration(String id, String name) {
        this.id = checkNotBlank(id);
        this.name = checkNotBlank(name);
    }

    @JsonProperty("_id")
    public String getId() {
        return id;
    }

    public void addClasse(ClasseConfiguration classeConf) {
        classes.add(classeConf);
    }

    public void addProcess(ProcessConfiguration processConf) {
        processes.add(processConf);
    }

    public void addView(ViewConfiguration viewConf) {
        views.add(viewConf);
    }

    public void addLookup(String lookupTypeName, List<LookupValue> lookupValues, Map<String, String> valueDescrTranslations, String lookupParentName) {
        checkArgument(!isNullOrEmpty(lookupValues), "empty values found for lookup =< %s >", lookupTypeName);

        // Wrapper object for lookup type and config, not using directly the contained attributes,
        // to avoid "local variable referenced from a lambda expression must be declared as finel" error
        LookupInfo rawLookupInfo = new LookupInfo();
        List<LookupValueConfiguration> lookupValueConfs = list();
        lookupValues.stream().forEach(v -> {
            LookupValueConfiguration curValueConf = buildValueConfFrom(v.getId(), lookupTypeName, v);
            curValueConf.setDescriptionTranslation(valueDescrTranslations.get(v.getCode()));
            lookupValueConfs.add(curValueConf);

            if (!rawLookupInfo.hasType()) {
                rawLookupInfo.setType(v.getType());
            }
        });

        addLookupConfiguration(buildLookupConfiguration(lookupTypeName, rawLookupInfo, lookupValueConfs, lookupParentName));
    }

    public void addDmsModel(ClasseConfiguration dmsModelConf) {
        dmsModels.add(dmsModelConf);
    }

    public void addDmsCategoryLookup(String dmsCategoryLookupTypeName, List<LookupValue> lookupValues, Map<String, String> valueDescrTranslations) {
        checkArgument(!isNullOrEmpty(lookupValues), "empty values found for dms category lookup =< %s >", dmsCategoryLookupTypeName);

        // Wrapper object for lookup type and config, not using directly the contained attributes,
        // to avoid "local variable referenced from a lambda expression must be declared as finel" error
        LookupInfo rawLookupInfo = new LookupInfo();
        List<DmsCategoryConfiguration> lookupValueConfs = list();
        lookupValues.stream().forEach(v -> {
            DmsCategoryConfiguration curValueConf = buildDmsCategoryConfFrom(v.getId(), dmsCategoryLookupTypeName, v);
            curValueConf.setDescriptionTranslation(valueDescrTranslations.get(v.getCode()));
            lookupValueConfs.add(curValueConf);

            if (!rawLookupInfo.hasType()) {
                rawLookupInfo.setType(v.getType());
            }
        });

        addDmsCategoryConfiguration(buildDmsLookupConfiguration(dmsCategoryLookupTypeName, rawLookupInfo, lookupValueConfs));
    }

    private LookupValueConfiguration buildValueConfFrom(long id, String lookupName, LookupValue lookupValue) {
        checkNotBlank(lookupValue.getCode(), "empty code for lookup value in lookup =< %s >".formatted(lookupName));
        checkNotBlank(lookupValue.getDescription(), "empty description for lookup value in lookup =< %s > for code =< %s >".formatted(lookupName, lookupValue.getCode()));
        LookupValueConfiguration result = new LookupValueConfiguration(id, lookupName, lookupValue.getCode(), lookupValue.getDescription());
        result.active = lookupValue.isActive();
        result.isDefault = lookupValue.isDefault();
        result.notes = lookupValue.getNotes();

        result.applyConfig(lookupValue.getConfig());

        return result;
    }

    private void addLookupConfiguration(LookupConfiguration lookupConf) {
        lookups.put(lookupConf.getName(), lookupConf);
    }

    private void addDmsCategoryConfiguration(LookupConfiguration lookupConf) {
        dmsCategoryLookups.put(lookupConf.getName(), lookupConf);
    }

    private LookupConfiguration buildDmsLookupConfiguration(String dmsCategoryLookupTypeName, LookupInfo rawLookupInfo,
            List<? extends LookupValueConfiguration> lookupValueConfs) {
        return buildLookupConfiguration(dmsCategoryLookupTypeName, rawLookupInfo, lookupValueConfs, null);
    }

    private LookupConfiguration buildLookupConfiguration(String lookupName, LookupInfo rawLookupInfo,
            List<? extends LookupValueConfiguration> lookupValueConfs, String lookupParentName) {
        final LookupType rawLookupType = rawLookupInfo.rawLookupType;

        LookupConfiguration lookupConf = new LookupConfiguration(lookupName, lookupValueConfs);
        lookupConf.setAccessType(rawLookupType.getAccessType());
        lookupConf.setSpeciality(rawLookupType.getSpeciality());
        lookupConf.parent = lookupParentName;

        return lookupConf;
    }

    private DmsCategoryConfiguration buildDmsCategoryConfFrom(long id, String lookupName, LookupValue lookupValue) {
        checkNotBlank(lookupValue.getCode(), "empty code for lookup value in dms category =< %s >".formatted(lookupName));
        checkNotBlank(lookupValue.getDescription(), "empty description for lookup value in dms category =< %s > for code =< %s >".formatted(lookupName, lookupValue.getCode()));
        DmsCategoryConfiguration result = new DmsCategoryConfiguration(id, lookupName, lookupValue.getCode(), lookupValue.getDescription());
        result.active = lookupValue.isActive();
        result.isDefault = lookupValue.isDefault();
        result.notes = lookupValue.getNotes();

        result.applyConfig(lookupValue.getConfig());

        return result;
    }

} // end ModelConfiguration class

class LookupInfo {

    LookupType rawLookupType = null;

    void setType(LookupType lookupType) {
        this.rawLookupType = lookupType;
    }

    boolean hasType() {
        return rawLookupType != null;
    }
}
