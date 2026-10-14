/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser.model;

public class DxfValueImpl implements DxfValue {

    private final String value;
    private final int groupCode;

    public DxfValueImpl(int groupCode, String value) {
        this.value = value;
        this.groupCode = groupCode;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public int getGroupCode() {
        return groupCode;
    }

    @Override
    public String toString() {
        return "DxfValue{" + "groupCode=" + groupCode + ", value=" + value + '}';
    }

}
