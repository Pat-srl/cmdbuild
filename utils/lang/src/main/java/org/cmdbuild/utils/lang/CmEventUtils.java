/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.atomic.AtomicReference;

/**
 *
 */
public class CmEventUtils {

    public static <E> E waitForEvent(EventBus eventBus, Class<E> eventClass) throws InterruptedException {
        AtomicReference<E> eventHolder = new AtomicReference<>();
        synchronized (eventHolder) {
            eventBus.register(new Object() {
                @Subscribe
                public void handleEvent(Object event) {
                    if (eventClass.isAssignableFrom(event.getClass())) {
                        synchronized (eventHolder) {
                            eventHolder.set((E) event);
                            eventHolder.notify();
                        }
                    }
                }
            });
            eventHolder.wait();
            return checkNotNull(eventHolder.get(), "event holder shouldn't be null at this point");
        }
    }
}
