/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.scheduler;

import java.util.Collection;
import org.cmdbuild.jobs.JobData;

public interface JobSource {

    String getJobSourceName();

    Collection<JobData> getJobs();

}
