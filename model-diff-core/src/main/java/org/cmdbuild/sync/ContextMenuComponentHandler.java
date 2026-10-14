/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import static java.util.stream.Collectors.toList;
import org.cmdbuild.classe.ExtendedClass;
import org.cmdbuild.contextmenu.ContextMenuItem;
import org.cmdbuild.contextmenu.ContextMenuType;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.modeldiff.diff.schema.CmContextMenuItemsSchemaNode;
import org.cmdbuild.modeldiff.schema.SchemaContextMenuComponentConfiguration;
import org.cmdbuild.ui.TargetDevice;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.UiComponentInfoImpl;
import org.cmdbuild.uicomponents.UiComponentVersionInfoImpl;
import org.cmdbuild.uicomponents.data.UiComponentData;
import org.cmdbuild.uicomponents.data.UiComponentRepositoryImpl;
import org.cmdbuild.uicomponents.utils.UiComponentUtils;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 * Handles data for a real <i>schema</i> {@link UiComponentData} stuff, that
 * represents JS components that can be used by UI, e.g. QR code generator.
 *
 * @author afelice
 */
public class ContextMenuComponentHandler {

    /**
     * For each <code>componentId</code>, there can be multiple <i>JS
     * scripts</i>, one for each {@link TargetDevice} type; see
     * {@link UiComponentInfo#getVersions()}.
     */
    private final Map<String, UiComponentInfo> infoByComponentId = map();

    /**
     * Components not found by given <code>componentId</code>.
     */
    private final Set<String> notFoundComponentId = set();

    /**
     * Resulting (componentId, {@link SchemaContextMenuComponentConfiguration})
     */
    private final Map<String, SchemaContextMenuComponentConfiguration> componentsConfByComponentId = map();

    private final ContextMenuComponentSync contextMenuComponentSync;

    public ContextMenuComponentHandler(ContextMenuComponentSync uiComponentRepository) {
        this.contextMenuComponentSync = uiComponentRepository;
    }

    public List<String> fetchComponentIds(ExtendedClass extendedClasse) {
        return extendedClasse.getContextMenuItems().stream()
                .filter(i -> ContextMenuType.COMPONENT.equals(i.getType()))
                .map(ContextMenuItem::getComponentId)
                .collect(toList());
    }

    /**
     * Cached (in {@link UiComponentRepositoryImpl}) fetch of
     * {@link UiComponentInfo}.
     *
     * <b>Note</b>: if running unit-testing, uses a synthesized {@link UiComponentData}.
     *
     * @param componentId
     * @return <code>null</code> if component with given
     * <code>componentId</code> not found.
     */
    public UiComponentInfo fetchComponent(String componentId) {
        return fetchComponent(componentId, isRunningUnderUnitTest());
    }

    /**
     * Cached (in {@link UiComponentRepositoryImpl}) fetch of
     * {@link UiComponentInfo}.
     *
     * @param componentId
     * @param bSynthesized <dl><dt>true<dd>if {@link UiComponentData} has to be synthesized, f.e. if a) in <i>merge step</i>; b)in <i>collect step</i>
     * and in running unit-testing.
     * <dt>false<dd>if {@link UiComponentData} has to be built from zip files on CMDBuild system, as in existing CMDBuild {UiComponentDataImpl#normalizeComponentData}.
     * </dl>
     * @return <code>null</code> if component with given
     * <code>componentId</code> not found.
     */
    public UiComponentInfo fetchComponent(String componentId, boolean bSynthesized) {
        if (infoByComponentId.containsKey(componentId)) {
            return infoByComponentId.get(componentId);
        }

        if (notFoundComponentId.contains(componentId)) {
            return null;
        }

        // Returns null if a JS component not found for given componentId
        UiComponentData componentData = contextMenuComponentSync.read(componentId);
        if (componentData == null) {
            return null;
        }

        return appendComponent(componentId, componentData, bSynthesized);
    }

    /**
     *
     * @param componentId
     * @param componentData
     * @param bSynthesized <dl><dt>true<dd>if {@link UiComponentData} has to be synthesized, f.e. if a) in <i>merge step</i>; b)in <i>collect step</i>
     * and in running unit-testing.
     * <dt>false<dd>if {@link UiComponentData} has to be built from zip files on CMDBuild system, as in existing CMDBuild {UiComponentDataImpl#normalizeComponentData}.
     * </dl>
     * @return
     */
    public UiComponentInfo appendComponent(String componentId, UiComponentData componentData, boolean bSynthesized) {
        if (componentData == null) {
            return null;
        }

        UiComponentInfo componentInfo = toComponentInfo(componentData, bSynthesized);

        appendComponent(componentId, componentInfo, componentData);

        return componentInfo;
    }

    /**
     * <p>
     * Used in <i><b>collect</b> step</i>, when collecting all {@link Classe}s.
     *
     * <p>
     * Adds <b>(only) new</b> {@link UiComponentInfo} representation
     * ({@link SchemaContextMenuComponentConfiguration}, if not already traced.
     *
     * @param componentId
     * @param componentInfo
     * @param componentData
     */
    public void appendComponent(String componentId, UiComponentInfo componentInfo, UiComponentData componentData) {
        if (!isAlreadyTreated(componentId)) {
            addComponent("collect", componentInfo, componentData);
        }
    }

    /**
     * <p>
     * Used <i><b>applyDiff</b> step</i>, when inserting/updating
     * {@link UiComponentInfo} .<b>Overwrites</b>
     * existing data for {@link UiComponentInfo} already traced.
     *
     * @param componentInfo component info with (possibly) multiple versions for
     * different target devices. <i>Wrapper</i> for {@link UiComponentData}.
     * @param componentData component data, containing <i>JS scripts</i> for
     * each supported target devices.
     *
     * @return
     */
    public SchemaContextMenuComponentConfiguration addComponent(String contextMsg, UiComponentInfo componentInfo, UiComponentData componentData) {
        String componentId = componentData.getName();
        infoByComponentId.put(componentId, componentInfo);

        SchemaContextMenuComponentConfiguration componentConfiguration = CmContextMenuItemsSchemaNode.buildComponentConfiguration(contextMsg, componentData);
        addComponentConfiguration(componentId, componentConfiguration);

        return componentConfiguration;
    }

    /**
     * <p>
     * Used in <i><b>applyDiff</b> step</i>, when initializing data for
     * {@link UiComponentData}.
     *
     * @param componentConfiguration
     */
    public void addComponent(SchemaContextMenuComponentConfiguration componentConfiguration) {
        addComponentConfiguration(componentConfiguration.getComponentId(), componentConfiguration);
    }

    public Map<String, SchemaContextMenuComponentConfiguration> getComponentsConfByComponentId() {
        return componentsConfByComponentId;
    }

    /**
     *
     * @param componentId
     * @return <code>null</code> if not found.
     */
    public UiComponentInfo getComponent(String componentId) {
        return infoByComponentId.get(componentId);
    }

    public boolean isAlreadyTreated(String componentId) {
        return infoByComponentId.containsKey(componentId);
    }

    public List<SchemaContextMenuComponentConfiguration> getComponentConfigurations() {
        return list(componentsConfByComponentId.values());
    }

    private void addComponentConfiguration(String componentId, SchemaContextMenuComponentConfiguration componentConfiguration) {
        componentsConfByComponentId.put(componentId, componentConfiguration);
    }

    /**
     * To overcome the <i>not testable</i> code in
     * {@link UiComponentUtils#parseExtComponentData(java.util.Map)}
     *
     * @param da
     * @return
     */
    private static UiComponentInfo toComponentInfo(UiComponentData data, boolean bSynthesized) {
        if (bSynthesized) {
            // In a unit-test: simulate UiComponentInfo bulding with a <fake.jscomponent.name>
            return UiComponentInfoImpl.builder()
                    .withName(data.getName())
                    .withExtjsAlias("<unnamed.synthesized.jscomponent.alias>")
                    .withExtjsComponentId("<unnamed.synthesized.jscomponent.name>")
                    .withId(data.getId())
                    .withActive(data.getActive())
                    .withDescription(data.getDescription())
                    .withLastUpdated(data.getLastUpdated())
                    .withType(data.getType())
                    .withVersions(list(data.getTargetDevices()).map(UiComponentVersionInfoImpl::new))
                    .build();
        } else {
            // Production code: will check that data is a valid zip file for each TargetDevice
            return UiComponentUtils.toComponentInfo(data);
        }
    }

    /**
     * Detects if this code is running in a <i>JUnit</i> unit-test.
     *
     * @return
     */
    public static boolean isRunningUnderUnitTest() {
        return Arrays.stream(Thread.currentThread().getStackTrace())
                .anyMatch(element
                        -> element.getClassName().startsWith("org.junit.")
                || element.getClassName().startsWith("org.testng."));
    }

} // end ContextMenuComponentHandler class
