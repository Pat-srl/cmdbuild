/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.collect.ImmutableList.copyOf;
import static java.util.Arrays.asList;
import java.util.Collection;
import java.util.List;
import org.cmdbuild.data.filter.RelationFilter;
import org.cmdbuild.data.filter.RelationFilterRule;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotEmpty;

public class RelationFilterImpl implements RelationFilter {

	private final List<RelationFilterRule> relationFilterRules;

	public RelationFilterImpl(RelationFilterRule... relationFilterRules) {
		this(asList(relationFilterRules));
	}

	public RelationFilterImpl(Collection<RelationFilterRule> relationFilterRules) {
		this.relationFilterRules = copyOf(checkNotEmpty(relationFilterRules));
	}

	@Override
	public List<RelationFilterRule> getRelationFilterRules() {
		return relationFilterRules;
	}

}
