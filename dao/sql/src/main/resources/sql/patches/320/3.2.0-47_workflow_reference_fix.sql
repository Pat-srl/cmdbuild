-- upgrade workflow references
  

CREATE OR REPLACE FUNCTION _patch_aux_find_card(_id bigint, _descr varchar) RETURNS record AS $$ DECLARE
    _count int;
    _card record;
BEGIN
    RAISE DEBUG 'card lookup for id = % descr =< % >', _id, _descr;
    SELECT COUNT(*) FROM "Class" WHERE "Id" = _id INTO _count;
    IF _count = 0 THEN
        RAISE NOTICE 'card not found for id = %', _id;
        RETURN NULL;
    ELSEIF _count = 1 THEN
        SELECT * FROM "Class" c WHERE "Id" = _id INTO _card;
        RAISE DEBUG 'found card = %', _card;
        RETURN _card;
    ELSE 
        SELECT COUNT(*) FROM "Class" WHERE "Id" = _id AND "Description" = _descr INTO _count;
        IF _count = 1 THEN
            SELECT * FROM "Class" c WHERE "Id" = _id AND "Description" = _descr INTO _card;
            RAISE DEBUG 'found card = %', _card;
            RETURN _card;
        ELSE
            RAISE WARNING 'multiple card results for id = %, unable to select single card', _id;
            RETURN NULL;
        END IF;
    END IF;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_disable('"Activity"');

DO $$ DECLARE
    _record record;
    _key varchar;
    _data jsonb;
    _value jsonb;
    _card record;
BEGIN
    FOR _record IN SELECT * FROM "Activity" WHERE "FlowData" IS NOT NULL LOOP
        _data = _record."FlowData";
        FOR _key, _value IN SELECT key, value FROM jsonb_each(_data) WHERE jsonb_typeof(value) = 'object' AND jsonb_typeof(value->'t') = 'number' AND (value->>'i')::bigint > 0 LOOP
            RAISE NOTICE 'processing reference type %[%].% with value = %', _cm3_utils_regclass_to_name(_record."IdClass"), _record."Id", _key, _value;
            _card = _patch_aux_find_card((_value->>'i')::bigint, _value->>'d'); 
            IF _card IS NULL THEN
                RAISE WARNING 'found orphan/invalid reference value %[%].% with value = %', _cm3_utils_regclass_to_name(_record."IdClass"), _record."Id", _key, _value;
            ELSE  
                _value = jsonb_build_object(
                        'i', _card."Id",
                        'T', _cm3_utils_regclass_to_name(_card."IdClass"),
                        'd', _card."Description",
                        'c', _card."Code" );
                _data = _data || jsonb_build_object(_key, _value);
                RAISE NOTICE 'upgrade reference type %[%].% set value = %', _cm3_utils_regclass_to_name(_record."IdClass"), _record."Id", _key, _value;
                UPDATE "Activity" SET "FlowData" = _data WHERE "Id" = _record."Id";
            END IF;
        END LOOP;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"Activity"');

DROP FUNCTION _patch_aux_find_card(_id bigint, _descr varchar);


-- DO $$ DECLARE
--     _record record;
--     _key varchar;
--     _data jsonb;
--     _value jsonb;
--     _card record;
-- BEGIN
--     FOR _record IN SELECT * FROM "Activity" WHERE "FlowData" IS NOT NULL LOOP
--         _data = _record."FlowData";
--         FOR _key, _value IN SELECT key, value FROM jsonb_each(_data) WHERE jsonb_typeof(value) = 'object' AND  value->'T' IS NOT NULL LOOP
--             RAISE NOTICE 'found valid reference type %[%].% with value = %', _cm3_utils_regclass_to_name(_record."IdClass"), _record."Id", _key, _value;
--         END LOOP;
--     END LOOP;
-- END $$ LANGUAGE PLPGSQL;

-- DO $$ DECLARE
--     _record record;
--     _key varchar;
--     _data jsonb;
--     _value jsonb;
--     _card record;
-- BEGIN
--     FOR _record IN SELECT * FROM "Activity" WHERE "FlowData" IS NOT NULL LOOP
--         _data = _record."FlowData";
--         FOR _key, _value IN SELECT key, value FROM jsonb_each(_data) WHERE jsonb_typeof(value) = 'object' AND  ( value->'T' IS NOT NULL OR jsonb_typeof(value->'t') = 'number' ) AND (value->>'i')::bigint > 0 IS NOT NULL LOOP
--             RAISE NOTICE 'found reference type %[%].% with value = %', _cm3_utils_regclass_to_name(_record."IdClass"), _record."Id", _key, _value;
--         END LOOP;
--     END LOOP;
-- END $$ LANGUAGE PLPGSQL;

