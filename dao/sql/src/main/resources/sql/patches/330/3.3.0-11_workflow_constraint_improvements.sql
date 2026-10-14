-- improve wf check


SELECT _cm3_class_triggers_disable('"Activity"');
UPDATE "Activity" SET "FlowData" = "FlowData" || jsonb_build_object('RiverFlowStatus', 'COMPLETE'), "FlowStatus" = _cm3_lookup('FlowStatus','closed.completed') WHERE _cm3_lookup_code("FlowStatus") IN ('open.running', 'open.not_running.suspended') AND "UniqueProcessDefinition" LIKE 'river%' AND cardinality("ActivityInstanceId") = 0;
UPDATE "Activity" SET "ActivityInstanceId" = ARRAY[]::varchar[], "ActivityDefinitionId" = ARRAY[]::varchar[], "NextExecutor" = ARRAY[]::varchar[] WHERE 
    _cm3_lookup_code("FlowStatus") NOT IN ('open.running', 'open.not_running.suspended')--TODO check this
    AND ( cardinality("ActivityInstanceId") <> 0 OR cardinality("ActivityDefinitionId") <> 0 OR cardinality("NextExecutor") <> 0 );
UPDATE "Activity" SET "FlowData" = "FlowData" || jsonb_build_object('RiverFlowStatus', 'ABORTED') WHERE _cm3_lookup_code("FlowStatus") = 'closed.aborted' AND "UniqueProcessDefinition" LIKE 'river%' AND "FlowData"->>'RiverFlowStatus' <> 'ABORTED';
UPDATE "Activity" SET "FlowData" = "FlowData" || jsonb_build_object('RiverFlowStatus', 'SUSPENDED') WHERE _cm3_lookup_code("FlowStatus") = 'open.not_running.suspended' AND "UniqueProcessDefinition" LIKE 'river%' AND "FlowData"->>'RiverFlowStatus' <> 'SUSPENDED';
SELECT _cm3_class_triggers_enable('"Activity"');

DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "Activity" WHERE NOT ( "UniqueProcessDefinition" NOT LIKE 'river%' OR (
                (
                     ( "FlowData"->>'RiverFlowStatus' IN ('RUNNING', 'SUSPENDED') AND cardinality("ActivityInstanceId") > 0 )
                        OR ( "FlowData"->>'RiverFlowStatus' NOT IN ('RUNNING', 'SUSPENDED') AND cardinality("ActivityInstanceId") = 0 )
                 ) AND CASE _cm3_lookup_code("FlowStatus") 
                         WHEN 'open.running' THEN "FlowData"->>'RiverFlowStatus' IN ('READY', 'RUNNING')
                         WHEN 'open.not_running.suspended' THEN "FlowData"->>'RiverFlowStatus' = 'SUSPENDED'
                         WHEN 'closed.aborted' THEN "FlowData"->>'RiverFlowStatus' = 'ABORTED'
                         WHEN 'closed.completed' THEN "FlowData"->>'RiverFlowStatus' = 'COMPLETE'
                         -- `closed.terminated` not supported here; TODO: check
                         ELSE false 
                     END
             ) ) LOOP
        RAISE WARNING 'invalid flow status for record =< % >', _record; 
    END LOOP;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "Activity" ADD CONSTRAINT "_cm3_activity_status_check" CHECK ( "UniqueProcessDefinition" NOT LIKE 'river%' OR (
   (
        ( "FlowData"->>'RiverFlowStatus' IN ('RUNNING', 'SUSPENDED') AND cardinality("ActivityInstanceId") > 0 )
           OR ( "FlowData"->>'RiverFlowStatus' NOT IN ('RUNNING', 'SUSPENDED') AND cardinality("ActivityInstanceId") = 0 )
    ) AND CASE _cm3_lookup_code("FlowStatus") 
            WHEN 'open.running' THEN "FlowData"->>'RiverFlowStatus' IN ('READY', 'RUNNING')
            WHEN 'open.not_running.suspended' THEN "FlowData"->>'RiverFlowStatus' = 'SUSPENDED'
            WHEN 'closed.aborted' THEN "FlowData"->>'RiverFlowStatus' = 'ABORTED'
            WHEN 'closed.completed' THEN "FlowData"->>'RiverFlowStatus' = 'COMPLETE'
            -- `closed.terminated` not supported here; TODO: check
            ELSE false 
        END
) );

ALTER TABLE "Activity" DROP CONSTRAINT "_cm3_activity_status_check"; --TODO improve this
-- ALTER TABLE "Activity" ALTER CONSTRAINT "_cm3_activity_status_check" NOT VALID; --TODO improve this

ALTER TABLE "Activity" ADD CONSTRAINT "_cm3_activity_status_check" CHECK ( "UniqueProcessDefinition" NOT LIKE 'river%' OR (
   (
        ( "FlowData"->>'RiverFlowStatus' IN ('RUNNING', 'SUSPENDED') AND cardinality("ActivityInstanceId") > 0 )
           OR ( "FlowData"->>'RiverFlowStatus' NOT IN ('RUNNING', 'SUSPENDED') AND cardinality("ActivityInstanceId") = 0 )
    ) AND CASE _cm3_lookup_code("FlowStatus") 
            WHEN 'open.running' THEN "FlowData"->>'RiverFlowStatus' IN ('READY', 'RUNNING')
            WHEN 'open.not_running.suspended' THEN "FlowData"->>'RiverFlowStatus' = 'SUSPENDED'
            WHEN 'closed.aborted' THEN "FlowData"->>'RiverFlowStatus' = 'ABORTED'
            WHEN 'closed.completed' THEN "FlowData"->>'RiverFlowStatus' = 'COMPLETE'
            -- `closed.terminated` not supported here; TODO: check
            ELSE false 
        END
) ) NOT VALID; --TODO improve this; this fixes an issue impacting db dump/restore with check constraints using functions
-- NOT VALID
