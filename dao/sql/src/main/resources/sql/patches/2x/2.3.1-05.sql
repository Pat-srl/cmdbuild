-- Creates "Delay" attribute for "Email" table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE INFO 'creating attribute Delay for class Email';
	PERFORM cm_create_class_attribute('Email', 'Delay', 'int4', null, false, false, 'MODE: user|FIELDMODE: write|DESCR: Delay|INDEX: 18|STATUS: active');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
