/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public interface JobExecutorService {

	final static String JOBUSER_SYSTEM = "org.cmdbuild.jobs.SYSTEM",
			JOBUSER_NOBODY = "org.cmdbuild.jobs.NOBODY";

	<T> Future<T> executeJobAs(Callable<T> job, String user);

	<T> Future<T> executeJobLaterAs(Callable<T> job, String user, long later);

	default Future<Void> executeJobAs(Runnable job, String user) {
		return executeJobAs((Callable<Void>) () -> {
			job.run();
			return null;
		}, user);
	}

	default Future<Void> executeJobLaterAs(Runnable job, String user, long later) {
		return executeJobLaterAs((Callable<Void>) () -> {
			job.run();
			return null;
		}, user, later);
	}

}
