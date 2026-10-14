/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import static com.google.common.base.Strings.nullToEmpty;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class GisNavTreeNodeImpl implements GisNavTreeNode {

    private final String classId, description, parentClassId, navTreeNodeId;
    private final long cardId;
    private final Long parentCardId;

    private GisNavTreeNodeImpl(NavTreeNodeImplBuilder builder) {
        this.classId = checkNotBlank(builder.classId);
        this.description = nullToEmpty(builder.description);
        this.parentClassId = (builder.parentClassId);
        this.cardId = builder.cardId;
        this.parentCardId = builder.parentCardId;
        this.navTreeNodeId = checkNotBlank(builder.navTreeNodeId);
    }

    @Override
    public String getClassId() {
        return classId;
    }

    @Override
    public String getDescription() {
        return description;
    }

    @Override
    public String getParentClassId() {
        return parentClassId;
    }

    @Override
    public long getCardId() {
        return cardId;
    }

    @Override
    public Long getParentCardId() {
        return parentCardId;
    }

    @Override
    public String getNavTreeNodeId() {
        return navTreeNodeId;
    }

    @Override
    public String toString() {
        return "GisNavTreeNode{" + "classId=" + classId + ", cardId=" + cardId + ", parentClassId=" + parentClassId + ", parentCardId=" + parentCardId + '}';
    }

    public static NavTreeNodeImplBuilder builder() {
        return new NavTreeNodeImplBuilder();
    }

    public static NavTreeNodeImplBuilder copyOf(GisNavTreeNode source) {
        return new NavTreeNodeImplBuilder()
                .withClassId(source.getClassId())
                .withDescription(source.getDescription())
                .withParentClassId(source.getParentClassId())
                .withCardId(source.getCardId())
                .withParentCardId(source.getParentCardId())
                .withNavTreeNodeId(source.getNavTreeNodeId());
    }

    public static class NavTreeNodeImplBuilder implements Builder<GisNavTreeNodeImpl, NavTreeNodeImplBuilder> {

        private String classId;
        private String description;
        private String parentClassId, navTreeNodeId;
        private long cardId;
        private Long parentCardId;

        public NavTreeNodeImplBuilder withNavTreeNodeId(String navTreeNodeId) {
            this.navTreeNodeId = navTreeNodeId;
            return this;
        }

        public NavTreeNodeImplBuilder withClassId(String classId) {
            this.classId = classId;
            return this;
        }

        public NavTreeNodeImplBuilder withDescription(String description) {
            this.description = description;
            return this;
        }

        public NavTreeNodeImplBuilder withParentClassId(String parentClassId) {
            this.parentClassId = parentClassId;
            return this;
        }

        public NavTreeNodeImplBuilder withCardId(long cardId) {
            this.cardId = cardId;
            return this;
        }

        public NavTreeNodeImplBuilder withParentCardId(Long parentCardId) {
            this.parentCardId = parentCardId;
            return this;
        }

        @Override
        public GisNavTreeNodeImpl build() {
            return new GisNavTreeNodeImpl(this);
        }

    }
}
