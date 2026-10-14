-- fixing IdClass default and constraint for gis tables

DO $$ DECLARE
    _class regclass;
BEGIN
    FOR _class IN SELECT * FROM _cm3_class_list_gis() x WHERE NOT _cm3_class_is_superclass(x) LOOP
        EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdClass" SET DEFAULT %L', _class, _class);
        EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "_cm3_IdClass_check"', _class);
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_IdClass_check" CHECK ( "IdClass" = %L::regclass )', _class, _class);
    END LOOP;
END $$ LANGUAGE PLPGSQL;