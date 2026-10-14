/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.api;

import static com.google.common.collect.Lists.transform;
import java.util.List;

public interface ConfigBeanRepository {

    List<ConfigServiceHelper> getConfigHelpers();

    default List<Object> getConfigBeans() {
        return transform(getConfigHelpers(), ConfigServiceHelper::getBean);
    }

    interface ConfigServiceHelper {

        Object getBean();

        ConfigComponent getAnnotation();

        Class getType();

        String getNamespace();

        void processBean(GlobalConfigService configAccessService);

    }

}
