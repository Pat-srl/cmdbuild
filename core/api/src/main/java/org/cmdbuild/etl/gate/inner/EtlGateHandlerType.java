/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

public interface EtlGateHandlerType {

    final String ETLHT_TEMPLATE = "template",
            ETLHT_SCRIPT = "script",
            ETLHT_IFC = "ifc",
            ETLHT_CAD = "cad",
            ETLHT_GATE = "gate",
            ETLHT_FILEREADER = "filereader",
            ETLHT_DIRECTORYREADER = "directoryreader",
            ETLHT_DATABASE = "database",
            ETLHT_URLREADER = "urlreader",
            ETLHT_NOOP = "noop";

}
