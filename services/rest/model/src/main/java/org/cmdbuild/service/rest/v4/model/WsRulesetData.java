/*
 * CMDBuild has been developed and is managed by PAT srl
 * You can use, distribute, edit CMDBuild according to the license
 */
package org.cmdbuild.service.rest.v4.model;


import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

/**
 * @author ldare
 */
public class WsRulesetData {

    private final String name;
    private final String description;
    private final String function;
    private final String attribute;
    private final String analysistype;
    private final String classattribute;
    private final Integer segments;
    private final String owner;
    private final String accessType;
    private final JsonNode rules;

    public WsRulesetData(@JsonProperty("name") String name,
                         @JsonProperty("description") String description,
                         @JsonProperty("function") String function,
                         @JsonProperty("attribute") String attribute,
                         @JsonProperty("analysistype") String analysistype,
                         @JsonProperty("classattribute") String classattribute,
                         @JsonProperty("segments") Integer segments,
                         @JsonProperty("owner") String owner,
                         @JsonProperty("accessType") String accessType,
                         @JsonProperty("_public") Boolean isPublic,
                         @JsonProperty("rules") JsonNode rules) {
        this.name = checkNotBlank(name, "missing required param 'name'");
        this.description = description;
        this.function = function;
        this.analysistype = analysistype;
        this.classattribute = classattribute;
        this.segments = segments;
        this.attribute = checkNotBlank(attribute, "missing required param 'attribute'");
        this.owner = checkNotBlank(owner, "missing required param 'owner'");
        this.accessType = accessType != null ? accessType : isPublic != null && !isPublic ? "private" : "public";
        this.rules = checkNotNull(rules, "missing required param 'rules'");
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getFunction() {
        return function;
    }

    public String getAttribute() {
        return attribute;
    }

    public String getAnalysistype() {
        return analysistype;
    }

    public String getClassattribute() {
        return classattribute;
    }

    public Integer getSegments() {
        return segments;
    }

    public String getOwner() {
        return owner;
    }

    public String getAccessType() {
        return accessType;
    }

    public JsonNode getRules() {
        return rules;
    }
}
