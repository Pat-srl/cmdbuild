/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.ecql.inner;

import static com.google.common.base.Preconditions.checkArgument;
import java.util.List;
import org.cmdbuild.ecql.EcqlBindingInfo;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Predicates.not;
import com.google.common.collect.ImmutableList;
import static java.util.Collections.emptyList;
import org.apache.commons.lang3.StringUtils;
import org.cmdbuild.utils.lang.Builder;

public class EcqlBindingInfoImpl implements EcqlBindingInfo {

	private final List<String> clientBindings, serverBindings, xaBindings;

	private EcqlBindingInfoImpl(EcqlBindingInfoImplBuilder builder) {
		this.clientBindings = ImmutableList.copyOf(checkNotNull(builder.clientBindings));
		checkArgument(clientBindings.stream().allMatch(not(StringUtils::isBlank)), "found blank binding in client bindings = %s", clientBindings);
		this.serverBindings = ImmutableList.copyOf(checkNotNull(builder.serverBindings));
		checkArgument(serverBindings.stream().allMatch(not(StringUtils::isBlank)), "found blank binding in server bindings = %s", serverBindings);
		this.xaBindings = ImmutableList.copyOf(checkNotNull(builder.xaBindings));
		checkArgument(xaBindings.stream().allMatch(not(StringUtils::isBlank)), "found blank binding in xa bindings = %s", xaBindings);
	}

	@Override
	public List<String> getClientBindings() {
		return clientBindings;
	}

	@Override
	public List<String> getServerBindings() {
		return serverBindings;
	}

	@Override
	public List<String> getXaBindings() {
		return xaBindings;
	}

	public static EcqlBindingInfoImplBuilder builder() {
		return new EcqlBindingInfoImplBuilder();
	}

	public static EcqlBindingInfoImplBuilder copyOf(EcqlBindingInfoImpl source) {
		return new EcqlBindingInfoImplBuilder()
				.withXaBindings(source.getXaBindings())
				.withClientBindings(source.getClientBindings())
				.withServerBindings(source.getServerBindings());
	}

	public static class EcqlBindingInfoImplBuilder implements Builder<EcqlBindingInfoImpl, EcqlBindingInfoImplBuilder> {

		private List<String> clientBindings = emptyList();
		private List<String> serverBindings = emptyList();
		private List<String> xaBindings = emptyList();

		public EcqlBindingInfoImplBuilder withClientBindings(List<String> clientBindings) {
			this.clientBindings = clientBindings;
			return this;
		}

		public EcqlBindingInfoImplBuilder withServerBindings(List<String> serverBindings) {
			this.serverBindings = serverBindings;
			return this;
		}

		public EcqlBindingInfoImplBuilder withXaBindings(List<String> xaBindings) {
			this.xaBindings = xaBindings;
			return this;
		}

		@Override
		public EcqlBindingInfoImpl build() {
			return new EcqlBindingInfoImpl(this);
		}

	}
}
