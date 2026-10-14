/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver;

import org.cmdbuild.auth.multitenant.config.DisabledMultitenantConfiguration;
import org.cmdbuild.auth.multitenant.config.MultitenantConfiguration;
import static org.cmdbuild.dao.driver.DatabaseAccessUserScope.DA_DEFAULT;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
@Qualifier(SYSTEM_LEVEL_ONE)
public class DummyDatabaseAccessConfig implements DatabaseAccessConfig {

    @Override
    public MultitenantConfiguration getMultitenantConfiguration() {
        return DisabledMultitenantConfiguration.INSTANCE;
    }

    @Override
    public DatabaseAccessUserContext getUserContext() {
        return new DatabaseAccessUserContextImpl(null, null, null, true, null, DA_DEFAULT, null);
    }

}
