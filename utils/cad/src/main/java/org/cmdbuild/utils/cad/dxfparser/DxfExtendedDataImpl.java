/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.cad.dxfparser;

import java.util.List;
import java.util.Map;
import org.cmdbuild.utils.cad.dxfparser.model.DxfExtendedData;
import org.cmdbuild.utils.lang.CmCollectionUtils;
import org.cmdbuild.utils.lang.CmMapUtils;

public class DxfExtendedDataImpl implements DxfExtendedData {

    private final Map<String, List<String>> xdata = CmMapUtils.map();

    public DxfExtendedDataImpl() {
    }

    public DxfExtendedDataImpl(DxfExtendedData xdata) {
        this.xdata.putAll(xdata.getXdata());
    }

    public void addXdata(String application, String value) {
        if (!xdata.containsKey(application)) {
            xdata.put(application, CmCollectionUtils.list());
        }
        xdata.get(application).add(value);
    }

    @Override
    public Map<String, List<String>> getXdata() {
        return xdata;
    }

}
