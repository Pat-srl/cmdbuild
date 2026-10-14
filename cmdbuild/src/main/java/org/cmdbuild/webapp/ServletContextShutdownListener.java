/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.webapp;

import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import static java.time.ZonedDateTime.now;
import java.util.List;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.jobs.JobRun;
import org.cmdbuild.jobs.JobService;
import org.cmdbuild.minions.MinionService;
import org.cmdbuild.requestcontext.RequestContextService;
import org.cmdbuild.systemplugin.SystemPluginService;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmExceptionUtils.runtime;
import static org.cmdbuild.utils.lang.CmExecutorUtils.sleepSafe;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

@WebListener
public class ServletContextShutdownListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent event) {
        //do nothing
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        logger.info("received context destroyed event");
        try {
            WebApplicationContext applicationContext = WebApplicationContextUtils.getRequiredWebApplicationContext(event.getServletContext());
            RequestContextService requestContextService = applicationContext.getBean(RequestContextService.class);
            requestContextService.initCurrentRequestContext("servlet context destroyed event");
            try {
                applicationContext.getBean(SystemPluginService.class).removeSystemPlugins();
                awaitCompletionJobs(applicationContext.getBean(JobService.class));
                stopServices(applicationContext.getBean(MinionService.class));

            } finally {
                requestContextService.destroyCurrentRequestContext();
            }
        } catch (Exception ex) {
            logger.error("error processing context destroyed event", ex);
            throw runtime(ex);
        }
    }

    private void stopServices(MinionService systemService) {
        systemService.stopSystem();
    }

    private void awaitCompletionJobs(JobService jobService) {
        boolean restart = false;
        int watchDog = 60;
        while (!restart || watchDog == 0) {
            List<JobRun> jobs = jobService.getJobRuns(DaoQueryOptionsImpl.builder().withFilter("{\"attribute\":{\"simple\":{\"attribute\":\"status\",\"operator\":\"in\",\"value\":[\"running\"]}}}").build()).elements();
            if (jobs.isEmpty()) {
                restart = true;
            } else {
                List<JobRun> jobRunning = list(jobs).filter(j -> j.getTimestamp().toEpochSecond() > now().toEpochSecond() - 60);
                logger.info("there are =< {} > jobs running now under 60 seconds", jobRunning.size());
                if (jobRunning.isEmpty()) {
                    restart = true;
                } else {
                    sleepSafe(1000);
                }
            }
            watchDog--;
        }
    }
}
