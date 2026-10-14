-- Adds "EnableRecursion" column for "_DomainTreeNavigation" table.

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	PERFORM cm_create_class_attribute('_DomainTreeNavigation', 'EnableRecursion', 'boolean', NULL, FALSE, FALSE, 'MODE: write|STATUS: active');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();