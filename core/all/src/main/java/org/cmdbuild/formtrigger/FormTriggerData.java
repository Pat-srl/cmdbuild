/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formtrigger;

import java.util.List;
import jakarta.annotation.Nullable;

public interface FormTriggerData {

	@Nullable
	Long getId();

	String getJsScript();

	String getClassId();

	int getIndex();

	boolean isActive();

	List<String> getBindings();

}
