/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formtrigger;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.Collection;
import java.util.Set;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public class FormTriggerImpl implements FormTrigger {

	private final String jsScript;
	private final boolean isActive;
	private final Set<FormTriggerBinding> bindings;

	private FormTriggerImpl(FormTriggerImplBuilder builder) {
		this.jsScript = checkNotNull(builder.jsScript);
		this.isActive = checkNotNull(builder.active);
		this.bindings = set(checkNotNull(builder.bindings)).immutable();
	}

	@Override
	public String getJsScript() {
		return jsScript;
	}

	@Override
	public boolean isActive() {
		return isActive;
	}

	@Override
	public Set<FormTriggerBinding> getBindings() {
		return bindings;
	}

	public static FormTriggerImplBuilder builder() {
		return new FormTriggerImplBuilder();
	}

	public static FormTriggerImplBuilder copyOf(FormTrigger source) {
		return new FormTriggerImplBuilder()
				.withJsScript(source.getJsScript())
				.withActive(source.isActive())
				.withBindings(source.getBindings());
	}

	public static class FormTriggerImplBuilder implements Builder<FormTriggerImpl, FormTriggerImplBuilder> {

		private String jsScript;
		private Boolean active;
		private Collection<FormTriggerBinding> bindings;

		public FormTriggerImplBuilder withJsScript(String jsScript) {
			this.jsScript = jsScript;
			return this;
		}

		public FormTriggerImplBuilder withActive(Boolean active) {
			this.active = active;
			return this;
		}

		public FormTriggerImplBuilder withBindings(Collection<FormTriggerBinding> bindings) {
			this.bindings = bindings;
			return this;
		}

		@Override
		public FormTriggerImpl build() {
			return new FormTriggerImpl(this);
		}

	}
}
