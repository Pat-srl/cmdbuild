-- fix bim object constraint

DROP INDEX IF EXISTS "_cm3__BimObject_GlobalId";

CREATE UNIQUE INDEX "_cm3__BimObject_ProjectId_GlobalId" ON "_BimObject" ("ProjectId", "GlobalId") WHERE "Status" = 'A' AND "GlobalId" IS NOT NULL;
