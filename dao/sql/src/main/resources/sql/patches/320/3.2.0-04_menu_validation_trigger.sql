-- menu validation trigger


CREATE TRIGGER _cm3_trigger_menu_validation BEFORE INSERT ON "_Menu" FOR EACH ROW WHEN ( NEW."Status" = 'A' ) EXECUTE PROCEDURE _cm3_trigger_menu_validation();

CREATE TABLE _patch_aux_nodes(menuid bigint not null, parentid bigint, nodeid bigint primary key, node jsonb not null);

CREATE FUNCTION _patch_aux_load_node(_menuid bigint, _parentid bigint, _node jsonb) RETURNS VOID AS $$ DECLARE
    _nodeid bigint;
    _child jsonb;
BEGIN
    SELECT COALESCE(MAX(nodeid)+1,0) FROM _patch_aux_nodes INTO _nodeid;
    INSERT INTO _patch_aux_nodes (menuid, parentid, nodeid, node) VALUES (_menuid, _parentid, _nodeid, _node - 'children');
    FOR _child IN SELECT * FROM jsonb_array_elements(_node->'children') LOOP
        PERFORM _patch_aux_load_node(_menuid, _nodeid, _child);
    END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE FUNCTION _patch_aux_build_menu_tree(_nodeid bigint) RETURNS jsonb AS $$ DECLARE
    _tree jsonb;
BEGIN
    SELECT node INTO _tree FROM _patch_aux_nodes WHERE nodeid = _nodeid;
    RETURN _tree || jsonb_build_object('children', (SELECT to_jsonb(coalesce(array_agg(_patch_aux_build_menu_tree(nodeid) ORDER BY nodeid), ARRAY[]::jsonb[])) FROM _patch_aux_nodes WHERE parentid = _nodeid));
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
    _record record;
    _child jsonb;
    _duplicate varchar;
    _newcode varchar;
    _to_update bigint[] = ARRAY[]::bigint[];
    _menuid bigint;
BEGIN

    FOR _record IN SELECT * FROM "_Menu" WHERE "Status" = 'A' LOOP
        RAISE DEBUG 'load menu record = %', _record."Id";
        FOR _child IN SELECT * FROM jsonb_array_elements(_record."Data"->'children') LOOP
            PERFORM _patch_aux_load_node(_record."Id", NULL, _child);
        END LOOP;
    END LOOP;    

    FOR _duplicate IN (WITH q AS (SELECT (node->>'code')::varchar code, nodeid FROM _patch_aux_nodes) SELECT code FROM (SELECT code, count(*) _count FROM q GROUP BY code) x WHERE _count > 1) LOOP
        RAISE WARNING 'CM: invalid menu data, found duplicate node id = % (will auto fix)', _duplicate;
        FOR _record IN SELECT * FROM _patch_aux_nodes WHERE node->>'code' = _duplicate ORDER BY nodeid LOOP
            _newcode = _cm3_utils_random_id();
            RAISE WARNING 'CM: rename node wich code =< % > within menu = % to new code =< % >', _duplicate, _record.menuid, _newcode;
            UPDATE _patch_aux_nodes SET node = node || jsonb_build_object('code', _newcode) WHERE nodeid = _record.nodeid;
            _to_update = array_append(_to_update, _record.menuid);
        END LOOP;
    END LOOP;

    FOR _menuid IN SELECT DISTINCT x FROM unnest(_to_update) x LOOP
        RAISE WARNING 'CM: fix menu id = %', _menuid;
        UPDATE "_Menu" SET "Data" = "Data" || jsonb_build_object('children', (SELECT to_jsonb(coalesce(array_agg(_patch_aux_build_menu_tree(nodeid) ORDER BY nodeid), ARRAY[]::jsonb[])) FROM _patch_aux_nodes WHERE menuid = _menuid AND parentid IS NULL)) WHERE "Id" = _menuid;
    END LOOP;

END $$ LANGUAGE PLPGSQL;


DROP TABLE _patch_aux_nodes;
DROP FUNCTION _patch_aux_load_node(_menuid bigint, _parentid bigint, _node jsonb);
DROP FUNCTION _patch_aux_build_menu_tree(_nodeid bigint);

