/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.cmdbuild.homepage.inner.HomepageDataImpl;

import static java.util.Collections.emptyList;
import static org.cmdbuild.utils.json.CmJsonUtils.toJson;
import static org.cmdbuild.utils.lang.CmMapUtils.map;

/**
 *
 * @author mbuzzulini
 */
public class WsHomepageData {

    private final String name, description, iconFont, groupName;
    private final Boolean active;
    private final Object config;

    public WsHomepageData(
            @JsonProperty("name") String name,
            @JsonProperty("group")  String groupName,
            @JsonProperty("description") String description,
            @JsonProperty("active") Boolean active,
            @JsonProperty("components") Object charts,
            @JsonProperty("layout") Object layout,
            @JsonProperty("icon_font") String iconFont,
            @JsonProperty("parameters") Object parameters
    ) {
        this.name = name;
        this.description = description;
        this.active = active;
        this.config = map("components", charts,
                "layout", layout,
                "parameters", parameters == null ? emptyList() : parameters);//TODO improve this
        this.iconFont = iconFont;
        this.groupName = groupName;
    }

    public HomepageDataImpl.HomepageDataImplBuilder toHomepage() {
        return HomepageDataImpl.builder()
                .withCode(this.name)
                .withDescription(this.description)
                .withActive(this.active)
                .withConfig(toJson(config))
                .withIconFont(this.iconFont)
                .withGroupName(this.groupName);
    }
}
