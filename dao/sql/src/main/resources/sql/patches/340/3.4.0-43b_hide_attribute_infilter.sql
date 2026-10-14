-- set hideInGrid for all hideInFilter attributes

SELECT _cm3_class_triggers_disable('"_AttributeMetadata"'::regclass);

DO $$ DECLARE
    _record record;
    _metadata jsonb;
    _key varchar;
BEGIN
    FOR _record IN SELECT * FROM "_AttributeMetadata" WHERE "Status" = 'A' LOOP
        _metadata = _record."Metadata";
        FOR _key IN SELECT k FROM jsonb_object_keys(_metadata) k WHERE k ~* '^cm_hide_in_filter$' LOOP
            IF _metadata ->> _key = 'true' THEN
                RAISE NOTICE 'key % with value % found for % - %', _key, _metadata ->> _key, _record."Owner", _record."Code";
                UPDATE "_AttributeMetadata"
                    SET "Metadata" = "Metadata" || jsonb_build_object('cm_hide_in_grid', 'true')
                    WHERE "Id" = _record."Id";
                    RAISE NOTICE 'adding "cm_hide_in_grid : true" for: % - %', _record."Owner", _record."Code";
            END IF;
        END LOOP;
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_AttributeMetadata"'::regclass);