-- convert all task key from gate to target
-- PARAMS: FORCE_IF_NOT_EXISTS=true

SELECT _cm3_class_triggers_disable('"_Job"'::regclass);

DO $$ DECLARE
    _record record;
    _config jsonb;
    _key varchar;
    _new_key varchar;
    _value varchar;
BEGIN
    FOR _record IN SELECT * FROM "_Job" WHERE "Type" = 'etl' AND "Config" ->> 'tag' IN ('ifc', 'cad') AND "Status" = 'A' LOOP
        _config = _record."Config"::jsonb;
        FOR _key IN SELECT k FROM jsonb_object_keys(_config) k WHERE k ~* '^gateconfig_handlers_._gate$' LOOP
            _value = _config ->> _key;
            IF _value IS NOT NULL THEN
                _new_key = REGEXP_REPLACE(_key, '(.*)gate$', '\1target');
                UPDATE "_Job"
                SET "Config" = "Config"::jsonb || jsonb_build_object(_new_key, _value)
                WHERE "Id" = _record."Id";
                UPDATE "_Job" SET "Config" = "Config"::jsonb - _key WHERE "Id" = _record."Id";
                RAISE NOTICE 'changing key from % to % for: %', _key, _new_key, _record."Code";
            END IF;
        END LOOP;
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_Job"'::regclass);