/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.Map;
import static org.cmdbuild.modeldiff.diff.schema.CmContextMenuItemsSchemaNode.buildUiComponentData_JSComponent_Synthesized;
import org.cmdbuild.uicomponents.UiComponentInfo;
import org.cmdbuild.uicomponents.contextmenu.ContextMenuComponentServiceImpl;
import org.cmdbuild.uicomponents.data.UiComponentData;
import org.cmdbuild.uicomponents.data.UiComponentDataImpl;
import org.cmdbuild.uicomponents.data.UiComponentRepository;
import static org.cmdbuild.uicomponents.data.UiComponentType.UCT_CONTEXTMENU;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class ContextMenuComponentSyncImpl implements ContextMenuComponentSync {

    private final UiComponentRepository uiComponentRepository;

    public ContextMenuComponentSyncImpl(UiComponentRepository uiComponentRepository) {
        this.uiComponentRepository = uiComponentRepository;
    }

    /**
     * As in {@link ContextMenuComponentServiceImpl#getByCode()}, but using the
     * {@link UiComponentData} returned by the {@link UiComponentRepository#getTypeAndName()}
     * instead of the wrapper {@link UiComponentInfo} (that hides the JS script).
     *
     * @param componentId
     * @return <code>null</code> if a JS component not found for given
     * <code>componentId</code>.
     */
    @Override
    public UiComponentData read(String componentId) {
        // Return null if not found
        UiComponentData componentData = uiComponentRepository.getByTypeAndNameOrNull(UCT_CONTEXTMENU, checkNotBlank(componentId));

        // @todo AFE cambiato qui tra JS8 e JS9
//        // Apply zip normalization
//        if (componentData != null && !ContextMenuComponentHandler.isRunningUnderUnitTest()) {
//            componentData = UiComponentDataImpl.copyOf(componentData).withData(componentData.getData()).build();
//        }

        return componentData;
    }

    /**
     * As in {@link ContextMenuComponentWs#create()}, but doesn't expect <i>JS scripts</i> to
     * be in a <i>Zipped file</i>. Relevant data comes for <i>compare</i> Json file.
     *
     * @param componentId
     * @param jsComponentSchemaSerialization with <code>base64</code> json
     * representation.
     * @return
     */
    @Override
    public UiComponentData add(String componentId, Map<String, Object> jsComponentSchemaSerialization) {
        // Apply Zip normalization
        // return uiComponentRepository.create(buildUiComponentData_JSComponent("applyDiffInsert", jsComponentSchemaSerialization));

        // Don't apply Zip normalization
        return uiComponentRepository.create(buildUiComponentData_JSComponent_Synthesized("applyDiffInsert", jsComponentSchemaSerialization));
    }

    /**
     * Only <i>disables</i> the {@link UiComponentData}.
     *
     * @param componentId
     * @param jsComponentSchemaSerialization
     * @return
     */
    @Override
    public UiComponentData deactivate(String componentId, Map<String, Object> jsComponentSchemaSerialization) {
        // Don't apply Zip normalization
        UiComponentData curComponentData = buildUiComponentData_JSComponent_Synthesized("applyDiffDeactivate", jsComponentSchemaSerialization);
        UiComponentData deactivatedComponentData = toDeactivated(curComponentData);

        return uiComponentRepository.update(deactivatedComponentData);
    }

    /**
     * Updates an existing {@link UiComponentData}.
     *
     * @param componentId
     * @param jsComponentSchemaSerialization
     * @return
     */
    @Override
    public UiComponentData update(String componentId, Map<String, Object> jsComponentSchemaSerialization) {
        // Don't apply Zip normalization
        return uiComponentRepository.update(buildUiComponentData_JSComponent_Synthesized("applyDiffUpdate", jsComponentSchemaSerialization));
    }

    private UiComponentData toDeactivated(UiComponentData origComponentData) {
        return UiComponentDataImpl.copyOf(origComponentData)
                .withActive(false)
                .build();
    }
}

