/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.utils;

import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.base.Splitter;
import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class PlanIdUtils {

	public static String buildPlanId(String packageId, String version, String processId) {
		return buildPlanId(new SimplePackageIdAndProcessIdAndVersion(packageId, version, processId));
	}

	public static String buildPlanId(PackageIdAndVersionAndDefinitionId planId) {
		return format("%s#%s#%s", planId.getPackageId(), planId.getVersion(), planId.getDefinitionId());
	}

	public static PackageIdAndVersionAndDefinitionId readPlanId(String planId) {
		try {
			String[] res = Splitter.on("#").limit(3).splitToList(planId).toArray(new String[]{});
			checkArgument(res.length == 3, "unable to parse plan id = '%s' expected plan id format = package#version#process", planId);
			return new SimplePackageIdAndProcessIdAndVersion(res[0], res[1], res[2]);
		} catch (Exception ex) {
			throw new IllegalArgumentException(format("error parsing shark plan id = '%s'", planId), ex);
		}
	}

	private static class SimplePackageIdAndProcessIdAndVersion implements PackageIdAndVersionAndDefinitionId {

		private final String packageId, processId, version;

		public SimplePackageIdAndProcessIdAndVersion(String packageId, String version, String processId) {
			this.packageId = checkNotBlank(packageId, "package id cannot be blank");
			this.processId = checkNotBlank(processId, "process id cannot be blank");
			this.version = checkNotBlank(version, "version cannot be blank");
			checkArgument(version.matches("[1-9][0-9]*|0"), "version syntax error; expected number, got '%s'", version);
		}

		@Override
		public String getPackageId() {
			return packageId;
		}

		@Override
		public String getDefinitionId() {
			return processId;
		}

		@Override
		public String getVersion() {
			return version;
		}

		@Override
		public String toString() {
			return "SimplePackageIdAndProcessIdAndVersion{" + "packageId=" + packageId + ", processId=" + processId + ", version=" + version + '}';
		}

	}

	public static interface PackageIdAndVersionAndDefinitionId {

		String getPackageId();

		String getDefinitionId();

		String getVersion();
	}

}
