-- add config field for geoattributes

SELECT _cm3_attribute_create('OWNER: _GisAttribute|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');

DO $$ DECLARE
    _record record;
BEGIN    
    FOR _record IN SELECT * FROM "_GisAttribute" WHERE "Status" = 'A' LOOP
        UPDATE "_GisAttribute" SET "Config" = '{"infoWindowEnabled": false,"infoWindowContent": null,"infoWindowImage": null}' WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;