-- bim constraint

SELECT _cm3_attribute_unique_set('"_BimProject"', 'Code');

CREATE UNIQUE INDEX "_cm3__BimObject_ProjectId" ON "_BimObject" ("ProjectId") WHERE "GlobalId" IS NULL AND "Status" = 'A'; --TODO check this
