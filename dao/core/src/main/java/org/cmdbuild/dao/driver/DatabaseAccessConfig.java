/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;

public interface DatabaseAccessConfig {

    final static Long IGNORE_TENANT_POLICIES = -1l;

    MultitenantConfiguration getMultitenantConfiguration();

    DatabaseAccessUserContext getUserContext();

}
