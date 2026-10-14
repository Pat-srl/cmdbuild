/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

import java.time.ZonedDateTime;

public interface XpdlInfo {

	String getVersion();

	String getPlanId();

	String getProvider();

	ZonedDateTime getLastUpdate();

	boolean isDefault();

}
