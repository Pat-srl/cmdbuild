-- fix view format in menu


CREATE OR REPLACE FUNCTION _patch_fix_menu_element(_menu jsonb) RETURNS jsonb AS $$ DECLARE
	_target varchar;
BEGIN
    RAISE DEBUG 'processing menu node = %', _menu->>'code';
	IF _menu->>'type' = 'view' THEN
		SELECT INTO _target "Name" FROM "_View" WHERE "Id" = (_menu->>'target')::bigint;
		IF _target IS NULL THEN
			RAISE WARNING 'invalid menu element = % with orphan view id = %, set target to null', _menu->>'code', _menu->'target';
		END IF;
		_menu = _menu || jsonb_build_object('target', _target);
	END IF;	
	_menu = _menu || jsonb_build_object('children', (SELECT coalesce(jsonb_agg(fixed_node),'[]'::jsonb) FROM ( SELECT _patch_fix_menu_element(x) fixed_node FROM jsonb_array_elements(_menu->'children') x WHERE jsonb_typeof(x) <> 'null') fixed_nodes));
	RETURN _menu;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_disable('"_Menu"');

DO $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_Menu" LOOP
        RAISE NOTICE 'processing menu record = %', _record."Id";
        UPDATE "_Menu" SET "Data" = _patch_fix_menu_element("Data") WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_Menu"');	

DROP FUNCTION _patch_fix_menu_element(_menu jsonb);

