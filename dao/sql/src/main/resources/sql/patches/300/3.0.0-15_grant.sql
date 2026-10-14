-- refactoring of grant table


SELECT _cm3_class_create('_Grant', '"Class"', 'MODE: reserved|TYPE: class|SUPERCLASS: false');
SELECT _cm3_attribute_create('"_Grant"', 'IdRole', 'integer', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_create('"_Grant"', 'Mode', 'character(1)', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_create('"_Grant"', 'Type', 'varchar', 'NOTNULL: true|MODE: read');
SELECT _cm3_attribute_create('"_Grant"', 'ObjectClass', 'regclass', 'MODE: read');
SELECT _cm3_attribute_create('"_Grant"', 'ObjectId', 'integer', 'MODE: read');
SELECT _cm3_attribute_create('"_Grant"', 'Filter', 'jsonb', 'MODE: read');
SELECT _cm3_attribute_create('"_Grant"', 'AttributePrivileges', 'jsonb', 'MODE: read');
SELECT _cm3_attribute_create('"_Grant"', 'OtherPrivileges', 'jsonb', 'MODE: read'); --TODO rename to CustomPrivileges

CREATE OR REPLACE FUNCTION _patch_30042_aux_attr(attr_privs IN varchar[]) RETURNS jsonb AS $$ DECLARE
	_res text[];
	_attribute_privilege varchar;
BEGIN
	IF attr_privs IS NULL THEN
		RETURN NULL;
	ELSE
		_res = array[]::text[];
		FOREACH _attribute_privilege IN ARRAY attr_privs LOOP
			IF _attribute_privilege IS NOT NULL THEN
                IF _attribute_privilege !~ '^.+:.+$' THEN
                    RAISE 'invalid attribute privilege part =< % > ( expected `attr_name:privilege` )', _attribute_privilege;
                END IF;
                _res = array_cat(_res, string_to_array(_attribute_privilege,':'));
			END IF;
		END LOOP;
		RETURN json_object(_res);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _patch_30042_aux_edit(card_edit_mode IN text) RETURNS jsonb AS $$ DECLARE
	mode varchar;
	_res jsonb;
BEGIN
	IF card_edit_mode IS NULL THEN
		RETURN NULL;
	ELSE
		_res = '{}'::jsonb;
		FOREACH mode IN ARRAY string_to_array(card_edit_mode,',') LOOP
			_res = jsonb_set(_res,ARRAY[((string_to_array(mode,'='))[1])::text], ((string_to_array(mode,'='))[2])::jsonb);
		END LOOP;
		RETURN _res;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _patch_30042_aux_privfilter(privfilter IN text) RETURNS jsonb AS $$ BEGIN
	IF privfilter IS NULL OR privfilter = '' THEN
		RETURN NULL;
	ELSE
		RETURN privfilter::jsonb;
	END IF;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
	_record RECORD;
BEGIN
	FOR _record IN SELECT * FROM "Grant" LOOP
		BEGIN
			PERFORM _patch_30042_aux_attr(_record."AttributesPrivileges");
		EXCEPTION WHEN others THEN
			RAISE EXCEPTION 'error processing Grant.AttributesPrivileges for record = %: %', _record."Id", SQLERRM;
		END;
		BEGIN
			PERFORM _patch_30042_aux_edit(_record."UI_EnabledCardEditMode");
		EXCEPTION WHEN others THEN
			RAISE EXCEPTION 'error processing Grant.UI_EnabledCardEditMode for record = %: %', _record."Id", SQLERRM;
		END;
		BEGIN
			PERFORM _patch_30042_aux_privfilter(_record."PrivilegeFilter");
		EXCEPTION WHEN others THEN
			RAISE EXCEPTION 'error processing Grant.PrivilegeFilter for record = %: %', _record."Id", SQLERRM;
		END;
	END LOOP;
END $$ LANGUAGE PLPGSQL;


-- UPDATE "_Grant" SET "Status" = 'N', "Notes" = 'removed with cleanup patch 3.0.0-6c_grant' WHERE "ObjectClass" IS NOT NULL AND _cm3_utils_name_to_regclass("ObjectClass") IS NULL AND "Status" = 'A';
DELETE FROM "Grant" WHERE "IdGrantedClass" IS NOT NULL AND _cm3_utils_name_to_regclass(_cm3_utils_regclass_to_name("IdGrantedClass")) IS NULL;

ALTER TABLE "_Grant" DISABLE TRIGGER USER;

INSERT INTO "_Grant" (
	"Id",
	"CurrentId",
	"IdClass",
	"Status",	
	"Description",
	"User",
	"BeginDate",
	"Code",
	"Notes",
	"IdRole",
	"Mode",
	"Type",
	"ObjectClass",
	"ObjectId",
	"Filter",
	"AttributePrivileges",
	"OtherPrivileges"
) SELECT 
	"Id",
	"Id",
	'"_Grant"'::regclass,
	'A',	
	"Description",
	"User",
	"BeginDate",
	"Code",
	"Notes",
	"IdRole",
	"Mode",
	"Type",
	"IdGrantedClass",
	"IdPrivilegedObject",
	_patch_30042_aux_privfilter("PrivilegeFilter"),
	_patch_30042_aux_attr("AttributesPrivileges"),
	_patch_30042_aux_edit("UI_EnabledCardEditMode")
FROM
	"Grant";

ALTER TABLE "_Grant" ENABLE TRIGGER USER;

DROP VIEW system_privilegescatalog;
DROP TABLE "Grant";

DROP FUNCTION  _patch_30042_aux_attr(attr_privs IN varchar[]);
DROP FUNCTION  _patch_30042_aux_edit(card_edit_mode IN text);
DROP FUNCTION _patch_30042_aux_privfilter(privfilter IN text);

CREATE OR REPLACE VIEW system_privilegescatalog AS 
	SELECT 
		DISTINCT ON (permission."IdClass", permission."Code", permission."Description", permission."Status", permission."User", permission."Notes", permission."IdRole", permission."IdGrantedClass") 
		permission."Id",
		permission."IdClass",
		permission."Code",
		permission."Description",
		permission."Status",
		permission."User",
		permission."BeginDate",
		permission."Notes",
		permission."IdRole",
		permission."IdGrantedClass",
		permission."Mode"
	FROM ( SELECT 
			"_Grant"."Id",
            "_Grant"."IdClass",
            "_Grant"."Code",
            "_Grant"."Description",
            "_Grant"."Status",
            "_Grant"."User",
            "_Grant"."BeginDate",
            "_Grant"."Notes",
            "_Grant"."IdRole",
            "_Grant"."ObjectClass" "IdGrantedClass",
            "_Grant"."Mode"
           FROM "_Grant"
        UNION
         SELECT 
			'-1'::integer AS int4,
            '"_Grant"'::regclass AS regclass,
            ''::character varying AS "varchar",
            ''::character varying AS "varchar",
            'A'::bpchar AS bpchar,
            'admin'::character varying AS "varchar",
            now() AS now,
            NULL::text AS unknown,
            "Role"."Id",
            system_classcatalog_1.classid::regclass AS classid,
            '-'::character varying AS "varchar"
           FROM 
				system_classcatalog system_classcatalog_1, 
				"Role"
          WHERE 
			system_classcatalog_1.classid::regclass::oid <> '"Class"'::regclass::oid 
			AND NOT (
				"Role"."Id"::text || system_classcatalog_1.classid::integer::text IN ( SELECT "_Grant"."IdRole"::text || "_Grant"."ObjectClass"::oid::integer::text FROM "_Grant")
			)
		) permission
     JOIN 
		system_classcatalog 
		ON 
			permission."IdGrantedClass"::oid = system_classcatalog.classid 
			AND (_cm_legacy_read_comment(system_classcatalog.classcomment::character varying::text, 'MODE'::character varying::text)::text = ANY (ARRAY['write'::character varying::text, 'read'::character varying::text]))
  ORDER BY 
		permission."IdClass", permission."Code", permission."Description", permission."Status", permission."User", permission."Notes", permission."IdRole", permission."IdGrantedClass";


DO $$ 
DECLARE
	_record RECORD;
BEGIN

	FOR _record IN SELECT DISTINCT "IdRole","Type",COALESCE("ObjectClass",0) "ObjectClass",COALESCE("ObjectId",0) "ObjectId" 
		FROM "_Grant" g 
		WHERE 
			"Status" = 'A' AND
			(SELECT COUNT(*) FROM "_Grant" g2 
			WHERE g2."IdRole"=g."IdRole" 
			AND g2."Type"=g."Type" 
			AND COALESCE(g2."ObjectClass",0) = COALESCE(g."ObjectClass",0) 
			AND COALESCE(g2."ObjectId",0) = COALESCE(g."ObjectId",0)
			AND "Status" = 'A' ) 
			> 1
	LOOP

		RAISE WARNING 'multiple records found for grant with IdRole = % Type = % ObjectClass = % ObjectId = % (will keep only the most recent record)', _record."IdRole", _record."Type", _record."ObjectClass", _record."ObjectId";

		UPDATE "_Grant" SET "Status" = 'N' WHERE "Id" IN (SELECT "Id" FROM "_Grant" 
			WHERE "Status" = 'A'
			AND "IdRole" = _record."IdRole" 
			AND "Type" = _record."Type" 
			AND COALESCE("ObjectClass",0) = _record."ObjectClass"
			AND COALESCE("ObjectId",0) = _record."ObjectId"
			ORDER BY "BeginDate" DESC OFFSET 1);

	END LOOP;

END $$ LANGUAGE PLPGSQL;

CREATE UNIQUE INDEX "_Unique__Grant_IdRoleTypeObjectClassObjectId" ON "_Grant" ("IdRole","Type",COALESCE("ObjectClass"::int,0),COALESCE("ObjectId",0)) WHERE "Status" = 'A';--TODO auto index with function


DO $$ DECLARE
	_record RECORD;
	_value jsonb;
BEGIN

	ALTER TABLE "_Grant" DISABLE TRIGGER USER;
	
	FOR _record IN SELECT * FROM "_Grant" WHERE "_Grant"."OtherPrivileges" IS NOT NULL LOOP
		_value = _record."OtherPrivileges";
		IF _value->>'modify' IS NOT NULL THEN
			_value = ( _value || jsonb_build_object('update', _value->'modify') ) - 'modify';
		END IF;
		IF _value->>'remove' IS NOT NULL THEN
			_value = ( _value || jsonb_build_object('delete', _value->'remove') ) - 'remove';
		END IF;
		IF _value::varchar <> (_record."OtherPrivileges")::varchar THEN
			EXECUTE format('UPDATE "_Grant" SET "OtherPrivileges" = %L WHERE "Id" = %L', _value, _record."Id");
		END IF;
	END LOOP;

	ALTER TABLE "_Grant" ENABLE TRIGGER USER;

END $$ LANGUAGE PLPGSQL;

DO $$ BEGIN

    PERFORM _cm3_utils_store_and_drop_dependant_views('"_Grant"'::regclass);

	ALTER TABLE "_Grant" ALTER COLUMN "IdRole" TYPE bigint;
	ALTER TABLE "_Grant" ALTER COLUMN "ObjectId" TYPE bigint;
    
    PERFORM _cm3_utils_restore_dependant_views();

END $$ LANGUAGE plpgsql;

UPDATE "_Grant" SET "Status" = 'N' WHERE "Type" ILIKE 'Class' AND "ObjectClass" IS NULL AND "Status" = 'A';
