package org.cmdbuild.dao;

@Deprecated
public class Const {

	private Const() {
		// prevents instantiation
	}

	public static class User {

		private User() {
			// prevents instantiation
		}

		public static final String USERNAME = "Username";
		public static final String DESCRIPTION = "Description";
		public static final String PASSWORD = "Password";
		public static final String EMAIL = "Email";
		public static final String ACTIVE = "Active";
		public static final String SERVICE = "Service";
		public static final String PRIVILEGED = "Privileged";

	}

//	public static class Role {
//
//		private Role() {
//			// prevents instantiation
//		}
//		public static final String CODE = "Code";
//		public static final String DESCRIPTION = "Description";
	public static final String ROLE_ATTR_EMAIL = "Email";
	public static final String ROLE_ATTR_ADMINISTRATOR = "Administrator";
	public static final String ROLE_ATTR_STARTING_CLASS = "startingClass";
//		public static final String RESTRICTED_ADINISTRATOR = "CloudAdmin";
	public static final String ROLE_ATTR_ACTIVE = "Active";
	public static final String ROLE_ATTR_DISABLED_MODULES = "DisabledModules";

//	}
	public static class Tenant {

		private Tenant() {
			// prevents instantiation
		}

//		public static final String CODE = "Code";
//		public static final String DESCRIPTION = "Description";
//		public static final String EMAIL = "Email";
//		public static final String ADMINISTRATOR = "Administrator";
//		public static final String STARTING_CLASS = "startingClass";
//		public static final String RESTRICTED_ADINISTRATOR = "CloudAdmin";
		public static final String ACTIVE = "Active";
//		public static final String DISABLED_MODULES = "DisabledModules";

	}

	public static class UserRole {

		private UserRole() {
			// prevents instantiation
		}

		public static final String DEFAULT_GROUP = "DefaultGroup";

	}

}
