-- Create translations table

DROP FUNCTION IF EXISTS apply_patch();
CREATE OR REPLACE FUNCTION apply_patch() RETURNS void AS $$
BEGIN
	PERFORM cm_create_class('_Translation', NULL, 'MODE: reserved|TYPE: simpleclass|DESCR: Translations|SUPERCLASS: false|STATUS: active');
	PERFORM cm_create_class_attribute('_Translation', 'Element', 'text', null, true, false, 'MODE: write|DESCR: Element|INDEX: 1|STATUS: active');
	PERFORM cm_create_class_attribute('_Translation', 'Lang', 'text', null, true, false, 'MODE: write|DESCR: Lang|INDEX: 2|STATUS: active');
	PERFORM cm_create_class_attribute('_Translation', 'Value', 'text', null, true, false, 'MODE: write|DESCR: Value|INDEX: 3|STATUS: active');
END
$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION IF EXISTS apply_patch();
