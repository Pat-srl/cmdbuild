-- converting etl gate templates to coma separated varchar list

DO $$ DECLARE
    _gate record;
    _item jsonb;
    _items jsonb;
BEGIN
    FOR _gate IN SELECT * FROM "_EtlGate" WHERE "Status"='A' LOOP
        FOR _item IN SELECT jsonb_array_elements(_gate."Items") LOOP
            IF _item->>'type' = 'database' AND jsonb_typeof(_item->'templates') ='array' THEN
                _item = _item || jsonb_build_object('templates', (SELECT string_agg(x, ',') FROM jsonb_array_elements_text(_item->'templates') x));
                _items = COALESCE(_items, '[]'::jsonb) || _item;
            END IF;
        END LOOP;
        IF _items IS NOT NULL THEN
            UPDATE "_EtlGate" SET "Items" = _items WHERE "Status"='A' AND "Id"=_gate."Id";
            _items = null;
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;