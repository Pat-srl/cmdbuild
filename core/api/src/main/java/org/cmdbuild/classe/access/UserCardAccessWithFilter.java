/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.classe.access;

import org.cmdbuild.auth.grant.GroupOfPrivileges;
import org.cmdbuild.data.filter.CmdbFilter;

public interface UserCardAccessWithFilter {

    String getName();

    CmdbFilter getFilter();

    GroupOfPrivileges getPrivileges();
}
