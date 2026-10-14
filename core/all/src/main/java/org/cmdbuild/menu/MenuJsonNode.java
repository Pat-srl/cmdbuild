/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.menu;

import java.util.List;
import jakarta.annotation.Nullable;

public interface MenuJsonNode {

	MenuItemType getMenuType();

	@Nullable
	String getTarget();

    @Nullable
	String getDescription();

	List<MenuJsonNode> getChildren();

	String getCode();

}
