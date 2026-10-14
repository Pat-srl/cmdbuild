package org.cmdbuild.dao.entrytype;

import jakarta.annotation.Nullable;

public interface DomainDefinition {

	@Nullable
	Long getOid();

	String getName();

	DomainMetadata getMetadata();

	String getSourceClassName();

	String getTargetClassName();
}
