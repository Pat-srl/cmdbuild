/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import org.cmdbuild.common.beans.CardIdAndClassName;
import org.cmdbuild.dao.beans.RelationDirection;

public interface RelationInfo {

    String getDomain();

    CardIdAndClassName getSource();

    CardIdAndClassName getDestination();

    RelationDirection getDirection();

    boolean canUpdate();

    default long getSourceId() {
        return getSource().getId();
    }

    default long getDestinationId() {
        return getDestination().getId();
    }
}
