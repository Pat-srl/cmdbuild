-- improve grant and role tables

ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('Class', 'CustomPage', 'Filter', 'View', 'Report', 'IETemplate') );
ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_Mode_check" CHECK ( "Mode" ~ '^[rw-]$' );

ALTER TABLE "Role" ALTER COLUMN "Type" SET DEFAULT 'default';

ALTER TABLE "User" ALTER COLUMN "Service" SET DEFAULT false;

SELECT _cm3_class_triggers_disable('"User"');

UPDATE "User" SET "Service" = FALSE WHERE "Service" IS NULL;

SELECT _cm3_class_triggers_enable('"User"');

SELECT _cm3_attribute_notnull_set('"User"', 'Service');
