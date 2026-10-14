/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.client.rest.model;

import java.util.List;
import jakarta.annotation.Nullable;

public interface MenuEntry {

	String getMenuType();

	@Nullable
	Long getObjectId();

	@Nullable
	String getObjectTypeOrNull();

	@Nullable
	String getObjectDescriptionOrNull();

	List<MenuEntry> getChildren();

}
