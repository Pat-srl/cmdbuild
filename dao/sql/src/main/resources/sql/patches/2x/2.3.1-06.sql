-- Creates "Delay" attribute for "_EmailTemplate" table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE INFO 'creating attribute Delay for class _EmailTemplate';
	PERFORM cm_create_class_attribute('_EmailTemplate', 'Delay', 'int4', null, false, false, 'MODE: user|FIELDMODE: write|DESCR: Delay|INDEX: 11|STATUS: active');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
