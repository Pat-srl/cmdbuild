package org.cmdbuild.cql.compiler.impl;

import org.cmdbuild.cql.compiler.from.ClassDeclaration;

public class ClassDeclarationImpl extends CQLElementImpl implements ClassDeclaration {

	String name;
	int id = -1;
	String as;

	@Override
	public String getAs() {
		return as;
	}

	@Override
	public int getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isClass(final String name) {
		return (this.name != null && this.name.equals(name)) || (this.as != null && this.as.equals(name));
	}

	@Override
	public void setAs(final String classAs) {
		this.as = classAs;
	}

	@Override
	public void setId(final int classId) {
		this.id = classId;
	}

	@Override
	public void setName(final String className) {
		this.name = className;
	}

	public DomainDeclarationImpl searchDomain(final String domainNameOrRef) {
		for (final CQLElementImpl c : children) {
			if (c instanceof DomainDeclarationImpl) {
				final DomainDeclarationImpl d = (DomainDeclarationImpl) c;
				final DomainDeclarationImpl out = d.searchDomain(domainNameOrRef);
				if (out != null) {
					return out;
				}
			}
		}
		return null;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ClassDeclarationImpl)) {
			return false;
		}
		final ClassDeclarationImpl o = (ClassDeclarationImpl) obj;
		if (name != null) {
			if (!name.equals(o.name)) {
				return false;
			}
		} else if (o.name != null) {
			return false;
		}
		if (id != o.id) {
			return false;
		}
		if (as != null) {
			if (!as.equals(o.as)) {
				return false;
			}
		} else if (o.as != null) {
			return false;
		}

		return true;
	}
}
