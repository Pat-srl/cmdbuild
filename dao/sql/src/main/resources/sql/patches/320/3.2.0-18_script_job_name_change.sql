-- change script job name, job name constraint

UPDATE "_Job" SET "Type" = 'script' WHERE "Type" = 'scheduled_script' AND "Status" = 'A';
UPDATE "_Job" SET "Type" = 'stored_function' WHERE "Type" = 'scheduled_db_function' AND "Status" = 'A';

DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_Job" WHERE "Type" NOT IN ('script','emailService','export_file','import_file','stored_function','workflow','etl') AND "Status" = 'A' LOOP
        RAISE WARNING 'unsupported job type = % for job = % %, set status to N', _record."Type", _record."Id", _record."Code";
        UPDATE "Email" SET "Card" = NULL WHERE "Status" = 'A' AND "Card" = _record."Id";
        UPDATE "_Job" SET "Status" = 'N' WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_Job" ADD CONSTRAINT "_cm3_Type_check" CHECK ("Status" <> 'A' OR "Type" IN ('script','emailService','export_file','import_file','stored_function','workflow','etl') );
