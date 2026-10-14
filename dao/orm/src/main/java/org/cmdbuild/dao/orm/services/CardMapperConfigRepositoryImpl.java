/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.orm.services;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.ImmutableList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.orm.CardMapperConfig;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.cmdbuild.dao.orm.CardMapperConfigRepository;

@Component
public class CardMapperConfigRepositoryImpl implements CardMapperConfigRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<CardMapperConfig> list = list();

    @Override
    public synchronized List<CardMapperConfig> getConfigs() {
        return ImmutableList.copyOf(list);
    }

    @Override
    public synchronized void putConfig(CardMapperConfig cardMapper) {
        list.add(checkNotNull(cardMapper));
    }

}
