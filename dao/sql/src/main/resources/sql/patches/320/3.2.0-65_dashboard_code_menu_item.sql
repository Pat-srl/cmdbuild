-- removed whitespaces from dashboard Code and changed dashboard target in menus

DO $$ DECLARE
    _code varchar;
    _id bigint;
BEGIN
    FOR _code, _id IN SELECT "Code", "Id" FROM "_Dashboard" WHERE "Status"='A' AND "Code" ~ '\s+' LOOP
	IF _code ~ '\s+' THEN
		RAISE NOTICE 'Removing whitespaces from Dashboard with Code = %', _code;
		UPDATE "_Dashboard" SET "Code" = REGEXP_REPLACE(_code, '\s+', '', 'g') WHERE "Id"=_id AND "Status"='A';
	END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE FUNCTION _patch_aux_fix_menu_item(_menu jsonb) RETURNS jsonb AS $$ BEGIN
	IF _menu->>'type' LIKE 'dashboard' AND _menu->>'target' ~ '^[0-9]+$' AND _cm3_utils_is_not_blank(_menu->>'target') THEN
		RAISE NOTICE 'A menu item has dashboard id instead of code, updating target field';
		RAISE NOTICE 'Target value: %', _menu->>'target';
                IF EXISTS (SELECT "Code" FROM "_Dashboard" WHERE "Id" = (_menu->>'target')::bigint AND "Status" = 'A') THEN
                    _menu = jsonb_set(_menu, ARRAY['target'], to_jsonb((SELECT "Code" FROM "_Dashboard" WHERE "Id" = (_menu->>'target')::bigint AND "Status" = 'A')));
                    RETURN _menu;
                ELSE
                    RAISE WARNING 'dashboard not found for id = % (delete menu element)', _menu->>'target';
                    RETURN NULL;
                END IF;
	ELSE
            RETURN _patch_aux_fix_menu_children(_menu);
        END IF;
END $$ LANGUAGE PLPGSQL;

CREATE FUNCTION _patch_aux_fix_menu_children(_menu jsonb) RETURNS jsonb AS $$ DECLARE
	_item jsonb;
	_items jsonb[];
BEGIN
	_items = ARRAY[]::jsonb[];
	FOR _item IN SELECT * FROM jsonb_array_elements(_menu->'children') LOOP
		_item = _patch_aux_fix_menu_item(_item);
                IF _item IS NOT NULL THEN
                    _items = array_append(_items, _item);
                END IF;
	END LOOP;
	RETURN jsonb_set(_menu, ARRAY['children'], to_jsonb(_items));
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_record RECORD;
	_menu jsonb;
	_item jsonb;
BEGIN	
	FOR _record IN SELECT * FROM "_Menu" WHERE "Status"='A' LOOP
		_menu = jsonb_set(_record."Data", ARRAY['version'], to_jsonb(3));
		_menu = _patch_aux_fix_menu_children(_menu);
		EXECUTE format('UPDATE "_Menu" SET "Data" = %L WHERE "Id" = %L', _menu, _record."Id");
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP FUNCTION _patch_aux_fix_menu_item(jsonb);
DROP FUNCTION _patch_aux_fix_menu_children(jsonb);