/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.sysmon;

import static java.lang.String.format;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public interface SysmonService {

    SystemStatusLog getSystemRuntimeStatus();

    default String getJavaRuntimeInfo() {
        return format("java %s (%s)", System.getProperty("java.version"), firstNotBlank(System.getProperty("java.vm.name"), System.getProperty("java.runtime.name"), "unknown") + " "
                + firstNotBlank(System.getProperty("java.vendor"), "unknown") + " "
                + firstNotBlank(System.getProperty("java.vm.version"), System.getProperty("java.runtime.version"), System.getProperty("java.version"), "unknown"));
    }

}
