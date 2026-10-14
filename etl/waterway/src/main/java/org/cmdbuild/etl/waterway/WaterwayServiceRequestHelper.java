package org.cmdbuild.etl.waterway;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import jakarta.annotation.Nullable;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageAttachment;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

public interface WaterwayServiceRequestHelper {

    // TODO add api for stored payload (file, temp etc)
    WaterwayServiceRequestHelper withTarget(String code);

    WaterwayServiceRequestHelper withMessageIdAndHistory(String messageId, List<String> history);

    WaterwayServiceRequestHelper withMeta(Map<String, String> meta);

    WaterwayServiceRequestHelper withPayload(Object payload);

    WaterwayServiceRequestHelper withPayload(String name, Object payload, @Nullable Map<String, String> meta);

    WaterwayServiceRequestHelper withPayload(Collection<WaterwayMessageAttachment> payload);

    WaterwayMessage submit();

    WaterwayMessage createQueued();

    default WaterwayServiceRequestHelper withMeta(String... meta) {
        return withMeta(map(meta));
    }

    default WaterwayServiceRequestHelper withPayload(String name, Object payload) {
        return withPayload(name, payload, null);
    }

    default WaterwayServiceRequestHelper accept(Consumer<WaterwayServiceRequestHelper> callback) {
        callback.accept(this);
        return this;
    }

}
