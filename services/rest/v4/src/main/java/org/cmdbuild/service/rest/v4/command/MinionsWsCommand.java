/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import com.google.common.collect.Ordering;
import org.cmdbuild.minions.Minion;
import org.cmdbuild.minions.MinionService;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static org.cmdbuild.utils.lang.CmConvertUtils.toBoolean;
import static org.cmdbuild.utils.lang.CmExceptionUtils.unsupported;

/**
 * @author ldare
 */
@Component
public class MinionsWsCommand {

    private final MinionService minionService;

    public MinionsWsCommand(MinionService servicesStatusService) {
        this.minionService = checkNotNull(servicesStatusService);
    }

    public List<Minion> doGetAll(String hidden) {
        return minionService.getMinions().stream().filter(m -> switch (hidden) {
            case "default" -> true;
            case "false", "true" -> equal(m.isHidden(), toBoolean(hidden));
            default -> throw unsupported("unsupported hidden parameter =< %s >", hidden);
        }).sorted(Ordering.natural().onResultOf(Minion::getDescription)).collect(toList());
    }

    public Minion doGetOne(String serviceId) {
        return minionService.getMinion(serviceId);
    }

    public Minion doStart(String serviceId) {
        minionService.startMinion(serviceId);
        return minionService.getMinion(serviceId);
    }

    public Minion doStop(String serviceId) {
        minionService.stopMinion(serviceId);
        return minionService.getMinion(serviceId);
    }
}
