-- email date

SELECT _cm3_attribute_create('"Email"', 'EmailDate', 'timestamptz', 'MODE: write|DESCR: email received/sent date');

SELECT _cm3_class_triggers_disable('"Email"');
UPDATE "Email" SET "EmailDate" = "BeginDate" WHERE "EmailStatus" IN ('received', 'sent');
SELECT _cm3_class_triggers_enable('"Email"');	
