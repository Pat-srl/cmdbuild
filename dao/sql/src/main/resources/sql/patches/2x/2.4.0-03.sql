-- Creates "_Icon" table.

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	PERFORM cm_create_class('_Icon', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: _Icon|SUPERCLASS: false|STATUS: active');
	PERFORM cm_create_class_attribute('_Icon', 'Element', 'text', null, true, true, 'MODE: write|DESCR: Element|STATUS: active');
	PERFORM cm_create_class_attribute('_Icon', 'Path', 'text', null, true, false, 'MODE: write|DESCR: Path|STATUS: active');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();