-- fix reference constraint for gis attr, email


DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_GisAttribute" WHERE "Status" = 'A' LOOP
        PERFORM _cm3_attribute_features_set(format('gis."Gis_%s_%s"', _cm3_utils_regclass_to_name(_record."Owner"), _record."Code")::regclass, 'Master', 'CASCADE', 'delete');
    END LOOP;    
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_attribute_features_set('"Email"'::regclass, 'Card', 'CASCADE', 'setnull');
