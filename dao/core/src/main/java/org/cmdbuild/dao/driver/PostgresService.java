package org.cmdbuild.dao.driver;

import java.util.List;
import java.util.Map;
import org.cmdbuild.dao.driver.repository.AttributeRepository;
import org.cmdbuild.dao.driver.repository.ClasseRepository;
import org.cmdbuild.dao.driver.repository.DomainRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.cmdbuild.dao.entrytype.EntryType;
import org.cmdbuild.dao.beans.DatabaseRecord;
import org.cmdbuild.dao.driver.repository.StoredFunctionRepository;

public interface PostgresService extends DomainRepository, AttributeRepository, StoredFunctionRepository, ClasseRepository {

    Long create(DatabaseRecord entry);

    List<Long> createBatch(List<DatabaseRecord> records);

    void update(DatabaseRecord entry);

    void delete(DatabaseRecord entry);

    void truncate(EntryType type);

    JdbcTemplate getJdbcTemplate();

    EntryType updateEntryTypeMetadata(EntryType owner, Map<String, String> entryTypeMetadata);

}
