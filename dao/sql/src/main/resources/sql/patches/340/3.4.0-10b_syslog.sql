-- syslog improvements (TODO: improve this, reorder columns! )

SELECT _cm3_attribute_create('OWNER: _SystemStatusLog|NAME: Params|TYPE: jsonb|NOTNULL: true|DESCR: Waterway Config params|DEFAULT: ''{}''::jsonb');


SELECT _cm3_attribute_create('OWNER: _SystemStatusLog|NAME: SystemMemoryTotal|TYPE: integer|MODE: read|DESCR: Total System Memory (MB)');
SELECT _cm3_attribute_create('OWNER: _SystemStatusLog|NAME: FilesystemMemoryTotal|TYPE: integer|MODE: read|DESCR: Total Filesystem Memory (MB)');
SELECT _cm3_attribute_create('OWNER: _SystemStatusLog|NAME: JavaMemoryTotal|TYPE: integer|MODE: read|DESCR: Total Java Memory (MB)');
SELECT _cm3_attribute_create('OWNER: _SystemStatusLog|NAME: JavaMemoryMax|TYPE: integer|MODE: read|DESCR: Max Java Memory (MB)');


SELECT _cm3_class_triggers_disable('"_SystemStatusLog"'::regclass);

UPDATE "_SystemStatusLog" SET "SystemMemoryTotal" = "SystemMemoryAvailable", "FilesystemMemoryTotal" = "FilesystemMemoryAvailable", "JavaMemoryTotal" = "JavaMemoryAvailable", "JavaMemoryMax" = "JavaMemoryAvailable";

SELECT _cm3_class_triggers_enable('"_SystemStatusLog"'::regclass);

ALTER TABLE "_SystemStatusLog" DROP COLUMN "SystemMemoryAvailable";
ALTER TABLE "_SystemStatusLog" DROP COLUMN "FilesystemMemoryAvailable";
ALTER TABLE "_SystemStatusLog" DROP COLUMN "JavaMemoryAvailable";

SELECT _cm3_attribute_notnull_set('"_SystemStatusLog"'::regclass, 'JavaMemoryTotal', TRUE);
SELECT _cm3_attribute_notnull_set('"_SystemStatusLog"'::regclass, 'JavaMemoryMax', TRUE);
