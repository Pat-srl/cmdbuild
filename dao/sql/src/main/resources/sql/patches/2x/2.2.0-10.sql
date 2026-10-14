-- Create LastExecution column in _Task table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	PERFORM cm_create_class_attribute('_Task', 'LastExecution', 'timestamp', null, false, false, 'MODE: write|DESCR: Last Execution|STATUS: active');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();