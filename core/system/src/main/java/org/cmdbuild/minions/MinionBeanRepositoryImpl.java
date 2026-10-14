/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import com.google.common.collect.ImmutableList;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@Component
public class MinionBeanRepositoryImpl implements MinionBeanRepositoryExt {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<InnerBean> minionBeans = Collections.synchronizedList(list());

    @Override
    public List<InnerBean> getMinionBeans() {
        return ImmutableList.copyOf(minionBeans);
    }

    @Override
    public void addMinionBean(InnerBean bean) {
        logger.debug("add minion bean = {}", bean.getBean());
        minionBeans.add(bean);
    }

}
