-- fix issue in legacy filter format

CREATE OR REPLACE FUNCTION _patch_aux_fix_classname(_classname varchar) RETURNS varchar AS $$ BEGIN
    IF EXISTS (SELECT * FROM _cm3_class_list() x WHERE _cm3_utils_regclass_to_name(x) = _classname) THEN
        RETURN _classname;
    ELSE
        RETURN (SELECT table_name FROM _cm3_class_list_detailed() x WHERE features->>'DESCR' = _classname);
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _patch_aux_fix_filter(_filter jsonb, _record_info varchar) RETURNS jsonb AS $$ DECLARE
    _relation_part jsonb;
    _card_part jsonb;
    _cards_new jsonb;
    _relations_new jsonb;
    _classname varchar;
BEGIN
    _relations_new = '[]'::jsonb;
    FOR _relation_part IN SELECT jsonb_array_elements(_filter->'relation') LOOP
        IF _relation_part->>'type' = 'oneof' THEN
            _cards_new = '[]'::jsonb;
            FOR _card_part IN SELECT jsonb_array_elements(_relation_part->'cards') LOOP
                _classname = _patch_aux_fix_classname(_card_part->>'className');
                IF _classname IS NULL THEN
                    SELECT _cm3_utils_regclass_to_name("IdClass") FROM "Class" WHERE "Id" = (_card_part->>'id')::bigint INTO _classname;
                END IF;
                IF _classname IS NULL THEN
                    RAISE 'unable to fix record = %, invalid class name =< % > found in filter =< % >', _record_info, _card_part->>'className', _filter;
                END IF;
                _card_part = _card_part || jsonb_build_object('className', _classname);
                _cards_new = _cards_new || _card_part;
            END LOOP;
            _relation_part = _relation_part || jsonb_build_object('cards', _cards_new);
        END IF;
        _relations_new = _relations_new || _relation_part;
    END LOOP;
    _filter = _filter || jsonb_build_object('relation', _relations_new);
    RETURN _filter;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
    _record record;
    _filter jsonb;
BEGIN
    FOR _record IN SELECT * FROM "_Filter" WHERE "Status" = 'A' AND "Filter"->'relation' IS NOT NULL LOOP
        IF NOT _record."Shared" THEN
            BEGIN
                _filter = _patch_aux_fix_filter(_record."Filter", format('_Filter[%s]', _record."Id"));
            EXCEPTION WHEN others THEN
                _filter = NULL;
                RAISE WARNING 'dropping invalid non-shared filter = %', _record;
                UPDATE "_Filter" SET "Status" = 'N' WHERE "Id" = _record."Id";
            END;
        ELSE
            _filter = _patch_aux_fix_filter(_record."Filter", format('_Filter[%s]', _record."Id"));
        END IF;
        IF _filter IS NOT NULL AND _filter::varchar <> _record."Filter"::varchar THEN
            RAISE NOTICE 'fix filter for record = _Filter[%] %', _record."Id", _record."Filter";
            UPDATE "_Filter" SET "Filter" = _patch_aux_fix_filter("Filter", 'x') WHERE "Id" = _record."Id";
        END IF;
    END LOOP;
    FOR _record IN SELECT * FROM "_View" WHERE "Status" = 'A' AND "Type" = 'filter' AND "Filter"->'relation' IS NOT NULL AND _patch_aux_fix_filter("Filter", format('_View[%s]', "Id"))::varchar <> "Filter"::varchar LOOP
        RAISE NOTICE 'fix filter for record = _View[%] %', _record."Id", _record."Filter";
        UPDATE "_View" SET "Filter" = _patch_aux_fix_filter("Filter", 'x') WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP FUNCTION _patch_aux_fix_classname(_classname varchar);
DROP FUNCTION _patch_aux_fix_filter(_filter jsonb, _record_info varchar);

