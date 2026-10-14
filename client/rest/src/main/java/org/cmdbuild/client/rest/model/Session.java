/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.List;
import org.cmdbuild.client.rest.model.Session.SessionBuilder;

import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@JsonDeserialize(builder = SessionBuilder.class)
public class Session {

	private final String username, password, role, tenant, userDescription, sessionId;
	private final List<String> activeTenants, availableRoles, availableTenants;

	private Session(SessionBuilder builder) {
		this.username = checkNotBlank(builder.username);
		this.password = (builder.password);
		this.role = (builder.role);
		this.tenant = (builder.tenant);
		this.userDescription = (builder.userDescription);
		this.activeTenants = (builder.activeTenants);
		this.availableRoles = (builder.availableRoles);
		this.availableTenants = (builder.availableTenants);
		this.sessionId = (builder.sessionId);
	}

	@JsonProperty("_id")
	public String getSessionId() {
		return sessionId;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public String getRole() {
		return role;
	}

	public String getTenant() {
		return tenant;
	}

	public String getUserDescription() {
		return userDescription;
	}

	public List<String> getActiveTenants() {
		return activeTenants;
	}

	public List<String> getAvailableRoles() {
		return availableRoles;
	}

	public List<String> getAvailableTenants() {
		return availableTenants;
	}

	public static SessionBuilder builder() {
		return new SessionBuilder();
	}

	public static SessionBuilder copyOf(Session source) {
		return new SessionBuilder()
				.withSessionId(source.getSessionId())
				.withUsername(source.getUsername())
				.withPassword(source.getPassword())
				.withRole(source.getRole())
				.withTenant(source.getTenant())
				.withUserDescription(source.getUserDescription())
				.withActiveTenants(source.getActiveTenants())
				.withAvailableRoles(source.getAvailableRoles())
				.withAvailableTenants(source.getAvailableTenants());
	}

	public static class SessionBuilder implements Builder<Session, SessionBuilder> {

		private String username, sessionId;
		private String password;
		private String role;
		private String tenant;
		private String userDescription;
		private List<String> activeTenants;
		private List<String> availableRoles;
		private List<String> availableTenants;

		public SessionBuilder withUsername(String username) {
			this.username = username;
			return this;
		}

		@JsonProperty("_id")
		public SessionBuilder withSessionId(String sessionId) {
			this.sessionId = sessionId;
			return this;
		}

		public SessionBuilder withPassword(String password) {
			this.password = password;
			return this;
		}

		public SessionBuilder withRole(String role) {
			this.role = role;
			return this;
		}

		public SessionBuilder withTenant(String tenant) {
			this.tenant = tenant;
			return this;
		}

		public SessionBuilder withUserDescription(String userDescription) {
			this.userDescription = userDescription;
			return this;
		}

		public SessionBuilder withActiveTenants(List<String> activeTenants) {
			this.activeTenants = activeTenants;
			return this;
		}

		public SessionBuilder withAvailableRoles(List<String> availableRoles) {
			this.availableRoles = availableRoles;
			return this;
		}

		public SessionBuilder withAvailableTenants(List<String> availableTenants) {
			this.availableTenants = availableTenants;
			return this;
		}

		@Override
		public Session build() {
			return new Session(this);
		}

	}
}
