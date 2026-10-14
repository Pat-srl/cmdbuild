/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.utils;

import org.cmdbuild.dao.beans.CMRelation;
import org.cmdbuild.dao.beans.RelationImpl;
import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static org.cmdbuild.utils.lang.CmCollectionUtils.set;

public class RelationUtils {

	public static CMRelation rotateRelationWithSource(CMRelation relation, long sourceCardId) {
		checkArgument(set(relation.getSourceId(), relation.getTargetId()).contains(sourceCardId), "this relation = %s does not contain card = %s", relation, sourceCardId);
		if (equal(relation.getSourceId(), sourceCardId)) {
			return relation;
		} else {
			return RelationImpl.copyOf(relation)
					.withDirection(relation.getDirection().inverse())
					.withSourceCard(relation.getTargetCard())
					.withSourceDescription(relation.getTargetDescription())
					.withSourceCode(relation.getTargetCode())
					.withTargetCard(relation.getSourceCard())
					.withTargetDescription(relation.getSourceDescription())
					.withTargetCode(relation.getSourceCode())
					.build();
		}
	}

}
