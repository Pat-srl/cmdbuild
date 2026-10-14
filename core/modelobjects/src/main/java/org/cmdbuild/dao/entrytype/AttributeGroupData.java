/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

public interface AttributeGroupData extends AttributeGroupInfo {

    final String ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE = "defaultDisplayMode", ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE_OPEN = "open", ATTRIBUTE_GROUP_DEFAULT_DISPLAY_MODE_CLOSED = "closed";

    int getIndex();

    String getOwnerName();

    EntryTypeType getOwnerType();
}
