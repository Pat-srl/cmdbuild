-- lookup trigger

ALTER TABLE "LookUp" ALTER COLUMN "Index" SET DEFAULT -1;

CREATE OR REPLACE FUNCTION _cm3_trigger_lookup() RETURNS trigger AS $$ DECLARE
    _type varchar;
BEGIN
    FOR _type IN SELECT DISTINCT "Type" FROM "LookUp" l1 WHERE "Status" = 'A' AND NOT EXISTS (SELECT * FROM "LookUp" l2 WHERE l1."Type" = l2."Type" AND "Status" = 'A' AND "Code" = 'org.cmdbuild.LOOKUPTYPE') LOOP
        INSERT INTO "LookUp" ("Code", "Type") VALUES ('org.cmdbuild.LOOKUPTYPE', _type);
    END LOOP;
	RETURN NULL;
END $$ LANGUAGE PLPGSQL;

CREATE TRIGGER "_cm3_lookup_trigger" AFTER INSERT OR UPDATE ON "LookUp" EXECUTE PROCEDURE _cm3_trigger_lookup();
