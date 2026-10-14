/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.q3;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Iterables.getOnlyElement;
import java.util.List;
import static java.util.stream.Collectors.toList;
import jakarta.annotation.Nullable;
import static org.cmdbuild.common.Constants.DMS_MODEL_DEFAULT_CLASS;
import org.cmdbuild.dao.driver.repository.ClasseReadonlyRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.cmdbuild.dao.entrytype.Attribute;
import org.cmdbuild.dao.entrytype.Classe;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.FOREIGNKEY;
import static org.cmdbuild.dao.entrytype.attributetype.AttributeTypeName.REFERENCE;
import org.cmdbuild.dao.entrytype.attributetype.ForeignKeyAttributeType;
import org.cmdbuild.dao.entrytype.attributetype.ReferenceAttributeType;
import static org.cmdbuild.utils.lang.CmExceptionUtils.illegalArgument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class RefAttrHelperServiceImpl implements RefAttrHelperService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final ClasseReadonlyRepository classeRepository;
    private final DomainRepository domainRepository;

    public RefAttrHelperServiceImpl(ClasseReadonlyRepository classeRepository, DomainRepository domainRepository) {
        this.classeRepository = checkNotNull(classeRepository);
        this.domainRepository = checkNotNull(domainRepository);
    }

    @Override
    public Classe getTargetClassForAttribute(Attribute a) {
        return switch (a.getType().getName()) {
            case REFERENCE ->
                domainRepository.getDomain((a.getType().as(ReferenceAttributeType.class)).getDomainName()).getReferencedClass(a);
            case FOREIGNKEY ->
                classeRepository.getClasse(a.getType().as(ForeignKeyAttributeType.class).getForeignKeyDestinationClassName());
            case FILE ->
                classeRepository.getClasse(DMS_MODEL_DEFAULT_CLASS);
            default ->
                throw illegalArgument("invalid reference/fk attr = %s", a);
        };
    }

    @Nullable
    @Override
    public Attribute getAttrForMasterCardFilterOrNull(Classe source, Classe target) {
        List<Attribute> attrs = source.getActiveServiceAttributes().stream().filter(a -> a.isOfType(FOREIGNKEY, REFERENCE)).filter(a -> equal(getTargetClassForAttribute(a), target)).collect(toList());
        if (attrs.size() > 1) {
            logger.info("unable to get unique reference attr from class = {} to master card of type = {}", source, target);
            return null;
        } else {
            return getOnlyElement(attrs, null);
        }
    }
}
