/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import java.util.List;
import org.cmdbuild.dao.entrytype.FkDomain;

public interface FkDomainRepository {

    List<FkDomain> getAllFkDomains();

    List<FkDomain> getFkDomainsForClass(String classId);

}
