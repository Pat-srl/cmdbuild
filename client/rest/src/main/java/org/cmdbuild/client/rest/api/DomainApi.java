/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.api;

import org.cmdbuild.client.rest.model.AttributeRequestData;
import org.cmdbuild.client.rest.model.DomainInfo;
import org.cmdbuild.dao.entrytype.DomainDefinition;

public interface DomainApi {

    DomainInfo create(DomainDefinition domain);

    void createAttr(String domainId, AttributeRequestData data);

}
