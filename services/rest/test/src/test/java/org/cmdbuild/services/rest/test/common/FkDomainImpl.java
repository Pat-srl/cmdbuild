/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.services.rest.test.common;


import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.DomainCardinality;
import org.cmdbuild.dao.entrytype.CascadeAction;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.entrytype.*;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.mockito.internal.util.MockUtil;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.dao.entrytype.DomainCardinality.MANY_TO_ONE;
import static org.cmdbuild.dao.entrytype.DomainCardinality.ONE_TO_MANY;
import static org.cmdbuild.services.rest.test.common.TestHelper_Model.mockBuildClasseWithAttr;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.mockito.Mockito.mock;

/**
 * @author ldare
 */
public class FkDomainImpl implements FkDomain {

    private final Classe source, target;
    private final Attribute attribute;
    private final CascadeAction cascadeAction;
    private final boolean isMasterDetail;
    private final String masterDetailDescription;
    private final DomainCardinality cardinality;

    private final ClasseRepository classeRepository;

    public FkDomainImpl(Attribute attribute) {
        this.attribute = checkNotNull(attribute);
        ForeignKeyAttributeType attributeType = attribute.getType().as(ForeignKeyAttributeType.class);
        this.source = attribute.getOwnerClass();
        classeRepository = mock(ClasseRepository.class);
        Mockito.when(classeRepository.getClasse(Matchers.anyString())).thenReturn(mockBuildClasseWithAttr("exampClasseName"));
        MockUtil util = new MockUtil();
        System.out.println(util.isMock(classeRepository));
        System.out.println(util.getMockHandler(classeRepository));
        System.out.println(util.getMockName(classeRepository));
        System.out.println(util.getMockSettings(classeRepository));
        this.target = classeRepository.getClasse(attributeType.getForeignKeyDestinationClassName());
        this.cascadeAction = attributeType.getForeignKeyCascadeAction();
        this.isMasterDetail = attribute.getMetadata().isMasterDetail();
        this.masterDetailDescription = firstNotBlank(attribute.getMetadata().getMasterDetailDescription(), target.getDescription());
        this.cardinality = MANY_TO_ONE;
    }

    public FkDomainImpl(FkDomain directDomainToReverse) {
        classeRepository = mock(ClasseRepository.class);
        Mockito.when(classeRepository.getClasse(Matchers.anyString())).thenReturn(mockBuildClasseWithAttr("exampClasseName"));
        MockUtil util = new MockUtil();
        System.out.println(util.isMock(classeRepository));
        System.out.println(util.getMockHandler(classeRepository));
        System.out.println(util.getMockName(classeRepository));
        System.out.println(util.getMockSettings(classeRepository));
        checkArgument(directDomainToReverse.isDirect());
        this.attribute = directDomainToReverse.getSourceAttr();
        this.source = directDomainToReverse.getTargetClass();
        this.target = directDomainToReverse.getSourceClass();
        this.cascadeAction = directDomainToReverse.getCascadeAction();
        this.isMasterDetail = directDomainToReverse.isMasterDetail();
        this.masterDetailDescription = firstNotBlank(attribute.getMetadata().getMasterDetailDescription(), target.getDescription());
        this.cardinality = ONE_TO_MANY;
    }

    @Override
    public Attribute getSourceAttr() {
        return attribute;
    }

    @Override
    public Classe getSourceClass() {
        return source;
    }

    @Override
    public Classe getTargetClass() {
        return target;
    }

    @Override
    public CascadeAction getCascadeAction() {
        return cascadeAction;
    }

    @Override
    public boolean isMasterDetail() {
        return isMasterDetail;
    }

    @Override
    public String getMasterDetailDescription() {
        return masterDetailDescription;
    }

    @Override
    public DomainCardinality getCardinality() {
        return cardinality;
    }
}
