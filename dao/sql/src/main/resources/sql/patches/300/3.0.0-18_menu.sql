-- menu table refactoring

CREATE TABLE _patch_aux_menu (group_name varchar,menu_data jsonb);

CREATE FUNCTION _patch_aux_build_json_menu_childs(_group_name varchar,_parent_id bigint) RETURNS jsonb AS $$ DECLARE
	_node_id bigint;
	_childs jsonb;
BEGIN
	_childs = '[]'::jsonb;
	FOR _node_id IN SELECT "Id" FROM "Menu" WHERE "GroupName" = _group_name AND "IdParent" = _parent_id AND "Status" = 'A' LOOP
		_childs = _childs || _patch_aux_build_json_menu_node(_group_name,_node_id);
	END LOOP;
	RETURN _childs;
END $$ LANGUAGE PLPGSQL;

CREATE FUNCTION _patch_aux_build_json_menu_node(_group_name varchar,_node_id bigint) RETURNS jsonb AS $$ DECLARE
	_record record;
	_childs jsonb;
BEGIN

	_childs = _patch_aux_build_json_menu_childs(_group_name,_node_id);

	SELECT * FROM "Menu" WHERE "GroupName" = _group_name AND "Id" = _node_id AND "Status" = 'A' INTO _record;

	RETURN jsonb_build_object(
		'type',_record."Type",
		'index',_record."Number",
		'objectType',CASE WHEN _record."IdElementClass" IS NULL THEN NULL ELSE _cm3_utils_regclass_to_name(_record."IdElementClass") END,
		'objectId',_record."IdElementObj",
		'description',_record."Description",
		'code',_record."Code",
		'children',_childs);

END $$ LANGUAGE PLPGSQL;

CREATE FUNCTION _patch_aux_build_json_menu(_group_name varchar) RETURNS jsonb AS $$ BEGIN
	RETURN jsonb_build_object('version',1,'children',_patch_aux_build_json_menu_childs(_group_name,0));
END $$ LANGUAGE PLPGSQL;

DO $$  DECLARE
	_group_name varchar;
BEGIN

	FOR _group_name IN SELECT DISTINCT "GroupName" FROM "Menu" WHERE "Status" = 'A' LOOP
		INSERT INTO _patch_aux_menu VALUES (_group_name,_patch_aux_build_json_menu(_group_name));
	END LOOP;

END $$ LANGUAGE PLPGSQL;

DROP FUNCTION _patch_aux_build_json_menu_childs(varchar,bigint);
DROP FUNCTION _patch_aux_build_json_menu_node(varchar,bigint);
DROP FUNCTION _patch_aux_build_json_menu(varchar);

DROP TABLE "Menu_history";
DROP TABLE "Menu";

SELECT _cm3_class_create('_Menu', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Group Menu|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Menu"', 'GroupName', 'varchar', 'UNIQUE: true|NOTNULL: true|MODE: read|DESCR: Group name');
SELECT _cm3_attribute_create('"_Menu"', 'Data', 'jsonb', 'NOTNULL: true|MODE: read|DESCR: Menu data');

UPDATE _patch_aux_menu SET group_name = '_default' WHERE group_name = '*';

INSERT INTO "_Menu" ("GroupName","Data") SELECT group_name,menu_data FROM _patch_aux_menu;
DROP TABLE _patch_aux_menu;


ALTER TABLE "_Menu" DISABLE TRIGGER USER;

CREATE FUNCTION _patch_aux_fix_menu_item(_menu jsonb) RETURNS jsonb AS $$ BEGIN

	_menu = _menu - 'index';

	IF _menu->>'type' LIKE 'report%' THEN
		_menu = jsonb_set(_menu, ARRAY['target'], to_jsonb((SELECT "Code" FROM "_Report" WHERE "Id" = (_menu->>'objectId')::bigint AND "Status" = 'A')));
	ELSEIF _menu->>'type' IN ('dashboard', 'view') THEN
		_menu = jsonb_set(_menu, ARRAY['target'], _menu->'objectId');
	ELSEIF _menu->>'type' IN ('processclass', 'class', 'custompage') THEN
		_menu = jsonb_set(_menu, ARRAY['target'], _menu->'objectType');
	END IF;

	_menu = _menu - 'objectId' - 'objectType';

	RETURN _patch_aux_fix_menu_children(_menu);
END $$ LANGUAGE PLPGSQL;

CREATE FUNCTION _patch_aux_fix_menu_children(_menu jsonb) RETURNS jsonb AS $$ DECLARE
	_item jsonb;
	_items jsonb[];
BEGIN
	_items = ARRAY[]::jsonb[];
	FOR _item IN SELECT * FROM jsonb_array_elements(_menu->'children') LOOP
		_item = _patch_aux_fix_menu_item(_item);
		_items = array_append(_items, _item);
	END LOOP;
	RETURN jsonb_set(_menu, ARRAY['children'], to_jsonb(_items));
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_record RECORD;
	_menu jsonb;
	_item jsonb;
BEGIN	
	FOR _record IN SELECT * FROM "_Menu" LOOP
		_menu = jsonb_set(_record."Data", ARRAY['version'], to_jsonb(2));
		_menu = _patch_aux_fix_menu_children(_menu);
		EXECUTE format('UPDATE "_Menu" SET "Data" = %L WHERE "Id" = %L', _menu, _record."Id");
	END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP FUNCTION _patch_aux_fix_menu_item(jsonb);
DROP FUNCTION _patch_aux_fix_menu_children(jsonb);

ALTER TABLE "_Menu" ENABLE TRIGGER USER;

