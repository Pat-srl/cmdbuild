/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkNotNull;
import java.time.ZonedDateTime;
import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.CardIdAndClassName;
import static org.cmdbuild.common.beans.CardIdAndClassNameImpl.card;

public class BimProjectExtImpl implements BimProjectExt {

    private final BimProject bimProject;
    private final CardIdAndClassName owner;

    public BimProjectExtImpl(BimProject bimProject, @Nullable CardIdAndClassName owner) {
        this.bimProject = checkNotNull(bimProject);
        this.owner = owner;
    }

    public BimProjectExtImpl(BimProject bimProject, @Nullable BimObject bimObject) {
        this.bimProject = checkNotNull(bimProject);
        this.owner = bimObject == null ? null : card(bimObject.getOwnerClassId(), bimObject.getOwnerCardId());
    }

    @Override
    @Nullable
    public Long getId() {
        return bimProject.getId();
    }

    @Override
    @Nullable
    public Long getParentId() {
        return bimProject.getParentId();
    }

    @Override
    public String getProjectId() {
        return bimProject.getProjectId();
    }

    @Override
    public String getName() {
        return bimProject.getName();
    }

    @Override
    public String getDescription() {
        return bimProject.getDescription();
    }

    @Override
    @Nullable
    public String getImportMapping() {
        return bimProject.getImportMapping();
    }

    @Override
    public boolean isActive() {
        return bimProject.isActive();
    }

    @Override
    @Nullable
    public ZonedDateTime getLastCheckin() {
        return bimProject.getLastCheckin();
    }

    @Override
    @Nullable
    public CardIdAndClassName getOwnerOrNull() {
        return owner;
    }

    @Override
    @Nullable
    public String getIfcFormat() {
        return bimProject.getIfcFormat();
    }

    @Override
    public byte[] getXktFile() {
        return bimProject.getXktFile();
    }

    @Override
    public String toString() {
        return "BimProjectExt{" + "id=" + getId() + ", poid=" + getProjectId() + ", name=" + getName() + ", description=" + getDescription() + ", isActive=" + isActive() + ", card=" + getOwnerOrNull() + '}';
    }

}
