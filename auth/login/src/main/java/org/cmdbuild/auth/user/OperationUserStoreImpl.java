package org.cmdbuild.auth.user;

import java.lang.invoke.MethodHandles;
import org.cmdbuild.requestcontext.RequestContextHolder;
import org.cmdbuild.requestcontext.RequestContextService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component("operationUserStore")
@Primary
public class OperationUserStoreImpl implements OperationUserStore {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    private final RequestContextHolder<OperationUser> holder;

    public OperationUserStoreImpl(RequestContextService requestContextService) {
        holder = requestContextService.createRequestContextHolder(OperationUserImpl::anonymousOperationUser);
    }

    @Override
    public OperationUser getUser() {
        return holder.get();
    }

    @Override
    public void setUser(OperationUser user) {
        LOGGER.debug("set thread local user to = {}", user);
        holder.set(user);
    }
}
