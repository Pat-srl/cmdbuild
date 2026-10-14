/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import java.util.List;

public interface RelationFilterRule {

    String getDomain();

    RelationFilterRuleType getType();

    RelationFilterDirection getDirection();

    List<RelationFilterCardInfo> getCardInfos();
    
    AttributeFilter getFilter();

    default boolean isOneOf() {
        return RelationFilterRuleType.ONEOF.equals(getType());
    }

    default boolean isNoone() {
        return RelationFilterRuleType.NOONE.equals(getType());
    }

    enum RelationFilterRuleType {
        NOONE, ONEOF, ANY, FROMFILTER
    }

    enum RelationFilterDirection {
        _1, _2
    }

}
