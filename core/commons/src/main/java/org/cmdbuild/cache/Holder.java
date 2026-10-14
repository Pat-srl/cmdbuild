/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import com.google.common.base.Supplier;
import jakarta.annotation.Nullable;

public interface Holder<T> {

	@Nullable
	T getIfPresent();

	boolean isPresent();

	T get(Supplier<T> loader);

	void invalidate();
}
