package org.cmdbuild.dao.entrytype.attributetype;

import jakarta.annotation.Nullable;
import org.cmdbuild.common.beans.IdAndDescription;

public class ReferenceArrayAttributeType implements CardAttributeType<IdAndDescription[]> {

	private final String targetClassName;

	public ReferenceArrayAttributeType(@Nullable String targetClassName) {
		this.targetClassName = targetClassName;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	@Override
	public AttributeTypeName getName() {
		return AttributeTypeName.REFERENCEARRAY;
	}

	@Override
	public void accept(CMAttributeTypeVisitor visitor) {
		visitor.visit(this);
	}

}
