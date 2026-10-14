-- cascade config
 
DO $$ DECLARE
    _domain regclass;
    _attribute record;
    _trigger record; 
    _cascade_direct varchar;
    _cascade_inverse varchar;
BEGIN
    FOR _domain IN SELECT * FROM _cm3_domain_list() LOOP
        _cascade_direct = 'setnull';
        _cascade_inverse = 'setnull';
        FOR _trigger IN SELECT * FROM (SELECT tgrelid::regclass _class, tgname, (_cm3_trigger_utils_tgargs_to_string_array(tgargs))[1]::regclass _owner, (_cm3_trigger_utils_tgargs_to_string_array(tgargs))[2] _attr from pg_trigger join pg_proc on pg_proc.oid = pg_trigger.tgfoid where proname = '_cm3_trigger_card_enforce_foreign_key_for_target') x LOOP
            IF NOT _cm3_attribute_is_foreignkey(_trigger._owner, _trigger._attr) THEN
                RAISE NOTICE 'clear reference trigger = % on class = %', _trigger.tgname, _cm3_utils_regclass_to_name(_trigger._class);
                EXECUTE format('DROP TRIGGER %I ON %s', _trigger.tgname, _trigger._class);
            END IF;
        END LOOP;
        IF EXISTS(SELECT * FROM _cm3_attribute_list_detailed() WHERE comment->>'REFERENCEDOM' = _cm3_utils_regclass_to_domain_name(_domain) AND comment->>'REFERENCEDIR' = 'direct' AND _cm3_utils_first_not_blank(comment->>'CASCADE', 'restrict') = 'restrict' ) THEN
            _cascade_inverse = 'restrict';
        END IF;
        IF EXISTS(SELECT * FROM _cm3_attribute_list_detailed() WHERE comment->>'REFERENCEDOM' = _cm3_utils_regclass_to_domain_name(_domain) AND comment->>'REFERENCEDIR' = 'inverse' AND _cm3_utils_first_not_blank(comment->>'CASCADE', 'restrict') = 'restrict' ) THEN
            _cascade_direct = 'restrict';
        END IF;
        RAISE NOTICE 'set domain cascade % = %, %', _cm3_utils_regclass_to_domain_name(_domain), _cascade_direct, _cascade_inverse;
        PERFORM _cm3_class_features_set(_domain, 'CASCADEDIRECT', _cascade_direct);
        PERFORM _cm3_class_features_set(_domain, 'CASCADEINVERSE', _cascade_inverse);
    END LOOP;
    FOR _attribute IN SELECT * FROM _cm3_attribute_list_detailed() WHERE _cm3_attribute_is_reference(owner, name) LOOP
            PERFORM _cm3_attribute_features_delete(_attribute.owner, _attribute.name, 'CASCADE');
    END LOOP;
END $$ LANGUAGE PLPGSQL;

