/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use CMDBuild according to the license
 */
package org.cmdbuild.sync;

import jakarta.activation.DataHandler;
import java.util.Collection;
import java.util.Map;
import org.cmdbuild.workflow.model.Process;

/**
 *
 * @author afelice
 */
public interface ProcessLoader {

    Collection<Process> getAllProcessColl();

    Process getProcess(String processClasseName);

    DataHandler getProcessXpdl(String processClasseName, String planId);

    Map<String, Object> serializeProcess(Process curProcess);
}
