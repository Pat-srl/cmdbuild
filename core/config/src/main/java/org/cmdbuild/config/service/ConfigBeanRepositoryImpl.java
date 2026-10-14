/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.cmdbuild.config.api.ConfigBeanRepository;
import org.springframework.stereotype.Component;

@Component
public class ConfigBeanRepositoryImpl implements ConfigBeanRepository {

    private final List<ConfigServiceHelper> configBeans = new CopyOnWriteArrayList<>();

    @Override
    public List<ConfigServiceHelper> getConfigHelpers() {
        return configBeans;
    }

    public synchronized void addBean(ConfigServiceHelper bean) {
        configBeans.add(checkNotNull(bean));
    }

}
