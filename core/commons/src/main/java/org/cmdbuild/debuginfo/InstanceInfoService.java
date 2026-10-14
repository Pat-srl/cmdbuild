/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.debuginfo;

import static java.lang.String.format;

public interface InstanceInfoService {

    String getVersion();

    String getRevision();

    String getNodeId();

    String getInstanceName();

    default String getInstanceInfo() {
        return format("%s %s", getInstanceName(), getNodeId());
    }

}
