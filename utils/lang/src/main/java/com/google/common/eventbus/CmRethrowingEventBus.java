/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.google.common.eventbus;

import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CmRethrowingEventBus extends EventBus {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    void handleSubscriberException(Throwable e, SubscriberExceptionContext context) {
        logger.debug("rethrowing exception with context =< {} >", context, e);
        throw runtime(e);
    }

}
