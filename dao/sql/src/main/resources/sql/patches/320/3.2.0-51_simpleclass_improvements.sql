-- add status column to simple class

ALTER TABLE "SimpleClass" ADD COLUMN "Status" char(1) NOT NULL DEFAULT 'A';
COMMENT ON COLUMN "SimpleClass"."Status" IS 'MODE: reserved';

DO $$ DECLARE
    _class regclass;
BEGIN
    FOR _class IN SELECT _cm3_class_list_simple() LOOP
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_Status_check" CHECK ("Status" = ''A'')', _class);
        EXECUTE format('COMMENT ON COLUMN %s."Status" IS ''MODE: reserved'';', _class);
    END LOOP;
END $$ LANGUAGE PLPGSQL;
