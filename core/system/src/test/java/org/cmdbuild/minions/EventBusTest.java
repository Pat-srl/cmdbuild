/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import java.util.concurrent.atomic.AtomicInteger;
import static org.cmdbuild.utils.lang.EventBusUtils.logExceptions;
import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventBusTest {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus = new EventBus(logExceptions(logger));

    @Test
    public void testOne() {
        AtomicInteger counter = new AtomicInteger(0);
        eventBus.register(new Object() {
            @Subscribe
            public void handleSomething(Something something) {
                counter.incrementAndGet();
            }
        });
        eventBus.post(new Something<>(new Object()));
        eventBus.post(new Something<>(One.INSTANCE));
        eventBus.post(new Something<>(Two.INSTANCE));
        assertEquals(3, counter.get());
    }

    @Test
    public void testTwo() {
        AtomicInteger counterAll = new AtomicInteger(0), counterOne = new AtomicInteger(0), counterTwo = new AtomicInteger(0);
        eventBus.register(new Object() {
            @Subscribe
            public void handleSomething(Object something) {
                counterAll.incrementAndGet();
            }
        });
        eventBus.register(new Object() {
            @Subscribe
            public void handleSomething(One something) {
                counterOne.incrementAndGet();
            }
        });
        eventBus.register(new Object() {
            @Subscribe
            public void handleSomething(Two something) {
                counterTwo.incrementAndGet();
            }
        });
        eventBus.post(new Object());
        eventBus.post(One.INSTANCE);
        eventBus.post(Two.INSTANCE);
        assertEquals(3, counterAll.get());
        assertEquals(1, counterOne.get());
        assertEquals(1, counterTwo.get());
    }

    @Test
    @Ignore("does not work as desired, because of type erasure in generics")
    public void testThree() {
        AtomicInteger counterAll = new AtomicInteger(0), counterOne = new AtomicInteger(0), counterTwo = new AtomicInteger(0);
        eventBus.register(new Object() {
            @Subscribe
            public void handleSomething(Something something) {
                counterAll.incrementAndGet();
            }
        });
        eventBus.register(new Object() {
            @Subscribe
            public void handleSomething(Something<One> something) {
                counterOne.incrementAndGet();
            }
        });
        eventBus.register(new Object() {
            @Subscribe
            public void handleSomething(Something<Two> something) {
                counterTwo.incrementAndGet();
            }
        });
        eventBus.post(new Something<>(new Object()));
        eventBus.post(new Something<>(One.INSTANCE));
        eventBus.post(new Something<>(Two.INSTANCE));
        assertEquals(3, counterAll.get());
        assertEquals(1, counterOne.get());
        assertEquals(1, counterTwo.get());
    }

    public static class Something<T> {

        private final T instance;

        public Something(T instance) {
            this.instance = instance;
        }

    }

    public static enum One {
        INSTANCE
    }

    public static enum Two {
        INSTANCE
    }
}
