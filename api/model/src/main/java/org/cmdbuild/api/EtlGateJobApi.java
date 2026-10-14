/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api;

import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.utils.lang.CmPreconditions;

public interface EtlGateJobApi {

    EtlApi then();

    WaterwayMessage getMessage();

    @Nullable
    Long getJobRunId();

    @Nullable
    String getOutputAsString();

    @Nullable
    DataSource getOutput();

    Map<String, String> getMeta();

    @Nullable
    default String getMeta(String key) {
        return getMeta().get(CmPreconditions.checkNotBlank(key));
    }

    @Nullable
    default String get(String key) {
        return getMeta(key);
    }

    List<FaultEvent> getFaultTolerantErrors();
}
