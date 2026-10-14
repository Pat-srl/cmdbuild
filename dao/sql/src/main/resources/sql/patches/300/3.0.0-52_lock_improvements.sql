--  improve lock table

TRUNCATE TABLE "_Lock";

SELECT _cm3_attribute_create('"_Lock"', 'RequestId', 'varchar(50)', 'NOTNULL: true|DESCR: Request id');
SELECT _cm3_attribute_create('"_Lock"', 'TimeToLive', 'int', 'NOTNULL: true|DESCR: time to live (seconds)');
SELECT _cm3_attribute_create('"_Lock"', 'Scope', 'varchar', 'NOTNULL: true|DESCR: lock scope');

ALTER TABLE "_Lock" ADD CONSTRAINT "_cm3_Scope_check" CHECK ("Scope" IN ('session', 'request'));
