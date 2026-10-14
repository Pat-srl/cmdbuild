package org.cmdbuild.gis;

import static com.google.common.base.Objects.equal;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import static com.google.common.collect.Iterables.getOnlyElement;
import static org.apache.commons.lang3.StringUtils.isBlank;
import org.cmdbuild.dao.ConfigurableDataSource;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotBlank;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GisSchemaServiceImpl implements GisSchemaService {//TODO merge this in gis service/gis repo

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final JdbcTemplate jdbcTemplate;
    private final ConfigurableDataSource dataSource;

    private final Supplier<String> postgisVersion = Suppliers.memoize(this::doGetPostgisVersion);

    public GisSchemaServiceImpl(ConfigurableDataSource dataSource) {
        this.dataSource = checkNotNull(dataSource);
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public String getPostgisVersion() {
        return postgisVersion.get();
    }

    @Override
    public void checkGisSchemaAndCreateIfMissing() {
        if (!gisSchemaExists()) {
            logger.info("gis schema not found; create gis schema");
            createGisSchema();
        }
        String version = getPostgisVersion();
        logger.info("postgis ready with version = {}", version);
    }

    @Override
    public boolean isGisSchemaOk() {
        try {
            doGetPostgisVersion();
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void createGisSchema() {
        try {
            String avaliablePostgisVersion = jdbcTemplate.queryForObject("SELECT default_version FROM pg_available_extensions WHERE name = 'postgis'", String.class),
                    currentPostgisVersion = jdbcTemplate.queryForObject("SELECT COALESCE((SELECT extversion FROM pg_extension WHERE extname = 'postgis'),'')", String.class);//TODO duplicate code
            checkNotBlank(avaliablePostgisVersion, "CM: postgis not available; jou should install the POSTGis postgres database extension before enabling GIS");
            //TODO check postgis supported version 
            logger.info("create gis schema");
            dataSource.doAsSuperuser(() -> {
                jdbcTemplate.execute("CREATE SCHEMA gis");
                if (isBlank(currentPostgisVersion)) {
                    jdbcTemplate.execute("CREATE EXTENSION postgis SCHEMA gis");
                    jdbcTemplate.execute("DO $$ BEGIN EXECUTE format('ALTER DATABASE %I SET search_path = \"$user\", public, gis', current_database()); END $$;");
                }
            });
            dataSource.reloadInner();
        } catch (Exception ex) {
            throw new GisException(ex, "unable to prepare the 'gis' schema");
        }
    }

    private boolean gisSchemaExists() {
        return jdbcTemplate.queryForObject("SELECT EXISTS(SELECT schema_name FROM information_schema.schemata WHERE schema_name = 'gis')", Boolean.class);
    }

    private String doGetPostgisVersion() {
        try {
            String pgExtensionVersion = checkNotBlank(getOnlyElement(jdbcTemplate.queryForList("SELECT extversion FROM pg_extension WHERE extname = 'postgis'", String.class), null), "postgis extension not found on this database");
            String pgFunctionstgisVersion = jdbcTemplate.queryForObject("SELECT postgis_lib_version()", String.class);
            checkNotBlank(pgFunctionstgisVersion, "postgis functions not found on schema gis");
            checkArgument(equal(pgFunctionstgisVersion, pgExtensionVersion), "postgis version mismatch: extension version =< %s > does not match function version =< %s >", pgExtensionVersion, pgFunctionstgisVersion);
            //TODO check postgis supported version 
            return pgExtensionVersion;
        } catch (Exception ex) {
            throw new GisException(ex, "error processing gis schema: invalid gis schema content");
        }
    }

}
