package org.cmdbuild.etl.waterway.storage;

import com.google.common.collect.ImmutableMap;
import static java.util.Collections.emptyMap;
import java.util.EnumSet;
import java.util.Map;
import static java.util.function.Function.identity;
import org.cmdbuild.etl.waterway.WaterwayMessagesStats;
import org.cmdbuild.etl.waterway.message.WaterwayMessageStatus;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import static org.cmdbuild.utils.lang.CmMapUtils.toMap;

public class WaterwayMessagesStatsImpl implements WaterwayMessagesStats {

    private final Map<WaterwayMessageStatus, Long> messageCount;

    public WaterwayMessagesStatsImpl(Map<WaterwayMessageStatus, Long> messageCount) {
        this.messageCount = ImmutableMap.copyOf(list(EnumSet.allOf(WaterwayMessageStatus.class)).collect(toMap(identity(), k -> messageCount.getOrDefault(k, 0l))));
    }

    public WaterwayMessagesStatsImpl() {
        this(emptyMap());
    }

    @Override
    public Map<WaterwayMessageStatus, Long> getMessageCountByStatus() {
        return messageCount;
    }

    public static WaterwayMessagesStats build(Iterable<WaterwayMessagesStats> stats) {
        Map<WaterwayMessageStatus, Long> map = map();
        list(stats).forEach(s -> s.getMessageCountByStatus().forEach((k, v) -> map.put(k, (map.getOrDefault(k, 0l) + v))));
        return new WaterwayMessagesStatsImpl(map);
    }

}
