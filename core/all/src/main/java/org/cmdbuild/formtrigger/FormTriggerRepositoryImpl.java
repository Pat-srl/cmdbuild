/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formtrigger;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import org.springframework.stereotype.Component;
import org.cmdbuild.dao.core.q3.DaoService;
import static org.cmdbuild.dao.core.q3.QueryBuilder.EQ;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

@Component
public class FormTriggerRepositoryImpl implements FormTriggerRepository {

	private final DaoService dao;

	public FormTriggerRepositoryImpl(DaoService dao) {
		this.dao = checkNotNull(dao);
	}

	@Override
	public List<FormTriggerData> getFormTriggersForClass(String className) {
		return dao.selectAll().from(FormTriggerDataImpl.class).where("Owner", EQ, checkNotBlank(className)).asList();
	}

	@Override
	public void updateFormTriggersForClass(String className, List<FormTriggerData> data) { //TODO currently implemented as delete-then-create, refactor as update
		deleteForClass(className);
		data.forEach(dao::create);
	}

	@Override
	public void deleteForClass(String className) {
		getFormTriggersForClass(className).forEach(dao::delete);
	}

}
