-- upgrade srid from 900913 to 3857
-- PARAMS: FORCE_IF_NOT_EXISTS=true

DO $$ DECLARE
    _table varchar;
BEGIN
    FOR _table IN SELECT table_name FROM information_schema.tables WHERE table_schema LIKE 'gis' AND table_name LIKE 'Gis_%' LOOP
        RAISE NOTICE 'table %', _table;
        PERFORM UpdateGeometrySRID('gis', _table, 'Geometry', 3857);
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION public._cm3_gis_table_create(
    _master regclass,
    _attr character varying,
    _type character varying)
    RETURNS regclass
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
    _class regclass;
BEGIN
    SELECT _cm3_class_create(format('gis.Gis_%s_%s', _cm3_utils_regclass_to_name(_master), _attr), NULL, 'MODE: reserved|TYPE: simpleclass') INTO _class;
    PERFORM _cm3_attribute_create(_class, 'Master', 'bigint', format('NOTNULL: true|UNIQUE: true|FKTARGETCLASS: %s|CASCADE: delete', _cm3_utils_regclass_to_name(_master)));
    EXECUTE format('ALTER TABLE %s ADD COLUMN "Geometry" geometry(%s,3857) NOT NULL', _class, _type);
    RETURN _class;
END
$BODY$;

CREATE OR REPLACE FUNCTION public._cm3_gis_find_values(
    _gis_attributes bigint[],
    _area character varying)
    RETURNS TABLE(ownerclass regclass, attrname character varying, ownercard bigint, geometry text)
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
    ROWS 1000
AS $BODY$
DECLARE
    _ownerclass regclass;
    _attrname varchar;
BEGIN
    FOR _ownerclass, _attrname IN SELECT "Owner", "Code" FROM "_GisAttribute" WHERE "Id" = ANY (_gis_attributes) LOOP
        RETURN QUERY EXECUTE format('SELECT %L::regclass ownerclass,%L::varchar attrname, "Master" ownercard, st_astext("Geometry") geometry FROM gis."Gis_%s_%s" AS g WHERE g."Geometry" && st_makeenvelope(%s,3857)',
                _ownerclass::regclass, _attrname, _cm3_utils_regclass_to_name(_ownerclass), _attrname, _area);
    END LOOP;
END
$BODY$;

CREATE OR REPLACE FUNCTION public._cm3_gis_value_set(
    _class regclass,
    _attr character varying,
    _card bigint,
    _value_astext character varying)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE PARALLEL UNSAFE
AS $BODY$
DECLARE
    _gis_table regclass = format('gis."Gis_%s_%s"', _cm3_utils_regclass_to_name(_class), _attr)::regclass;
BEGIN
    EXECUTE format('INSERT INTO %s ("Master", "Geometry") VALUES (%L, st_geomfromtext(%L, 3857)) ON CONFLICT ("Master") DO UPDATE SET "Geometry" = EXCLUDED."Geometry"', _gis_table, _card, _value_astext);
END
$BODY$;