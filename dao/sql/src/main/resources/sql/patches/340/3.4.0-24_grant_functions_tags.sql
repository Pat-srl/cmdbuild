-- add tag filterFn to functions used as filter

DO $$ DECLARE
    _record record;
    _filter jsonb;
    _key varchar;
    _function_array jsonb;
    _function_json jsonb;
    _keyf varchar;
    _function_name varchar;
    _function_oid oid;
    _function_tag varchar;
BEGIN
    FOR _record IN SELECT * FROM "_Grant" WHERE "Status" = 'A' LOOP
        _filter = _record."Filter"::jsonb;
        FOR _key IN SELECT k FROM jsonb_object_keys(_filter) k WHERE k ~* '^functions$' LOOP
            _function_array = _filter ->> _key;
            FOR _function_json IN SELECT fn FROM jsonb_array_elements(_function_array) fn LOOP
                FOR _keyf IN SELECT kf FROM jsonb_object_keys(_function_json) kf WHERE kf ~* '^name$' LOOP
                    _function_name = _function_json ->> _keyf;
                    SELECT oid INTO _function_oid FROM pg_proc WHERE proname = _function_name;
                    SELECT tags INTO _function_tag FROM _cm3_function_comment_get(_function_oid, 'TAGS') tags;
                    IF _function_tag IS NULL THEN
                        PERFORM _cm3_function_comment_set(_function_oid, 'TAGS', 'filterFn');
                        RAISE NOTICE 'function % used as filter, added tag filterFn', _function_name;
                    END IF;
                END LOOP;
            END LOOP;
        END LOOP;
    END LOOP;
END; $$ LANGUAGE PLPGSQL;