package org.cmdbuild.minions;

import org.cmdbuild.minions.MinionHandler;

public interface MinionComponent extends MinionComponentCommons {

    MinionHandler getMinionHandler();

    @Override
    default String getName() {
        return getMinionHandler().getName();
    }

    @Override
    default boolean isEnabled() {
        return getMinionHandler().isEnabled();
    }

    @Override
    default MinionRuntimeStatus getRuntimeStatus() {
        return getMinionHandler().getRuntimeStatus();
    }

    default void start() {

    }

    default void stop() {

    }

}
