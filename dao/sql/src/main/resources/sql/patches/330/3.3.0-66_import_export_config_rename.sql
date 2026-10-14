-- alwaysHandleMissingRecords has been renamed to handleMissingRecordsOnError

DO $$ DECLARE
    _record record;
BEGIN    
    FOR _record IN SELECT * FROM "_ImportExportTemplate" WHERE "Status" = 'A' AND "Config"->'alwaysHandleMissingRecords' IS NOT NULL LOOP 
	UPDATE "_ImportExportTemplate" SET "Config" = _record."Config" - 'alwaysHandleMissingRecords' || jsonb_build_object('handleMissingRecordsOnError', _record."Config"->'alwaysHandleMissingRecords') WHERE "Id" = _record."Id" AND "Status"='A';
    END LOOP;
END $$ LANGUAGE PLPGSQL;