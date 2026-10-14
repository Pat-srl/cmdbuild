-- fk trigger fix


DO $$ DECLARE
	_class regclass; 
	_otherclass regclass; 
	_trigger varchar;
	_attr varchar;
	_args varchar[];
BEGIN
	FOR _class, _trigger, _args IN 
			WITH _triggers AS (SELECT tgrelid, tgname, _cm3_trigger_utils_tgargs_to_string_array(tgargs) args from pg_trigger join pg_proc on pg_proc.oid = pg_trigger.tgfoid where proname = '_cm3_trigger_card_enforce_foreign_key_for_source')
			SELECT tgrelid::regclass, tgname, args FROM _triggers WHERE tgrelid::regclass IN (SELECT _cm3_class_list()) AND cardinality(args) = 3 LOOP
		_otherclass = _args[2]::regclass;
		_attr = _args[1];
		EXECUTE format('DROP TRIGGER "%s" ON %s', _trigger, _class);
        IF _args[3] ILIKE 'simple' THEN
    		EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_enforce_foreign_key_for_source(%L,%L)', _cm3_utils_shrink_name_lon(_attr), _class, _attr, _otherclass);
        ELSE
            EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_source(%L,%L)', _cm3_utils_shrink_name_lon(_attr), _class, _attr, _otherclass);
        END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL;

-- 
-- -- REGENERATE ALL TRIGGERS
-- 
-- DO $$ DECLARE
--     _trigger record;
--     _class regclass;
--     _attr varchar;
--     _domain regclass;
--     _direction varchar;
--     _sub_class regclass;
--     _cardin varchar;
--     _fk_target regclass;
-- BEGIN
-- 
--     PERFORM _cm3_system_login();
-- 
--     FOR _trigger IN SELECT * FROM _cm3_class_triggers_list_detailed() WHERE trigger_function IN ('_cm3_trigger_card_update_relations','_cm3_trigger_relation_update_references','_cm3_trigger_card_enforce_foreign_key_for_source','_cm3_trigger_card_enforce_foreign_key_for_target','_cm3_trigger_simplecard_enforce_foreign_key_for_source') LOOP
--         RAISE NOTICE 'drop trigger = % on class = %', _trigger.trigger_name, _cm3_utils_regclass_to_name(_trigger.owner);
--         EXECUTE format('DROP TRIGGER %I ON %s', _trigger.trigger_name, _trigger.owner);
--     END LOOP;
-- 
--     FOR _class, _attr IN SELECT owner, name FROM _cm3_attribute_list_detailed() WHERE inherited = FALSE LOOP
--         RAISE NOTICE 'process attr %.%', _cm3_utils_regclass_to_name(_class), _attr;
--         IF _cm3_attribute_is_foreignkey(_class, _attr) OR _cm3_attribute_is_reference(_class, _attr) THEN
--             RAISE NOTICE 'attr is foreignkey or reference: activating fk triggers';
--             _fk_target = _cm3_attribute_foreignkey_or_reference_target_class_get(_class, _attr);
--             FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
--                 IF _cm3_class_is_simple(_sub_class) THEN 
--                     EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_enforce_foreign_key_for_source(%L,%L)', _cm3_utils_shrink_name_lon(_attr), _sub_class, _attr, _fk_target);
--                 ELSE
--                     EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_source(%L,%L)', _cm3_utils_shrink_name_lon(_attr), _sub_class, _attr, _fk_target);
--                 END IF;			
--             END LOOP;
--             FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_fk_target) LOOP
--                 EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s_%s" BEFORE UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target(%L,%L)', 
--                     _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name(_attr), _sub_class, _class, _attr);
--             END LOOP;
--         END IF;
--         IF _cm3_attribute_is_reference(_class, _attr) THEN
--             RAISE NOTICE 'attr is reference: activating reference triggers';
--             _domain = _cm3_attribute_reference_domain_get(_class, _attr);
--             _direction = _cm3_attribute_reference_direction_get(_class, _attr);
--             _cardin = _cm3_class_comment_get(_domain, 'CARDIN');
--             IF ( _direction = 'direct' AND _cardin NOT LIKE '%:1' ) OR ( _direction = 'inverse' AND _cardin NOT LIKE '1:%' ) THEN
--                 RAISE 'invalid domain = % and direction = % for reference attr of class = % (domain cardin = %)', _cm3_utils_regclass_to_domain_name(_domain), _direction, _cm3_utils_regclass_to_name(_class), _cardin;
--             END IF;
--             FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
--                 EXECUTE format('CREATE TRIGGER "_cm3_card_update_rels_%s" AFTER INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_update_relations(%L,%L,%L)', 
--                     _cm3_utils_shrink_name_lon(_attr), _sub_class, _attr, _domain, _direction);
--             END LOOP;
--             EXECUTE format('CREATE TRIGGER "_cm3_rel_update_refs_%s_%s" AFTER INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_relation_update_references(%L,%L,%L)',
--                 _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name(_attr), _domain, _class, _attr, _direction);
--         END IF;
--     END LOOP;
-- 
-- END $$ LANGUAGE PLPGSQL;
-- 
