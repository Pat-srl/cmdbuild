/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.config.service;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Component;

@Component
public class ConfigListenerBeansRepositoryImpl implements ConfigListenerBeansRepository {

    private final List<ConfigListenerBean> list = new CopyOnWriteArrayList<>();

    @Override
    public List<ConfigListenerBean> getConfigListeners() {
        return list;
    }

    public synchronized void addBean(ConfigListenerBean bean) {
        list.add(checkNotNull(bean));
    }
}
