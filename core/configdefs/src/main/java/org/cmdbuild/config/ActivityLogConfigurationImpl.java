/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.config;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.cmdbuild.activitylog.ActivityLogAction;
import org.cmdbuild.config.api.ConfigComponent;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.utils.json.CmJsonUtils.MAP_OF_OBJECTS;
import static org.cmdbuild.utils.json.CmJsonUtils.fromJson;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmCollectionUtils.onlyElement;
import static org.cmdbuild.utils.lang.CmMapUtils.map;
import org.springframework.stereotype.Component;

/**
 *
 * @author ataboga
 */
@Component
@ConfigComponent("org.cmdbuild.activitylog")
public class ActivityLogConfigurationImpl implements ActivityLogConfiguration {

    @ConfigValue(key = "entries", description = "meta for activity log, example `{\"InternalEmployee\", [\"C\",\"R\",\"U\",\"D\",\"L\"]}", defaultValue = "{}")
    private String entries;

    @Override
    public Map<String, List<ActivityLogAction>> getEntries() {
        Map<String, List<Object>> map = (Map) fromJson(entries, MAP_OF_OBJECTS);
        return map(map)
                .mapValues(l -> list(l).map(String::valueOf)
                .map(v -> list(ActivityLogAction.values())
                .filter(e -> Objects.equals(String.valueOf(e.getCode()), v))
                .collect(onlyElement())));
    }
}
