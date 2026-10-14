/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.postgres.services;

import static com.google.common.base.Preconditions.checkNotNull;
import org.cmdbuild.dao.driver.repository.CardIdService;
import static org.cmdbuild.utils.lang.CmPreconditions.checkNotNullAndGtZero;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class CardIdServiceImpl implements CardIdService {

    private final JdbcTemplate jdbcTemplate;

    public CardIdServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
    }

    @Override
    public long newCardId() {
        return checkNotNullAndGtZero(jdbcTemplate.queryForObject("SELECT _cm3_utils_new_card_id()", Long.class));
    }

}
