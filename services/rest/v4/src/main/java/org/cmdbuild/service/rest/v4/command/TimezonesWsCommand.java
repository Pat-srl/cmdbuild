/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;


import org.springframework.stereotype.Component;

import java.time.ZoneId;
import java.util.Set;

/**
 * @author ldare
 */
@Component
public class TimezonesWsCommand {

    public TimezonesWsCommand() {
    }

    public Set<String> doReadAvailableTimezones() {
        return ZoneId.getAvailableZoneIds();
    }
}
