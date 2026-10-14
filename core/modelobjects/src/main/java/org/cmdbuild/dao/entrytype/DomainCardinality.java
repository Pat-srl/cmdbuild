/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

public enum DomainCardinality {
    ONE_TO_ONE, ONE_TO_MANY, MANY_TO_ONE, MANY_TO_MANY;

    public DomainCardinality inverse() {
        return switch (this) {
            case MANY_TO_ONE ->
                ONE_TO_MANY;
            case ONE_TO_MANY ->
                MANY_TO_ONE;
            default ->
                this;
        };
    }
}
