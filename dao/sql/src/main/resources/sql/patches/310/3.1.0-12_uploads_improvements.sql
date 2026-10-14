-- improve uploads table

SELECT _cm3_class_triggers_disable('"_Upload"');

UPDATE "_Upload" SET "Code" = NULL;

ALTER TABLE "_Upload" ALTER COLUMN "Path" TYPE varchar USING array_to_string("Path",'/');

SELECT _cm3_class_triggers_enable('"_Upload"');

