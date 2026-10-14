/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.jobs.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import org.cmdbuild.jobs.JobData;
import org.cmdbuild.jobs.JobRepository;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import org.springframework.stereotype.Component;

@Component
public class JobRepositoryImpl implements JobRepository {

    private final DaoService dao;

    public JobRepositoryImpl(DaoService dao) {
        this.dao = checkNotNull(dao);
    }

    @Override
    public List<JobData> getAllJobs() {
        return dao.selectAll().from(JobData.class).orderBy(ATTR_CODE).asList();
    }

    @Override
    public JobData getOne(long jobId) {
        return dao.getById(JobData.class, jobId);
    }

    @Override
    public JobData getOneByCode(String code) {
        checkNotBlank(code, "code param cannot be null");
        return checkNotNull(dao.selectAll().from(JobData.class).where(ATTR_CODE, EQ, code).getOneOrNull(), "job not found for code = %s", code);
    }

    @Override
    public JobData create(JobData data) {
        return dao.create(data);
    }

    @Override
    public JobData update(JobData data) {
        return dao.update(data);
    }

    @Override
    public void delete(long id) {
        dao.delete(JobData.class, id);
    }

    @Override
    public void definitiveDelete(long id) {
        dao.definitiveDelete(JobData.class, id);
    }

}
