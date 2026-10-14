-- entity metadata upgrade 2

SELECT _cm3_attribute_index_unique_create('"_AttributeMetadata"', 'Owner', 'Code');

SELECT _cm3_class_triggers_disable('"_EmailTemplate"');
SELECT _cm3_class_triggers_disable('"_CardMetadata"'); 

UPDATE "_EmailTemplate" SET "Data" = coalesce((SELECT "Data" FROM "_CardMetadata" WHERE "Status" = 'A' AND "OwnerClass" = '"_EmailTemplate"'::regclass AND "OwnerCard" = "_EmailTemplate"."Id"),'{}'::jsonb) WHERE "Status" = 'A';
DELETE FROM "_CardMetadata" WHERE "OwnerClass" = '"_EmailTemplate"'::regclass;

UPDATE "_EmailTemplate" SET "KeepSynchronization" = true WHERE "KeepSynchronization" IS NULL;
UPDATE "_EmailTemplate" SET "PromptSynchronization" = true WHERE "PromptSynchronization" IS NULL;

SELECT _cm3_attribute_modify('"_EmailTemplate"', 'KeepSynchronization', 'boolean', 'DEFAULT: true|NOTNULL: true|MODE: write|DESCR: Keep synchronization');
SELECT _cm3_attribute_modify('"_EmailTemplate"', 'PromptSynchronization', 'boolean', 'DEFAULT: true|NOTNULL: true|MODE: write|DESCR: Prompt synchronization'); 

SELECT _cm3_class_triggers_enable('"_EmailTemplate"');
SELECT _cm3_class_triggers_enable('"_CardMetadata"');
