-- fix begindate on simple class

COMMENT ON COLUMN "SimpleClass"."BeginDate" IS 'MODE: reserved';

DO $$ DECLARE
    _class regclass;
BEGIN
    FOR _class IN SELECT * FROM _cm3_class_list_simple() LOOP
        EXECUTE format('COMMENT ON COLUMN %s."BeginDate" IS ''MODE: reserved''', _class);
    END LOOP;
END $$ LANGUAGE PLPGSQL;
