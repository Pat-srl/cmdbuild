/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.lang;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import static org.cmdbuild.utils.lang.EventBusUtils.rethrowingEventBus;
import org.junit.Test;

public class CmEventBusUtilsTest {

    @Test
    public void testStandardEventBus() {
        EventBus eventBus = new EventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void handleMyEvent(MyEvent event) {
                throw new MyCustomException();
            }
        });
        eventBus.post(MyEvent.INSTANCE);
    }

    @Test(expected = MyCustomException.class)
    public void testRethrowingEventBus() {
        EventBus eventBus = rethrowingEventBus();
        eventBus.register(new Object() {
            @Subscribe
            public void handleMyEvent(MyEvent event) {
                throw new MyCustomException();
            }
        });
        eventBus.post(MyEvent.INSTANCE);
    }

    static class MyCustomException extends RuntimeException {

        public MyCustomException() {
            super("TEST EXCEPTION");
        }

    }

    enum MyEvent {
        INSTANCE;
    }

}
