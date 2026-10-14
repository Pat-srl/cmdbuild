package org.cmdbuild.api;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.concurrent.Callable;
import static org.cmdbuild.auth.AuthConst.SYSTEM_USER;

public interface ImpersonateApi<T> {

    ImpersonateApi username(String username);

    ImpersonateApi group(String group);

    ImpersonateApi sponsor(String sponsor);

    T impersonate();

    T transientImpersonate();

    <O> O call(Callable<O> callable);

    default ImpersonateApi system() {
        return username(SYSTEM_USER);
    }

    default ImpersonateApi admin() {
        return system();
    }

    default ImpersonateApi actingAs(String sponsor) {
        return sponsor(sponsor);
    }

    default T then() {
        return impersonate();
    }

    default void run(Runnable runnable) {
        checkNotNull(runnable);
        call(() -> {
            runnable.run();
            return null;
        });
    }
}
