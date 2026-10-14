/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter;

import org.cmdbuild.data.filter.SorterElementDirection;

public interface SorterElement {

    String getProperty();

    SorterElementDirection getDirection();

    default int getDirectionMultiplier() {
        return switch (getDirection()) {
            case ASC ->
                1;
            case DESC ->
                -1;
            default ->
                throw new IllegalStateException();
        };
    }

}
