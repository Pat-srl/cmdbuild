/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.utils;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import jakarta.annotation.Nullable;
import static org.cmdbuild.utils.lang.CmConvertUtils.toIntegerOrNull;
import static org.cmdbuild.utils.lang.CmNullableUtils.firstNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;

public class PostgresUrl {

    private final String host, database;
    private final int port;

    public PostgresUrl(@Nullable String host, @Nullable Integer port, String database) {
        this.host = firstNotBlank(host, "localhost");
        this.port = firstNotNull(port, 5432);
        this.database = checkNotBlank(database, "missing database name");
    }

    public static PostgresUrl parse(String value) {
        checkNotBlank(value);
        Matcher matcher = Pattern.compile("^jdbc:postgresql://([^:/]+)(:([0-9]+))?(,[^/]+)?/([^?]+)[?]?.*$").matcher(value);
        checkArgument(matcher.find(), "invalid pg url syntax for value =< %s >", value);
        return new PostgresUrl(matcher.group(1), toIntegerOrNull(matcher.group(3)), matcher.group(5));
    }

    public String getHost() {
        return host;
    }

    public String getDatabase() {
        return database;
    }

    public int getPort() {
        return port;
    }

    public String toJdbcUrl() {
        return format("jdbc:postgresql://%s:%s/%s", host, port, database);
    }

    @Override
    public String toString() {
        return toJdbcUrl();
    }

    public PostgresUrl withDatabase(String database) {
        return new PostgresUrl(host, port, database);
    }

}
