/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.minions;

import static org.cmdbuild.utils.lang.CmConvertUtils.serializeEnumUpper;

public class SystemStatusUtils {

	public static String serializeSystemStatus(SystemStatus status) {
		return serializeEnumUpper(status);
	}

}
