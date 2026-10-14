/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.utils;

import org.cmdbuild.dao.beans.RelationDirection;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.ReverseDomain;

public enum RelationDirectionQueryHelper {

    DIRECT("1", "2", RelationDirection.RD_DIRECT), INVERSE("2", "1", RelationDirection.RD_INVERSE);

    private final String one;
    private final String two;
    private final RelationDirection direction;

    public static RelationDirectionQueryHelper forDirection(RelationDirection direction) {
        return direction.isDirect() ? DIRECT : INVERSE;
    }

    public static RelationDirectionQueryHelper forDomain(Domain d) {
        return d instanceof ReverseDomain ? RelationDirectionQueryHelper.INVERSE : RelationDirectionQueryHelper.DIRECT;
    }

    private RelationDirectionQueryHelper(String one, String two, RelationDirection direction) {
        this.one = one;
        this.two = two;
        this.direction = direction;
    }

    public RelationDirection toRelationDirection() {
        return direction;
    }

    public String getSourceCardIdExpr() {
        return String.format("\"IdObj%s\"", one);
    }

    public String getTargetCardIdExpr() {
        return String.format("\"IdObj%s\"", two);
    }

    public String getSourceClassIdExpr() {
        return String.format("\"IdClass%s\"", one);
    }

    public String getTargetClassIdExpr() {
        return String.format("\"IdClass%s\"", two);
    }

}
