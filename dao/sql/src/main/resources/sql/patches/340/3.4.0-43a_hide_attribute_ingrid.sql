-- convert all virtual attributes adding new metadata

SELECT _cm3_class_triggers_disable('"_ClassMetadata"'::regclass);

DO $$ DECLARE
    _record record;
    _metadata jsonb;
    _key varchar;
BEGIN
    FOR _record IN SELECT * FROM "_ClassMetadata" WHERE "Status" = 'A' LOOP
        _metadata = _record."Metadata";
        FOR _key IN SELECT k FROM jsonb_object_keys(_metadata) k WHERE k ~* '^cm_virtual_attributes(.+)cm_hide_in_filter' LOOP
            UPDATE "_ClassMetadata"
                SET "Metadata" = "Metadata" || jsonb_build_object(REPLACE(_key, 'cm_hide_in_filter', 'cm_hide_in_grid'), 'true')
                WHERE "Id" = _record."Id";
                RAISE NOTICE 'adding value % to %', REPLACE(_key, 'cm_hide_in_filter', 'cm_hide_in_grid'), _record."Code";
            IF _metadata ->> _key <> 'true' THEN
                RAISE NOTICE 'key % found for %', _key, _record."Code";
                UPDATE "_ClassMetadata"
                    SET "Metadata" = "Metadata" || jsonb_build_object(_key, 'true')
                    WHERE "Id" = _record."Id";
                    RAISE NOTICE 'changing value from % to % for: %', 'false', 'true', _key;
            END IF;
        END LOOP;
        FOR _key IN SELECT k FROM jsonb_object_keys(_metadata) k WHERE k ~* '^cm_virtual_attributes(.+)cm_ui_sortable' LOOP
            IF _metadata ->> _key <> 'false' THEN
                RAISE NOTICE 'key % found for %', _key, _record."Code";
                UPDATE "_ClassMetadata"
                    SET "Metadata" = "Metadata" || jsonb_build_object(_key, 'false')
                    WHERE "Id" = _record."Id";
                    RAISE NOTICE 'changing value from % to % for: %', 'true', 'false', _key;
            END IF;
        END LOOP;
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_ClassMetadata"'::regclass);