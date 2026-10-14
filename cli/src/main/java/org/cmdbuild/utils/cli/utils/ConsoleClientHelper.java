/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cli.utils;

import com.google.common.base.Objects;
import com.google.common.base.Strings;
import jakarta.websocket.ClientEndpointConfig;
import jakarta.websocket.CloseReason;
import jakarta.websocket.ContainerProvider;
import jakarta.websocket.Endpoint;
import jakarta.websocket.EndpointConfig;
import jakarta.websocket.MessageHandler;
import jakarta.websocket.Session;
import jakarta.websocket.WebSocketContainer;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.common.http.HttpConst;
import org.cmdbuild.utils.console.CmCandidate;
import org.cmdbuild.utils.console.CmConsole;
import org.cmdbuild.utils.json.CmJsonUtils;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import org.cmdbuild.utils.lang.CmExceptionUtils;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import org.cmdbuild.utils.lang.CmMapUtils;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.lang.CmStringUtils;
import static org.cmdbuild.utils.lang.CmStringUtils.toStringOrNullSafe;
import static org.cmdbuild.utils.random.CmRandomUtils.randomId;
import org.jline.reader.Candidate;
import org.jline.reader.EndOfFileException;
import org.jline.reader.ParsedLine;
import org.jline.reader.UserInterruptException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsoleClientHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BlockingQueue<Map<String, Object>> queue = new ArrayBlockingQueue<>(10);

    public void run(String sessionId, String baseUrl) throws Exception {
        System.out.printf("open connection to < %s > ... ", baseUrl);
        checkNotBlank(sessionId);
        String prompt = baseUrl;
        ClientEndpointConfig cec = ClientEndpointConfig.Builder.create().configurator(new ClientEndpointConfig.Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put(HttpConst.CMDBUILD_AUTHORIZATION_HEADER, Collections.singletonList(sessionId));
            }
        }).build();
        WebSocketContainer websocketClient = ContainerProvider.getWebSocketContainer();
        CompletableFuture connectionReadyFuture = new CompletableFuture();
        Session websocketSession = websocketClient.connectToServer(new Endpoint() {
            @Override
            public void onOpen(Session session, EndpointConfig config) {
                try {
                    logger.debug("websocket client session opened = {}", session.getId());
                    session.addMessageHandler(String.class, (MessageHandler.Whole<String>) (String msg) -> {
                        try {
                            logger.debug("received message =< {} >", msg);
                            Map<String, Object> payload = CmJsonUtils.fromJson(msg, CmJsonUtils.MAP_OF_OBJECTS);
                            switch (Strings.nullToEmpty(CmStringUtils.toStringOrNull(payload.get("_event")))) {
                                case "socket.session.ok" -> {
                                    logger.debug("connection ready");
                                    connectionReadyFuture.complete(true);
                                }
                                case "console.response" ->
                                    queue.put(payload);
                                case "socket.error" -> {
                                    ((CompletableFuture) connectionReadyFuture).completeExceptionally(CmExceptionUtils.runtime("error opening socket connection: %s", msg));
                                    System.out.println("\nsocket error");
                                    System.exit(1);
                                }
                            }
                        } catch (Exception ex) {
                            logger.error("error processing message", ex);
                        }
                    });
                    session.getBasicRemote().sendText(CmJsonUtils.toJson(CmMapUtils.map("_action", "socket.session.login", "token", sessionId)));
                    logger.debug("session login request sent");
                } catch (Exception ex) {
                    logger.error("error processing websocket open session event", ex);
                }
            }

            @Override
            public void onClose(Session session, CloseReason closeReason) {
                logger.debug("websocket session closed, session = {}, reason = {}", session.getId(), closeReason);
                System.out.println("\nconnection closed");
                System.exit(1);
            }
        }, cec, new URI(checkNotBlank(baseUrl).replaceFirst("http", "ws") + "/services/websocket/v1/main"));
        connectionReadyFuture.get(5, TimeUnit.SECONDS);
        websocketSession.getBasicRemote().sendText(CmJsonUtils.toJson(CmMapUtils.map("_action", "console.open", "_id", randomId())));
        System.out.println("ready");

        CmConsole console = CmConsole.builder().withCompleter((ParsedLine line, List<Candidate> candidates) -> {
            logger.info("line =< {} >", line.word());
            try {
                String part = line.word();
                String requestId = randomId();
                websocketSession.getBasicRemote().sendText(toJson(map("_action", "console.autocomplete", "_id", requestId, "line", part)));
                Map<String, Object> response = getResponse(requestId);
                ((List<String>) response.get("candidates")).stream().map(c -> part + c).map(CmCandidate::new).forEach(candidates::add);
            } catch (Exception ex) {
                logger.warn("error processing autocomplete with buffer =< {} > cursor = {}", line.word(), ex);
                candidates.clear();
            }
        }).withHistory(".cm_console_history").build();

        try {
            while (true) {
                String line = console.readLine(String.format("%s : ", prompt));
                if (StringUtils.isNotBlank(line)) {
                    switch (line) {
                        case "quit", "exit" -> {
                            throw new UserInterruptException("exit");
                        }
                        default -> {
                            try {
                                String requestId = randomId();
                                websocketSession.getBasicRemote().sendText(toJson(map("_action", "console.exec", "_id", requestId, "line", line)));
                                Map<String, Object> response = getResponse(requestId);
                                Object res = response.get("output");
                                if (res != null) {
                                    console.writeLine("%s > %s", prompt, toStringOrNullSafe(res));
                                }
                            } catch (Exception ex) {
                                System.out.println("ERROR : " + ex.toString());
                                System.out.println();
                            }
                        }
                    }
                }
            }
        } catch (UserInterruptException | EndOfFileException e) {
            logger.info("user interrupt!");
        } finally {
            websocketSession.close();
            console.close();
        }
    }

    private Map<String, Object> getResponse(String requestId) {
        try {
            Map<String, Object> response;
            do {
                response = queue.take();
            } while (!Objects.equal(requestId, response.get("requestId")));
            return response;
        } catch (InterruptedException ex) {
            throw runtime(ex);
        }
    }

}
