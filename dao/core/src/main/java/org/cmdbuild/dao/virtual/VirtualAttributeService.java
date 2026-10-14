/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.virtual;

import java.util.Collection;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.entrytype.Attribute;

public interface VirtualAttributeService {

    <T extends DatabaseRecord> T loadVirtualAttributes(T databaseRecord);

    <T extends DatabaseRecord> T loadVirtualAttributes(T databaseRecord, Collection<Attribute> attribute);
}
