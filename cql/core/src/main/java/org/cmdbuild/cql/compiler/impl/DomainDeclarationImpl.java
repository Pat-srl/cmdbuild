package org.cmdbuild.cql.compiler.impl;

import static org.cmdbuild.spring.SpringIntegrationUtils.applicationContext;

import org.cmdbuild.cql.CQLBuilderListener.DomainDirection;
import org.cmdbuild.cql.compiler.from.DomainDeclaration;
import org.cmdbuild.exception.ORMException;
import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.dao.entrytype.Domain; 

@SuppressWarnings("unchecked")
public class DomainDeclarationImpl extends CQLElementImpl implements DomainDeclaration {

	String as;
	DomainDirection direction;

	long id = -1;
	String name;

	DomainDeclarationImpl subDomain = null;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DomainDeclarationImpl)) {
			return false;
		}
		DomainDeclarationImpl o = (DomainDeclarationImpl) obj;
		if (direction != o.direction) {
			return false;
		}
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
		if (subDomain != null) {
			if (!subDomain.equals(o.subDomain)) {
				return false;
			}
		} else if (o.subDomain != null) {
			return false;
		}

		return true;
	}

	@Override
	public String getAs() {
		return as;
	}

	@Override
	public DomainDirection getDirection() {
		return direction;
	}

	@Override
	public long getId() {
		return id;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public DomainDeclarationImpl getSubdomain() {
		return subDomain;
	}

	@Override
	public DomainDeclarationImpl searchDomain(String nameOrRef) {
		if (this.name != null && this.name.equals(nameOrRef)) {
			return this;
		}
		if (this.as != null && this.as.equals(nameOrRef)) {
			return this;
		}
		if (this.subDomain != null) {
			return this.subDomain.searchDomain(nameOrRef);
		}
		return null;
	}

	@Override
	public void setAs(String domainAs) {
		this.as = domainAs;
	}

	@Override
	public void setDirection(DomainDirection direction) {
		this.direction = direction;
	}

	@Override
	public void setId(long domainId) {
		this.id = domainId;
	}

	@Override
	public void setName(String domainName) {
		this.name = domainName;
	}

	@Override
	public void setSubdomain(DomainDeclaration subdomain) {
		this.subDomain = (DomainDeclarationImpl) subdomain;
	}

//	private Domain getIDomain(DataView dataView) {
//		DataView _dataView = (dataView == null) ? applicationContext().getBean(DataView.class) : dataView;
//		if (this.id > 0) {
//			return _dataView.findDomain(id);
//		} else {
//			return _dataView.findDomain(name);
//		}
//	}

//	private Classe getEndClassTable() {
//		return getClassTable(false, (DataView) null);
//	}
//
//	public Classe getEndClassTable(DataView dataView) {
//		return getClassTable(false, dataView);
//	}
//
//	protected Classe getClassTable(boolean start ) {
//		Domain domain = getIDomain(dataView);
//		Classe classe;
//		if (this.parent instanceof ClassDeclarationImpl) {
//			ClassDeclarationImpl p = parentAs();
//			classe = dataView.getClasse(p.getName());
//		} else {
//			DomainDeclarationImpl p = parentAs();
//			classe = p.getEndClassTable(dataView);
//		}
//
//		if (!domain.getSourceClass().isAncestorOf(classe) && !domain.getTargetClass().isAncestorOf(classe)) {
//			throw new RuntimeException("Table " + classe.getName() + " not found for domain " + domain.getName());
//		}
//
//		if (start) {
//			return classe;
//		}
//		switch (direction) {
//			case INVERSE:
//				return domain.getSourceClass();
//			default:
//				try {
//					if (domain.getSourceClass().isAncestorOf(classe)) {
//						return domain.getTargetClass();
//					} else {
//						return domain.getSourceClass();
//					}
//				} catch (ORMException exc) {
//					if (ORMException.ORMExceptionType.ORM_AMBIGUOUS_DIRECTION == exc.getExceptionType()) {
//						return domain.getTargetClass();
//					}
//					throw exc;
//				}
//		}
//
//	}

//	public Domain getDirectedDomain(DataView dataView) {
//		return getIDomain(dataView);
//	}

//	public void check() {
//		if (FactoryImpl.CmdbuildCheck) {
//			getEndClassTable(); // this check the existence of the domain and
//			// that the tables are consistent
//			if (subDomain != null) {
//				subDomain.check();
//			}
//		}
//	}

}
