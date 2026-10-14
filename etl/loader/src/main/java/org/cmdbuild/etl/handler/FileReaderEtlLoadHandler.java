/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.handler;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import jakarta.activation.DataSource;
import static org.apache.commons.io.FileUtils.byteCountToDisplaySize;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_FILEREADER;
import org.cmdbuild.etl.handler.FileReaderHelperService.FileReaderHelper;
import org.cmdbuild.etl.job.EtlLoadHandler;
import org.cmdbuild.etl.job.EtlLoaderApi;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import org.cmdbuild.etl.waterway.message.WaterwayMessageDataImpl;
import static org.cmdbuild.jobs.JobRun.JOB_OUTPUT;
import static org.cmdbuild.utils.io.CmIoUtils.toDataSource;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FileReaderEtlLoadHandler implements EtlLoadHandler {

    public static final String FILEREADER_FILENAME_META = "filename";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final FileReaderHelperService helperService;

    public FileReaderEtlLoadHandler(FileReaderHelperService helperService) {
        this.helperService = checkNotNull(helperService);
    }

    @Override
    public String getType() {
        return ETLHT_FILEREADER;
    }

    @Override
    public WaterwayMessageData load(EtlLoaderApi api) {
        FileReaderHelper helper = helperService.newHelper(api.getConfig());
        File file = helper.getFileForImport();
        DataSource data;
        if (file == null) {
            logger.info("file not found");
            data = null;
        } else {
            data = toDataSource(file);
            logger.info("found file = {} ( {} {} )", file.getAbsolutePath(), byteCountToDisplaySize(file.length()), data.getContentType());
            helper.handlePostImportAction();//TODO handle error in downstream handlers (??)
        }
        return WaterwayMessageDataImpl.build(JOB_OUTPUT, data, map(api.getMeta()).with(FILEREADER_FILENAME_META, file == null ? null : file.getAbsolutePath()));
    }

}
