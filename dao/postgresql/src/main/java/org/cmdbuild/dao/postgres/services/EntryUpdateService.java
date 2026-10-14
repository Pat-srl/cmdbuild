package org.cmdbuild.dao.postgres.services;

import java.util.List;
import org.cmdbuild.dao.beans.DatabaseRecord;

public interface EntryUpdateService {

    long executeInsertAndReturnKey(DatabaseRecord entry);

    List<Long> executeBatchInsertAndReturnKeys(List<DatabaseRecord> records);

    void executeUpdate(DatabaseRecord entry);

}
