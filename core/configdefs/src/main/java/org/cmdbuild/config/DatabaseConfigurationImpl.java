package org.cmdbuild.config;

import java.util.List;
import jakarta.annotation.Nullable;
import org.cmdbuild.cleanup.RecordCleanupRule;
import static org.cmdbuild.config.api.ConfigCategory.CC_DATA;
import org.cmdbuild.config.api.ConfigComponent;
import static org.cmdbuild.config.api.ConfigLocation.CL_FILE_ONLY;
import org.cmdbuild.config.api.ConfigValue;
import static org.cmdbuild.config.api.ConfigValue.FALSE;
import static org.cmdbuild.config.api.ConfigValue.TRUE;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import static org.cmdbuild.dao.config.DatabaseConfiguration.DATABASE_CONFIG_NAMESPACE;
import org.springframework.stereotype.Component;

@Component("databaseConfiguration")
@ConfigComponent(DATABASE_CONFIG_NAMESPACE)
public final class DatabaseConfigurationImpl implements DatabaseConfiguration {

    @ConfigValue(key = DATABASE_CONFIG_URL, description = "database url", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
    private String url;

    @ConfigValue(key = DATABASE_CONFIG_USERNAME, description = "database username", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
    private String username;

    @ConfigValue(key = DATABASE_CONFIG_PASSWORD, description = "database password", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
    private String password;

    @ConfigValue(key = "db.driverClassName", description = "database driver", defaultValue = DEFAULT_DB_DRIVER_CLASS_NAME, location = CL_FILE_ONLY, isProtected = true)
    private String driverClassName;

    @ConfigValue(key = DATABASE_CONFIG_ADMIN_USERNAME, description = "database admin username", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
    private String adminUsername;

    @ConfigValue(key = DATABASE_CONFIG_ADMIN_PASSWORD, description = "database admin password", defaultValue = "", location = CL_FILE_ONLY, isProtected = true)
    private String adminPassword;

    @ConfigValue(key = DATABASE_CONFIG_AUTOPATCH, description = "enable db auto patch", defaultValue = FALSE, location = CL_FILE_ONLY, isProtected = true)
    private boolean enableAutoPatch;

    @ConfigValue(key = "checkConnectionAtStartup", description = "check db connection at startup", defaultValue = TRUE, location = CL_FILE_ONLY, isProtected = true)
    private boolean checkDbAtStartup;

    @ConfigValue(key = "cleanup_rules", description = "list of cleanup modules", defaultValue = "systemstatuslog_default[maxRecordAgeToKeepSeconds=7776000],request_default[maxRecordsToKeep=50000],eventlog_default[maxRecordAgeToKeepSeconds=7776000],jobrun_default[maxRecordsToKeep=50000],etlmessage_default[maxRecordsToKeep=10000]", modular = "cleanup_rule")
    private List<RecordCleanupRule> cleanupRules;

    @ConfigValue(key = "vert.version", description = "vert version", isProtected = true, category = CC_DATA)
    private String vertVersionNumber;

    @ConfigValue(key = "vert.name", description = "vert name", isProtected = true, category = CC_DATA)
    private String vertName;

    @Override
    @Nullable
    public String getVertVersionNumber() {
        return vertVersionNumber;
    }

    @Override
    @Nullable
    public String getVertName() {
        return vertName;
    }

    @Override
    public List<RecordCleanupRule> getCleanupRules() {
        return cleanupRules;
    }

    @Override
    public String getDatabaseUrl() {
        return url;
    }

    @Override
    public String getDatabaseUser() {
        return username;
    }

    @Override
    public String getDatabasePassword() {
        return password;
    }

    @Override
    public String getDatabaseAdminUsernameOrNull() {
        return adminUsername;
    }

    @Override
    public String getDatabaseAdminPasswordOrNull() {
        return adminPassword;
    }

    @Override
    public String getDriverClassName() {
        return driverClassName;
    }

    @Override
    public boolean enableAutoPatch() {
        return enableAutoPatch;
    }

    @Override
    public boolean enableDatabaseConnectionEagerCheck() {
        return checkDbAtStartup;
    }

}
