/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.formtrigger;

import java.util.List;
import org.cmdbuild.dao.entrytype.Classe;

public interface FormTriggerService {

    List<FormTrigger> getFormTriggersForClass(Classe classe);

    void updateFormTriggersForClass(Classe classe, List<FormTrigger> data);

    void deleteForClass(Classe classe);

}
