-- updating context menu config field from text to jsonb

SELECT _cm3_class_triggers_disable('"_ContextMenu"');
UPDATE "_ContextMenu" SET "Config" = null;
SELECT _cm3_class_triggers_enable('"_ContextMenu"');

SELECT _cm3_attribute_delete('"_ContextMenu"', 'Config');
SELECT _cm3_attribute_create('"_ContextMenu"', 'Config', 'jsonb', 'DEFAULT: ''{}''::jsonb|NOTNULL: true|MODE: read|DESCR: Config');