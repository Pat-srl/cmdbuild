/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.runners.script;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;
import java.util.Map;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRunContext;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.script.ScriptService;
import static org.cmdbuild.utils.encode.CmPackUtils.unpackIfPacked;
import static org.cmdbuild.utils.lang.CmConvertUtils.parseEnumInvalidToNull;
import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnum;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.lang.CmStringUtils.mapToLoggableStringLazy;
import static org.cmdbuild.utils.lang.CmStringUtils.normalize;
import org.cmdbuild.script.ScriptType;
import static org.cmdbuild.script.ScriptType.ST_GROOVY;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ScriptJobRunner implements JobRunner {

    public static final String SCRIPT_JOB_TYPE = "script",
            SCRIPT_CONFIG_SCRIPT = "script",
            SCRIPT_CONFIG_LANGUAGE = "language",
            SCRIPT_CONFIG_CLASSPATH = "classpath";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ScriptService scriptService;

    public ScriptJobRunner(ScriptService scriptService) {
        this.scriptService = checkNotNull(scriptService);
    }

    @Override
    public String getJobRunnerName() {
        return SCRIPT_JOB_TYPE;
    }

    @Override
    public void vaildateJob(JobData jobData) {
        new ScriptJobRunHelper(jobData);
    }

    @Override
    public void runJob(JobData jobData, JobRunContext jobContext) {
        new ScriptJobRunHelper(jobData).run();
    }

    private class ScriptJobRunHelper {

        private final JobData jobData;
        private final String script;
        private final String type;

        public ScriptJobRunHelper(JobData jobData) {
            this.jobData = checkNotNull(jobData);
            script = unpackIfPacked(jobData.getConfigNotBlank(SCRIPT_CONFIG_SCRIPT));
            type = firstNotBlank(jobData.getConfig(SCRIPT_CONFIG_LANGUAGE), serializeEnum(ST_GROOVY)).trim().toLowerCase();
            checkArgument(parseEnumInvalidToNull(type, ScriptType.class) != null, "invalid script type =< %s >", jobData.getConfig(SCRIPT_CONFIG_LANGUAGE));
        }

        public void run() {
            Map<String, Object> result = scriptService.helper().withScript(script).withClassLoader(jobData.getConfig(SCRIPT_CONFIG_CLASSPATH))
                    .withLogger(LoggerFactory.getLogger(format("%s.JOB.%s", getClass().getName(), normalize(jobData.getCode()))))
                    .execute(map("job", jobData));
            logger.debug("executed job = {}, result =\n\n{}\n", jobData, mapToLoggableStringLazy(result));
        }

    }

}
