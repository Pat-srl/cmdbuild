-- class default template config

DO $$ DECLARE
    _class regclass;
    _config varchar;
BEGIN
    FOR _class, _config IN WITH q AS (SELECT table_id::regclass c, features->>'cm_default_import_template' val FROM _cm3_class_list_detailed()) SELECT c, val FROM q WHERE _cm3_utils_is_not_blank(val) AND val ~ '^[0-9]+$' LOOP
        PERFORM _cm3_class_features_set(_class, 'cm_default_import_template', format('template:%s', _config));
    END LOOP;
END $$ LANGUAGE PLPGSQL;
