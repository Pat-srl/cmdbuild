package org.cmdbuild.etl.waterway;

import java.util.Map;
import java.util.function.Consumer;
import org.cmdbuild.etl.waterway.message.WaterwayMessage;
import org.cmdbuild.etl.waterway.message.WaterwayMessageData;
import org.cmdbuild.etl.waterway.message.WaterwayMessageImpl;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import org.cmdbuild.utils.lang.CmMapUtils;

public interface WaterwayMessageUpdateHelper {

    WaterwayMessageUpdateHelper withStatus(WaterwayMessageStatus status);

    WaterwayMessageUpdateHelper forwardTo(String queue);

    WaterwayMessageUpdateHelper accept(Consumer<WaterwayMessageImpl.WaterwayMessageImplBuilder> callback);

    WaterwayMessage update();

    default WaterwayMessageUpdateHelper withMeta(String key, String value) {
        return this.accept(b -> b.withMeta(key, value));
    }

    default WaterwayMessageUpdateHelper withMeta(Map<String, String> meta) {
        return this.accept(b -> b.withMeta(meta));
    }

    default WaterwayMessageUpdateHelper withMeta(Object... items) {
        return this.withMeta(CmMapUtils.map(items));
    }

    default WaterwayMessageUpdateHelper withData(WaterwayMessageData data) {
        //            return this.accept(b -> b.clearAttachments().clearMeta().withData(data));//TODO improve this (log/error merge or clear ??)
        return this.accept(b -> b.withData(data)); //TODO improve this (log/error merge or clear ??)
        //TODO improve this (log/error merge or clear ??)
    }

    default WaterwayMessageUpdateHelper withoutMeta(String... keys) {
        return this.accept(b -> b.withoutMeta(keys));
    }

}
