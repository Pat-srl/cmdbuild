-- Creates the _CustomPage table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	PERFORM cm_create_class('_CustomPage', 'Class', 'MODE: reserved|TYPE: class|DESCR: CustomPage|SUPERCLASS: false|STATUS: active');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
