/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.console;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Splitter;
import static com.google.common.base.Strings.nullToEmpty;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.eventbus.Subscribe;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import jakarta.annotation.Nullable;
import jakarta.annotation.PreDestroy;
import static java.lang.String.format;
import java.util.Collection;
import static java.util.Collections.emptyList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.joining;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.api.CmApiServiceExt;
import static org.cmdbuild.auth.role.RolePrivilege.RP_SYSTEM_ACCESS;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.auth.session.model.Session;
import org.cmdbuild.event.EventService;
import org.cmdbuild.event.RawEvent;
import org.cmdbuild.event.WebsocketSessionClosedEvent;
import org.cmdbuild.requestcontext.RequestContextService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmConvertUtils.isPrimitiveOrWrapper;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmReflectionUtils;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNullSafe;
import static org.cmdbuild.utils.lang.KeyFromPartsUtils.key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ConsoleHelperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CmApiServiceExt apiService;
    private final EventService eventService;
    private final SessionService sessionService;
    private final RequestContextService requestContextService;

    private final ExecutorService executor = Executors.newCachedThreadPool(namedThreadFactory(getClass()));

    private final Cache<String, ConsoleSessionHelper> helpers = CacheBuilder.newBuilder().expireAfterAccess(1, TimeUnit.HOURS).build();

    public ConsoleHelperService(CmApiServiceExt apiService, EventService eventService, SessionService sessionService, RequestContextService requestContextService) {
        this.apiService = checkNotNull(apiService);
        this.eventService = checkNotNull(eventService);
        this.sessionService = checkNotNull(sessionService);
        this.requestContextService = checkNotNull(requestContextService);
        eventService.getEventBus().register(new Object() {
            @Subscribe
            public void handleRawEvent(RawEvent event) {
                if (event.hasAction()) {
                    switch (event.getAction()) {
                        case "console.open" ->
                            handleConsoleOpen(event.getSessionId(), event.getClientId());
                        case "console.exec" ->
                            handleConsoleExec(event.getMessageId(), event.getSessionId(), event.getClientId(), event.getStringNotBlank("line"));
                        case "console.autocomplete" ->
                            handleConsoleAutocomplete(event.getMessageId(), event.getSessionId(), event.getClientId(), event.getStringValue("line"));
                    }
                }
            }

            @Subscribe
            public void handleRawEvent(WebsocketSessionClosedEvent event) {
                logger.info("close console session for sessionId =< {} > consoleId =< {} >", event.getSessionId(), event.getClientId());
                helpers.invalidate(key(event.getSessionId(), event.getClientId()));
                logger.info("active console session count = {}", helpers.asMap().size());
            }
        });
    }

    @PreDestroy
    public void cleanup() {
        shutdownQuietly(executor);
    }

    private void handleConsoleOpen(String sessionId, String consoleId) {
        validateSession(sessionId);
        logger.info("create console session for sessionId =< {} > consoleId =< {} >", sessionId, consoleId);
        helpers.put(key(sessionId, checkNotBlank(consoleId)), new ConsoleSessionHelper(sessionId));
    }

    private void handleConsoleExec(String requestId, String sessionId, String consoleId, String line) {
        validateSession(sessionId);
        checkNotBlank(requestId);
        checkNotBlank(line);
        ConsoleSessionHelper helper = getHelper(sessionId, consoleId);
        try {
            Object output = helper.exec(line);
            if (output != null && !isPrimitiveOrWrapper(output)) {
                output = toStringOrNullSafe(output);
            }
            eventService.sendEventMessage(sessionId, "console.response", map("requestId", requestId, "output", output, "success", true));
        } catch (Exception ex) {
            logger.error("console error processing line =< {} >", line, ex);
            eventService.sendEventMessage(sessionId, "console.response", map("requestId", requestId, "output", ex.toString(), "success", false));
        }
    }

    private void handleConsoleAutocomplete(String requestId, String sessionId, String consoleId, @Nullable String line) {
        validateSession(sessionId);
        checkNotBlank(requestId);
        ConsoleSessionHelper helper = getHelper(sessionId, consoleId);
        logger.debug("autocomplete line =< {} >", line);
        try {
            List<String> candidates = list();
            if (isBlank(line) || !line.contains(".")) {
                ((Collection<Object>) helper.getGroovyShell().getContext().getVariables().keySet()).stream().map(CmStringUtils::toStringNotBlank)
                        .filter(k -> k.startsWith(nullToEmpty(line)))
                        .map(k -> k.substring(nullToEmpty(line).length()))
                        .forEach(candidates::add);
            } else {
                executor.submit(() -> {
                    requestContextService.initCurrentRequestContext("console session");//TODO
                    sessionService.setCurrent(sessionId);
                    try {
                        Matcher matcher = Pattern.compile("(([^.]+)[.])(.*)").matcher(line.trim());
                        if (matcher.find()) {
                            String var = matcher.group(2), element = nullToEmpty(matcher.group(3)), prefix = matcher.group(1);
                            Object bean = helper.getGroovyShell().getVariable(var);
                            if (bean != null) {
                                while (true) {
                                    Matcher elementMatcher = Pattern.compile("(([^.(]+)([(]([^)]*)[)])?[.])(.*)").matcher(element);
                                    if (elementMatcher.matches()) {
                                        prefix += elementMatcher.group(1);
                                        element = elementMatcher.group(5);
                                        String methodName = elementMatcher.group(2), arg = elementMatcher.group(4);
                                        bean = checkNotNull(CmReflectionUtils.executeMethod(bean, methodName, list().accept(l -> {
                                            if (isNotBlank(arg)) {
                                                Splitter.on(",").trimResults().splitToList(arg).stream().map(e -> e.replaceAll("^\"|\"$", "")).forEach(l::add);
                                            }
                                        })));
                                    } else {
                                        String pattern = element;
                                        list(bean.getClass().getMethods()).stream()
                                                .filter(m -> m.getName().startsWith(pattern))
                                                .map(m -> format("%s(%s)", m.getName().substring(pattern.length()), list(m.getParameters()).map(p -> format("%s %s", p.getType().getSimpleName(), p.getName())).collect(joining(", "))))
                                                .forEach(candidates::add);
                                        break;
                                    }
                                }
                            }
                        }
                    } finally {
                        sessionService.setCurrent(null);
                        requestContextService.destroyCurrentRequestContext();
                    }
                }).get();
            }
            eventService.sendEventMessage(sessionId, "console.response", map("requestId", requestId, "candidates", candidates, "success", true));
        } catch (Exception ex) {
            logger.error("console error processing autocomplete line =< {} >", line, ex);
            eventService.sendEventMessage(sessionId, "console.response", map("requestId", requestId, "candidates", emptyList(), "success", false));
        }

    }

    private ConsoleSessionHelper getHelper(String sessionId, String consoleId) {
        return checkNotNull(helpers.getIfPresent(key(sessionId, checkNotBlank(consoleId))), "console session not found for sessionId =< %s > consoleId =< %s >", sessionId, consoleId);
    }

    private Session validateSession(String sessionId) {
        Session session = sessionService.getSessionById(sessionId);
        session.getOperationUser().checkPrivileges(p -> p.hasPrivileges(RP_SYSTEM_ACCESS));
        return session;
    }

    private class ConsoleSessionHelper {

        private final String sessionId;
        private final GroovyShell groovyShell;

        public ConsoleSessionHelper(String sessionId) {
            this.sessionId = checkNotBlank(sessionId);
            groovyShell = new GroovyShell(new Binding(map(apiService.getCmApiAsDataMap()).with("logger", logger)));
        }

        public Object exec(String line) throws InterruptedException, ExecutionException {
            return executor.submit(() -> {
                requestContextService.initCurrentRequestContext("console session");//TODO
                sessionService.setCurrent(sessionId);
                try {
                    return groovyShell.evaluate(checkNotBlank(line));
                } finally {
                    sessionService.setCurrent(null);
                    requestContextService.destroyCurrentRequestContext();
                }
            }).get();
        }

        public GroovyShell getGroovyShell() {
            return groovyShell;
        }

    }

}
