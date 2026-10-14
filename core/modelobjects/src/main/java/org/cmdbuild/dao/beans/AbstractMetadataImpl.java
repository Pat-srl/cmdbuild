/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import static com.google.common.base.Strings.nullToEmpty;
import java.util.Map;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.AbstractMetadata;
import static org.cmdbuild.dao.entrytype.AbstractMetadata.ACTIVE;
import static org.cmdbuild.dao.entrytype.AbstractMetadata.DESCRIPTION;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBooleanOrDefault;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public abstract class AbstractMetadataImpl implements AbstractMetadata {

    private final Map<String, String> metadata;
    private final Map<String, String> otherMetadata;

    private final String description;
    private final boolean isActive;

    protected AbstractMetadataImpl(Map<String, String> allAttrs, Map<String, String> customAttrs) {
        metadata = map(allAttrs).immutable();
        otherMetadata = map(customAttrs).withoutKeys(ACTIVE, DESCRIPTION).immutable();
        description = nullToEmpty(allAttrs.get(DESCRIPTION));
        isActive = toBooleanOrDefault(allAttrs.get(ACTIVE), true);
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public boolean isActive() {
        return isActive;
    }

    @Override
    @Nullable
    public String get(String key) {
        return metadata.get(checkNotBlank(key));
    }

    @Override
    public Map<String, String> getAll() {
        return metadata;
    }

    @Override
    public Map<String, String> getCustomMetadata() {
        return otherMetadata;
    }

}
