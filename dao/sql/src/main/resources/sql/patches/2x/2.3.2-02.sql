-- Moves "LastExecution" from "_Task" table to the new "_TaskRuntime" table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE NOTICE 'creating new table';
	PERFORM cm_create_class('_TaskRuntime', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: _TaskRuntime|SUPERCLASS: false|STATUS: active');

	RAISE NOTICE 'creating new attributes';
	PERFORM cm_create_class_attribute('_TaskRuntime', 'Owner', 'integer', null, false, false, 'MODE: write|DESCR: Owner|STATUS: active|FKTARGETCLASS: _Task');
	PERFORM cm_create_class_attribute('_TaskRuntime', 'LastExecution', 'timestamp', null, false, false, 'MODE: write|DESCR: Last Execution|STATUS: active');

	RAISE NOTICE 'moving values';
	INSERT INTO "_TaskRuntime"
		("Owner", "LastExecution")
		SELECT "Id", "LastExecution"
			FROM "_Task"
			WHERE "Status" = 'A';

	RAISE NOTICE 'deleting column';
	ALTER TABLE "_Task" DISABLE TRIGGER USER;
	UPDATE "_Task" SET "LastExecution" = NULL;
	ALTER TABLE "_Task" ENABLE TRIGGER USER;
	PERFORM cm_delete_class_attribute('_Task', 'LastExecution');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();