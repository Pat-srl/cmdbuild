-- shared filters and join

CREATE TABLE _aux AS SELECT "Id", "UserId" FROM "_Filter";

ALTER TABLE "_Filter" DROP COLUMN "UserId";
 
SELECT _cm3_attribute_create('OWNER: _Filter|NAME: UserId|TYPE: bigint|FKTARGETCLASS: User');

SELECT _cm3_class_triggers_disable('"_Filter"');

UPDATE "_Filter" f SET "UserId" = a."UserId" FROM _aux a WHERE a."Id" = f."Id";
UPDATE "_Filter" SET "UserId" = NULL WHERE "Shared" = TRUE;

SELECT _cm3_class_triggers_enable('"_Filter"');

UPDATE "_Filter" SET "Status" = 'N' WHERE "Status" = 'A' AND "Shared" = FALSE AND "UserId" IS NULL; -- delete invalid records

CREATE UNIQUE INDEX "_cm3__Filter_Code_ClassId_UserId" ON "_Filter" ("Code", "ClassId", "UserId") WHERE "Shared" = FALSE AND "Status" = 'A';
CREATE UNIQUE INDEX "_cm3__Filter_Code_ClassId" ON "_Filter" ("Code", "ClassId") WHERE "Shared" = TRUE AND "Status" = 'A';

DROP TABLE _aux;
 
SELECT _cm3_attribute_create('OWNER: _View|NAME: UserId|TYPE: bigint|FKTARGETCLASS: User');
SELECT _cm3_attribute_create('OWNER: _View|NAME: Shared|TYPE: boolean|NOTNULL: true|DEFAULT: true');

ALTER TABLE "_Filter" ADD CONSTRAINT "_cm3_Shared_check" CHECK ( ( "Shared" = TRUE AND "UserId" IS NULL ) OR ( "Shared" = FALSE AND "UserId" IS NOT NULL ) OR "Status" <> 'A' );
ALTER TABLE "_View" ADD CONSTRAINT "_cm3_Shared_check" CHECK ( ( "Shared" = TRUE AND "UserId" IS NULL ) OR ( "Shared" = FALSE AND "UserId" IS NOT NULL ) OR "Status" <> 'A' );

DROP INDEX "_cm3__View_Code";

SELECT _cm3_attribute_index_delete('"_View"', 'Code'); 

CREATE UNIQUE INDEX "_cm3__View_Code_UserId" ON "_View" ("Code", "UserId") WHERE "Shared" = FALSE AND "Status" = 'A';
CREATE UNIQUE INDEX "_cm3__View_Code" ON "_View" ("Code") WHERE "Shared" = TRUE AND "Status" = 'A';
