-- Reversing navtree item directions

CREATE OR REPLACE FUNCTION _patch_fix_navtree_element(_navtree jsonb) RETURNS jsonb AS $$ DECLARE
_target varchar;
BEGIN
    RAISE DEBUG 'processing navtree node = %', _navtree->>'code';
IF _navtree->>'direction' = 'direct' THEN
            RAISE NOTICE 'is direct';
            _navtree = _navtree || jsonb_build_object('direction', 'inverse');
ELSE
            IF _navtree->>'direction' = 'inverse' THEN
                RAISE NOTICE 'is inverse';
                _navtree = _navtree || jsonb_build_object('direction', 'direct');
            END IF;
        END IF;
_navtree = _navtree || jsonb_build_object('nodes', (SELECT coalesce(jsonb_agg(fixed_node),'[]'::jsonb) FROM ( SELECT _patch_fix_navtree_element(x) fixed_node FROM jsonb_array_elements(_navtree->'nodes') x WHERE jsonb_typeof(x) <> 'null') fixed_nodes));
RETURN _navtree;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_disable('"_NavTree"');

DO $$ DECLARE
    _record RECORD;
BEGIN
    FOR _record IN SELECT * FROM "_NavTree" WHERE "Status"='A' AND ("Code"='gisnavigation' OR "Type"='menu') LOOP
        RAISE NOTICE 'processing navtree record = %', _record."Id";
        UPDATE "_NavTree" SET "Data" = _patch_fix_navtree_element("Data") WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_NavTree"');

DROP FUNCTION _patch_fix_navtree_element(_navtree jsonb);