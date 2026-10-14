-- gis fix


SELECT _cm3_class_triggers_disable('"_AttributeMetadata"');
SELECT _cm3_class_triggers_disable('"_ClassMetadata"');

DELETE FROM "_ClassMetadata" WHERE NOT EXISTS ( SELECT 1 FROM pg_class c WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND c.oid::regclass = "Owner" );
DELETE FROM "_AttributeMetadata" WHERE NOT EXISTS ( SELECT 1 FROM pg_class c WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND c.oid::regclass = "Owner" );

SELECT _cm3_class_triggers_enable('"_AttributeMetadata"');
SELECT _cm3_class_triggers_enable('"_ClassMetadata"');
