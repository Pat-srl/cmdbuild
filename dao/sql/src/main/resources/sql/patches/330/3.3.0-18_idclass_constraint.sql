-- idclass constraint 

DO $$ DECLARE
    _class regclass;
BEGIN
    FOR _class IN SELECT * FROM _cm3_class_list() x WHERE NOT _cm3_class_is_superclass(x) LOOP
        EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdClass" SET DEFAULT %L', _class, _class);
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "_cm3_IdClass_check"', _class);
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_IdClass_check" CHECK ( "IdClass" = %L::regclass )', _class, _class);
        IF _cm3_class_has_history(_class) THEN
            EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdClass" SET DEFAULT %L', _cm3_utils_regclass_to_history(_class), _class);
        END IF;
    END LOOP;
    FOR _class IN SELECT * FROM _cm3_domain_list() x LOOP
        EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdDomain" SET DEFAULT %L', _class, _class);
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "_cm3_IdDomain_check"', _class);
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_IdDomain_check" CHECK ( "IdDomain" = %L::regclass )', _class, _class);
        EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdDomain" SET DEFAULT %L', _cm3_utils_regclass_to_history(_class), _class);
    END LOOP;
END $$ LANGUAGE PLPGSQL;
