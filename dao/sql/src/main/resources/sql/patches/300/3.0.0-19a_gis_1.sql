-- upgrade gis tables 1
-- PARAMS: RELOAD_CONNECTION_AFTER=true


--- GIS CORE TABLES UPGRADE ---

DO $$ DECLARE
	_class regclass;
BEGIN

	FOR _class IN SELECT x FROM _cm3_gis_class_list() x LOOP 
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "Id" TYPE bigint', _class);
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "Master" TYPE bigint', _class);
		EXECUTE format('ALTER TABLE %s ALTER COLUMN "Id" SET DEFAULT _cm3_utils_new_card_id()', _class);
	END LOOP;
	
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_Layer" ALTER COLUMN "MapStyle" TYPE jsonb USING "MapStyle"::jsonb;

ALTER TABLE "_Layer" ALTER COLUMN "Visibility" TYPE varchar[] USING string_to_array("Visibility",',');
ALTER TABLE "_Layer" ALTER COLUMN "CardsBinding" TYPE varchar[] USING string_to_array("CardsBinding",',');

SELECT _cm3_attribute_create('"_Layer"', 'Owner', 'varchar', 'MODE: read');

UPDATE "_Layer" SET "Owner" = regexp_replace("FullName",'gis.Detail_(.+)_.+','\1') WHERE NOT "FullName" ILIKE '%Detail__GeoServer%';  --TODO check this

SELECT _cm3_class_create('_GisAttribute', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Gis Class Attributes|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_GisAttribute"', 'Type', 'varchar', 'NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisAttribute"', 'Owner', 'regclass', 'NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisAttribute"', 'Index', 'integer', 'DEFAULT: 0|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisAttribute"', 'MinimumZoom', 'integer', 'DEFAULT: 1|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisAttribute"', 'DefaultZoom', 'integer', 'DEFAULT: 7|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisAttribute"', 'MaximumZoom', 'integer', 'DEFAULT: 9|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisAttribute"', 'Visibility', 'varchar[]', 'DEFAULT: ''{}''::varchar[]|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisAttribute"', 'MapStyle', 'jsonb', 'DEFAULT: ''{}''::jsonb|NOTNULL: true|MODE: write');

SELECT _cm3_class_create('_GisLayer', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Gis Geoserver Layer|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_GisLayer"', 'Type', 'varchar', 'NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'OwnerClass', 'regclass', 'NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'OwnerCard', 'integer', 'NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'GeoserverName', 'varchar', 'NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'Index', 'integer', 'DEFAULT: 0|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'MinimumZoom', 'integer', 'DEFAULT: 1|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'DefaultZoom', 'integer', 'DEFAULT: 7|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'MaximumZoom', 'integer', 'DEFAULT: 9|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_create('"_GisLayer"', 'Visibility', 'varchar[]', 'DEFAULT: ''{}''::varchar[]|NOTNULL: true|MODE: write');
SELECT _cm3_attribute_index_unique_create('"_GisLayer"', 'Code');

ALTER TABLE "_GisAttribute" DISABLE TRIGGER USER;
ALTER TABLE "_GisLayer" DISABLE TRIGGER USER;

INSERT INTO "_GisAttribute" (
	"Id",
	"CurrentId",
	"IdClass",
	"Code",
	"Description",
	"Type",
	"Owner",
	"Index",
	"MinimumZoom",
	"DefaultZoom",
	"MaximumZoom",
	"MapStyle",
	"Visibility",
	"Notes",
	"Status"
) SELECT
	"Id",
	"Id",
	'"_GisAttribute"'::regclass,
	"Name",
	"Description",
	"Type",
	_cm3_utils_name_to_regclass("Owner"),
	"Index",
	"MinimumZoom",
	"MinimumZoom",
	"MaximumZoom",
	"MapStyle",
	"Visibility",
	format('migrated from table _Layer with patch 30038; old gis table name = ''%s''',"FullName"),
	'A'
FROM 
	"_Layer" 
WHERE 
	_cm3_utils_name_to_regclass("Owner") IS NOT NULL;

INSERT INTO "_GisLayer" (
	"Id",
	"CurrentId",
	"IdClass",
	"Code",
	"Description",
	"Type",
	"Index",
	"MinimumZoom",
	"DefaultZoom",
	"MaximumZoom",
	"GeoserverName",
	"OwnerClass",
	"OwnerCard",
	"Visibility",
	"Notes",
	"Status"
) SELECT
	"Id",
	"Id",
	'"_GisLayer"'::regclass,
	"Name",
	"Description",
	"Type",
	"Index",
	"MinimumZoom",
	"MinimumZoom",
	"MaximumZoom",
	"GeoServerName",
	regexp_replace("CardsBinding"[1],'(.+)_([0-9]+)$','"\1"')::regclass,
	regexp_replace("CardsBinding"[1],'(.+)_([0-9]+)$','\2')::integer,
	"Visibility",
	format('migrated from table _Layer with patch 30038; old gis CardsBinding = ''%s''',"CardsBinding"),
	'A'
FROM 
	"_Layer" 
WHERE 
	"Owner" IS NULL;

ALTER TABLE "_GisAttribute" ENABLE TRIGGER USER;
ALTER TABLE "_GisLayer" ENABLE TRIGGER USER;

DROP TABLE "_Layer";


--- GIS SCHEMA UPGRADE (step 1) ---

DO $$ DECLARE 
	_gis_master regclass;
	_gis_master_attr varchar;
	_gis_attr_type varchar;
	_class regclass;
BEGIN
	IF EXISTS (SELECT * FROM information_schema.schemata WHERE schema_name = 'gis') THEN
		CREATE TABLE _cm3_gis_patch_aux(id bigint, usr varchar, begindate timestamp, masterclass regclass, master bigint, masterattr varchar, geometry bytea);
		CREATE TABLE _cm3_gis_trigger_aux(trigger_query varchar);
        INSERT INTO _cm3_gis_trigger_aux VALUES ('SELECT 1;');
		FOR _gis_master, _gis_master_attr IN SELECT "Owner", "Code" FROM "_GisAttribute" WHERE "Status" = 'A' LOOP 
			EXECUTE format('INSERT INTO _cm3_gis_patch_aux(id,usr,begindate,masterclass,master,masterattr,geometry) SELECT "Id","User","BeginDate",%L,"Master",%L,ST_AsEWKB("Geometry") FROM gis."Detail_%s_%s"',
				_gis_master, _gis_master_attr, _cm3_utils_regclass_to_name(_gis_master), _gis_master_attr);
            UPDATE _cm3_gis_trigger_aux SET trigger_query = trigger_query || (SELECT coalesce(string_agg(replace(pg_get_triggerdef(t.oid),
                format('Detail_%s_%s',_cm3_utils_regclass_to_name(_gis_master), _gis_master_attr),format('Gis_%s_%s',_cm3_utils_regclass_to_name(_gis_master), _gis_master_attr)),';'),'SELECT 1') || ';' 
                    FROM pg_trigger t WHERE t.tgrelid = format('gis."Detail_%s_%s"',_cm3_utils_regclass_to_name(_gis_master), _gis_master_attr)::regclass AND NOT tgisinternal AND tgname !~ '^(Detail_.*_Master_fkey|_SanityCheck)$');
		END LOOP;
		DROP SCHEMA gis CASCADE;
	END IF;
END $$ LANGUAGE PLPGSQL;


