/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class DebugInfoImpl implements BugReportInfo {

	final String fileName;

	public DebugInfoImpl(String fileName) {
		this.fileName = checkNotBlank(fileName);
	}

	@Override
	public String getFileName() {
		return fileName;
	}

}
