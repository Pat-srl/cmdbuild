/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRun;

public interface JobRunCompletedEvent {

    JobData getJob();

    JobRun getJobRun();

}
