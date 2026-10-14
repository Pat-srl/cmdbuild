/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.calendar;

import jakarta.annotation.Nullable;

public interface CalendarTriggerInfo {

    @Nullable
    Long getId();

    String getOwnerClass();

    String getOwnerAttr();

    String getDescription();

    boolean isActive();
}
