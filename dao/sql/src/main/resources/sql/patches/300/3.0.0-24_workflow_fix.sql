-- fix invalid workflow data, add check

DO $$ DECLARE
	_process regclass;
BEGIN
	FOR _process IN SELECT x FROM _cm3_process_list() x LOOP
		PERFORM _cm3_class_triggers_disable(_process);
		EXECUTE format('UPDATE %s SET "NextExecutor" = ''{}'' WHERE cardinality("NextExecutor") > 0 AND cardinality("ActivityInstanceId") = 0', _process);
		PERFORM _cm3_class_triggers_enable(_process);
	END LOOP;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "Activity" ADD CONSTRAINT "_cm3_activity_count_check" CHECK ( cardinality("ActivityDefinitionId") = cardinality("ActivityInstanceId") AND cardinality("ActivityInstanceId") = cardinality("NextExecutor") );

