--- fix widget content

DO $$ DECLARE
    _record record;
    _data jsonb;
BEGIN
    FOR _record IN SELECT * FROM "_Widget" WHERE "Status" = 'A' LOOP
        _data = (SELECT jsonb_object_agg(key, val) FROM  (SELECT key, (CASE 
                WHEN value IS NULL OR value ~* '^(".*"|[{][a-z]+:.*[}]|[0-9]+|true|false)$' THEN value
                ELSE format('"%s"', value) END ) val 
            FROM jsonb_each_text(_record."Data") entry) x);
        IF ( _data::varchar ) <> ( _record."Data"::varchar ) THEN
            RAISE NOTICE 'update widget data for record = %', _record."Id";
            UPDATE "_Widget" SET "Data" = _data WHERE "Id" = _record."Id";
        END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

