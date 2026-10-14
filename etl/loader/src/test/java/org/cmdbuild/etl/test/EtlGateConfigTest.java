/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.test;

import java.util.Map;
import org.cmdbuild.etl.gate.inner.EtlGate;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_FILEREADER;
import static org.cmdbuild.etl.gate.inner.EtlGateHandlerType.ETLHT_GATE;
import org.cmdbuild.etl.gate.inner.EtlGateImpl;
import static org.cmdbuild.utils.lang.CmInlineUtils.unflattenMap;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class EtlGateConfigTest {

    @Test
    public void testEtlGateFromEmbeddedConfig() {
        Map<String, String> config = map(
                "tag", "cad",
                "gate", "INLINE",
                "cronExpression", "* * * * ?",
                "gateconfig_handlers_0_type", "filereader",
                "gateconfig_handlers_0_directory", "/tmp/source",
                //  other file source config:  directory, filePattern, postImportAction, failOnMissingSourceData, targetDirectory
                "gateconfig_handlers_1_type", "gate",
                "gateconfig_handlers_1_gate", "myCadGate");
        EtlGate gate = EtlGateImpl.builder().withCode("EMBEDDED").withConfig(unflattenMap(config, "gateconfig")).build();
        assertEquals(2, gate.getHandlers().size());
        assertEquals(ETLHT_FILEREADER, gate.getHandlers().get(0).getType());
        assertEquals(ETLHT_GATE, gate.getHandlers().get(1).getType());
        assertEquals("myCadGate", gate.getHandlers().get(1).getConfig("gate"));
    }

}
