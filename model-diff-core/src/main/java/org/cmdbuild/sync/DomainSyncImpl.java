/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import com.fasterxml.jackson.databind.ObjectMapper;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.classe.access.UserDomainService;
import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.Card;
import org.cmdbuild.dao.beans.DomainMetadataImpl;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptions;
import org.cmdbuild.dao.driver.postgres.q3.DaoQueryOptionsImpl;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain;
import org.cmdbuild.dao.entrytype.DomainImpl;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.postgres.repository.DomainRepositoryImpl;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper;
import org.cmdbuild.service.rest.common.serializationhelpers.DomainSerializationHelper.WsDomainData;
import org.cmdbuild.utils.json.CmJsonUtils;
import org.cmdbuild.utils.lang.CmMapUtils.FluentMap;
import org.springframework.stereotype.Component;

/**
 *
 * @author afelice
 */
@Component
public class DomainSyncImpl implements DomainSync {

    private static final ObjectMapper OBJECT_MAPPER = CmJsonUtils.getObjectMapper();

    private final UserDomainService domainService;
    private final DomainSerializationHelper domainHelper;
    private final DomainRepository domainRepository;
    
    public DomainSyncImpl(UserDomainService domainService, DomainSerializationHelper domainSerializationHelper,
            DomainRepository domainRepository) {
        this.domainService = checkNotNull(domainService);
        this.domainHelper = checkNotNull(domainSerializationHelper);
        this.domainRepository = checkNotNull(domainRepository);
    }

    @Override
    public Domain read(String domainName, boolean includeReserved) {
        if (includeReserved) {
            return domainService.getDomain(domainName);
        } else {        
            // As in DomainWs.read()
            return domainService.getUserDomain(domainName, true);
        }
    }

    /**
     * Used for <code>FORMULA</code> {@link Attribute} workaround.
     * 
     * @param domainName
     * @return 
     */
    @Override
    public Domain read_Fresh(String domainName) {
        ((DomainRepositoryImpl)domainRepository).invalidateCache_Domain(domainName);
        return read(domainName, true);
    }    
    
    /**
     * Requires that current user has <i>service list permission</i>
     * (<code>PS_SERVICE</code>, <code>CP_LIST</code>).
     *
     * @param includeReserved
     * @return
     */
    @Override
    public List<Domain> readAll(boolean includeReserved) {
        if (includeReserved) {
            return domainService.getDomains();
        } else {
            // As in DomainWs.readAll()
            return domainService.getUserDomains();
        }
    }

    @Override
    public Domain add(String domainName, Map<String, Object> domainCmdbSerialization) {
        WsDomainData domainData = buildDomainData(domainCmdbSerialization);
        
        // @todo AFE add Domain description translation: _descriptionDirect_translation, _descriptionInverse_translation, _descriptionMasterDetail_translation
        
        // As in DomainWs.create()
        return domainRepository.createDomain(domainHelper.toDomainDefinition(domainData).build());
    }

    @Override
    public Domain update(String domainName, Map<String, Object> domainCmdbSerialization) {
        WsDomainData domainData = buildDomainData(domainCmdbSerialization);
        
        // @todo AFE add Domain description translation: _descriptionDirect_translation, _descriptionInverse_translation, _descriptionMasterDetail_translation
        
        Domain existingDomain = read(domainName, true);
        // As in DomainWs.update()
        return domainRepository.updateDomain(domainHelper.toDomainDefinition(domainData).withOid(existingDomain.getId()).build());
    }

    @Override
    public Domain deactivate(Domain domain) {
        Domain deactivatedDomain = DomainImpl.copyOf(domain)
                .withMetadata(
                        DomainMetadataImpl.copyOf(domain.getMetadata())
                                .withIsActive(false)
                                .build()
                ).build();
        return update(domain.getName(), buildDomainData(deactivatedDomain));
    }

    @Override
    public void remove(String domainName) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public FluentMap<String, Object> serializeDomainProps(Domain domain) {
        return domainHelper.serializeDetailedDomain(domain);
    }

    @Override
    public List<CMRelation> readRelationsForCard(Card card) {
        return domainService.getUserRelationsForCard(card);
    }     
    
    /**
     * Returns the list of {@link Domain} names that have some reference {@link Attribute} in 
     * given {@link Classe}.
     * 
     * 
     * <p>As done in CardHistoryServiceImpl.getHistoryElements()
     * 
     * @param classe
     * @return list of {@link Domain} names that have some reference
     */
    @Override
    // @todo AFE TBC
    public Set<String> getReferenceDomainNames(Classe classe) {
        return classe.getActiveServiceAttributes().stream()
                .filter(a -> a.isOfType(REFERENCE))
                .map(a -> a.getMetadata().getDomain())
                .collect(toImmutableSet());
    }    
    
    private Domain update(String domainName, WsDomainData domainData) {
        // See DomainWs.update()
        return domainRepository.updateDomain(domainHelper.toDomainDefinition(domainData).build());
    }

    private WsDomainData buildDomainData(Domain domain) {
        return buildDomainData(domainHelper.serializeDetailedDomain(domain));
    }

    /**
     * {@link Domain} <i>metadata</i> is exploded in CMDBuild serialization and
     * needs to be build again before <i>adding</i>/<i>updating</i>.
     *
     * @param domainCmdbSerialization
     * @return
     */
    private WsDomainData buildDomainData(Map<String, Object> domainCmdbSerialization) {
        // Reconstructs metadata from Domain serialization        
        return getSystemObjectMapper().convertValue(domainCmdbSerialization, DomainSerializationHelper.WsDomainData.class);
    }

    private ObjectMapper getSystemObjectMapper() {
        return OBJECT_MAPPER;
    }

}
