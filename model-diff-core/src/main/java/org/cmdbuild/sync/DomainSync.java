/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;

/**
 *
 * @author afelice
 */
public interface DomainSync {

    Domain read(String domainName, boolean includeReserved);

    Domain read_Fresh(String domainName);
    
    List<Domain> readAll(boolean includeReserved);

    Domain add(String domainName, Map<String, Object> domainCmdbSerialization);

    Domain update(String domainName, Map<String, Object> domainCmdbSerialization);

    Domain deactivate(Domain domain);

    void remove(String domainName);

    FluentMap<String, Object> serializeDomainProps(Domain domain);
    
    List<CMRelation> readRelationsForCard(Card card);
    
    Set<String> getReferenceDomainNames(Classe classe);
}
