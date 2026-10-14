/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.api.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import jakarta.activation.DataSource;
import jakarta.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.cmdbuild.api.EtlApi;
import org.cmdbuild.api.EtlGateJobApi;
import org.cmdbuild.etl.waterway.WaterwayService;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import static org.cmdbuild.etl.waterway.message.WaterwayMessageData.WY_JOB_RUN;
import static org.cmdbuild.etl.waterway.message.utils.WaterwayMessageUtils.getOutputDataFromMessage;
import org.cmdbuild.fault.FaultEvent;
import org.cmdbuild.utils.io.CmIoUtils;
import static org.cmdbuild.utils.lang.CmConvertUtils.toLongOrNull;
import org.springframework.stereotype.Component;

@Component
public class EtlApiImpl implements EtlApi {

    private final WaterwayService waterwayService;

    public EtlApiImpl(WaterwayService waterwayService) {
        this.waterwayService = checkNotNull(waterwayService);
    }

    @Override
    public EtlGateJobApi load(String gate, DataSource data, Map<String, String> meta) {
        return new EtlGateJobApiImpl(waterwayService.submitRequest(gate, data, meta));
    }

    private class EtlGateJobApiImpl implements EtlGateJobApi {

        final WaterwayMessage message;

        public EtlGateJobApiImpl(WaterwayMessage message) {
            this.message = checkNotNull(message);
        }

        @Override
        public EtlApi then() {
            return EtlApiImpl.this;
        }

        @Override
        public WaterwayMessage getMessage() {
            return message;
        }

        @Override
        @Nullable
        public String getOutputAsString() {
            return Optional.ofNullable(getOutput()).map(CmIoUtils::readToString).orElse(null);
        }

        @Override
        @Nullable
        public DataSource getOutput() {
            return getOutputDataFromMessage(message);
        }

        @Override
        public Map<String, String> getMeta() {
            return message.getMeta();
        }

        @Override
        @Nullable
        public Long getJobRunId() {
            return toLongOrNull(message.getMeta(WY_JOB_RUN));
        }

        @Override
        public List<FaultEvent> getFaultTolerantErrors() {
            return message.getFaultTolerantErrors();
        }
    }

}
