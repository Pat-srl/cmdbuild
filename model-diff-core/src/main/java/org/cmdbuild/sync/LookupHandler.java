/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.lookup.LookupType;
import org.cmdbuild.lookup.LookupValue;
import org.cmdbuild.modeldiff.data.ModelConfiguration;
import org.cmdbuild.modeldiff.schema.SchemaLookupConfiguration;
import org.cmdbuild.modeldiff.schema.SchemaLookupValueConfiguration;
import static org.cmdbuild.utils.lang.CmCollectionUtils.isNullOrEmpty;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * Handles data for a real <i>schema</i> {@link LookupType} stuff, composed of
 * value items.
 *
 * <p>
 * Contains even <i>lookup parent types</i>.
 *
 * @author afelice
 */
public class LookupHandler {

    // Cached map
    private final Map<Long, LookupType> parentTypes = map();

    private final Map<LookupType, List<LookupValue>> alreadyTracedLookup = map();

    /**
     * Resulting (lookupTypeName, {@link SchemaLookupConfiguration})
     */
    public Map<String, SchemaLookupConfiguration> lookups = map();

    private final LookupSync lookupSync;

    public LookupHandler(LookupSync lookupSync) {
        this.lookupSync = lookupSync;
    }

    /**
     * Similar to {@link ModelConfiguration#addLookup()}.
     *
     * <p>
     * Used when collecting all <i>lookups</i>.
     *
     * <p>
     * Adds <b>(only) new</b> {@link LookupType} representation
     * ({@link LookupConfiguration} and related {@link LookupValueConfiguration} for
     * {@link LookupValue}) if not already traced.
     *
     * @param lookupType
     * @param values
     */
    public void appendLookup(LookupType lookupType, List<LookupValue> values) {
        checkArgument(!isNullOrEmpty(values), "empty values found for lookup =< %s >", lookupType.getName());

        if (!isAlreadyTreated(lookupType.getName())) {
            addLookup(lookupType, values);
        }
    }

    /**
     * <p>
     * Used when inserting/updating/disabling <i>lookups</i>.<b>Overwrites</b>
     * existing data for {@link LookupType} already traced.
     *
     * @param lookupType
     * @param values
     * @return
     */
    public SchemaLookupConfiguration addLookup(LookupType lookupType, List<LookupValue> values) {
        alreadyTracedLookup.put(lookupType, values);

        SchemaLookupConfiguration lookupConfiguration = buildLookupTypeConfiguration(lookupType, values);
        addLookupConfiguration(lookupConfiguration);

        return lookupConfiguration;
    }

    public SchemaLookupConfiguration buildLookupTypeConfiguration(LookupType lookupType, List<LookupValue> values) {
        String lookupParentName = fetchLookupParentName(lookupType, values);
        final String lookupTypeName = lookupType.getName();
        // (code, description_translation)
        Map<String, String> valueDescrTranslations = lookupSync.fetchLookupDescrTranslations(lookupTypeName, values);
        List<SchemaLookupValueConfiguration> lookupValueConfs = values.stream()
                .map(v -> buildLookupValueConfiguration(v, lookupTypeName, valueDescrTranslations, lookupParentName))
                .collect(toList());

        SchemaLookupConfiguration lookupConfiguration = buildLookupTypeConfiguration(lookupType, lookupValueConfs, lookupParentName);

        return lookupConfiguration;
    }

    public SchemaLookupValueConfiguration buildLookupValueConfiguration(SchemaLookupConfiguration lookupTypeConf, LookupValue value) {
        String lookupParentName = lookupTypeConf.parent;
        final String lookupTypeName = lookupTypeConf.getName();
        // (code, description_translation)
        Map<String, String> valueDescrTranslations = lookupSync.fetchLookupDescrTranslations(lookupTypeName, list(value));
        return buildLookupValueConfiguration(value, lookupTypeName, valueDescrTranslations, lookupParentName);
    }

    public SchemaLookupValueConfiguration buildLookupValueConfiguration(LookupValue value, String lookupTypeName) {
        // (code, description_translation)
        Map<String, String> valueDescrTranslations = lookupSync.fetchLookupDescrTranslations(lookupTypeName, list(value));

        return buildLookupValueConfiguration(value, lookupTypeName, valueDescrTranslations, fetchLookupParentName(value));
    }

    public Map<LookupType, List<LookupValue>> getLookups() {
        return alreadyTracedLookup;
    }

    public LookupType getLookup(String lookupTypeName) {
        Optional<LookupType> lookupOpt = alreadyTracedLookup.keySet().stream().filter(lt -> lt.getName().equals(lookupTypeName)).findFirst();
        if (lookupOpt.isEmpty()) {
            return null;
        }

        return lookupOpt.get();
    }

    public List<LookupValue> getLookupValues(String lookupTypeName) {
        Optional<Map.Entry<LookupType, List<LookupValue>>> lookupOpt = alreadyTracedLookup.entrySet().stream().filter(e -> e.getKey().getName().equals(lookupTypeName)).findFirst();

        if (lookupOpt.isEmpty()) {
            return null;
        }

        return lookupOpt.get().getValue();
    }

    public boolean isAlreadyTreated(String lookupTypeName) {
        return alreadyTracedLookup.keySet().stream().filter(lt -> lt.getName().equals(lookupTypeName)).findFirst().isPresent();
    }

    public List<SchemaLookupConfiguration> getLookupConfigurations() {
        return list(lookups.values());
    }

    public String fetchLookupParentName(List<LookupValue> values) {
        return fetchLookupParentName(values.iterator().next());
    }

    public void updateLookupConfiguration(SchemaLookupConfiguration lookupConf) {
        addLookupConfiguration(lookupConf);
    }

    public void setDeactivatedValue(String ownerLookupTypeName, String valueCode) {
        lookups.get(ownerLookupTypeName).deactivate(valueCode);
    }

    private void addLookupConfiguration(SchemaLookupConfiguration lookupConf) {
        lookups.put(lookupConf.getName(), lookupConf);
    }

    /**
     * As in {@link ModelConfiguration}.
     *
     * @param ownerLookupType
     * @param lookupValueConfs
     * @param lookupParentName
     * @return
     */
    private SchemaLookupConfiguration buildLookupTypeConfiguration(LookupType ownerLookupType,
            List<? extends SchemaLookupValueConfiguration> lookupValueConfs, String lookupParentName) {
        SchemaLookupConfiguration result;

        String lookupName = ownerLookupType.getName();

        if (lookupValueConfs.isEmpty()) {
            // Workaround for diff-schema insertion/update of LookupType: LookupValues are inserted after this
            result = new SchemaLookupConfiguration(lookupName);
        } else {
            result = new SchemaLookupConfiguration(lookupName, lookupValueConfs);
        }

        result.setAccessType(ownerLookupType.getAccessType());
        result.setSpeciality(ownerLookupType.getSpeciality());
        result.parent = lookupParentName;

        return result;
    }

    private SchemaLookupValueConfiguration buildLookupValueConfiguration(LookupValue v, String lookupTypeName,
            Map<String, String> valueDescrTranslations, final String finalLookupParentName) {
        SchemaLookupValueConfiguration result = buildValueConfFrom(v.getId(), lookupTypeName, v);
        result.setDescriptionTranslation(valueDescrTranslations.get(v.getCode()));

        if (finalLookupParentName != null) {
            result.lookupParentName = finalLookupParentName;
        }

        return result;
    }

    /**
     * As in {@link ModelConfiguration}.
     *
     * @param id
     * @param lookupName
     * @param lookupValue
     * @return
     */
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

    /**
     * Cached fetch of parent {@link LookupType}.
     *
     * @param lookupType
     * @param values
     * @return
     */
    private String fetchLookupParentName(LookupType lookupType, List<LookupValue> values) {
        String lookupParentName = null;
        if (lookupType.hasParent() && !values.isEmpty()) {
            // fetch parent lookup from id
            lookupParentName = fetchLookupParentName(values.iterator().next());
        }
        return lookupParentName;
    }

    /**
     * Cached fetch of parent {@link LookupType}.
     *
     * @param value
     * @return
     */
    private String fetchLookupParentName(LookupValue value) {
        Long parentId = value.getType().getParent();

        if (parentId == null) {
            return null;
        }

        // Caching of LookupType
        parentTypes.computeIfAbsent(parentId, id -> lookupSync.readType(id));
        LookupType parentType = parentTypes.get(parentId);

        if (alreadyTracedLookup.keySet().stream()
                .filter(lt -> lt.getName().equals(parentType.getName())).findAny()
                .isEmpty()) {
            List<LookupValue> parentValues = lookupSync.readValues(parentType.getName());
            appendLookup(parentType, parentValues);
        }

        return parentType.getName();
    }

} // end LookupHandler class
