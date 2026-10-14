/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.handler;

import com.google.common.collect.ImmutableList;
import java.io.File;
import static java.lang.String.format;
import static java.util.Collections.emptyMap;
import java.util.List;
import jakarta.activation.DataSource;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_URLREADER;
import org.cmdbuild.etl.job.EtlLoadHandler;
import org.cmdbuild.etl.job.EtlLoaderApi;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import static org.cmdbuild.jobs.JobRun.JOB_OUTPUT;
import org.cmdbuild.utils.UrlHandler;
import org.cmdbuild.utils.UrlHandlerResponse;
import org.cmdbuild.utils.UrlHandlerResponseImpl;
import static org.cmdbuild.utils.io.CmIoUtils.countBytes;
import static org.cmdbuild.utils.io.CmIoUtils.isUrl;
import static org.cmdbuild.utils.io.CmIoUtils.tempFile;
import static org.cmdbuild.utils.io.CmIoUtils.urlToDataSource;
import static org.cmdbuild.utils.io.CmIoUtils.writeToFile;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class UrlReaderEtlLoadHandler implements EtlLoadHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final List<UrlHandler> urlHandlers;

    public UrlReaderEtlLoadHandler(List<UrlHandler> urlHandlers) {
        this.urlHandlers = (List) ImmutableList.builder().addAll(urlHandlers).add(new DefaultHandler()).build();
    }

    @Override
    public String getType() {
        return ETLHT_URLREADER;
    }

    @Override
    public WaterwayMessageData load(EtlLoaderApi api) {
        String urlParam = firstNotBlank(api.getConfig("urlParam"), "url");
        String url = isUrl(urlParam) ? urlParam : api.getParam(urlParam);
        if (isBlank(url)) {
            logger.info("source url is missing");
            return WaterwayMessageDataImpl.builder().withMeta(api.getMeta()).build();
        } else {
            logger.info("load data from url =< {} >", url);
            for (UrlHandler handler : urlHandlers) {
                if (handler.handlesUrl(url)) {
                    UrlHandlerResponse response = handler.loadFromUrl(url);
                    DataSource data = response.getDataSource();
                    logger.info("found data = {} ( {} {} ) with meta =\n\n{}\n", data.getName(), byteCountToDisplaySize(countBytes(data)), data.getContentType());
                    if (logger.isDebugEnabled()) {
                        File tempFile = tempFile();
                        writeToFile(data, tempFile);
                        logger.debug("data payload stored here for debug =< {} >", tempFile.getAbsolutePath());
                    }
                    return WaterwayMessageDataImpl.build(JOB_OUTPUT, data, map(api.getMeta()).with(map(response.getMeta()).mapKeys(k -> format("param_%s", k))));
                }
            }
            throw new IllegalStateException();
        }
    }

    private class DefaultHandler implements UrlHandler {

        @Override
        public boolean handlesUrl(String url) {
            return true;
        }

        @Override
        public UrlHandlerResponse loadFromUrl(String url) {
            return new UrlHandlerResponseImpl(urlToDataSource(url), emptyMap());//TODO meta
        }

    }

}
