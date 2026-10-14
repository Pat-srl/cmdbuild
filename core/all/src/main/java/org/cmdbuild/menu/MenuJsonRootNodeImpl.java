/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import static com.google.common.base.Preconditions.checkArgument;
import com.google.common.collect.ImmutableList;
import java.util.List;

public class MenuJsonRootNodeImpl implements MenuJsonRootNode {

    public static final int MENU_JSON_FORMAT_VERSION = 3;

    private final List<MenuJsonNode> menuNodes;
    private final int version;

    @JsonCreator
    public MenuJsonRootNodeImpl(@JsonProperty("children") List<MenuJsonNodeImpl> menuNodes, @JsonProperty("version") Integer version) {
        this.menuNodes = ImmutableList.copyOf(menuNodes);
        this.version = version;
        checkArgument(version == MENU_JSON_FORMAT_VERSION, "incompatible json menu format version");
    }

    public MenuJsonRootNodeImpl(List<MenuJsonNode> menuNodes) {
        this((List) menuNodes, MENU_JSON_FORMAT_VERSION);
    }

    @Override
    @JsonProperty("children")
    public List<MenuJsonNode> getMenuNodes() {
        return menuNodes;
    }

    @JsonProperty("version")
    public int getVersion() {
        return version;
    }

}
