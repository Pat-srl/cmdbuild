/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.cache;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import java.lang.instrument.Instrumentation;
import jakarta.annotation.Nullable;
import net.bytebuddy.agent.ByteBuddyAgent;
import org.slf4j.LoggerFactory;

public class InstrumentationUtils {

	private final static Supplier<Instrumentation> INSTRUMENTATION_INSTANCE_SUPPLIER = Suppliers.memoize(InstrumentationUtils::doGetInstrumentationSafe);

	public static @Nullable
	Instrumentation getInstrumentationOrNull() {
		return INSTRUMENTATION_INSTANCE_SUPPLIER.get();
	}

	private static @Nullable
	Instrumentation doGetInstrumentationSafe() {
		try {
			Instrumentation instrumentation = ByteBuddyAgent.install();
			LoggerFactory.getLogger(InstrumentationUtils.class).info("instrumentation agent initialized");
			return instrumentation;
		} catch (Exception ex) {
			LoggerFactory.getLogger(InstrumentationUtils.class).warn("unable to initialize instrumentation agent (for extended jvm monitoring)", ex);
			return null;
		}
	}

}
