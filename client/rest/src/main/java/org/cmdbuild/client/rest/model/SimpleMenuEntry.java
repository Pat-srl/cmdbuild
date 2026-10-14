/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;

public class SimpleMenuEntry implements MenuEntry {

	private final String menuType, objectType, objectDescription;
	private final Long objectId;
	private final List<MenuEntry> children;

	private SimpleMenuEntry(SimpleMenuEntryBuilder builder) {
		this.menuType = checkNotNull(builder.menuType);
		this.objectType = builder.objectType;
		this.objectDescription = builder.objectDescription;
		this.objectId = builder.objectId;
		this.children = checkNotNull(builder.children);
	}

	@Override
	public String getMenuType() {
		return menuType;
	}

	@Override
	@Nullable
	public String getObjectTypeOrNull() {
		return objectType;
	}

	@Override
	@Nullable
	public String getObjectDescriptionOrNull() {
		return objectDescription;
	}

	@Override
	@Nullable
	public Long getObjectId() {
		return objectId;
	}

	@Override
	public List<MenuEntry> getChildren() {
		return children;
	}

	public static SimpleMenuEntryBuilder builder() {
		return new SimpleMenuEntryBuilder();
	}

	public static SimpleMenuEntryBuilder copyOf(MenuEntry source) {
		return new SimpleMenuEntryBuilder()
				.withMenuType(source.getMenuType())
				.withObjectType(source.getObjectTypeOrNull())
				.withObjectDescription(source.getObjectDescriptionOrNull())
				.withObjectId(source.getObjectId())
				.withChildren(source.getChildren());
	}

	public static class SimpleMenuEntryBuilder implements Builder<SimpleMenuEntry, SimpleMenuEntryBuilder> {

		private String menuType;
		private String objectType;
		private String objectDescription;
		private Long objectId;
		private List<MenuEntry> children;

		public SimpleMenuEntryBuilder withMenuType(String menuType) {
			this.menuType = menuType;
			return this;
		}

		public SimpleMenuEntryBuilder withObjectType(String objectType) {
			this.objectType = objectType;
			return this;
		}

		public SimpleMenuEntryBuilder withObjectDescription(String objectDescription) {
			this.objectDescription = objectDescription;
			return this;
		}

		public SimpleMenuEntryBuilder withObjectId(Long objectId) {
			this.objectId = objectId;
			return this;
		}

		public SimpleMenuEntryBuilder withChildren(List<MenuEntry> children) {
			this.children = children;
			return this;
		}

		@Override
		public SimpleMenuEntry build() {
			return new SimpleMenuEntry(this);
		}

	}
}
