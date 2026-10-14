/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import jakarta.annotation.Nullable;
import org.cmdbuild.ui.TargetDevice;

public interface MenuInfo {

    @Nullable
    Long getId();

    String getGroup();

    TargetDevice getTargetDevice();

    String getCode();

    MenuType getType();
}
