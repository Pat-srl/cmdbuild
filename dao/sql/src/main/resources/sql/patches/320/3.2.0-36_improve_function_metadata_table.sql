-- improve function metadata table

SELECT _cm3_attribute_create('OWNER: _FunctionMetadata|NAME: Owner|TYPE: varchar');

SELECT _cm3_class_triggers_disable('"_FunctionMetadata"');
UPDATE "_FunctionMetadata" SET "Owner" = "OwnerFunction"::regproc::varchar;
SELECT _cm3_class_triggers_enable('"_FunctionMetadata"');

ALTER TABLE "_FunctionMetadata" DROP COLUMN "OwnerFunction" CASCADE;

SELECT _cm3_attribute_notnull_set('"_FunctionMetadata"', 'Owner');
SELECT _cm3_attribute_unique_set('"_FunctionMetadata"', 'Owner');

