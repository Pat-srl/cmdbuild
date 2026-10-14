-- convert task key from gate to target
-- PARAMS: FORCE_IF_NOT_EXISTS=true

SELECT _cm3_class_triggers_disable('"_Job"'::regclass);

DO $$ DECLARE
    _record record;
    _config jsonb;
    _key varchar;
    _value varchar;
BEGIN
    FOR _record IN SELECT * FROM "_Job" WHERE "Type" = 'etl' AND "Config" ->> 'tag' = 'database' AND "Status" = 'A' LOOP
        _config = _record."Config"::jsonb;
        FOR _key IN SELECT k FROM jsonb_object_keys(_config) k WHERE k ~* '^gateconfig_handlers_0_gate$' LOOP
            _value = _config ->> _key;
            IF _value IS NOT NULL THEN
                UPDATE "_Job"
                SET "Config" = "Config"::jsonb || jsonb_build_object('gateconfig_handlers_0_target', _value)
                WHERE "Id" = _record."Id";
                RAISE NOTICE 'changing key from gateconfig_handlers_0_gate to gateconfig_handlers_0_target for: %', _record."Code";
            END IF;
        END LOOP;
        UPDATE "_Job" SET "Config" = "Config"::jsonb - 'gateconfig_handlers_0_gate' WHERE "Id" = _record."Id";
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_Job"'::regclass);