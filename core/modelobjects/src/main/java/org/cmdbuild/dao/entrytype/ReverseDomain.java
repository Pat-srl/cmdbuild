/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Maps.transformValues;
import java.util.Collection;
import static java.util.Collections.emptyMap;
import java.util.Map;
import java.util.Set;
import org.cmdbuild.auth.grant.GrantAttributePrivilege;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public class ReverseDomain implements Domain {

    private final Domain delegate;

    public ReverseDomain(Domain delegate) {
        this.delegate = checkNotNull(delegate);
    }

    public static Domain of(Domain domain) {
        return domain instanceof ReverseDomain ? ((ReverseDomain) domain).delegate : new ReverseDomain(domain);
    }

    @Override
    public void accept(CMEntryTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean hasHistory() {
        return delegate.hasHistory();
    }

    @Override
    public Map<String, Attribute> getAllAttributesAsMap() {
        return map(transformValues(delegate.getAllAttributesAsMap(), a -> AttributeImpl.copyOf(a).withOwner(ReverseDomain.this).build()));
    }

    @Override
    public DomainMetadata getMetadata() {
        return delegate.getMetadata();//TODO reverse metadata
    }

    @Override
    public String getPrivilegeId() {
        return delegate.getPrivilegeId();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public Long getId() {
        return delegate.getId();
    }

    @Override
    public Classe getSourceClass() {
        return delegate.getTargetClass();
    }

    @Override
    public Classe getTargetClass() {
        return delegate.getSourceClass();
    }

    @Override
    public Collection<Classe> getSourceClasses() {
        return delegate.getTargetClasses();
    }

    @Override
    public Collection<Classe> getTargetClasses() {
        return delegate.getSourceClasses();
    }

    @Override
    public String getDirectDescription() {
        return delegate.getInverseDescription();
    }

    @Override
    public String getInverseDescription() {
        return delegate.getDirectDescription();
    }

    @Override
    public DomainCardinality getCardinality() {
        return delegate.getCardinality().inverse();
    }

    @Override
    public boolean isMasterDetail() {
        return delegate.isMasterDetail();
    }

    @Override
    public String getMasterDetailDescription() {
        return delegate.getMasterDetailDescription();
    }

    @Override
    public String getMasterDetailFilter() {
        return delegate.getMasterDetailFilter();
    }

    @Override
    public Collection<String> getDisabledSourceDescendants() {
        return delegate.getDisabledTargetDescendants();
    }

    @Override
    public Collection<String> getDisabledTargetDescendants() {
        return delegate.getDisabledSourceDescendants();
    }

    @Override
    public int getIndexForSource() {
        return delegate.getIndexForTarget();
    }

    @Override
    public int getIndexForTarget() {
        return delegate.getIndexForSource();
    }

    @Override
    public Map<PermissionScope, Set<ClassPermission>> getPermissionsMap() {
        return delegate.getPermissionsMap();
    }

    @Override
    public Map<String, Object> getOtherPermissions() {
        return delegate.getOtherPermissions();
    }

    @Override
    public String toString() {
        return "ReverseDomain{" + "delegate=" + delegate + '}';
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getDmsPermissions() {
        return emptyMap();
    }

    @Override
    public Map<String, Set<GrantAttributePrivilege>> getGisPermissions() {
        return emptyMap();
    }

}
