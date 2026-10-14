/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.io.File;
import static java.lang.String.format;
import jakarta.activation.DataSource;
import jakarta.activation.FileDataSource;
import jakarta.mail.util.ByteArrayDataSource;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import org.cmdbuild.dao.config.DatabaseConfiguration;
import org.cmdbuild.dao.postgres.services.DumpService;
import static org.cmdbuild.utils.date.CmDateUtils.dateTimeFileSuffix;
import static org.cmdbuild.utils.io.CmIoUtils.tempDir;
import static org.cmdbuild.utils.io.CmIoUtils.toByteArray;
import org.cmdbuild.utils.postgres.PostgresUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class DumpServiceImpl implements DumpService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final DatabaseConfiguration databaseConfiguration;

    public DumpServiceImpl(DatabaseConfiguration databaseConfiguration) {
        this.databaseConfiguration = checkNotNull(databaseConfiguration);
    }

    @Override
    public void dumpDatabaseToFile(File file) {
        logger.info("dump cmdbuild database to file = {}", file.getAbsolutePath());
        PostgresUtils.newHelper(
                databaseConfiguration.getHost(),
                databaseConfiguration.getPort(),
                databaseConfiguration.hasAdminAccount() ? databaseConfiguration.getDatabaseAdminUsername() : databaseConfiguration.getDatabaseUser(),
                databaseConfiguration.hasAdminAccount() ? databaseConfiguration.getDatabaseAdminPassword() : databaseConfiguration.getDatabasePassword())
                .withDatabase(databaseConfiguration.getDatabase())
                .buildHelper()
                .dumpDatabaseToFile(file);
    }

    public DataSource dumpDatabaseToDataSource() {
        File tempDir = tempDir(), tempFile = new File(tempDir, format("cmdbuild_%s.dump", dateTimeFileSuffix()));
        dumpDatabaseToFile(tempFile);
        DataSource dataSource;
        if (tempFile.length() < 1073741824) {
            dataSource = new ByteArrayDataSource(toByteArray(tempFile), "application/octet-stream");
            ((ByteArrayDataSource) dataSource).setName(tempFile.getName());
            deleteQuietly(tempFile);
        } else {
            dataSource = new FileDataSource(tempFile);
        }
        return dataSource;
    }
}
