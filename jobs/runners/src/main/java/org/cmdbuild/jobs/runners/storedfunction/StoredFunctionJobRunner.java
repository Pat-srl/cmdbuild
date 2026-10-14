/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.storedfunction;

import static com.google.common.base.Functions.identity;
import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import java.util.Map;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.cmdbuild.dao.core.q3.DaoService;
import org.cmdbuild.dao.core.q3.ResultRow;
import org.cmdbuild.dao.function.StoredFunction;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringInline;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class StoredFunctionJobRunner implements JobRunner {

    public static final String STORED_FUNCTION_JOB_TYPE = "stored_function",
            STORED_FUNCTION_PARAM = "function";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DaoService dao;

    public StoredFunctionJobRunner(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public String getJobRunnerName() {
        return STORED_FUNCTION_JOB_TYPE;
    }

    @Override
    public void vaildateJob(JobData jobData) {
        jobData.getConfigNotBlank(STORED_FUNCTION_PARAM);
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        String functionName = jobData.getConfigNotBlank(STORED_FUNCTION_PARAM);
        StoredFunction function = dao.getFunctionByName(functionName);
        logger.info(marker(), "execute stored function = {}", function);
        Map<String, Object> functionParams = map(function.getInputParameterNames(), identity(), jobData.getConfig()::get);
        List<ResultRow> result = dao.selectFunction(function, functionParams).run();
        for (int i = 0; i < result.size(); i++) {
            logger.info(marker(), "result row {} : {}", i, mapToLoggableStringInline(result.get(i).asMap()));
        }
    }

}
