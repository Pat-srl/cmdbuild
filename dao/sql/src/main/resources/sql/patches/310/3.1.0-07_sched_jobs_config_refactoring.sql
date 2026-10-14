--  sched job config upgrade

SELECT _cm3_class_triggers_disable('"_Job"');

CREATE OR REPLACE FUNCTION pg_temp._cm3_patch_aux_translate_key(_key varchar) RETURNS varchar AS $$ BEGIN
    RETURN CASE _key 
        WHEN 'action.cronExpression' THEN 'cronExpression'
        ELSE regexp_replace(_key, '[.]', '_', 'g') END;        
END $$ LANGUAGE PLPGSQL;

UPDATE "_Job" SET "Config" = (WITH q AS (SELECT pg_temp._cm3_patch_aux_translate_key(key) _key, value _value FROM  jsonb_each(COALESCE("Config",'{}'::jsonb))) SELECT COALESCE(jsonb_object_agg(_key, _value), '{}'::jsonb) FROM q);

DROP FUNCTION pg_temp._cm3_patch_aux_translate_key(_key varchar);

SELECT _cm3_class_triggers_enable('"_Job"');
