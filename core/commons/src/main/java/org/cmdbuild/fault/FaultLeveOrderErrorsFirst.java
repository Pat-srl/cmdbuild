/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.fault;

import java.util.Comparator;

public enum FaultLeveOrderErrorsFirst implements Comparator<FaultLevel> {
    INSTANCE;

    @Override
    public int compare(FaultLevel o1, FaultLevel o2) {
        return Integer.compare(o1.getIndex(), o2.getIndex());
    }

}
