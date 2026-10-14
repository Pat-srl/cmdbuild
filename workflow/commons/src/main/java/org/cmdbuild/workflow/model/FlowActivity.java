/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.model;

public interface FlowActivity {

    String getInstanceId();

    String getDefinitionId();

    String getPerformerGroup();

    String getDescription();

}
