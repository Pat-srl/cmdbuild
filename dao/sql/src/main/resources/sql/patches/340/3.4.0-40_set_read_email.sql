-- set read to all email received

SELECT _cm3_class_triggers_disable('"Email"'::regclass);

DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "Email" WHERE "EmailStatus"='received' AND "Meta" ->> 'cm_readByUser' IS NULL AND "Status" = 'A' LOOP
        UPDATE "Email" SET "Meta" = "Meta" || jsonb_build_object('cm_readByUser', 'true') WHERE "Id" = _record."Id";
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"Email"'::regclass);