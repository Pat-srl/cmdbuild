/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.utils;

import jakarta.annotation.Nullable;
import static java.lang.String.format;
import java.sql.SQLWarning;
import javax.sql.DataSource;
import static org.cmdbuild.utils.lang.CmExceptionUtils.marker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.JdbcTemplate;

public class MyJdbcTemplate extends JdbcTemplate {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    public MyJdbcTemplate(DataSource dataSource) {
        super(dataSource);
    }

    @Override
    public boolean isIgnoreWarnings() {
        return false;
    }

    @Override
    protected void handleWarnings(@Nullable SQLWarning warning) throws SQLWarningException {
        while (warning != null) {
            logger.trace("sql warning", warning);
//			 warning.getErrorCode() --> always 0 for pg, not meaningful
            String message = warning.getMessage(),
                    sqlState = warning.getSQLState();
            boolean isWarning = !sqlState.matches("00..."),
                    hasCustomCode = !sqlState.matches("0[01]000");
            if (hasCustomCode) {
                message = format("%s %s", warning.getSQLState(), message);
            }
            if (isWarning) {
                logger.warn(marker(), message);
            } else {
                logger.info(marker(), message);
            }
            warning = warning.getNextWarning();
        }
    }

}
