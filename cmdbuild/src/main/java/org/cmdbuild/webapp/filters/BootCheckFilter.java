/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp.filters;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Strings.nullToEmpty;
import static com.google.common.collect.MoreCollectors.toOptional;
import com.google.common.eventbus.Subscribe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import static java.util.Arrays.stream;
import org.apache.commons.lang3.StringUtils;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.StringUtils.trimToNull;
import org.cmdbuild.cache.CacheService;
import static org.cmdbuild.common.http.HttpConst.CMDBUILD_MAINTENANCE_MODE_PASSTOKEN_HEADER_OR_COOKIE;
import org.cmdbuild.config.CoreConfiguration;
import org.cmdbuild.eventbus.EventBusService;
import org.cmdbuild.minions.MinionService;
import org.cmdbuild.minions.SystemReadyEvent;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNullOrNull;
import static org.cmdbuild.webapp.utils.LandingPageUtils.generateCustomLandingPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.OncePerRequestFilter;

@Configuration("BootCheckFilter")
public class BootCheckFilter extends OncePerRequestFilter {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final MinionService bootService;
    private final CoreConfiguration coreConfig;

    private final Object lock = new Object();

    private boolean systemReady = false;

    public BootCheckFilter(CoreConfiguration config, MinionService bootService, EventBusService systemEventService, CacheService cacheService) {
        this.bootService = checkNotNull(bootService);
        this.coreConfig = checkNotNull(config);
        systemEventService.getSystemEventBus().register(new Object() {
            @Subscribe
            public void handleSystemReadyEvent(SystemReadyEvent event) {
                synchronized (lock) {
                    logger.info("system running, disable boot services");
                    systemReady = true;
                    lock.notifyAll();
                }
            }
        });
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (systemReady) {
            doFilterAfterBootCheck(request, response, filterChain);
        } else {
            handleBootServices(request, response, filterChain);
        }
    }

    private void handleBootServices(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.trace("system booting, handle boot services");
        boolean isBootWsRequest = getRequestPath(request).matches("/services/rest/v./boot(/.*)?"),
                isUiRequest = getRequestPath(request).matches("/(index|ui|ui_dev)(/.*)?|");
        if (isBootWsRequest) {
            filterChain.doFilter(request, response);
        } else if (bootService.isWaitingForUser()) {
            if (isUiRequest) {
                doFilterAfterBootCheck(request, response, filterChain);//TODO check that this works correctly (configs might not be available yet a this point)
            } else {
                logger.debug("system boot is waiting for user, rejecting request for path = {}", getRequestPath(request));
//                addNoCacheHeaders(response);
                response.setContentType("application/json");
                response.setStatus(503);
                response.getWriter().write(toJson(map("success", false, "messages", list(map("level", "ERROR", "message", "system boot is waiting for operator intervention, unable to process this request", "show_user", true)))));
            }
        } else {
            logger.debug("system boot in progress, stalling request for path = {}", getRequestPath(request));
            synchronized (lock) {
                while (!systemReady) {
                    try {
                        lock.wait();
                    } catch (InterruptedException ex) {
                        throw runtime(ex);
                    }
                }
            }
            doFilterAfterBootCheck(request, response, filterChain);
        }
    }

    private void doFilterAfterBootCheck(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        if (coreConfig.isMaintenanceModeEnabled()) {
            handleMaintenanceMode(request, response, filterChain);
        } else {
            filterChain.doFilter(request, response);
        }
    }

    private void handleMaintenanceMode(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        logger.trace("maintenance mode enabled");
        String passToken = firstNotNullOrNull(trimToNull(request.getParameter(CMDBUILD_MAINTENANCE_MODE_PASSTOKEN_HEADER_OR_COOKIE)),
                trimToNull(request.getHeader(CMDBUILD_MAINTENANCE_MODE_PASSTOKEN_HEADER_OR_COOKIE)),
                request.getCookies() == null ? null : stream(request.getCookies()).filter((c) -> c.getName().equalsIgnoreCase(CMDBUILD_MAINTENANCE_MODE_PASSTOKEN_HEADER_OR_COOKIE)).collect(toOptional()).map(Cookie::getValue).map(StringUtils::trimToNull).orElse(null));

        boolean hasPassToken;
        if (isNotBlank(passToken)) {
            if (equal(passToken, coreConfig.getMaintenanceModePasstoken())) {
                hasPassToken = true;
            } else {
                logger.warn("invalid maintenance mode pass token");
                hasPassToken = false;
            }
        } else {
            hasPassToken = false;
        }

        if (hasPassToken) {
            filterChain.doFilter(request, response);
        } else {
            String requestPath = getRequestPath(request);
//            addNoCacheHeaders(response);
            if (requestPath.matches("^(/index|/|/ui(/|/index.html)?)?([&#].*)?$")) {
                response.setContentType("text/html");
                response.setStatus(200);
                response.getOutputStream().write(generateCustomLandingPage("Temporary Down for Maintenance", "We are performing scheduled maintenance.<br /> We should be back online shortly."));
            } else {
                response.setContentType("application/json");
                response.setStatus(503);
                response.getWriter().write(toJson(map("success", false, "messages", list(map("level", "ERROR", "message", "system maintenance in progress, unable to process this request. Please retry later", "show_user", true)))));
            }
        }
    }

    private void handleClusterFailed(HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setStatus(503);
        response.getWriter().write(toJson(map("success", false, "messages", list(map("level", "ERROR", "message", "cluster is not working properly", "show_user", true)))));
    }

    private static String getRequestPath(HttpServletRequest request) {
        return request.getServletPath() + nullToEmpty(request.getPathInfo());
    }
}
