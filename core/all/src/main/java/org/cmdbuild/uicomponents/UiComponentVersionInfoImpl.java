/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.uicomponents;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.ui.TargetDevice;

public class UiComponentVersionInfoImpl implements UiComponentVersionInfo {

    private final TargetDevice targetDevice;

    public UiComponentVersionInfoImpl(TargetDevice targetDevice) {
        this.targetDevice = checkNotNull(targetDevice);
    }

    @Override
    public TargetDevice getTargetDevice() {
        return targetDevice;
    }

}
