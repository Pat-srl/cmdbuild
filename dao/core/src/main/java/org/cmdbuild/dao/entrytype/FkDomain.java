/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Objects.equal;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import static org.cmdbuild.dao.beans.RelationDirection.RD_INVERSE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;

public interface FkDomain {

    Attribute getSourceAttr();

    Classe getSourceClass();

    Classe getTargetClass();

    CascadeAction getCascadeAction();

    boolean isMasterDetail();

    String getMasterDetailDescription();

    DomainCardinality getCardinality();

    default boolean isDirect() {
        return equal(getCardinality(), MANY_TO_ONE);
    }

    default RelationDirection getDirection() {
        return isDirect() ? RD_DIRECT : RD_INVERSE;
    }
}
