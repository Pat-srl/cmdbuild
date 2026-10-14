/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formtrigger;

import java.util.List;

public interface FormTriggerRepository {

	List<FormTriggerData> getFormTriggersForClass(String className);

	void updateFormTriggersForClass(String className, List<FormTriggerData> data);

	void deleteForClass(String className);
}
