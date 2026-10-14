/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.command;

import org.cmdbuild.dao.entrytype.Domain;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;

/**
 *
 * @author schursin
 */
@Component
public class ClassOrProcessDomainsWsCommand {


    public ClassOrProcessDomainsWsCommand() {
    }

    public List<Domain> doGetDomains(String classId, Function<String, List<Domain>> function) {
        return function.apply(classId);
    }
}
