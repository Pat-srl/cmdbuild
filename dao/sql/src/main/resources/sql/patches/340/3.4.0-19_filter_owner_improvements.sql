-- improve filter owner mechanism

DROP INDEX IF EXISTS "_cm3__Filter_Code_ClassId_UserId";
DROP INDEX IF EXISTS "_cm3__Filter_Code_ClassId";

ALTER TABLE "_Filter" ALTER COLUMN "ClassId" TYPE varchar USING _cm3_utils_regclass_to_name("ClassId");
ALTER TABLE "_Filter" RENAME COLUMN "ClassId" TO "Owner";
SELECT _cm3_attribute_create('OWNER: _Filter|NAME: OwnerType|TYPE: varchar|DEFAULT: class|NOTNULL: true|VALUES: class,view');

CREATE UNIQUE INDEX "_cm3__Filter_Code_Owner_OwnerType_UserId" ON "_Filter" ("Code", "Owner", "OwnerType", "UserId") WHERE "Shared" = FALSE AND "Status" = 'A';
CREATE UNIQUE INDEX "_cm3__Filter_Code_Owner_OwnerType" ON "_Filter" ("Code", "Owner", "OwnerType") WHERE "Shared" = TRUE AND "Status" = 'A';