/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.driver.repository;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainDefinition;

public interface DomainRepository {

    @Nullable
    Domain getDomainOrNull(Long id);

    @Nullable
    Domain getDomainOrNull(String localname);

    List<Domain> getAllDomains();

    Domain createDomain(DomainDefinition definition);

    Domain updateDomain(DomainDefinition definition);

    void deleteDomain(Domain domain);

    List<Domain> getDomainsForClasse(Classe classe);

    default Domain getDomain(long id) {
        return checkNotNull(getDomainOrNull(id), "domain not found for id = %s", id);
    }

    default Domain getDomain(String name) {
        return checkNotNull(getDomainOrNull(name), "domain not found for name =< %s >", name);
    }
}
