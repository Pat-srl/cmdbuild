/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs;

import static com.google.common.base.Objects.equal;
import java.util.List;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public interface JobRepository {

    List<JobData> getAllJobs();

    JobData getOne(long jobId);

    JobData getOneByCode(String code);

    JobData create(JobData data);

    JobData update(JobData data);

    void delete(long id);

    void definitiveDelete(long id);

    default List<JobData> getAllEnabledJobsForType(String type) {
        checkNotBlank(type);
        return getAllJobs().stream().filter(JobData::isEnabled).filter(j -> equal(j.getType(), type)).collect(toList());
    }
}
