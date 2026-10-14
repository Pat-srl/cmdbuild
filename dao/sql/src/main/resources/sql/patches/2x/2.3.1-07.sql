-- Creates "OutputFolder" attribute for "_EmailAccount" table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	RAISE INFO 'creating attribute OutputFolder for class _EmailAccount';
	PERFORM cm_create_class_attribute('_EmailAccount', 'OutputFolder', 'varchar(100)', null, false, false, 'MODE: write|DESCR: Output Folder|INDEX: 15|STATUS: active');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
