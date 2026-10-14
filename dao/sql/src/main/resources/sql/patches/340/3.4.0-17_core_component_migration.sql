-- core component migrate to wy

DO $$ DECLARE
    _record record;
    _data jsonb;
BEGIN
    FOR _record IN SELECT * FROM "_CoreComponent" WHERE "Status" = 'A' LOOP
        _data = jsonb_build_object('script', _record."Code", 'data', _record."Data", 'description', _record."Description");
        INSERT INTO "_EtlConfig" ("Code","Description","Enabled","Config","Data","Version") VALUES (_record."Code",_record."Description",_record."Active", _data, jsonb_pretty(_data), 1);
    END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP TABLE "_CoreComponent" CASCADE;
