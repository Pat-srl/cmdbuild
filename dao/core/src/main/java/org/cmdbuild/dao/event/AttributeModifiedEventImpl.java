/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.event;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.entrytype.EntryType;

public class AttributeModifiedEventImpl implements AttributesModifiedEvent {

    private final EntryType owner;

    public AttributeModifiedEventImpl(EntryType owner) {
        this.owner = checkNotNull(owner);
    }

    @Override
    public EntryType getOwner() {
        return owner;
    }

}
