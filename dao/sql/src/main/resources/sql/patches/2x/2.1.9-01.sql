-- Update Grant table to define UI card edit mode privileges

CREATE OR REPLACE FUNCTION apply_patch() RETURNS VOID AS $$

BEGIN
	PERFORM cm_create_class_attribute('Grant', 'UI_EnabledCardEditMode', 'text', null, false, false, 'MODE: write|DESCR: UI_EnabledCardEditMode|INDEX: 12|BASEDSP: false|STATUS: active');
END

$$ LANGUAGE PLPGSQL;

SELECT apply_patch();

DROP FUNCTION apply_patch();