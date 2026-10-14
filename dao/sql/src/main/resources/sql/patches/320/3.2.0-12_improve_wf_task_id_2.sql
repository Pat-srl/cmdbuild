--- improve wf task id format 2

DO $$ DECLARE
    _class regclass;
    _record record;
BEGIN
    FOR _class IN SELECT x FROM _cm3_process_list() x LOOP
        IF EXISTS (SELECT * FROM "Activity" WHERE "IdClass" = _class AND "UniqueProcessDefinition" LIKE 'river#%') THEN --TODO improve this
            PERFORM _cm3_class_triggers_disable(_class);
            EXECUTE format('UPDATE %s SET "ActivityDefinitionId" = ( SELECT array_agg(regexp_replace(a,''_activityset'','''',''g'')) FROM ( SELECT unnest("ActivityDefinitionId") a ) x )', _class);
            PERFORM _cm3_class_triggers_enable(_class);        
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

