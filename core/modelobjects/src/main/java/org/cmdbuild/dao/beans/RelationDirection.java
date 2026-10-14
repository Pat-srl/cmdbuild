/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.beans;

import static com.google.common.base.Objects.equal;

public enum RelationDirection {
    RD_DIRECT, RD_INVERSE;

    public static RelationDirection fromDirect(boolean direct) {
        return direct ? RD_DIRECT : RD_INVERSE;
    }

    public RelationDirection inverse() {
        return switch (this) {
            case RD_DIRECT ->
                RD_INVERSE;
            case RD_INVERSE ->
                RD_DIRECT;
            default ->
                throw new IllegalStateException();
        };
    }

    public boolean isDirect() {
        return equal(this, RD_DIRECT);
    }

}
