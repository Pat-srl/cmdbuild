-- waterway config format improvements

SELECT _cm3_attribute_create('OWNER: _EtlConfig|NAME: Config|TYPE: jsonb|DESCR: Waterway Config data, jsonb format');

SELECT _cm3_class_triggers_disable('"_EtlConfig"'::regclass);

UPDATE "_EtlConfig" SET "Config" = "Data"::jsonb WHERE "Status" = 'A'; --this will fail for pure yaml config, in which case you'll have to manually fix db :/
UPDATE "_EtlConfig" SET "Config" = '{}'::jsonb WHERE "Config" IS NULL;

SELECT _cm3_attribute_notnull_set('"_EtlConfig"', 'Config');

DO $$ DECLARE
    _config jsonb;
    _item jsonb;
    _items jsonb;
    _handler jsonb;
    _handlers jsonb;
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_EtlConfig" WHERE "Status" = 'A' LOOP
        _config = _record."Config";
        IF _config->'items' IS NOT NULL THEN
            IF _config->'code' IS NOT NULL THEN
                _config = _config - 'code' || jsonb_build_object('module', _config->>'code');
            END IF;
            _items = '[]'::jsonb;
            FOR _item IN SELECT * FROM jsonb_array_elements(_config->'items') LOOP 
                IF _item->'handlers' IS NOT NULL THEN
                    _handlers = '[]'::jsonb;
                    FOR _handler IN SELECT * FROM jsonb_array_elements(_item->'handlers') LOOP 
--                         _handler = _handler - 'type' || jsonb_build_object('handler', _handler->>'type');
                        IF _handler->'gate' IS NOT NULL THEN
                            _handler = _handler - 'gate' || jsonb_build_object('target', _handler->>'gate');
                        END IF;
                        _handlers = _handlers || _handler;
                    END LOOP;
                    _item = _item || jsonb_build_object('handlers', _handlers);
                END IF;
                _items = _items || ( _item - 'code' - 'type' || jsonb_build_object(_item->>'type', _item->>'code') );
            END LOOP;
            _config = _config || jsonb_build_object('items', _items);
        ELSE
            _item = _config;
             IF _item->'handlers' IS NOT NULL THEN
                _handlers = '[]'::jsonb;
                FOR _handler IN SELECT * FROM jsonb_array_elements(_item->'handlers') LOOP 
--                         _handler = _handler - 'type' || jsonb_build_object('handler', _handler->>'type');
                    IF _handler->'gate' IS NOT NULL THEN
                        _handler = _handler - 'gate' || jsonb_build_object('target', _handler->>'gate');
                    END IF;
                    _handlers = _handlers || _handler;
                END LOOP;
                _item = _item || jsonb_build_object('handlers', _handlers);
            END IF;
            _config = _item - 'code' - 'type' || jsonb_build_object(_item->>'type', _item->>'code');
        END IF;
        RAISE NOTICE 'new config =< % >', _config;
        UPDATE "_EtlConfig" SET "Config" = _config, "Data" = jsonb_pretty(_config)::varchar WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_EtlConfig"'::regclass);

