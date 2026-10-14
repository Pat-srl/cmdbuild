/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkArgument;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@CardMapping("_BimObject")
public class BimObjectImpl implements BimObject {

    static final String BIM_OBJECT_ATTR_OWNER_CLASS_ID = "OwnerClassId", BIM_OBJECT_ATTR_OWNER_CARD_ID = "OwnerCardId";

    private final Long id;
    private final long ownerCardId;
    private final String ownerClassId, poid, globalId;

    private BimObjectImpl(BimObjectImplBuilder builder) {
        this.id = builder.id;
        this.ownerClassId = checkNotBlank(builder.ownerClassId);
        this.ownerCardId = builder.ownerCardId;
        this.poid = trimToNull(builder.poid);
        this.globalId = trimToNull(builder.globalId);
        checkArgument(poid != null || globalId != null, "a bim object must set at least one between project id and global id");
    }

    @Override
    @CardAttr(ATTR_ID)
    @Nullable
    public Long getId() {
        return id;
    }

    @Override
    @CardAttr(BIM_OBJECT_ATTR_OWNER_CLASS_ID)
    public String getOwnerClassId() {
        return ownerClassId;
    }

    @Override
    @CardAttr(BIM_OBJECT_ATTR_OWNER_CARD_ID)
    public long getOwnerCardId() {
        return ownerCardId;
    }

    @Override
    @CardAttr
    @Nullable
    public String getProjectId() {
        return poid;
    }

    @Override
    @CardAttr
    @Nullable
    public String getGlobalId() {
        return globalId;
    }

    public static BimObjectImplBuilder builder() {
        return new BimObjectImplBuilder();
    }

    public static BimObjectImplBuilder copyOf(BimObject source) {
        return new BimObjectImplBuilder()
                .withId(source.getId())
                .withOwnerClassId(source.getOwnerClassId())
                .withOwnerCardId(source.getOwnerCardId())
                .withProjectId(source.getProjectId())
                .withGlobalId(source.getGlobalId());
    }

    public static class BimObjectImplBuilder implements Builder<BimObjectImpl, BimObjectImplBuilder> {

        private Long id;
        private String ownerClassId;
        private Long ownerCardId;
        private String poid;
        private String globalId;

        public BimObjectImplBuilder withId(Long id) {
            this.id = id;
            return this;
        }

        public BimObjectImplBuilder withOwnerClassId(String ownerClassId) {
            this.ownerClassId = ownerClassId;
            return this;
        }

        public BimObjectImplBuilder withOwnerCardId(Long ownerCardId) {
            this.ownerCardId = ownerCardId;
            return this;
        }

        public BimObjectImplBuilder withProjectId(String projectId) {
            this.poid = projectId;
            return this;
        }

        public BimObjectImplBuilder withGlobalId(String globalId) {
            this.globalId = globalId;
            return this;
        }

        @Override
        public BimObjectImpl build() {
            return new BimObjectImpl(this);
        }

    }
}
