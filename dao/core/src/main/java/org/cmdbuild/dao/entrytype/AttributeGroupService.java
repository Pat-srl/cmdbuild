/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.entrytype;

import java.util.List;
import org.cmdbuild.dao.driver.repository.AttributeGroupRepository;

public interface AttributeGroupService extends AttributeGroupRepository {

    List<AttributeGroupInfo> updateAttributeGroupsForEntryType(EntryType entryType, List<AttributeGroupInfo> attributeGroups);

}
