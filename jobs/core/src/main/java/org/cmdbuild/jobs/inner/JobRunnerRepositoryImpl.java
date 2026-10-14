/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.uniqueIndex;
import java.util.List;
import java.util.Map;
import org.cmdbuild.jobs.JobRunner;
import org.cmdbuild.jobs.JobRunnerRepository;
import org.springframework.stereotype.Component;

@Component
public class JobRunnerRepositoryImpl implements JobRunnerRepository {

	private final Map<String, JobRunner> jobRunners;

	public JobRunnerRepositoryImpl(List<JobRunner> jobRunners) {
		this.jobRunners = uniqueIndex(jobRunners, JobRunner::getJobRunnerName);
	}

	@Override
	public JobRunner getJobRunner(String name) {
		return checkNotNull(jobRunners.get(name), "job runner not found for name = %s", name);
	}

}
