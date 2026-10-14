/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.utils.lang;

import java.util.function.Consumer;
import java.util.function.Supplier;
import org.apache.commons.lang3.concurrent.ConcurrentException;
import org.apache.commons.lang3.concurrent.LazyInitializer;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;

/**
 *
 * @author ataboga
 */
public class CmConcurrentUtils {

    public static <T> CmLazyInitializer<T> lazyInitializer(Supplier<T> init) {
        return new CmLazyInitializer<T>() {
            @Override
            protected T initialize() {
                return init.get();
            }
        };
    }

    public static <T> CmLazyInitializer<T> lazyInitializer(Supplier<T> init, Consumer<T> close) {
        return new CmLazyInitializer<T>() {
            @Override
            protected T initialize() {
                return init.get();
            }

            @Override
            public void close() {
                close.accept(get());
            }
        };
    }

    public static class CmLazyInitializer<T> extends LazyInitializer<T> {

        @Override
        public T get() {
            try {
                return super.get();
            } catch (ConcurrentException ex) {
                throw runtime(ex);
            }
        }

        @Override
        public void close() {
            try {
                super.close();
            } catch (ConcurrentException ex) {
                throw runtime(ex);
            }
        }
    };
}
