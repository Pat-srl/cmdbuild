-- adding waterway config table

ALTER TABLE "_Grant" DROP CONSTRAINT "_cm3_ObjectClassObjectIdObjectCode_check";

SELECT _cm3_class_triggers_disable('"_Grant"'::regclass);
DELETE FROM "_Grant" WHERE "Type" = 'etltemplate' AND NOT EXISTS (SELECT * FROM "_ImportExportTemplate" WHERE "Id" = "ObjectId");
UPDATE "_Grant" SET "ObjectCode" = (SELECT "Code" FROM "_ImportExportTemplate" WHERE "Id" = "ObjectId"), "ObjectId" = NULL WHERE "Type" = 'etltemplate';
SELECT _cm3_class_triggers_enable('"_Grant"'::regclass);

ALTER TABLE "_Grant" ADD CONSTRAINT "_cm3_ObjectClassObjectIdObjectCode_check" CHECK ( ( "Type" IN ('class','process') AND "ObjectClass" IS NOT NULL AND "ObjectId" IS NULL AND "ObjectCode" IS NULL ) 
    OR ( "Type" IN ('etlgate','etltemplate') AND "ObjectClass" IS NULL AND "ObjectId" IS NULL AND _cm3_utils_is_not_blank("ObjectCode") )
    OR ( "Type" NOT IN ('class','process','etlgate','etltemplate') AND "ObjectClass" IS NULL AND "ObjectId" IS NOT NULL AND "ObjectCode" IS NULL  ) );

DO $$ DECLARE
    _record record;
    _metadata jsonb;
    _key varchar;
	_template varchar;
	_id_template bigint;
	_code_template varchar;
BEGIN
	FOR _record IN SELECT * FROM "_ClassMetadata" WHERE "Status" = 'A' LOOP
        _metadata = _record."Metadata"::jsonb;
        FOR _key IN SELECT k FROM jsonb_object_keys(_metadata) k WHERE k ~* '^(cm_default_import_template|cm_default_export_template)$' LOOP
			_template = _metadata ->> _key;
			IF _template ~* '^template:[0-9]+$' THEN
				_id_template = SUBSTRING(_template, '[0-9]+');
				_code_template = _cm3_card_code_get(_cm3_utils_name_to_regclass('_ImportExportTemplate'), _id_template);
				IF _code_template IS NOT NULL THEN
					_metadata = _metadata || jsonb_build_object(_key, REPLACE(_template, _id_template::varchar, _code_template));
				END IF;
			END IF;
        END LOOP;
        UPDATE "_ClassMetadata" SET "Metadata" = _metadata WHERE "Id" = _record."Id";
    END LOOP;
END; $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
    _config jsonb;
    _template record;
BEGIN
    FOR _template IN SELECT * FROM "_ImportExportTemplate" WHERE "Status" = 'A' LOOP
 
        _config = jsonb_build_object('type', 'template', 'code', _template."Code", 'description', _template."Description", 'mode', _template."Config"->'type' ) || ( _template."Config" - 'importKeyAttribute' - 'type' );

        IF _cm3_utils_is_not_blank(_template."Config"->>'importKeyAttribute') THEN
            _config = _config || jsonb_build_object('importKeyAttributes', ARRAY[_template."Config"->>'importKeyAttribute']);
        END IF;
        IF _cm3_utils_is_not_blank(_template."Config"->>'notificationTemplate') THEN
            _config = _config || jsonb_build_object('notificationTemplate', (SELECT "Code" FROM "_EmailTemplate" WHERE "Id" = (_template."Config"->>'notificationTemplate')::bigint AND "Status" = 'A'),
                'notificationAccount', (SELECT "Code" FROM "_EmailAccount" WHERE "Id" = (_template."Config"->>'errorAccount')::bigint AND "Status" = 'A'));
                --this is not an error, legacy `errorAccount` was used for notification account too
        END IF; 
        IF _cm3_utils_is_not_blank(_template."Config"->>'errorTemplate') THEN
            _config = _config || jsonb_build_object('errorTemplate', (SELECT "Code" FROM "_EmailTemplate" WHERE "Id" = (_template."Config"->>'errorTemplate')::bigint AND "Status" = 'A'),
                'errorAccount', (SELECT "Code" FROM "_EmailAccount" WHERE "Id" = (_template."Config"->>'errorAccount')::bigint AND "Status" = 'A'));
        END IF; 

        RAISE NOTICE 'new template config =< % >', _config;
        INSERT INTO "_EtlConfig" ("Code", "Version", "Enabled", "Data") VALUES (_template."Code", 1, _template."Active", jsonb_pretty(_config)::varchar);
    END LOOP;
END $$ LANGUAGE PLPGSQL;

DROP TABLE "_ImportExportTemplate" CASCADE; 
