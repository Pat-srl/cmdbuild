/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formtrigger;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.annotation.Nullable;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_ID;
import org.cmdbuild.dao.orm.annotations.CardAttr;
import org.cmdbuild.dao.orm.annotations.CardMapping;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmCollectionUtils.listOf;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;

@CardMapping("_FormTrigger")
public class FormTriggerDataImpl implements FormTriggerData {

	private final Long id;
	private final String jsScript;
	private final boolean isActive;
	private final List<String> bindings;
	private final int index;
	private final String classId;

	private FormTriggerDataImpl(FormTriggerDataImplBuilder builder) {
		this.id = builder.id;
		this.jsScript = checkNotNull(builder.jsScript);
		this.classId = checkNotBlank(builder.classId);
		this.isActive = checkNotNull(builder.active);
		this.bindings = list(checkNotNull(builder.bindings)).immutable();
		this.index = checkNotNull(builder.index);
	}

	@Nullable
	@CardAttr(ATTR_ID)
	@Override
	public Long getId() {
		return id;
	}

	@Override
	@CardAttr("Script")
	public String getJsScript() {
		return jsScript;
	}

	@Override
	@CardAttr("Owner")
	public String getClassId() {
		return classId;
	}

	@Override
	@CardAttr("Active")
	public boolean isActive() {
		return isActive;
	}

	@Override
	@CardAttr("Bindings")
	public List<String> getBindings() {
		return bindings;
	}

	@Override
	@CardAttr("Index")
	public int getIndex() {
		return index;
	}

	public static FormTriggerDataImplBuilder builder() {
		return new FormTriggerDataImplBuilder();
	}

	public static FormTriggerDataImplBuilder copyOf(FormTriggerData source) {
		return new FormTriggerDataImplBuilder()
				.withId(source.getId())
				.withJsScript(source.getJsScript())
				.withActive(source.isActive())
				.withBindings(source.getBindings())
				.withClassId(source.getClassId())
				.withIndex(source.getIndex());
	}

	public static class FormTriggerDataImplBuilder implements Builder<FormTriggerDataImpl, FormTriggerDataImplBuilder> {

		private Long id;
		private String jsScript;
		private String classId;
		private Boolean active;
		private List<String> bindings;
		private Integer index;

		public FormTriggerDataImplBuilder withId(Long id) {
			this.id = id;
			return this;
		}

		public FormTriggerDataImplBuilder withJsScript(String jsScript) {
			this.jsScript = jsScript;
			return this;
		}

		public FormTriggerDataImplBuilder withClassId(String classId) {
			this.classId = classId;
			return this;
		}

		public FormTriggerDataImplBuilder withActive(Boolean active) {
			this.active = active;
			return this;
		}

		public FormTriggerDataImplBuilder withBindings(List<String> bindings) {
			this.bindings = bindings;
			return this;
		}

		public FormTriggerDataImplBuilder withIndex(Integer index) {
			this.index = index;
			return this;
		}

		@Override
		public FormTriggerDataImpl build() {
			return new FormTriggerDataImpl(this);
		}

	}
}
