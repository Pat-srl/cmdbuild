/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.bim;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.net.URI;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import static org.cmdbuild.bim.BimService.BIMSERVER_SOURCE_PROJECT_ID;
import org.cmdbuild.utils.UrlHandler;
import org.cmdbuild.utils.UrlHandlerResponse;
import org.cmdbuild.utils.UrlHandlerResponseImpl;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLong;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.cmdbuild.utils.url.CmUrlUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class BimserverUrlHandler implements UrlHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final BimService bimService;

    public BimserverUrlHandler(BimService bimService) {
        this.bimService = checkNotNull(bimService);
    }

    @Override
    public boolean handlesUrl(String url) {
        return url.startsWith("bimserver:");
    }

    @Override
    public UrlHandlerResponse loadFromUrl(String url) {
        logger.debug("load data from url =< {} >", url);
        Matcher matcher = Pattern.compile("bimserver:/*project/([0-9]+)/ifc").matcher(checkNotBlank(url));
        checkArgument(matcher.find(), "illegal bimserver url format, for url =< %s >", url);
        long bimProject = toLong(matcher.group(1));
        BimProject project = bimService.getProjectById(bimProject);
        String ifcFormat = CmUrlUtils.decodeUrlParams(URI.create(url).getQuery()).get("ifcFormat");
        return new UrlHandlerResponseImpl(toDataSource(bimService.downloadIfcFile(project.getId(), ifcFormat)), map(BIMSERVER_SOURCE_PROJECT_ID, project.getId().toString()));
    }

}
