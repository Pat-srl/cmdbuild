-- grant table improvements

UPDATE "_Grant" SET "Status" = 'N' WHERE "Status" = 'A' AND "Mode" = '-';

ALTER TABLE "_Grant" DROP CONSTRAINT "_cm3_Mode_check";
ALTER TABLE "_Grant" DROP CONSTRAINT "_cm3_Type_check";

SELECT _cm3_utils_store_and_drop_dependant_views('"_Grant"'::regclass);

ALTER TABLE "_Grant" ALTER COLUMN "Mode" TYPE varchar;

SELECT _cm3_utils_restore_dependant_views();

SELECT _cm3_class_triggers_disable('"_Grant"');

UPDATE "_Grant" SET "Type" = lower("Type");
DELETE FROM "_Grant" WHERE "ObjectClass" IS NULL AND "Type" = 'class';
DELETE FROM "_Grant" WHERE "ObjectId" IS NULL AND "Type" <> 'class';
UPDATE "_Grant" SET "Type" = 'process' WHERE "Type" = 'class' AND _cm3_class_is_process("ObjectClass");
UPDATE "_Grant" SET "Type" = 'etltemplate' WHERE "Type" = 'ietemplate';
UPDATE "_Grant" SET "Mode" = CASE "Mode" WHEN 'r' THEN 'read' ELSE 'write' END WHERE "Type" <> 'process';
UPDATE "_Grant" SET "Mode" = CASE "Mode" WHEN 'r' THEN 'wf_default' ELSE 'wf_plus' END WHERE "Type" = 'process';

SELECT _cm3_class_triggers_enable('"_Grant"');

ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_Mode_check" CHECK ( ("Type" = 'process' AND "Mode" IN ('wf_default','wf_plus','wf_basic')) OR ("Type" <> 'process' AND "Mode" IN ('read','write')) );
ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_Type_check" CHECK ( "Type" IN ('class','process','custompage','filter','view','report','etltemplate','dashboard') );
ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_ObjectClassObjectId_check" CHECK ( ( "Type" IN ('class','process') AND "ObjectClass" IS NOT NULL AND "ObjectId" IS NULL ) OR ( "Type" NOT IN ('class','process') AND "ObjectClass" IS NULL AND "ObjectId" IS NOT NULL ) );
