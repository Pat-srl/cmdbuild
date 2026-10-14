/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.inject.Provider;
import java.lang.reflect.Method;
import java.util.Stack;
import java.util.concurrent.Callable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.api.ApiImpersonateHelper;
import org.cmdbuild.api.ExtendedApi;
import org.cmdbuild.auth.session.ImpersonateRequest;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.TransientSessionHelper;
import org.cmdbuild.auth.user.OperationUser;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmReflectionUtils.wrapProxy;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringSafe;
import org.cmdbuild.utils.lang.ProxyWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ApiImpersonateHelperImpl implements ApiImpersonateHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final Provider<ExtendedApi> extendedApiProvider;
    private final SessionService sessionService;

    public ApiImpersonateHelperImpl(Provider<ExtendedApi> extendedApi, SessionService sessionService) {
        this.extendedApiProvider = checkNotNull(extendedApi);
        this.sessionService = checkNotNull(sessionService);
    }

    @Override
    public ExtendedApi buildImpersonateApiWrapper(ImpersonateRequest request) {
        logger.debug("build impersonate api wrapper for request = {}", request);
        checkArgument(isNotBlank(request.getUsername()) || isNotBlank(request.getGroup()) || isNotBlank(request.getSponsor()), "cannot impersonate, both username and group and sponsor are null");
        ExtendedApi inner = checkNotNull(extendedApiProvider.get());
        return wrapImpersonateProxy(ExtendedApi.class, inner, request, true);
    }

    @Override
    public <O> O run(ImpersonateRequest impersonateRequest, Callable<O> runnable) {
        try {
            return (O) wrapImpersonateProxy(Callable.class, runnable, impersonateRequest, false).call();
        } catch (Exception ex) {
            throw runtime(ex);
        }
    }

    private <T> T wrapImpersonateProxy(Class<T> type, T inner, ImpersonateRequest request, boolean wrapResponseBean) {
        return wrapProxy(type, inner, new ProxyWrapper() {

            private final Stack<Runnable> afterAction = new Stack();

            @Override
            public void beforeMethodInvocation(Method method, Object[] params) {
                OperationUser currentUser = sessionService.getCurrentSession().getOperationUser();
                if ((!request.hasUsername() || equal(request.getUsername(), currentUser.getUsername()))
                        && (!request.hasGroup() || equal(request.getGroup(), currentUser.getDefaultGroupNameOrNull()))
                        && (!request.hasSponsor() || equal(request.getSponsor(), currentUser.getSponsorUsername()))) {
                    logger.debug("no need to impersonate request = {} before invoking method = {}, already running with correct user/etc", request, method);
                    afterAction.push(() -> {
                        logger.debug("no need to deimpersonate after invoking method = {}, already running with correct user", method);
                    });
                } else {
                    logger.debug("impersonate = {} before invoking method = {}", request, method);
                    if (request.isTransient()) {
                        TransientSessionHelper transientSession = sessionService.createAndSetTransient(request.getSponsor());
                        afterAction.push(() -> {
                            logger.debug("deimpersonate after invoking method = {}", method);
                            transientSession.close();
                        });
                    } else {
                        sessionService.impersonate(request);
                        afterAction.push(() -> {
                            logger.debug("deimpersonate after invoking method = {}", method);
                            sessionService.deimpersonate();
                        });
                    }
                }
            }

            @Override
            public Object afterSuccessfullMethodInvocation(Method method, Object[] params, Object response) {
                if (wrapResponseBean) {
                    if (response != null) {
                        Class resType = method.getReturnType();
                        if (!isPrimitiveOrWrapper(resType) && resType.getPackageName().matches("^org.cmdbuild.(api.fluent|workflow.core.fluentapi|workflow.api|api).*")) {
                            try {
                                response = wrapImpersonateProxy(resType, (T) response, request, wrapResponseBean);
                            } catch (Exception ex) {
                                throw runtime(ex, "error wrapping impersonate proxy for object =< %s > return value of method =< %s >", toStringSafe(response), method);
                            }
                        }
                    }
                }
                return response;
            }

            @Override
            public void afterMethodInvocation(Method method, Object[] params) {
                afterAction.pop().run();
            }
        });
    }

}
