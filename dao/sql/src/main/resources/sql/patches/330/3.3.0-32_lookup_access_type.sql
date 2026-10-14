-- add lookup access type, replace lookup isSystem attr

SELECT _cm3_attribute_create('OWNER: LookUp|NAME: AccessType|TYPE: varchar|NOTNULL: true|DEFAULT: default|VALUES: default,system,protected');

UPDATE "LookUp" SET "AccessType" = 'system' WHERE "IsSystem" = TRUE AND "Status" = 'A';
UPDATE "LookUp" SET "AccessType" = 'protected' WHERE "Type" IN ('CalendarFrequency', 'CalendarPriority', 'CalendarCategory') AND "Status" = 'A';

ALTER TABLE "LookUp" DROP COLUMN "IsSystem" CASCADE;
    