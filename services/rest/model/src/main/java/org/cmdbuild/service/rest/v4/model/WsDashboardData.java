/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.dashboard.inner.DashboardDataImpl;

import static java.util.Collections.emptyList;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author schursin
 */
public class WsDashboardData {

    private final String name, description, iconFont;
    private final Boolean active;
    private final Object config;

    public WsDashboardData(
            @JsonProperty("name") String name,
            @JsonProperty("description") String description,
            @JsonProperty("active") Boolean active,
            @JsonProperty("components") Object charts,
            @JsonProperty("layout") Object layout,
            @JsonProperty("icon_font") String iconFont,
            @JsonProperty("parameters") Object parameters) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.config = map("components", charts,
                "layout", layout,
                "parameters", parameters == null ? emptyList() : parameters );//TODO improve this
        this.iconFont = iconFont;
    }

    public DashboardDataImpl.DashboardDataImplBuilder toDashboard() {
        return DashboardDataImpl.builder()
                .withCode(name)
                .withDescription(description)
                .withActive(active)
                .withConfig(toJson(config))
                .withIconFont(iconFont);
    }
}
