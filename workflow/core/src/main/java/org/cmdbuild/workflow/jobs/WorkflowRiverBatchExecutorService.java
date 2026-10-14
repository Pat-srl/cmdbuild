/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.jobs;

import org.cmdbuild.workflow.model.Flow;

public interface WorkflowRiverBatchExecutorService {

    void executeBatchTasks(Flow flow);

}
