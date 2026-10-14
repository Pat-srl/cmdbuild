/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.cmdbuild.dao.config.inner;

import static com.google.common.base.Preconditions.checkNotNull;
import java.util.List;
import static org.cmdbuild.spring.BeanNamesAndQualifiers.SYSTEM_LEVEL_ONE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import static org.cmdbuild.utils.date.CmDateUtils.toDateTime;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_BEGINDATE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_CODE;
import static org.cmdbuild.dao.constants.SystemAttributes.ATTR_DESCRIPTION;

@Component
public class PatchCardRepositoryImpl implements PatchCardRepository {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String CATEGORY_ATTR = "Category",
            HASH_ATTR = "Hash",
            CONTENT_ATTR = "Content";

    private final JdbcTemplate jdbcTemplate;

    public PatchCardRepositoryImpl(@Qualifier(SYSTEM_LEVEL_ONE) JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = checkNotNull(jdbcTemplate);
    }

    @Override
    public void store(Patch patch) {
        jdbcTemplate.update("INSERT INTO \"_Patch\" (\"Code\",\"Description\",\"Category\",\"Hash\",\"Content\") VALUES (?,?,?,?,?)",
                patch.getVersion(), patch.getDescription(), patch.getCategory(), patch.getHash(), patch.getContent());
    }

    @Override
    public void rebuildPatch(Patch patch) {
        jdbcTemplate.update("UPDATE \"_Patch\" SET \"Hash\" = ?,\"Content\" = ? WHERE \"Code\" = ? AND \"Status\" = 'A'",
                patch.getHash(), patch.getContent(), patch.getVersion());
    }

    @Override
    public List<Patch> findAll() {
        return jdbcTemplate.query("SELECT \"Code\",\"Description\",\"BeginDate\",\"Category\",\"Hash\",\"Content\" FROM \"_Patch\" ", (RowMapper<Patch>) (row, i) -> PatchImpl.builder()
                .withVersion(row.getString(ATTR_CODE))
                .withDescription(row.getString(ATTR_DESCRIPTION))
                .withCategory(row.getString(CATEGORY_ATTR))
                .appliedOn(toDateTime(row.getTimestamp(ATTR_BEGINDATE)))
                .withContent(row.getString(CONTENT_ATTR))
                .withHash(row.getString(HASH_ATTR))
                .build());
    }

}
