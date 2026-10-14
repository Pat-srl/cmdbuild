-- gis fix

DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_GisAttribute" WHERE "Status" = 'A' LOOP
        IF EXISTS (SELECT * FROM information_schema.columns 
                WHERE table_name = format('Gis_%s_%s', _cm3_utils_regclass_to_name(_record."Owner"), _record."Code") 
                AND udt_name IS DISTINCT FROM 'geometry' AND table_schema = 'gis' AND column_name = 'Geometry') THEN            
            RAISE NOTICE 'fix gis table for layer = %', _record;
            EXECUTE format('ALTER TABLE gis."Gis_%s_%s" ALTER COLUMN "Geometry" TYPE geometry(%s,900913) USING ST_SetSRID("Geometry"::geometry(%s),900913)',
                _cm3_utils_regclass_to_name(_record."Owner"), _record."Code", _record."Type", _record."Type");
        END IF;
        PERFORM _cm3_attribute_unique_set(format('gis."Gis_%s_%s"', _cm3_utils_regclass_to_name(_record."Owner"), _record."Code")::regclass, 'Master', TRUE);
    END LOOP;
END $$ LANGUAGE PLPGSQL;

