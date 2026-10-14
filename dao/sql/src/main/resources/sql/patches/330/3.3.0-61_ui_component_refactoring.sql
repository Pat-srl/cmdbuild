-- ui component refactoring

CREATE TABLE _aux AS SELECT "Code", "Data", "TargetDevice", "Type" FROM "_UiComponent" WHERE "Status" = 'A';

-- ALTER TABLE "_UiComponent" DROP CONSTRAINT "_cm3_TargetDevice_check";
UPDATE "_UiComponent" u SET "TargetDevice" = 'default' WHERE "TargetDevice" = 'mobile' AND "Status" = 'A' AND NOT EXISTS (SELECT * FROM "_UiComponent" WHERE "TargetDevice" = 'default' AND "Type" = u."Type" AND "Code" = u."Code" AND "Status" = 'A');
UPDATE "_UiComponent" u SET "Status" = 'N' WHERE "TargetDevice" = 'mobile' AND "Status" = 'A';

ALTER TABLE "_UiComponent" DROP COLUMN "TargetDevice";
ALTER TABLE "_UiComponent" DROP COLUMN "Data";

SELECT _cm3_attribute_create('OWNER: _UiComponent|NAME: DataDefault|TYPE: bytea');
SELECT _cm3_attribute_create('OWNER: _UiComponent|NAME: DataMobile|TYPE: bytea');

UPDATE "_UiComponent" u SET "DataDefault" = (SELECT "Data" FROM _aux a WHERE a."Code" = u."Code" AND a."Type" = u."Type" AND "TargetDevice" = 'default'), "DataMobile" = (SELECT "Data" FROM _aux a WHERE a."Code" = u."Code" AND a."Type" = u."Type" AND "TargetDevice" = 'mobile') WHERE "Status" = 'A';

ALTER TABLE "_UiComponent" ADD CONSTRAINT "_cm3_Data_check" CHECK ("DataDefault" IS NOT NULL OR "DataMobile" IS NOT NULL OR "Status" <> 'A');

DROP TABLE _aux;

