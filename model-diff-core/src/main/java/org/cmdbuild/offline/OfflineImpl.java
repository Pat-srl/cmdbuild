/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.offline;

import javax.annotation.Nullable;

/**
 *
 * @author ataboga
 */
public class OfflineImpl implements Offline {

    private final long id;
    private final String code, description, metadata;
    private final Boolean isActive;

    public OfflineImpl(@Nullable Long id, String code, String description, String metadata, Boolean isActive) {
        this.id = id;
        this.code = code;
        this.description = description;
        this.metadata = metadata;
        this.isActive = isActive;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getMetadata() {
        return metadata;
    }

    @Override
    public Boolean isActive() {
        return isActive;
    }

}
