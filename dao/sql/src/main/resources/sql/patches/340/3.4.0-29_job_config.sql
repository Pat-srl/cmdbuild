-- convert parsing keys into mapper for email tasks

SELECT _cm3_class_triggers_disable('"_Job"'::regclass);

DO $$ DECLARE
    _record record;
    _config jsonb;
    _key varchar;
    _value varchar;
    _mapper_key_init varchar;
    _mapper_key_end varchar;
    _mapper_value_init varchar;
    _mapper_value_end varchar;
BEGIN
    FOR _record IN SELECT * FROM "_Job" WHERE "Type" = 'emailService' AND "Status" = 'A' LOOP
        _config = _record."Config"::jsonb;
        FOR _key IN SELECT k FROM jsonb_object_keys(_config) k WHERE k ~* '^parsing_active$' LOOP
            _value = _config ->> _key;
            _mapper_key_init = _config ->> 'mapper_key_init';
            _mapper_key_end = _config ->> 'mapper_key_end';
            _mapper_value_init = _config ->> 'mapper_value_init';
            _mapper_value_end = _config ->> 'mapper_value_end';
            IF _value IS NOT NULL THEN
                UPDATE "_Job"
                SET "Config" = "Config"::jsonb || jsonb_build_object('mapper_active', _value)
                                               || jsonb_build_object('mapper_key_init', COALESCE(_mapper_key_init, '<key>'))
                                               || jsonb_build_object('mapper_key_end', COALESCE(_mapper_key_end, '</key>'))
                                               || jsonb_build_object('mapper_value_init', COALESCE(_mapper_value_init, '<value>'))
                                               || jsonb_build_object('mapper_value_end', COALESCE(_mapper_value_end, '</value>'))
                WHERE "Id" = _record."Id";
                RAISE NOTICE 'changing key from parsing to mapper for: %', _record."Code";
            END IF;
        END LOOP;
        UPDATE "_Job" SET "Config" = "Config"::jsonb - 'parsing_active' - 'parsing_key_init' - 'parsing_key_end' - 'parsing_value_init' - 'parsing_value_end' WHERE "Id" = _record."Id";
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_Job"'::regclass);