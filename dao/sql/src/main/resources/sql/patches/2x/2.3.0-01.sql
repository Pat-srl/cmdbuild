-- Creates Service and Privileged columns for User class

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	PERFORM cm_create_class_attribute('User', 'Service', 'boolean', null, false, false, 'MODE: user|DESCR: Service|INDEX: 9');
	PERFORM cm_create_class_attribute('User', 'Privileged', 'boolean', null, false, false, 'MODE: user|DESCR: Privileged|INDEX: 10');
END;
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();