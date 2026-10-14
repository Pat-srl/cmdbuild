/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

@Component
public class MinionBeanHandler implements BeanPostProcessor {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MinionBeanRepositoryExt minionBeanRepository;

    public MinionBeanHandler(MinionBeanRepositoryExt minionBeanRepository) {
        this.minionBeanRepository = checkNotNull(minionBeanRepository);
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof MinionComponent minionComponent) {
            logger.info("register minion bean = {} ( {} )", beanName, minionComponent.getClass().getName());
            minionBeanRepository.addMinionBean(new InnerBeanImpl(beanName, minionComponent));
        }
        return bean; // nothing to do
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }

    private final static class InnerBeanImpl implements InnerBean {

        private final String name;
        private final MinionComponent bean;

        public InnerBeanImpl(String name, MinionComponent bean) {
            this.name = checkNotBlank(name);
            this.bean = checkNotNull(bean);
        }

        @Override
        public String getName() {
            return name;
        }

        @Override
        public MinionComponent getBean() {
            return bean;
        }

    }

}
