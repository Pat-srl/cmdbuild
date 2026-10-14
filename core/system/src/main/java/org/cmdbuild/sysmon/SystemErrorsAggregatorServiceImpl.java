/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.collect.Ordering;
import java.time.ZonedDateTime;
import java.util.List;
import static java.util.stream.Collectors.joining;
import org.cmdbuild.jobs.JobRunRepository;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import org.springframework.stereotype.Component;
import static org.cmdbuild.fault.FaultLevel.FL_WARNING;

@Component
public class SystemErrorsAggregatorServiceImpl implements SystemErrorsAggregatorService {

    private final JobRunRepository jobRunRepository;
    private final SysmonRepository sysmonRepository;

    public SystemErrorsAggregatorServiceImpl(JobRunRepository jobRunRepository, SysmonRepository sysmonRepository) {
        this.jobRunRepository = checkNotNull(jobRunRepository);
        this.sysmonRepository = checkNotNull(sysmonRepository);
    }

    @Override
    public List<SystemErrorInfo> getEventsSince(ZonedDateTime from) {
        checkNotNull(from);
        List<SystemErrorInfo> list = list();
        sysmonRepository.getRecordsSince(from).stream().filter(SystemStatusLog::hasWarnings).map(s -> SystemErrorInfoImpl.builder()
                .withCategory("sysmon")
                .withSource("node=%s", s.getNodeId())
                .withLevel(FL_WARNING)//TODO improve this
                .withMessage(s.getWarnings())
                .withTimestamp(s.getBeginDate())
                .build()).forEach(list::add);
        jobRunRepository.getJobErrors(from).stream().map(r -> SystemErrorInfoImpl.builder()
                .withCategory("jobrun")
                .withSource("job=%s run=%s", r.getJobCode(), r.getId())
                .withLevel(r.getMaxErrorLevel())
                .withMessage(r.getErrorOrWarningEvents().stream().map((event) -> event.getLevel().name() + ": " + event.getMessage()).collect(joining("; ")))
                .withTimestamp(r.getTimestamp())
                .build()).forEach(list::add);
        list.sort(Ordering.natural().onResultOf(SystemErrorInfo::getTimestamp).reversed());
        return list;
    }

}
