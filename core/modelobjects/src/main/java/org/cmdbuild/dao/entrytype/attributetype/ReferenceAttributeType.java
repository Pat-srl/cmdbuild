package org.cmdbuild.dao.entrytype.attributetype;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.beans.RelationDirection;
import static org.cmdbuild.dao.beans.RelationDirection.RD_DIRECT;
import org.cmdbuild.dao.entrytype.Domain;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class ReferenceAttributeType extends AbstractReferenceAttributeType {

    private final String domainName;
    private final RelationDirection direction;

    public ReferenceAttributeType(String domainName, RelationDirection direction) {
        this.domainName = checkNotBlank(domainName, "domain name cannot be null");
        this.direction = checkNotNull(direction, "reference direction cannot be null");
    }

    public ReferenceAttributeType(Domain domain, RelationDirection direction) {
        this(domain.getName(), direction);
    }

    public String getDomainName() {
        return domainName;
    }

    public RelationDirection getDirection() {
        return direction;
    }

    public boolean isDirect() {
        return equal(RD_DIRECT, direction);
    }

    public boolean isInverse() {
        return !isDirect();
    }

    @Override
    public void accept(CMAttributeTypeVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public AttributeTypeName getName() {
        return AttributeTypeName.REFERENCE;
    }

    @Override
    public String toString() {
        return "ReferenceAttributeType{" + "domainName=" + domainName + ", direction=" + direction + '}';
    }

}
