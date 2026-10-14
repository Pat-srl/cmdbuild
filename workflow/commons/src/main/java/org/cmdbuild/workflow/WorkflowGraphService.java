/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow;

import jakarta.activation.DataSource;
import org.cmdbuild.workflow.model.Flow;

public interface WorkflowGraphService {

	DataSource getGraphImageForFlow(Flow card);

	DataSource getSimplifiedGraphImageForFlow(Flow card);

}
