-- restoring missing fk triggers

DO $$ DECLARE
    _class regclass;
    _sub_class regclass;
    _attribute varchar;
    _fk_target regclass;
BEGIN
    FOR _class IN SELECT * FROM _cm3_class_list_descendants_and_self('"SimpleClass"') LOOP
        FOR _attribute IN SELECT * FROM _cm3_attribute_list(_class) a WHERE _cm3_attribute_is_foreignkey(_class, a) LOOP
		_fk_target = _cm3_attribute_foreignkey_or_reference_target_class_get(_class, _attribute);
		RAISE NOTICE 'handling class % attr % target %', _class, _attribute, _fk_target;
		EXECUTE format('DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_%s" ON %s',  _cm3_utils_shrink_name_lon(_attribute), _class);
		EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_enforce_foreign_key_for_source(%L,%L)', 
                    _cm3_utils_shrink_name_lon(_attribute), _class, _attribute, _fk_target);

		FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_fk_target) LOOP
			EXECUTE format('DROP TRIGGER IF EXISTS "_cm3_card_enforce_fk_%s_%s" ON %s', _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name_lon(_attribute), _sub_class);
			EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s_%s" BEFORE UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target(%L,%L)', 
				_cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name(_attribute), _sub_class, _class, _attribute);
		END LOOP;
        END LOOP;
    END LOOP;
END $$ LANGUAGE PLPGSQL;
