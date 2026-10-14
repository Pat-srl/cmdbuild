/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cluster;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import org.cmdbuild.auth.session.SessionService;
import org.cmdbuild.fault.FaultEventCollectorService;
import org.cmdbuild.temp.TempService;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.cmdbuild.utils.ws3.api.WsResourceRepository;
import org.cmdbuild.utils.ws3.api.WsWarningSource;
import org.cmdbuild.utils.ws3.inner.WsRequestHandler;
import org.cmdbuild.utils.ws3.inner.WsRequestHandlerImpl;
import org.cmdbuild.utils.ws3.inner.WsResponsePrinter;
import static org.cmdbuild.utils.ws3.utils.WsRpcUtils.parseRpcRequestV4;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class RpcHelperImpl implements RpcHelper {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final WsRequestHandler requestHandler;
    private final SessionService sessionService;
    private final FaultEventCollectorService faultService;
    private final TempService tempService;

    public RpcHelperImpl(TempService tempService, SessionService sessionService, WsResourceRepository repository, WsWarningSource warningSource, FaultEventCollectorService faultService) {
        this.requestHandler = new WsRequestHandlerImpl(repository, warningSource);
        this.sessionService = checkNotNull(sessionService);
        this.faultService = checkNotNull(faultService);
        this.tempService = checkNotNull(tempService);
    }

    @Override
    public String invokeRpcMethod(@Nullable String sessionId, String payload) {
        if (isNotBlank(sessionId)) {
            sessionService.setCurrent(sessionId);
        }
        try {
            logger.debug("invoke rpc method =< {} >", abbreviate(payload));
            WsResponsePrinter printer = requestHandler.handleRequest(parseRpcRequestV4(payload)).prepareResponse();
            if (printer.isJson()) {//TODO check also for `attachment` file download headers
                return printer.getResponseAsString();
            } else {
                DataSource responseData = printer.getResponseAsDataSource();
//                Map<String, String> responseHeaders = response.getResponseHeaders();
                String tempId = tempService.putTempData(responseData);//TODO set temp ttl, pin temp data to current session id; TODO store other headers
//                Content-Disposition: inline; filename="cmdbuild_sys.log"//TODO
                return toJson(map("success", true, "_response", map(
                        "type", "download",
                        "downloadId", tempId,
                        "contentType", responseData.getContentType(),
                        "fileName", responseData.getName(),
                        "size", countBytes(responseData)
                )));
            }
        } catch (Exception ex) {
            logger.error("error processing request", ex);
            return toJson(map("success", false, "messages", faultService.buildMessagesForJsonResponse(ex)));
        } finally {
            if (isNotBlank(sessionId)) {
                sessionService.setCurrent(null);
            }
        }
    }

}
