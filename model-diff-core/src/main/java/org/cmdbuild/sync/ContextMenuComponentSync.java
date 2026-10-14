/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.Map;
import org.cmdbuild.uicomponents.data.UiComponentData;

/**
 *
 * @author afelice
 */
public interface ContextMenuComponentSync {
    UiComponentData read(String componentId);
    
    UiComponentData add(String componentId, Map<String, Object> jsComponentSchemaSerialization);
    
    UiComponentData deactivate(String componentId, Map<String, Object> jsComponentSchemaSerialization);
    
    UiComponentData update(String componentId, Map<String, Object> jsComponentSchemaSerialization);
}
