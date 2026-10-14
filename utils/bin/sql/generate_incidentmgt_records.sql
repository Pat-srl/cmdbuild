


DO $$ DECLARE
	_id bigint;
BEGIN

	PERFORM _cm3_class_triggers_disable('"IncidentMgt"');
	FOR i IN 1..100000 LOOP
		_id = _cm3_utils_new_card_id();
		INSERT INTO "IncidentMgt" ("Status","BeginDate","IdClass","Id","CurrentId","Code","ActivityDefinitionId","ProcessCode","NextExecutor","ActivityInstanceId","PrevExecutors","UniqueProcessDefinition","FlowData","ProcessStatus","FlowStatus","CurrentRole") 
			VALUES ('A',now(),'"IncidentMgt"',_id,_id,'IM03 - Helpdesk classification',ARRAY['IM03-HDClassification'],_cm3_utils_random_id(),'{HWHelpdesk}',ARRAY[_cm3_utils_random_id()],'{HWHelpdesk}','river#0#xwz7si8cz8yzifqe2ec1fu9d','{"RiverFlowStatus":"RUNNING"}',20036,1,3885);
	END LOOP;
	PERFORM _cm3_class_triggers_enable('"IncidentMgt"');

END $$ LANGUAGE PLPGSQL;

