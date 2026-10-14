/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.ifc;

import java.util.Map;

public interface IfcModelEntriesReport {

    Map<String, IfcModelEntryReport> getEntries();

    default long getCount() {
        return getEntries().values().stream().mapToLong(IfcModelEntryReport::getCount).sum();
    }

}
