/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.Map;
import java.util.Set;
import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.dao.entrytype.DaoPermissionUtils.mergeAttributePermissions;

public class AttributePermissionsImpl implements AttributePermissions {

	private final Map<PermissionScope, Set<AttributePermission>> permissions;

	private AttributePermissionsImpl(AttributePermissionsImplBuilder builder) {
		this.permissions = checkNotNull(builder.permissions);//TODO make immutable
	}

	@Override
	public Map<PermissionScope, Set<AttributePermission>> getPermissionMap() {
		return permissions;
	}

	public static AttributePermissionsImplBuilder builder() {
		return new AttributePermissionsImplBuilder();
	}

	public static AttributePermissionsImplBuilder copyOf(AttributePermissions source) {
		return new AttributePermissionsImplBuilder()
				.withPermissions(source.getPermissionMap());
	}

	public static class AttributePermissionsImplBuilder implements Builder<AttributePermissionsImpl, AttributePermissionsImplBuilder> {

		private Map<PermissionScope, Set<AttributePermission>> permissions;

		public AttributePermissionsImplBuilder withPermissions(Map<PermissionScope, Set<AttributePermission>> permissions) {
			this.permissions = permissions;
			return this;
		}

		@Override
		public AttributePermissionsImpl build() {
			return new AttributePermissionsImpl(this);
		}

		public AttributePermissionsImplBuilder addPermissions(AttributePermissions toAdd) {
			this.permissions = mergeAttributePermissions(permissions, toAdd.getPermissionMap());
			return this;
		}

	}
}
