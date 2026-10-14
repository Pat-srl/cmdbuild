/*
 * CMDBuild has been developed and is managed by PAT srl
 You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.core.CmModelDiffErrors;
import org.cmdbuild.modeldiff.core.LookupInfo;
import static org.cmdbuild.modeldiff.diff.schema.CmSchemaHelper.toMapByName;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Represent all model configurations
 * <ul>
 * <li>{@link ClasseConfiguration};
 * <li>{@link ProcessConfiguration};
 * <li>{@link DomainConfiguration};
 * <li>{@link SchemaLookupConfiguration};
 * </ul>
 * needed on mobile offline mode.
 *
 * @author afelice
 */
public class SchemaConfiguration {

    @JsonProperty("_id")
    public final String id;
    public final String name;
    
    public List<ClasseConfiguration> classes = list();
    public List<ProcessConfiguration> processes = list();
    public List<DomainConfiguration> domains = list();

    public String xpdlZipFile = null;

    public List<String> xpdlFilenames = list();
    public String modelDiffSchemaVersion = null;
    
    /**
     * (lookupTypeName, {@link SchemaLookupConfiguration})
     */
    @JsonIgnore
    public Map<String, SchemaLookupConfiguration> internalLookups = map();

    // DMS
    public List<ClasseConfiguration> dmsModels = list();
    public Map<String, DmsCategoryTypeConfiguration> dmsCategoryLookups = map();

    /** 
     * JS Components for context menu items
     * 
     * <p>Components by <code>componentId</code>.
     */
    public SchemaContextMenuItemConfiguration contextMenuItems = new SchemaContextMenuItemConfiguration();
    
    @JsonIgnore // Needed to ignore when reading json serialization (so write-only): @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) doesn't work
    public CmModelDiffErrors errors = new CmModelDiffErrors("collectSchema errors");    
    
    @JsonCreator
    public SchemaConfiguration(@JsonProperty("_id") String id, @JsonProperty("name") String name) {
        this.id = checkNotBlank(id);
        this.name = checkNotBlank(name);
    }

    @JsonProperty("lookups")
    public List<SchemaLookupConfiguration> getLookups() {
        return list(internalLookups.values());
    }

    @JsonProperty("lookups")
    public void setLookups(List<SchemaLookupConfiguration> lookups) {
        this.internalLookups = toMapByName(lookups.stream(), SchemaLookupConfiguration::getName);
    }

    public void addClasse(ClasseConfiguration classeConf) {
        classes.add(classeConf);
    }

    @JsonIgnore
    public List<String> getClasseNames() {
        return classes.stream().map(cc -> cc.name).collect(toList());
    }

    @JsonProperty("errors") // Needed to ignore when reading json serialization (so write-only): @JsonProperty(access = JsonProperty.Access.WRITE_ONLY) doesn't work
    public CmModelDiffErrors getErrors() {
        return errors;
    }
    
    public void addProcess(ProcessConfiguration processConf) {
        processes.add(processConf);
    }

    @JsonIgnore
    public List<String> getProcessNames() {
        return processes.stream().map(cc -> cc.name).collect(toList());
    }

    @JsonIgnore
    public List<String> getDmsModelNames() {
        return dmsModels.stream().map(cc -> cc.name).collect(toList());
    }

    public void addDomain(DomainConfiguration domainConf) {
        domains.add(domainConf);
    }

    public void addLookup(String lookupTypeName, List<LookupValue> lookupValues, Map<String, String> valueDescrTranslations,
            String lookupParentName) {
        checkArgument(!isNullOrEmpty(lookupValues), "empty values found for lookup =< %s >", lookupTypeName);
        
        // Wrapper object for lookup type and config, not using directly the contained attributes,
        // to avoid "local variable referenced from a lambda expression must be declared as finel" error
        LookupInfo rawLookupInfo = new LookupInfo(); 
        List<SchemaLookupValueConfiguration> lookupValueConfs = list();
        lookupValues.stream().forEach(v -> {
            SchemaLookupValueConfiguration curValueConf = buildValueConfFrom(v.getId(), lookupTypeName, v);
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
    
    public void addDmsCategoryLookup(String dmsCategoryTypeName, List<LookupValue> lookupValues, Map<String, String> valueDescrTranslations) {
        checkArgument(!isNullOrEmpty(lookupValues), "empty values found for dms category LookupType =< %s >", dmsCategoryTypeName);
        
        // Wrapper object for lookup type and config, not using directly the contained attributes,
        // to avoid "local variable referenced from a lambda expression must be declared as finel" error
        LookupInfo rawLookupInfo = new LookupInfo(); 
        List<DmsCategoryConfiguration> lookupValueConfs = list();
        lookupValues.stream().forEach(v -> {
            DmsCategoryConfiguration curValueConf = buildDmsCategoryConfFrom(v.getId(), dmsCategoryTypeName, v);
            curValueConf.setDescriptionTranslation(valueDescrTranslations.get(v.getCode()));
            lookupValueConfs.add(curValueConf);
            
            if (!rawLookupInfo.hasType()) {
                rawLookupInfo.setType(v.getType());
            }
        });
        
        addDmsCategoryConfiguration(buildDmsCategoryTypeConfiguration(dmsCategoryTypeName, rawLookupInfo, lookupValueConfs));
    }    
    
    private SchemaLookupValueConfiguration buildValueConfFrom(long id, String lookupName, LookupValue lookupValue) {
        checkNotBlank(lookupValue.getCode(), "empty code for lookup value in lookup =< %s >".formatted(lookupName));
        checkNotBlank(lookupValue.getDescription(), "empty description for lookup value in lookup =< %s > for code =< %s >".formatted(lookupName, lookupValue.getCode()));
        SchemaLookupValueConfiguration result = new SchemaLookupValueConfiguration(id, lookupName, lookupValue.getCode(), lookupValue.getDescription());
        result.active = lookupValue.isActive();
        result.isDefault = lookupValue.isDefault();
        result.notes = lookupValue.getNotes();
        
        result.applyConfig(lookupValue.getConfig());
        
        return result;
    }
    
    private void addLookupConfiguration(SchemaLookupConfiguration lookupConf) {
        internalLookups.put(lookupConf.getName(), lookupConf);
    }

    private void addDmsCategoryConfiguration(DmsCategoryTypeConfiguration lookupConf) {
        dmsCategoryLookups.put(lookupConf.getName(), lookupConf);
    }

    // @todo AFE TBC
    private SchemaLookupConfiguration buildSchemaLookupConfiguration(String schemaLookupTypeName, LookupInfo rawLookupInfo,
            List<? extends SchemaLookupValueConfiguration> lookupValueConfs) {
        return buildLookupConfiguration(schemaLookupTypeName, rawLookupInfo, lookupValueConfs, null);
    }

    private SchemaLookupConfiguration buildLookupConfiguration(String lookupName, LookupInfo rawLookupInfo, 
            List<? extends SchemaLookupValueConfiguration> lookupValueConfs, String lookupParentName) {
        final LookupType rawLookupType = rawLookupInfo.getType();
        
        SchemaLookupConfiguration lookupConf = new SchemaLookupConfiguration(lookupName, lookupValueConfs);
        lookupConf.setAccessType(rawLookupType.getAccessType());
        lookupConf.setSpeciality(rawLookupType.getSpeciality());
        lookupConf.parent = lookupParentName;
        
        return lookupConf;
    }    

    private DmsCategoryTypeConfiguration buildDmsCategoryTypeConfiguration(String dmsCategoryTypeName, LookupInfo rawLookupInfo,
            List<DmsCategoryConfiguration> dmsCategoryValueConfs) {
        return buildDmsCategoryTypeConfiguration(dmsCategoryTypeName, rawLookupInfo, dmsCategoryValueConfs, null);
    }

    private DmsCategoryTypeConfiguration buildDmsCategoryTypeConfiguration(String dmsCategoryTypeName, LookupInfo rawLookupInfo,
            List<DmsCategoryConfiguration> dmsCategoryValueConfs, String dmsCategoryTypeParentName) {
        final LookupType rawLookupType = rawLookupInfo.getType();

        DmsCategoryTypeConfiguration lookupConf = new DmsCategoryTypeConfiguration(dmsCategoryTypeName, dmsCategoryValueConfs);
        lookupConf.setAccessType(rawLookupType.getAccessType());
        lookupConf.setSpeciality(rawLookupType.getSpeciality());
        lookupConf.parent = dmsCategoryTypeParentName;

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
