/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.modeldiff.diff.patch;

import org.cmdbuild.modeldiff.diff.CmModelNode;

/**
 * A delta that needs to be applied to <code>source</code> to obtain a
 * <code>target</code>.
 *
 * @see
 * <a href="https://javadoc.io/static/io.github.java-diff-utils/java-diff-utils/4.12/index.html?overview-tree.html">java-diff-utils::AbstractDelta</a>
 *
 * @author afelice
 * @param <T> Model node
 * @param <U> Model class
 */
public abstract class AbstractCmDelta<T extends CmModelNode<T, U>, U> {

    protected CmDeltaType deltaType;

    protected Class<T> modelNodeClass;

    protected T sourceModelNode;
    protected T targetModelNode;

    protected String distinguishingName;

    public AbstractCmDelta(CmDeltaType deltaType, Class<T> modelNodeClass, String distinguishingName, T sourceModelNode, T targetModelNode) {
        this.modelNodeClass = modelNodeClass;
        this.deltaType = deltaType;
        this.sourceModelNode = sourceModelNode;
        this.targetModelNode = targetModelNode;
        this.distinguishingName = distinguishingName;
    }

    public CmDeltaType getDeltaType() {
        return deltaType;
    }

    /**
     * Model node class
     *
     * @return
     */
    public Class<T> getModelNodeClass() {
        return modelNodeClass;
    }

    /**
     * Model node source.
     *
     * @return
     */
    public T getSourceModelNode() {
        return sourceModelNode;
    }

    /**
     * Model node target.
     *
     * @return
     */
    public T getTargetModelNode() {
        return targetModelNode;
    }

    public String getDistinguishingName() {
        return distinguishingName;
    }

    public boolean isEqual() {
        return deltaType == CmDeltaType.CM_DT_EQUAL;
    }

    public boolean isInsert() {
        return deltaType == CmDeltaType.CM_DT_INSERT;
    }

    public boolean isRemove() {
        return deltaType == CmDeltaType.CM_DT_REMOVE;
    }

    public boolean isChange() {
        return deltaType == CmDeltaType.CM_DT_CHANGE;
    }
} // end AbstractCmDelta class

