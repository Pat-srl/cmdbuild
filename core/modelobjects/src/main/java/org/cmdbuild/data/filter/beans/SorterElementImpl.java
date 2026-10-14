/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.data.filter.beans;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.data.filter.SorterElement;
import org.cmdbuild.data.filter.SorterElementDirection;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

public class SorterElementImpl implements SorterElement {

	private final String property;
	private final SorterElementDirection direction;

	public SorterElementImpl(String property, SorterElementDirection direction) {
		this.property = checkNotBlank(property);
		this.direction = checkNotNull(direction);
	}

	@Override
	public String getProperty() {
		return property;
	}

	@Override
	public SorterElementDirection getDirection() {
		return direction;
	}

	@Override
	public String toString() {
		return "SorterElementImpl{" + "p=" + property + ", d=" + direction + '}';
	}

}
