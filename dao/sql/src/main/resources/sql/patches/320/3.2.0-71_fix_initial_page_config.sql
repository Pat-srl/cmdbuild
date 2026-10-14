-- upgrade format for initial (landing) page preferences
  
CREATE OR REPLACE FUNCTION _patch_aux_fix_value(_value varchar) RETURNS varchar AS $$ DECLARE
    _new varchar;
BEGIN
    WITH q AS (SELECT _cm3_utils_regclass_to_name(x) _name FROM _cm3_class_list() x WHERE NOT _cm3_class_is_process(x) AND _cm3_utils_regclass_to_name(x) = _value ) 
        SELECT format('class:%s', _name) FROM q INTO _new;
    IF _new IS NOT NULL THEN
        RAISE NOTICE 'convert < % > to < % >', _value, _new;
        RETURN _new;
    END IF;    
    WITH q AS (SELECT _cm3_utils_regclass_to_name(x) _name FROM _cm3_class_list() x WHERE _cm3_class_is_process(x) AND _cm3_utils_regclass_to_name(x) = _value )
        SELECT format('process:%s', _name) FROM q INTO _new;
    IF _new IS NOT NULL THEN
        RAISE NOTICE 'convert < % > to < % >', _value, _new;
        RETURN _new;
    END IF;    
    WITH q AS (SELECT "Code" _name FROM "_View" WHERE "Status" = 'A' AND ( "Code" = _value OR "Id"::varchar = _value ) ) SELECT format('view:%s', _name) FROM q INTO _new;
    IF _new IS NOT NULL THEN
        RAISE NOTICE 'convert < % > to < % >', _value, _new;
        RETURN _new;
    END IF;    
    WITH q AS (SELECT "Code" _name FROM "_UiComponent" WHERE "Status" = 'A' AND "Type" = 'custompage' AND "Code" = _value ) SELECT format('custompage:%s', _name) FROM q INTO _new;
    IF _new IS NOT NULL THEN
        RAISE NOTICE 'convert < % > to < % >', _value, _new;
        RETURN _new;
    END IF;    
    WITH q AS (SELECT "Code" _name FROM "_Dashboard" WHERE "Status" = 'A' AND ( "Code" = _value OR "Id"::varchar = _value ) ) SELECT format('dashboard:%s', _name) FROM q INTO _new;
    IF _new IS NOT NULL THEN
        RAISE NOTICE 'convert < % > to < % >', _value, _new;
        RETURN _new;
    END IF;
    IF _value ~ '^(class|process|view|custompage|dashboard):.+$' THEN
        RAISE NOTICE 'skip value < % > (already converted)', _value;
        RETURN _value;
    END IF;
    RAISE WARNING 'unable to convert < % > : value not found; will set to null', _value;
    RETURN '';    
END $$ LANGUAGE PLPGSQL;

DO $$ DECLARE
    _record record;
BEGIN
    IF _cm3_utils_is_not_blank(_cm3_system_config_get('org.cmdbuild.core.startingclass')) THEN
        PERFORM _cm3_system_config_set('org.cmdbuild.core.startingclass',_patch_aux_fix_value(_cm3_system_config_get('org.cmdbuild.core.startingclass')));
    END IF;
    UPDATE "_UserConfig" SET "Data" = "Data" || jsonb_build_object('cm_ui_startingClass', _patch_aux_fix_value("Data"->>'cm_ui_startingClass')) WHERE "Status" = 'A' AND _cm3_utils_is_not_blank("Data"->>'cm_ui_startingClass');
    UPDATE "Role" SET "Config" = "Config" || jsonb_build_object('startingClass', _patch_aux_fix_value("Config"->>'startingClass')) WHERE "Status" = 'A' AND _cm3_utils_is_not_blank("Config"->>'startingClass');
END $$ LANGUAGE PLPGSQL;

DROP FUNCTION _patch_aux_fix_value(varchar);
