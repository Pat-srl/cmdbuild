/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import java.util.List;
import org.cmdbuild.utils.json.JsonBean;

@JsonBean(MenuJsonRootNodeImpl.class)
public interface MenuJsonRootNode {

    List<MenuJsonNode> getMenuNodes();

}
