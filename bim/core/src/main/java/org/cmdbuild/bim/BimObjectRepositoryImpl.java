/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.bim.BimObjectImpl.BIM_OBJECT_ATTR_OWNER_CARD_ID;
import static org.cmdbuild.bim.BimObjectImpl.BIM_OBJECT_ATTR_OWNER_CLASS_ID;
import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.dao.core.q3.WhereOperator.ISNULL;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class BimObjectRepositoryImpl implements BimObjectRepository {

    private final DaoService dao;

    public BimObjectRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Nullable
    @Override
    public BimObject getBimObjectForCardOrNull(CardIdAndClassName card) {
        return dao.selectAll().from(BimObject.class)
                .where(BIM_OBJECT_ATTR_OWNER_CLASS_ID, EQ, card.getClassName())
                .where(BIM_OBJECT_ATTR_OWNER_CARD_ID, EQ, card.getId())
                .getOneOrNull();
    }

    @Override
    @Nullable
    public BimObject getBimObjectForProjectOrNull(BimProject bimProject) {
        return dao.selectAll().from(BimObject.class).where("ProjectId", EQ, bimProject.getProjectId()).where("GlobalId", ISNULL).getOneOrNull();
    }

    @Override
    @Nullable
    public BimObject getBimObjectForProjectGlobalIdOrNull(BimProject bimProject, String globalId) {
        return dao.selectAll().from(BimObject.class).where("ProjectId", EQ, bimProject.getProjectId()).where("GlobalId", EQ, checkNotBlank(globalId)).getOneOrNull();
    }

    @Override
    @Nullable
    @Deprecated //global id may be duplicate, use projectid+globalid
    public BimObject getBimObjectForGlobalIdOrNull(String globalId) {
        return dao.selectAll().from(BimObject.class).where("GlobalId", EQ, checkNotBlank(globalId)).getOneOrNull();
    }

    @Override
    public BimObject createBimObjectForProject(BimProject bimProject, CardIdAndClassName card) {
        return dao.create(BimObjectImpl.builder().withProjectId(bimProject.getProjectId()).withOwnerClassId(card.getClassName()).withOwnerCardId(card.getId()).build());
    }

    @Override
    public void delete(BimObject bimObject) {
        dao.delete(bimObject);
    }

    @Override
    public BimObject create(BimObject bimObject) {
        return dao.create(bimObject);
    }

    @Override
    @Nullable
    public BimObject setOwnerForProject(BimProjectExt data, @Nullable CardIdAndClassName owner) {
        BimObject current = getBimObjectForProjectOrNull(data);
        if (equal(current == null ? null : current.getOwnerCardId(), owner == null ? null : owner.getId())) {
            return current;
        } else {
            if (current != null) {
                dao.delete(current);
            }
            if (owner != null) {
                return createBimObjectForProject(data, owner);
            } else {
                return null;
            }
        }
    }

}
