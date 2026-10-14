--- fix permission of templates table

DO $$ BEGIN
    IF _cm3_class_features_get('"_Templates"', 'MODE') = 'default' THEN
        PERFORM _cm3_class_features_set('"_Templates"', 'MODE', 'protected');
    END IF;
END $$ LANGUAGE PLPGSQL;