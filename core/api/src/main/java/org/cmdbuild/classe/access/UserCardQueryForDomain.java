/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import org.cmdbuild.dao.beans.RelationDirection;

public interface UserCardQueryForDomain {

    String getDomainName();

    RelationDirection getDirection();

    long getOriginId();

    boolean getAll();

}
