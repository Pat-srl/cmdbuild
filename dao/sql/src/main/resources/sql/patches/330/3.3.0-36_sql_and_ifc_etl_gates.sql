-- sql and ifc etl gates
 
DO $$ DECLARE
    _handler_id bigint;
    _job record;
BEGIN
    FOR _job IN SELECT * FROM "_Job" WHERE "Status" = 'A' AND "Type" = 'import_database' LOOP
        _handler_id = _cm3_utils_new_card_id();
        RAISE NOTICE 'migrate import db job = %', _job;
        INSERT INTO "_EtlGate" ("Code", "Description", "Handlers", "Items", "Config") VALUES (
            _job."Code", 
            _job."Description", 
            ARRAY[_handler_id], 
            '[]'::jsonb || jsonb_build_object('Id', _handler_id, 'IdClass', '_EtlGateHandler', 'type', 'database', 'templates', (SELECT array_agg(t."Code") FROM "_ImportExportTemplate" t WHERE t."Id" = ANY (SELECT unnest(string_to_array(_job."Config"->>'etlTemplates', ','))::bigint) AND "Status" = 'A')),
            ( _job."Config" - 'etlTemplates' - 'cronExpression' - 'etlTemplatesArray'));
        UPDATE "_Job" SET "Type" = 'etl', "Config" = jsonb_build_object('gate', _job."Code", 'cronExpression', _job."Config"->>'cronExpression') WHERE "Id" = _job."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;
 
ALTER TABLE "_Job" DROP CONSTRAINT "_cm3_Type_check";
ALTER TABLE "_Job" ADD CONSTRAINT "_cm3_Type_check" CHECK ("Status" <> 'A' OR "Type" IN ('script','emailService','export_file','import_file','stored_function','workflow','etl','sendemail'));

