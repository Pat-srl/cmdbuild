/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.cmdbuild.utils.lang.CmPreconditions.firstNotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.cmdbuild.minions.PostStartup;

@Component
public class PostgresDateServiceImpl implements PostgresDateService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final JdbcTemplate jdbcTemplate;

    public PostgresDateServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
    }

    @PostStartup
    public void printInfo() {
        logger.info("postgres db timezone = {}", getTimezone());
        logger.info("postgres db timezone offset = {}", getOffset());
    }

    @Override
    public String getTimezone() {
        return firstNotBlank(jdbcTemplate.queryForObject("SHOW timezone", String.class), "unknown");
    }

    @Override
    public String getOffset() {
        return firstNotBlank(jdbcTemplate.queryForObject("SELECT current_timestamp - current_timestamp AT TIME ZONE 'UTC'", String.class), "unknown")
                .replaceFirst("([+-]?[0-9]+:[0-9]+)(:[0-9]+)?", "$1").replaceFirst("^[0-9]", "+$0");
    }

}
