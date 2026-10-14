/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.workflow.dao;

import jakarta.annotation.Nullable;

public interface PlanData {

    @Nullable
    Long getId();

    String getPlanId();

    String getData();

    String getClasseId();
}
