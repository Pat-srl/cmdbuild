package org.cmdbuild.dao.config;

import jakarta.annotation.Nullable;

public interface SqlConfiguration {

    boolean enableSqlLogging();

    boolean enableSqlPrettyPrint();

    @Nullable
    String includeSqlRegex();

    @Nullable
    String excludeSqlRegex();

    boolean enableSqlLoggingTimeTracking();

    boolean enableDdlLogging();

    @Nullable
    String includeDdlRegex();

    @Nullable
    String excludeDdlRegex();

    boolean enablePoolDebug();

    int getPoolDebugRemoveAbandonedTimeoutSeconds();

    int getMaxIdle();

    int getMaxActive();

    int getConnectionTimeout();

    int getConnectionLifetime();

    default boolean enableSqlOrDdlLogging() {
        return enableSqlLogging() || enableDdlLogging();
    }
}
