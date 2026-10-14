-- refactoring of lookup table

DROP TRIGGER IF EXISTS cm_set_translationuuid ON "LookUp";
DROP FUNCTION IF EXISTS cm_set_translationuuid();

ALTER TABLE "LookUp" DISABLE TRIGGER USER;

UPDATE "LookUp" SET "Type" = 'X' WHERE "Type" IS NULL OR "Type" ~ '^[[:blank:][:cntrl:]]*$';
UPDATE "LookUp" SET "Code" = "Description" WHERE "Code" IS NULL OR "Code" ~ '^[[:blank:][:cntrl:]]*$';
UPDATE "LookUp" SET "Code" = "Type" WHERE "Code" IS NULL OR "Code" ~ '^[[:blank:][:cntrl:]]*$';
 
DO $$ DECLARE
	_code varchar;
	_type varchar;
BEGIN
	FOR _code, _type IN SELECT l."Code", l."Type" FROM (SELECT "Code", "Type", count(*) c FROM "LookUp" WHERE "Status" = 'A' GROUP BY "Code","Type") l WHERE l.c > 1 LOOP
		RAISE NOTICE 'found duplicated codes for lookup type = % code = %; replacing codes with unique values', _type, _code;
		UPDATE "LookUp" SET "Code" = format('%s_%s', "Code", "Id") WHERE "Id" IN
			(SELECT "Id" FROM "LookUp" WHERE "Code" = _code AND "Type" = _type AND "Status" = 'A' ORDER BY "BeginDate" DESC OFFSET 1); --TODO check this
	END LOOP;
	FOR _code, _type IN SELECT l."Code", l."Type" FROM (SELECT "Code", "Type", count(*) c FROM "LookUp" WHERE "Status" = 'N' GROUP BY "Code","Type") l WHERE l.c > 1 LOOP
		RAISE NOTICE 'found duplicated codes for lookup type = % code = %; replacing codes with unique values', _type, _code;
		UPDATE "LookUp" SET "Code" = format('%s_%s', "Code", "Id") WHERE "Id" IN
			(SELECT "Id" FROM "LookUp" WHERE "Code" = _code AND "Type" = _type AND "Status" = 'N' ORDER BY "BeginDate" DESC OFFSET 1); --TODO check this
	END LOOP;
END $$ LANGUAGE PLPGSQL;

UPDATE "_Translation" t SET "Code" = 'lookup.'||l."Type"||'.'||l."Code"||'.description' FROM "LookUp" l WHERE t."Code" = 'lookup.'||l."TranslationUuid"||'.description' AND l."Status" = 'A' AND t."Status" = 'A';
UPDATE "_Translation" SET "Status" = 'N' WHERE "Code" ~ 'lookup.[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}.description' AND "Status" = 'A';--TODO update these records above and keep them as status=n in translation table (requires conversion of trans table to standard class before this patch

ALTER TABLE "LookUp" DROP COLUMN "TranslationUuid";

UPDATE "LookUp" SET "IsDefault" = FALSE WHERE "IsDefault" IS NULL;

ALTER TABLE "LookUp" ENABLE TRIGGER USER;

DO $$ DECLARE
    _record record;
BEGIN

	CREATE TABLE lookup_aux AS (SELECT "Id","User","BeginDate","Code","Description","Status","Notes","Type","ParentType","ParentId","Number","IsDefault" FROM "LookUp" l);

    PERFORM _cm3_utils_store_and_drop_dependant_views('"LookUp"'::regclass);

	DROP TABLE "LookUp";

	PERFORM _cm3_class_create('LookUp', '"Class"', 'MODE: reserved|TYPE: class|DESCR: Lookup list|SUPERCLASS: false');
	PERFORM _cm3_attribute_create('"LookUp"', 'Type', 'varchar', 'NOTNULL: true|MODE: read');
	PERFORM _cm3_attribute_create('"LookUp"', 'ParentType', 'varchar', 'MODE: read');
	PERFORM _cm3_attribute_create('"LookUp"', 'ParentId', 'bigint', 'MODE: read'); 
	PERFORM _cm3_attribute_create('"LookUp"', 'IsDefault', 'boolean', 'DEFAULT: false|NOTNULL: true|MODE: read');
	PERFORM _cm3_attribute_create('"LookUp"', 'IconType', 'varchar', 'DEFAULT: none|NOTNULL: true|MODE: read|DESCR: IconType');
	PERFORM _cm3_attribute_create('"LookUp"', 'IconImage', 'varchar', 'MODE: read|DESCR: Icon Image');
	PERFORM _cm3_attribute_create('"LookUp"', 'IconFont', 'varchar', 'MODE: read|DESCR: Icon Font'); --TODO add constraint 
	PERFORM _cm3_attribute_create('"LookUp"', 'Index', 'integer', 'NOTNULL: true|MODE: read|DESCR: Index');
	PERFORM _cm3_attribute_create('"LookUp"', 'IconColor', 'varchar', 'MODE: read|DESCR: Icon Color');
	PERFORM _cm3_attribute_create('"LookUp"', 'TextColor', 'varchar', 'MODE: read|DESCR: Text Color');
    PERFORM _cm3_attribute_create('"LookUp"', 'IsActive', 'boolean', 'DEFAULT: true|NOTNULL: true|MODE: read');
	ALTER TABLE "LookUp" ADD CONSTRAINT "_cm3_IconType_check" CHECK ( "IconType" ~ '^(font|image|none)$' );
	ALTER TABLE "LookUp" ADD CONSTRAINT "_cm3_IconColor_check" CHECK ( "IconColor" ~* '^#?[A-F0-9]{6}$' );
	ALTER TABLE "LookUp" ADD CONSTRAINT "_cm3_TextColor_check" CHECK ( "TextColor" ~* '^#?[A-F0-9]{6}$' );
	ALTER TABLE "LookUp" ADD CONSTRAINT "_cm3_Code_check" CHECK ( NOT "Code" ~ '^[[:blank:][:cntrl:]]*$' );
	PERFORM _cm3_attribute_notnull_set('"LookUp"', 'Code', true); 
	PERFORM _cm3_attribute_index_unique_create('"LookUp"', 'Code', 'Type');

	ALTER TABLE "LookUp" DISABLE TRIGGER USER;
	
    INSERT INTO "LookUp" ("Id","CurrentId","IdClass","User","BeginDate","Code","Description","Status","Notes","Type","ParentType","ParentId","Index","IsDefault" )
		SELECT "Id","Id",'"LookUp"'::regclass,"User","BeginDate","Code","Description",'A',"Notes","Type","ParentType","ParentId",COALESCE("Number", -1),"IsDefault"  FROM lookup_aux WHERE "Status" = 'A';

    FOR _record IN SELECT * FROM lookup_aux WHERE "Status" = 'N' LOOP
        IF EXISTS (SELECT * FROM "LookUp" WHERE "Status" = 'A' AND "Type" = _record."Type" AND "Code" = _record."Code") THEN
            RAISE NOTICE 'code conflict on disabled lookup record (will drop record) = % ', _record;
        ELSE
            INSERT INTO "LookUp" ("Id","CurrentId","IdClass","User","BeginDate","Code","Description","Status","Notes","Type","ParentType","ParentId","Index","IsDefault","IsActive") VALUES
                (_record."Id",_record."Id",'"LookUp"'::regclass,_record."User",_record."BeginDate",_record."Code",_record."Description",'A',_record."Notes",_record."Type",_record."ParentType",_record."ParentId",COALESCE(_record."Number", -1),_record."IsDefault",FALSE);
        END IF;
    END LOOP;

	ALTER TABLE "LookUp" ENABLE TRIGGER USER;

	DROP TABLE lookup_aux;
 
    PERFORM _cm3_utils_restore_dependant_views();

END $$ LANGUAGE PLPGSQL;

INSERT INTO "LookUp" ("Code","Type","ParentType","Index","IsDefault") SELECT DISTINCT 'org.cmdbuild.LOOKUPTYPE', "Type", "ParentType", 0, FALSE FROM "LookUp" WHERE "Status" = 'A';
