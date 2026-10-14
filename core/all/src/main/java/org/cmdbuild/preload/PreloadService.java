/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.preload;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.cmdbuild.config.CoreConfiguration; 
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.url.CmUrlUtils.decodeUrlPathAndParams;
import org.cmdbuild.utils.url.UrlPathAndParams;
import org.cmdbuild.utils.ws3.inner.WsRestRequestImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.cmdbuild.utils.ws3.api.WsResourceRepository;
import org.cmdbuild.utils.ws3.inner.WsRequestHandler;
import org.cmdbuild.utils.ws3.inner.WsRequestHandlerImpl;
import org.cmdbuild.minions.PostStartup;

@Component
public class PreloadService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final CoreConfiguration config;
    private final WsResourceRepository wsResourceRepository;

    public PreloadService(CoreConfiguration config, WsResourceRepository wsResourceRepository) {
        this.config = checkNotNull(config);
        this.wsResourceRepository = checkNotNull(wsResourceRepository);
    }

    public void runPreload() {
        runPreloadAtStartup();
    }

    @PostStartup(delay = "PT5s" )
    public void runPreloadAtStartup() {
        if (config.isPreloadEnabled() && !config.getPreloadRestUrls().isEmpty()) {
            logger.info("preload configured rest resources");
            WsRequestHandler requestHandler = new WsRequestHandlerImpl(wsResourceRepository);
            config.getPreloadRestUrls().forEach(r -> {
                try {
                    logger.info("preload resource =< {} >", r);
                    UrlPathAndParams pathAndParams = decodeUrlPathAndParams(r);
                    String path = pathAndParams.getPath();
                    //TODO check this (localization ?? )
                    String requestType;
                    Matcher matcher = Pattern.compile("(.*):(.*)").matcher(r);
                    if (matcher.matches()) {
                        requestType = matcher.group(1);
                        path = path.replace(requestType + ":", "");
                    } else {
                        requestType = "get";
                    }
                    requestHandler.handleRequest(new WsRestRequestImpl(null, format("ws4rest:%s:%s", requestType, path), pathAndParams.getParams(), map(), map("CMDBuild-Localization", config.getDefaultLanguage()), null));
                } catch (Exception ex) {
                    logger.warn("error preloading resource =< {} >", r, ex);
                }
            });
            logger.info("preload completed");
        }
    }

}
