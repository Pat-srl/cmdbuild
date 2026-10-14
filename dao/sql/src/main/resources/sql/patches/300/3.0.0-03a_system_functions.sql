-- mixed system access function


--- PLACEHOLDERS (db objects that are required here to create functions, but will be replaced in other patches later) ---

CREATE TABLE "SimpleClass" (id int);


-- legacy functions cleanup
 
DROP FUNCTION IF EXISTS _cm3_function_list(
		OUT function_name text,
		OUT function_id oid,
		OUT arg_io char[],
		OUT arg_names text[],
		OUT arg_types text[],
		OUT returns_set boolean,
		OUT comment text,
		OUT metadata jsonb
	);

-- mixed functions (legacy definitions)

CREATE OR REPLACE FUNCTION _cm3_card_cascade_after_delete(_card bigint) RETURNS void AS $$ BEGIN
    UPDATE "Map" SET "Status" = 'N' WHERE "Status" = 'A' AND ( "IdObj1" = _card OR "IdObj2" = _card );
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_delete(_class regclass, _attr varchar) RETURNS VOID AS $$ DECLARE
-- 	GeoType text := _cm_get_geometry_type(_class, _attr); TODO
	_fk_target regclass;
	_sub_class regclass;
	_domain regclass;
BEGIN
	IF _cm3_attribute_is_inherited(_class, _attr) THEN
		RAISE EXCEPTION 'CM_FORBIDDEN_OPERATION: cannot remove attribute %.%: attribute is inherited', _class, _attr;
	END IF;
    IF _cm3_attribute_has_data(_class, _attr) THEN
		RAISE EXCEPTION 'CM_CONTAINS_DATA: cannot remove attribute %.%: attribute contains data', _class, _attr;
	END IF;
	IF _cm3_attribute_is_foreignkey(_class, _attr) THEN
		_fk_target = _cm3_attribute_foreignkey_or_reference_target_class_get(_class, _attr);
		FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
			EXECUTE format('DROP TRIGGER "_cm3_card_enforce_fk_%s" ON %s', _cm3_utils_shrink_name_lon(_attr), _sub_class);
		END LOOP;
		FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_fk_target) LOOP
			EXECUTE format('DROP TRIGGER "_cm3_card_enforce_fk_%s_%s" ON %s', _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name(_attr), _sub_class);
		END LOOP;
	END IF;
	IF _cm3_attribute_is_reference(_class, _attr) THEN
		_domain = _cm3_attribute_reference_domain_get(_class, _attr);
		FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
			EXECUTE format('DROP TRIGGER "_cm3_card_enforce_fk_%s" ON %s', _cm3_utils_shrink_name_lon(_attr), _sub_class);
		END LOOP;
		FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
			EXECUTE format('DROP TRIGGER "_cm3_card_update_rels_%s" ON %s', _cm3_utils_shrink_name_lon(_attr), _sub_class);
		END LOOP;
		EXECUTE format('DROP TRIGGER "_cm3_rel_update_refs_%s_%s" ON %s', _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name(_attr), _domain);
	END IF;
-- 	IF GeoType IS NOT NULL THEN --TODO
-- 		PERFORM DropGeometryColumn(_cm_cmschema(_class), _cm_cmtable(_class), _attr);
-- 	ELSE
    EXECUTE format('ALTER TABLE %s DROP COLUMN %I CASCADE', _class, _attr);
-- 	END IF;
    IF _cm3_class_is_domain(_class) THEN
        PERFORM _cm3_domain_composite_index_rebuild(_class);
    END IF;
END $$ LANGUAGE PLPGSQL;