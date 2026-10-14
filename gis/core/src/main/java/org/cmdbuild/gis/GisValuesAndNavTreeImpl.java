/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.gis;

import com.google.common.collect.ImmutableList;
import java.util.List;

public class GisValuesAndNavTreeImpl implements GisValuesAndNavTree {

    private final List<GisValue> gisValues;
    private final List<GisNavTreeNode> navTree;

    public GisValuesAndNavTreeImpl(List<GisValue> gisValues, List<GisNavTreeNode> navTree) {
        this.gisValues = ImmutableList.copyOf(gisValues);
        this.navTree = ImmutableList.copyOf(navTree);
    }

    @Override
    public List<GisValue> getGisValues() {
        return gisValues;
    }

    @Override
    public List<GisNavTreeNode> getNavTree() {
        return navTree;
    }

}
