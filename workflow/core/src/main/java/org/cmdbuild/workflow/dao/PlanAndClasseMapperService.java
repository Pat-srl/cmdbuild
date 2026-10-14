/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import org.cmdbuild.dao.entrytype.Classe;
import org.cmdbuild.workflow.model.Process;

public interface PlanAndClasseMapperService {

    public static final String DEFAULT_PLAN_ID = "default";

    String getClasseIdByPlanId(String planId);

    Process classeAndPlanIdToPlanClasse(Classe classe, String planId);

}
