--  trigger fix

DO $$ DECLARE
	_class regclass; 
	_otherclass_name varchar; 
	_trigger varchar;
	_attr varchar;
	_args varchar[];
BEGIN
	FOR _class, _trigger, _args IN 
			WITH _triggers AS (SELECT tgrelid, tgname, _cm3_trigger_utils_tgargs_to_string_array(tgargs) args from pg_trigger join pg_proc on pg_proc.oid = pg_trigger.tgfoid where proname = '_cm3_trigger_card_enforce_foreign_key_for_target')
			SELECT tgrelid::regclass, tgname, args FROM _triggers WHERE tgrelid::regclass IN (SELECT _cm3_class_list()) AND cardinality(args) = 2 LOOP
		_otherclass_name = _args[1];
		_attr = _args[2];
        IF _otherclass_name LIKE '"Gis_%' AND _cm3_utils_name_to_regclass(_otherclass_name) IS NULL THEN
            EXECUTE format('DROP TRIGGER "%s" ON %s', _trigger, _class);
            EXECUTE format('CREATE TRIGGER "%s" BEFORE DELETE OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target(%L,%L)', _trigger, _class, format('gis.%s', _otherclass_name), _attr);
        END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL;
