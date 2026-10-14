-- upgrade workflow

DO $$ DECLARE
	_process regclass;
BEGIN
	FOR _process IN SELECT x FROM _cm3_process_list() x LOOP
		IF _cm3_class_metadata_get(_process,'cm_workflow_provider') IS NULL THEN
			PERFORM _cm3_class_metadata_set(_process,'cm_workflow_provider','shark');
		END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

