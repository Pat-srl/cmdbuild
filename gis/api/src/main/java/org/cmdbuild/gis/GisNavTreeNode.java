/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import jakarta.annotation.Nullable;

public interface GisNavTreeNode {

    String getClassId();

    String getDescription();

    long getCardId();

    @Nullable
    String getParentClassId();

    @Nullable
    Long getParentCardId();

    String getNavTreeNodeId();
}
