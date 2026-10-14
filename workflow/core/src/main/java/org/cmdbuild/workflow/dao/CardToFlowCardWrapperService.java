/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.workflow.model.Flow;

public interface CardToFlowCardWrapperService {

    Flow cardToFlowCard(DatabaseRecord card);

}
