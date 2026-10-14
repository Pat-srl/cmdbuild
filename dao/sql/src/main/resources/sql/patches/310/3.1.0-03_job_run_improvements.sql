-- improved job run logs, and system status log

SELECT _cm3_attribute_create('"_JobRun"', 'Logs', 'varchar', '');
SELECT _cm3_attribute_create('"_JobRun"', 'HasError', 'boolean', '');
SELECT _cm3_attribute_create('"_JobRun"', 'NodeId', 'varchar', '');

UPDATE "_JobRun" SET "HasError" = COALESCE( "Errors"::varchar <> '{"data":[]}'::jsonb::varchar , false);

SELECT _cm3_attribute_notnull_set('"_JobRun"', 'HasError');

SELECT _cm3_attribute_create('"_SystemStatusLog"', 'NodeId', 'varchar', '');
