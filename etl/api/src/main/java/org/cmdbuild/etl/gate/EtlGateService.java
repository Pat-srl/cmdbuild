/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate;

import java.util.List;
import org.cmdbuild.etl.gate.inner.EtlGate;
import org.cmdbuild.etl.gate.inner.EtlGateRepository;

public interface EtlGateService extends EtlGateRepository {

    final String ETLGATE_REQUEST_PATH = "ETLGATE_REQUEST_PATH",
            ETLGATE_REQUEST_METHOD = "ETLGATE_REQUEST_METHOD";

    List<EtlGate> getAllForCurrentUser();

    EtlGate getByCodeForCurrentUser(String code);

    boolean currentUserCanAccess(EtlGate gate);
}
