/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.gate.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import javax.annotation.Nullable;

public interface EtlGateRepository {

    EtlGate create(EtlGate gate);

    EtlGate update(EtlGate gate);

    void delete(String code);

    List<EtlGate> getAll();

    @Nullable
    EtlGate getByCodeOrNull(String code);

    default EtlGate getByCode(String code) {
        return checkNotNull(getByCodeOrNull(code), "gate not found for code =< %s >", code);
    }

}
