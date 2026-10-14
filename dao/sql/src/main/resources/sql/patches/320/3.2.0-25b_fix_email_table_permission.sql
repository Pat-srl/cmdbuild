-- improve email table permissions, constraint

DO $$ BEGIN
    IF _cm3_class_features_get('"Email"', 'MODE') = 'reserved' THEN
        PERFORM _cm3_class_features_set('"Email"', 'MODE', 'protected');
    END IF;
END $$ LANGUAGE PLPGSQL;

UPDATE "Email" SET "EmailDate" = "BeginDate" WHERE "Status"='A' AND "EmailStatus" IN ('received','sent') AND "EmailDate" IS NULL;

ALTER TABLE "Email" ADD CONSTRAINT "_cm3_EmailDate_check" CHECK (  "Status" <> 'A' OR "EmailStatus" NOT IN ('received','sent') OR "EmailDate" IS NOT NULL );

