-- adding waterway config table

SELECT _cm3_class_create('NAME: _EtlConfig|MODE: reserved|DESCR: Waterway Config');

SELECT _cm3_attribute_create('OWNER: _EtlConfig|NAME: Version|TYPE: int|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlConfig|NAME: Enabled|TYPE: boolean|DEFAULT: true|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlConfig|NAME: Disabled|TYPE: varchar[]|DEFAULT: ARRAY[]::varchar[]|NOTNULL: true');
SELECT _cm3_attribute_create('OWNER: _EtlConfig|NAME: Data|TYPE: varchar|NOTNULL: true|DESCR: Waterway Config data, yaml format');

ALTER TABLE "_EtlConfig" ADD CONSTRAINT "_cm3_Version_check" CHECK ( "Version" > 0 );

SELECT _cm3_attribute_notnull_set('"_EtlConfig"', 'Code');
SELECT _cm3_attribute_index_unique_create('"_EtlConfig"', 'Code', 'Version'); 

SELECT _cm3_attribute_create('OWNER: _Grant|NAME: ObjectCode|TYPE: varchar');
DROP INDEX "_Unique__Grant_IdRoleTypeObjectClassObjectId";
ALTER TABLE "_Grant" DROP CONSTRAINT "_cm3_ObjectClassObjectId_check";

SELECT _cm3_class_triggers_disable('"_Grant"'::regclass);
UPDATE "_Grant" SET "ObjectCode" = (SELECT "Code" FROM "_EtlGate" WHERE "Id" = "ObjectId"), "ObjectId" = NULL WHERE "Type" = 'etlgate';
SELECT _cm3_class_triggers_enable('"_Grant"'::regclass);

CREATE UNIQUE INDEX "_Unique__Grant_IdRoleTypeObjectClassObjectIdObjectCode" ON "_Grant" ("IdRole","Type",COALESCE("ObjectClass"::int,0),COALESCE("ObjectId",0),COALESCE("ObjectCode",'')) WHERE "Status" = 'A';
ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_ObjectClassObjectIdObjectCode_check" CHECK ( ( "Type" IN ('class','process') AND "ObjectClass" IS NOT NULL AND "ObjectId" IS NULL AND "ObjectCode" IS NULL ) 
    OR ( "Type" IN ('etlgate') AND "ObjectClass" IS NULL AND "ObjectId" IS NULL AND _cm3_utils_is_not_blank("ObjectCode") )
    OR ( "Type" NOT IN ('class','process','etlgate') AND "ObjectClass" IS NULL AND "ObjectId" IS NOT NULL AND "ObjectCode" IS NULL  ) );

DO $$ DECLARE
    _config jsonb;
    _handler jsonb;
    _handlers jsonb;
    _gate record;
BEGIN
    FOR _gate IN SELECT * FROM "_EtlGate" WHERE "Status" = 'A' LOOP
        _handlers = '[]'::jsonb;
        FOR _handler IN SELECT * FROM jsonb_array_elements(_gate."Items") LOOP
            _handlers = _handlers || jsonb_build_array(_handler - 'Id' - 'IdClass'
                 - '_shape_import_include_or_exclude' - '_shape_import_key_attr_description' - '_shape_import_target_attr_description');
        END LOOP;
        _config = jsonb_build_object('code', _gate."Code", 'description', COALESCE(_gate."Description", _gate."Code"), 'items', jsonb_build_array(_gate."Config" - 'tag' || jsonb_build_object(
            'type', 'gate', 
            'code', _gate."Code", 
            'description', _gate."Description", 
            'access', CASE WHEN _gate."AllowPublicAccess" THEN 'public' ELSE 'private' END,
            'processing', _gate."ProcessingMode",
            'handlers', _handlers
            )));
        IF _cm3_utils_is_not_blank(_gate."Config"->>'tag') THEN
            _config = _config || jsonb_build_object('tag', _gate."Config"->'tag');
        END IF;
        RAISE NOTICE 'new gate config =< % >', _config;
        INSERT INTO "_EtlConfig" ("Code", "Description", "Version", "Enabled", "Data") VALUES (_gate."Code", COALESCE(_gate."Description", _gate."Code"), 1, _gate."Enabled", jsonb_pretty(_config)::varchar);
    END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP TABLE "_EtlGate" CASCADE;
