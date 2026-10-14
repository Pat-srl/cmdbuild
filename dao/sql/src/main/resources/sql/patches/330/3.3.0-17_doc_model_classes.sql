-- doc model classes, lookup refactoring

SELECT _cm3_class_create('NAME: DmsModel|PARENT: Class|MODE: protected|TYPE: class|DESCR: Parent DMS model|SUPERCLASS: true');
SELECT _cm3_attribute_create('OWNER: DmsModel|NAME: Card|TYPE: bigint|MODE: syshidden');
SELECT _cm3_attribute_create('OWNER: DmsModel|NAME: DocumentId|TYPE: varchar|MODE: syshidden');

SELECT _cm3_class_create('NAME: BaseDocument|PARENT: DmsModel|MODE: protected|TYPE: class|DESCR: Default DMS model');

SELECT _cm3_attribute_create('OWNER: LookUp|NAME: Speciality|TYPE: varchar|VALUES: default,dmscategory|DEFAULT: default');
SELECT _cm3_attribute_create('OWNER: LookUp|NAME: Config|TYPE: jsonb|NOTNULL: true|DEFAULT: ''{}''::jsonb');

SELECT _cm3_class_triggers_disable('"LookUp"');
UPDATE "LookUp" SET "Speciality" = 'default' WHERE "Code" = 'org.cmdbuild.LOOKUPTYPE';
SELECT _cm3_class_triggers_enable('"LookUp"');

DO $$ DECLARE
    _lookuptype varchar;
BEGIN
    FOR _lookuptype IN SELECT coalesce(_cm3_system_config_get('org.cmdbuild.dms.category.lookup'),'AlfrescoCategory') UNION SELECT DISTINCT features->>'ATTACHMENT_TYPE_LOOKUP' FROM _cm3_class_list_detailed() WHERE _cm3_utils_is_not_blank(features->>'ATTACHMENT_TYPE_LOOKUP') LOOP
        RAISE NOTICE 'add dms category = %', _lookuptype;
        UPDATE "LookUp" SET "Speciality" = 'dmscategory', "Config" = "Config" || jsonb_build_object('cm_dms_modelClass', 'BaseDocument') WHERE "Status" = 'A' AND "Type" = _lookuptype;
    END LOOP;
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
    _class record;
BEGIN
    FOR _class IN SELECT * FROM _cm3_class_list_detailed() x WHERE _cm3_utils_is_not_blank(features->>'ATTACHMENT_TYPE_LOOKUP') LOOP
        PERFORM _cm3_class_features_set(_class.table_id::oid::regclass, 'cm_dms_category', _class.features->>'ATTACHMENT_TYPE_LOOKUP');
        PERFORM _cm3_class_features_delete(_class.table_id::oid::regclass, 'ATTACHMENT_TYPE_LOOKUP');
    END LOOP;
END $$ LANGUAGE PLPGSQL;

DO $$ BEGIN
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.dms.category.lookup')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.dms.category', _cm3_system_config_get('org.cmdbuild.dms.category.lookup'));
        PERFORM _cm3_system_config_delete('org.cmdbuild.dms.category.lookup');
    END IF;
END $$ LANGUAGE PLPGSQL;
