-- set default processing for report

SELECT _cm3_class_triggers_disable('"_Report"'::regclass);

DO $$ DECLARE
    _config jsonb;
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_Report" WHERE "Status" = 'A' LOOP
        _config = _record."Config";
        IF _cm3_utils_is_blank(_config->>'processing') THEN
            _config = _config || jsonb_build_object('processing', 'realtime');
            UPDATE "_Report" SET "Config" = _config WHERE "Id" = _record."Id";
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_Report"'::regclass);