-- gis
-- REQUIRE PATCH 3.0.0-18


--- GIS FUNCTIONS ---

CREATE OR REPLACE FUNCTION _cm3_gis_class_list() RETURNS SETOF regclass AS $$
	SELECT oid::regclass FROM pg_class WHERE _cm3_class_is_simple_or_standard(oid) AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='gis');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_gis_table_create(_master regclass, _attr varchar, _type varchar) RETURNS regclass AS $$ DECLARE
	_class regclass;
BEGIN
	SELECT _cm3_class_create(format('gis.Gis_%s_%s', _cm3_utils_regclass_to_name(_master), _attr), NULL, 'MODE: reserved|TYPE: simpleclass') INTO _class;
	PERFORM _cm3_attribute_create(_class, 'Master', 'bigint', format('NOTNULL: true|UNIQUE: true|FKTARGETCLASS: %s|CASCADE: delete', _cm3_utils_regclass_to_name(_master)));
    EXECUTE format('ALTER TABLE %s ADD COLUMN "Geometry" geometry(%s,3857) NOT NULL', _class, _type);
	RETURN _class;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_gis_find_values(_gis_attributes bigint[],_area varchar) RETURNS TABLE(ownerclass regclass,attrname varchar,ownercard bigint,geometry text) AS $$ DECLARE
	_ownerclass regclass;
	_attrname varchar;
BEGIN
	FOR _ownerclass, _attrname IN SELECT "Owner", "Code" FROM "_GisAttribute" WHERE "Id" = ANY (_gis_attributes) LOOP
		RETURN QUERY EXECUTE format('SELECT %L::regclass ownerclass,%L::varchar attrname, "Master" ownercard, st_astext("Geometry") geometry FROM gis."Gis_%s_%s" AS g WHERE g."Geometry" && st_makeenvelope(%s,3857)',
			_ownerclass::regclass, _attrname, _cm3_utils_regclass_to_name(_ownerclass), _attrname, _area);
	END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_gis_value_set(_class regclass, _attr varchar, _card bigint, _value_astext varchar) RETURNS VOID AS $$ DECLARE
    _gis_table regclass = format('gis."Gis_%s_%s"', _cm3_utils_regclass_to_name(_class), _attr)::regclass;
BEGIN
    EXECUTE format('INSERT INTO %s ("Master", "Geometry") VALUES (%L, st_geomfromtext(%L, 3857)) ON CONFLICT ("Master") DO UPDATE SET "Geometry" = EXCLUDED."Geometry"', _gis_table, _card, _value_astext);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_gis_value_delete(_class regclass, _attr varchar, _card bigint) RETURNS VOID AS $$ DECLARE
    _gis_table regclass = format('gis."Gis_%s_%s"', _cm3_utils_regclass_to_name(_class), _attr)::regclass;
BEGIN
    EXECUTE format('DELETE FROM %s WHERE "Master" = %s', _gis_table, _card);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_gis_value_get(_class regclass, _attr varchar, _card bigint) RETURNS varchar AS $$ DECLARE
    _gis_table regclass = format('gis."Gis_%s_%s"', _cm3_utils_regclass_to_name(_class), _attr)::regclass;
    _value varchar;
BEGIN
    EXECUTE format('SELECT st_astext("Geometry") FROM %s WHERE "Master" = %s', _gis_table, _card) INTO _value;
    RETURN _value;
END $$ LANGUAGE PLPGSQL;

