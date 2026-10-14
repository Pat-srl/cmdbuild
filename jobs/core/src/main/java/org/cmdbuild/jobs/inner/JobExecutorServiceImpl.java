/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.PreDestroy;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.cmdbuild.jobs.JobExecutorService;
import org.cmdbuild.jobs.JobSessionService;
import static org.cmdbuild.utils.lang.CmExecutorUtils.namedThreadFactory;
import static org.cmdbuild.utils.lang.CmExecutorUtils.shutdownQuietly;
import static org.cmdbuild.utils.lang.CmStringUtils.abbreviate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class JobExecutorServiceImpl implements JobExecutorService {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private final ExecutorService executorService = Executors.newCachedThreadPool(namedThreadFactory(getClass()));
	private final ScheduledExecutorService scheduledExecutorService = Executors.newSingleThreadScheduledExecutor(namedThreadFactory(getClass()));

	private final JobSessionService jobSessionService;

	public JobExecutorServiceImpl(JobSessionService jobSessionService) {
		this.jobSessionService = checkNotNull(jobSessionService);
	}

	@PreDestroy
	public void stop() {
		shutdownQuietly(scheduledExecutorService, executorService);
	}

	@Override
	public <T> Future<T> executeJobAs(Callable<T> job, String user) {
		return executorService.submit(() -> runJob(job, user));
	}

	@Override
	public <T> Future<T> executeJobLaterAs(Callable<T> job, String user, long later) {
		return scheduledExecutorService.schedule(() -> runJob(job, user), later, TimeUnit.MILLISECONDS);
	}

	private <T> T runJob(Callable<T> job, String user) throws Exception {
            try {
                //TODO remove this service, merge with job run helper
                logger.debug("jobExecutorServiceImpl - running a callable");
                logger.debug("jobExecutorServiceImpl - creating job session context for user {}", user);
                jobSessionService.createJobSessionContextWithUser(user, "job %s", abbreviate(job));//TODO job name
                try {
                    logger.debug("jobExecutorServiceImpl - calling the job");
                    return job.call();
                } finally {
                    logger.debug("jobExecutorServiceImpl - destroying session context for user {}", user);
                    jobSessionService.destroyJobSessionContext();
                    logger.debug("jobExecutorServiceImpl - destroyed job session context for user {}", user);
                }
            } catch (Exception ex) {
                logger.error("error in job " + job, ex);//TODO store/mark exception
                throw ex;
            }
	}

}
