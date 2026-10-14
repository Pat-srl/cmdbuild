-- add fault tolerant errors on etl message table

SELECT _cm3_class_triggers_disable('"_EtlMessage"'::regclass);

UPDATE "_EtlMessage" SET "Errors" = ('{"errors":' || "Errors"::text || ',"faultTolerantErrors":[]}')::jsonb WHERE jsonb_typeof("Errors") = 'array';

SELECT _cm3_class_triggers_enable('"_EtlMessage"'::regclass);