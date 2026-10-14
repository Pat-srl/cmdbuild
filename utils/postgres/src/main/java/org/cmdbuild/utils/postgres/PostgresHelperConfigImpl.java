/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.utils.postgres;

import static com.google.common.base.MoreObjects.firstNonNull;
import com.google.common.collect.ImmutableList;
import java.io.File;
import java.net.URI;
import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.utils.lang.Builder;
import static org.cmdbuild.utils.lang.CmCollectionUtils.list;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotBlank;
import static org.cmdbuild.utils.lang.CmNullableUtils.isNotNullAndGtZero;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import static org.cmdbuild.utils.postgres.PostgresUtils.POSTGRES_VERSION_AUTO;

public class PostgresHelperConfigImpl implements PostgresHelperConfig {

    private final String host, username, password, database;
    private final int port;
    private final String postgresBinariesVersion;
    private final boolean verbose, createSchema, handleExitStatus, xzCompression, lightMode, singleCore;
    private final List<String> schemas;

    private PostgresHelperConfigImpl(PostgresHelperBuilder builder) {
        this.host = firstNotBlank(builder.host, "localhost");
        this.username = firstNotBlank(builder.username, "postgres");
        this.password = firstNonNull(builder.password, "postgres");
        this.database = firstNonNull(builder.database, username);
        this.port = firstNonNull(builder.port, 5432);
        this.postgresBinariesVersion = firstNotBlank(builder.postgresBinariesVersion, POSTGRES_VERSION_AUTO);
        this.verbose = firstNonNull(builder.verbose, false);
        this.createSchema = firstNonNull(builder.createSchema, true);
        this.schemas = ImmutableList.copyOf(builder.schemas);
        this.handleExitStatus = firstNonNull(builder.handleExitStatus, true);
        this.xzCompression = firstNonNull(builder.xzCompression, false);
        this.singleCore = firstNonNull(builder.singleCore, false);
        this.lightMode = firstNonNull(builder.lightMode, false);
    }

    @Override
    public String getDatabase() {
        return database;
    }

    @Override
    public boolean handleExitStatus() {
        return handleExitStatus;
    }

    @Override
    public String getHost() {
        return host;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public int getPort() {
        return port;
    }

    @Override
    public String getPostgresBinariesVersion() {
        return postgresBinariesVersion;
    }

    @Override
    public boolean getVerbose() {
        return verbose;
    }

    @Override
    public boolean getCreateSchema() {
        return createSchema;
    }

    @Override
    public List<String> getSchemas() {
        return schemas;
    }

    @Override
    public boolean getXzCompression() {
        return xzCompression;
    }

    @Override
    public boolean getLightMode() {
        return lightMode;
    }

    @Override
    public boolean isSingleCore() {
        return singleCore;
    }

    public static PostgresHelperBuilder builder() {
        return new PostgresHelperBuilder();
    }

    public static PostgresHelperBuilder copyOf(PostgresHelperConfigImpl source) {
        return new PostgresHelperBuilder()
                .withHost(source.getHost())
                .withUsername(source.getUsername())
                .withPassword(source.getPassword())
                .withPort(source.getPort())
                .withPostgresVersion(source.getPostgresBinariesVersion())
                .withVerbose(source.getVerbose())
                .withCreateSchema(source.getCreateSchema())
                .withSchemas(source.getSchemas())
                .withXzCompression(source.getXzCompression())
                .withSingleCore(source.isSingleCore())
                .withLightMode(source.getLightMode());
    }

    public static class PostgresHelperBuilder implements Builder<PostgresHelperConfigImpl, PostgresHelperBuilder> {

        private String host;
        private String username;
        private String password, database;
        private Integer port;
        private String postgresBinariesVersion;
        private Boolean verbose, lightMode, createSchema, handleExitStatus, xzCompression, singleCore;
        private final List<String> schemas = list();

        public PostgresHelperBuilder withDatabase(String database) {
            this.database = database;
            return this;
        }

        public PostgresHelperBuilder withLightMode(Boolean lightMode) {
            this.lightMode = lightMode;
            return this;
        }

        public PostgresHelperBuilder withXzCompression(Boolean xzCompression) {
            this.xzCompression = xzCompression;
            return this;
        }

        public PostgresHelperBuilder withSingleCore(Boolean singleCore) {
            this.singleCore = singleCore;
            return this;
        }

        public PostgresHelperBuilder withHost(String host) {
            this.host = host;
            return this;
        }

        public PostgresHelperBuilder withUsername(String username) {
            this.username = username;
            return this;
        }

        public PostgresHelperBuilder withPassword(String password) {
            this.password = password;
            return this;
        }

        public PostgresHelperBuilder withPort(Integer port) {
            this.port = port;
            return this;
        }

        public PostgresHelperBuilder withPostgresVersion(String postgresBinariesVersion) {
            this.postgresBinariesVersion = postgresBinariesVersion;
            return this;
        }

        public PostgresHelperBuilder withVerbose(Boolean verbose) {
            this.verbose = verbose;
            return this;
        }

        public PostgresHelperBuilder withHandleExitStatus(Boolean handleExitStatus) {
            this.handleExitStatus = handleExitStatus;
            return this;
        }

        public PostgresHelperBuilder withCreateSchema(Boolean createSchema) {
            this.createSchema = createSchema;
            return this;
        }

        public PostgresHelperBuilder withSchema(@Nullable String schema) {
            this.schemas.clear();
            if (isNotBlank(schema)) {
                this.schemas.add(schema);
            }
            return this;
        }

        public PostgresHelperBuilder withSchemas(List<String> schemas) {
            this.schemas.clear();
            this.schemas.addAll(schemas);
            return this;
        }

        public PostgresHelperBuilder withUrl(String dbUrl) {
            URI uri = URI.create(checkNotBlank(dbUrl).replaceFirst("jdbc:postgresql", "x"));
            if (isNotNullAndGtZero(uri.getPort())) {
                withPort(uri.getPort());
            }
            withHost(uri.getHost());
            if (isNotBlank(uri.getPath())) {
                withDatabase(new File(uri.getPath()).getName());//TODO check this
            }
            return this;
        }

        @Override
        public PostgresHelperConfigImpl build() {
            return new PostgresHelperConfigImpl(this);
        }

        public PostgresHelper buildHelper() {
            return new PostgresHelperImpl(build());
        }

    }
}
