package org.cmdbuild.easytemplate;

import jakarta.annotation.Nullable;

public interface Easytemplate {

	@Nullable
	Long getId();

	String getKey();

	String getValue();
}
