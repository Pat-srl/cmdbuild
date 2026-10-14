--  stored filters improvements

SELECT _cm3_attribute_create('"Map_FilterRole"', 'DefaultFor', 'regclass', '');

SELECT _cm3_class_triggers_disable('"Map_FilterRole"');
UPDATE "Map_FilterRole" m SET "DefaultFor" = (SELECT "ClassId" FROM "_Filter" WHERE "Id" = m."IdObj1" AND "Status" = 'A') WHERE "Status" = 'A';
SELECT _cm3_class_triggers_enable('"Map_FilterRole"');

SELECT _cm3_attribute_notnull_set('"Map_FilterRole"', 'DefaultFor');
CREATE UNIQUE INDEX "_cm3_Map_FilterRole_DefaultFor" ON "Map_FilterRole" ("IdObj1", "IdObj2", "DefaultFor") WHERE "Status" = 'A';
