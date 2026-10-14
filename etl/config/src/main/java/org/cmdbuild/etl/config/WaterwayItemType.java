/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.etl.config;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableSet;
import java.util.EnumSet;
import java.util.Set;

public enum WaterwayItemType {
    WYCIT_GATE, WYCIT_TEMPLATE, WYCIT_NOTIFICATION, WYCIT_HANDLER, WYCIT_COLLECTOR, WYCIT_BUS, WYCIT_STORAGE, WYCIT_TRIGGER, WYCIT_FUNCTION, WYCIT_SCRIPT, WYCIT_WEBHOOK;

    public static final Set<WaterwayItemType> TOP_LEVEL_TYPES = unmodifiableSet(EnumSet.of(WYCIT_GATE, WYCIT_TEMPLATE, WYCIT_NOTIFICATION, WYCIT_COLLECTOR, WYCIT_BUS, WYCIT_TRIGGER, WYCIT_FUNCTION, WYCIT_SCRIPT, WYCIT_WEBHOOK)),
            SECOND_LEVEL_TYPES = unmodifiableSet(EnumSet.of(WYCIT_STORAGE)),
            NESTED_TYPES = unmodifiableSet(EnumSet.of(WYCIT_HANDLER));

    public static boolean isNestedType(WaterwayItemType type) {
        return NESTED_TYPES.contains(checkNotNull(type));
    }

}
