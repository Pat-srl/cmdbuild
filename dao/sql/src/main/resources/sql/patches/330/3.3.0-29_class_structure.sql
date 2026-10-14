-- class structure tables refactoring

SELECT _cm3_class_triggers_disable('"_AttributeMetadata"');
SELECT _cm3_class_triggers_disable('"_ClassMetadata"');
SELECT _cm3_class_triggers_disable('"_AttributeGroup"');

UPDATE "_ClassMetadata" SET "Code" = _cm3_utils_regclass_to_name("Owner");
ALTER TABLE "_ClassMetadata" DROP COLUMN "Owner" CASCADE;

CREATE TABLE _patch_aux_1 AS SELECT "Id" _id, _cm3_utils_regclass_to_name("Owner") _owner FROM "_AttributeMetadata";
CREATE TABLE _patch_aux_2 AS SELECT "Id" _id, _cm3_utils_regclass_to_name("Owner") _owner FROM "_AttributeGroup";

ALTER TABLE "_AttributeMetadata" DROP COLUMN "Owner" CASCADE;
ALTER TABLE "_AttributeMetadata" ADD COLUMN "Owner" VARCHAR;
ALTER TABLE "_AttributeGroup" DROP COLUMN "Owner" CASCADE;
ALTER TABLE "_AttributeGroup" ADD COLUMN "Owner" VARCHAR;

UPDATE "_AttributeMetadata" SET "Owner" = (SELECT _owner FROM _patch_aux_1 WHERE _id = "Id");
UPDATE "_AttributeGroup" SET "Owner" = (SELECT _owner FROM _patch_aux_2 WHERE _id = "Id");

SELECT _cm3_class_triggers_enable('"_AttributeMetadata"');
SELECT _cm3_class_triggers_enable('"_ClassMetadata"');
SELECT _cm3_class_triggers_enable('"_AttributeGroup"');

DROP TABLE _patch_aux_1;
DROP TABLE _patch_aux_2;

