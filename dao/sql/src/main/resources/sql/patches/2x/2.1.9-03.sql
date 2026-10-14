-- Create column NoSubjectPrefix to Email table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE INFO 'creating attribute "%" to class "%"', 'NoSubjectPrefix', 'Email';
	PERFORM cm_create_class_attribute('Email', 'NoSubjectPrefix', 'boolean', 'false', false, false, 'MODE: write|DESCR: No subject prefix|INDEX: 12|BASEDSP: false|STATUS: active');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
