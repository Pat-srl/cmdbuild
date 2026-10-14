-- index and constraint upgrade


--- UPGRADE INDEXES ---

DO $$ DECLARE
	_class regclass;
	_prefix_pattern varchar;
	_index_name varchar;
	_attr varchar;
	_unique boolean;
	_cardinality varchar;
BEGIN
	FOR _class IN SELECT _cm3_domain_list() UNION SELECT _cm3_class_list() LOOP
		_prefix_pattern = format('^(idx_%s_|_Unique_%s_)', replace(_cm3_utils_regclass_to_name(_class),'_','_?'), _cm3_utils_regclass_to_name(_class));
		FOR _index_name, _unique IN  
			WITH _indexes AS (SELECT (SELECT relname FROM pg_class WHERE oid = pg_index.indexrelid) n,indisunique u FROM pg_index WHERE NOT indisprimary AND indrelid = _class) 
			SELECT n, u FROM _indexes WHERE n ~* _prefix_pattern ORDER BY u LOOP BEGIN
				_attr = lower(regexp_replace(_index_name, _prefix_pattern, '', 'i'));
				IF _cm3_class_is_domain(_class) AND _attr IN ('activerows', 'uniqueleft', 'uniqueright') AND _unique THEN
					RAISE NOTICE 'upgrade special domain index = % of class = %', _index_name, _class;
					EXECUTE format('DROP INDEX %I', _index_name);
					IF _attr = 'activerows' THEN
						EXECUTE format('CREATE UNIQUE INDEX "_cm3_%s_relations" ON %s ("IdClass1", "IdObj1", "IdClass2", "IdObj2") WHERE "Status" = ''A''', _cm3_utils_shrink_name_lon(_cm3_utils_regclass_to_name(_class)), _class);
					ELSEIF _attr = 'uniqueleft' THEN
						EXECUTE format('CREATE UNIQUE INDEX "_cm3_%s_source" ON %s ("IdClass1", "IdObj1") WHERE "Status" = ''A''', _cm3_utils_shrink_name_lon(_cm3_utils_regclass_to_name(_class)), _class);
					ELSEIF _attr = 'uniqueright' THEN
						EXECUTE format('CREATE UNIQUE INDEX "_cm3_%s_target" ON %s ("IdClass2", "IdObj2") WHERE "Status" = ''A''', _cm3_utils_shrink_name_lon(_cm3_utils_regclass_to_name(_class)), _class);
					END IF;
				ELSE
					SELECT INTO _attr x FROM _cm3_attribute_list(_class) x WHERE lower(x) = _attr;
					IF _attr IS NULL OR _attr = '' THEN
						RAISE EXCEPTION 'error processing index = % for class = %: attribute not found', _index_name, _class;
					END IF;
					RAISE NOTICE 'upgrade index = % of class = %', _index_name, _class;
					EXECUTE format('DROP INDEX %I', _index_name);
					PERFORM _cm3_attribute_index_create(_class, _unique, _attr);
				END IF;
			EXCEPTION WHEN others THEN
				RAISE WARNING 'unable to upgrade index = % of class = %: %', _index_name, _class, SQLERRM;
			END;
		END LOOP;		
	END LOOP;
END $$ LANGUAGE PLPGSQL; 


--- UPGRADE MAP PK AND MAP HISTORY CONSTRAINTS ---

DO $$ DECLARE
	_class regclass;
	_history regclass;
	_record record;
BEGIN
	FOR _class IN SELECT _cm3_domain_list() UNION SELECT '"Map"'::regclass  LOOP
		EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "%s"', _class, COALESCE((SELECT relname FROM pg_class JOIN pg_index ON pg_class.oid = pg_index.indexrelid WHERE indisprimary AND indrelid = _class),'_index_not_found_'));
		EXECUTE format('ALTER TABLE %s ADD PRIMARY KEY ("Id")', _class);
		IF _class <> '"Map"'::regclass THEN
			_history = _cm3_utils_regclass_to_history(_class);
			EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "%s"', _history, COALESCE((SELECT relname FROM pg_class JOIN pg_index ON pg_class.oid = pg_index.indexrelid WHERE indisprimary AND indrelid = _history),'_index_not_found_'));
			EXECUTE format('ALTER TABLE %s ADD PRIMARY KEY ("Id")', _history);
			EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_CurrentId_fkey" FOREIGN KEY ("CurrentId") REFERENCES %s("Id")', _history, _class);
			EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_Status_check" CHECK ("Status" = ''U'')', _history);
			PERFORM _cm3_attribute_index_create(_history, 'CurrentId');
		END IF;
	END LOOP;
END $$ LANGUAGE PLPGSQL; 


--- UPGRADE CHECK CONSTRAINTS ---

DO $$ DECLARE
	_class regclass;
	_check_name varchar;
	_attr varchar;
BEGIN
	FOR _class IN SELECT _cm3_domain_list() UNION SELECT _cm3_class_list() LOOP
		FOR _check_name, _attr IN SELECT c.conname, a.attname FROM pg_attribute a JOIN pg_constraint c ON c.conrelid = a.attrelid AND c.conname ILIKE format('_NotNull_%s',a.attname) WHERE a.attrelid = _class AND coninhcount = 0 LOOP
			IF _cm3_class_is_superclass(_class) THEN
				RAISE WARNING 'unable to upgrade check notnull constraint of class = % attr = % : class is superclass', _class, _attr;
			ELSE
				RAISE NOTICE 'upgrade check notnull constraint of class = % attr = %', _class, _attr;
				EXECUTE format('ALTER TABLE %s DROP CONSTRAINT %I', _class, _check_name);
				PERFORM _cm3_attribute_notnull_set(_class, _attr, true);
			END IF;
		END LOOP;
	END LOOP;
END $$ LANGUAGE PLPGSQL; 


--- UPGRADE CLASS HISTORY CONSTRAINTS ---

DO $$ DECLARE
	_class regclass;
	_history regclass;
	_invalid_record_id bigint;
BEGIN
	FOR _class IN SELECT c FROM _cm3_class_list_standard() c WHERE _cm3_class_has_history(c) LOOP
		_history = _cm3_utils_regclass_to_history(_class);
		RAISE NOTICE 'upgrade constraints for history = %', _history;
		EXECUTE format('CREATE TABLE _patch_aux_invalid_records AS SELECT "Id" FROM %s h WHERE NOT EXISTS (SELECT * FROM %s c WHERE c."Id" = h."CurrentId" AND c."Status" <> ''U'')', _history, _class);
		FOR _invalid_record_id IN SELECT "Id" FROM _patch_aux_invalid_records LOOP
			RAISE WARNING 'found orphan record for class = % record = % (will be deleted)', _class, _invalid_record_id;
			PERFORM _cm3_class_triggers_disable(_history);
			EXECUTE format('DELETE FROM %s WHERE "Id" = %s', _history, _invalid_record_id);
			PERFORM _cm3_class_triggers_enable(_history);
		END LOOP;
		DROP TABLE _patch_aux_invalid_records;
		EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "%s"', _history, COALESCE((SELECT conname FROM pg_constraint WHERE conname ILIKE '%_CurrentId_fkey' AND conrelid = _history),'_index_not_found_'));
		EXECUTE format('ALTER TABLE %s DROP CONSTRAINT "_Check_Status"', _history);
		EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_CurrentId_foreign_key" FOREIGN KEY ("CurrentId") REFERENCES %s("Id")', _history, _class);
		EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_Status_check" CHECK ("Status" = ''U'')', _history);
		EXECUTE format('DROP INDEX IF EXISTS "%s"', COALESCE((SELECT relname FROM pg_class JOIN pg_index ON pg_class.oid = pg_index.indexrelid WHERE indrelid = _history AND relname ILIKE 'idx_%_currentid'),'_index_not_found_'));
		PERFORM _cm3_attribute_index_create(_history, 'CurrentId');
	END LOOP;
END $$ LANGUAGE PLPGSQL; 


--- UPGRADE TRIGGERS ---
 
DO $$ DECLARE
	_class regclass; 
	_otherclass regclass; 
	_trigger_names varchar[];
	_trigger varchar;
	_attr varchar;
	_args varchar[];
    _new_name varchar;
BEGIN
	FOR _class IN SELECT c FROM _cm3_class_list() c WHERE _cm3_class_is_superclass(c) LOOP
		EXECUTE format('CREATE TRIGGER "_cm3_superclass_forbid_operations" BEFORE INSERT OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_superclass_forbid_operations()', _class);
	END LOOP;
	FOR _class IN SELECT _cm3_domain_list() UNION SELECT c FROM _cm3_class_list() c WHERE _cm3_class_has_history(c) LOOP
		EXECUTE format('DROP TRIGGER IF EXISTS "_CreateHistoryRow" ON %s', _class);
		EXECUTE format('CREATE TRIGGER "_cm3_card_create_history" AFTER UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_create_history()', _class);
	END LOOP;
	FOR _class IN SELECT c FROM _cm3_class_list() c WHERE NOT _cm3_class_is_superclass(c) LOOP
		EXECUTE format('DROP TRIGGER IF EXISTS "_CascadeDeleteOnRelations" ON %s', _class);
		IF _cm3_class_is_simple(_class) THEN
			EXECUTE format('CREATE TRIGGER "_cm3_card_cascade_delete_on_relations" AFTER DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_cascade_delete_on_relations()', _class);
		ELSE
			EXECUTE format('CREATE TRIGGER "_cm3_card_cascade_delete_on_relations" AFTER UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_cascade_delete_on_relations()', _class);
		END IF;
	END LOOP;
	FOR _class, _trigger_names, _args IN 
			WITH _triggers AS (SELECT tgrelid, tgname, _cm3_trigger_utils_tgargs_to_string_array(tgargs) args from pg_trigger join pg_proc on pg_proc.oid = pg_trigger.tgfoid where proname = '_cm_trigger_restrict')
			SELECT tgrelid::regclass, array_agg(tgname), args FROM _triggers WHERE tgrelid::regclass IN (SELECT _cm3_class_list()) GROUP BY tgrelid, args LOOP
        FOREACH _trigger IN ARRAY _trigger_names LOOP EXECUTE format('DROP TRIGGER "%s" ON %s', _trigger, _class); END LOOP;
		IF _args[2] = 'Master' AND _args[1] LIKE '"Detail_%' THEN
			RAISE NOTICE 'drop _cm_trigger_restrict for class = % with name = % args = % (it is a gis trigger, will be fixed in gis patch)', _class, _trigger, _args;
		ELSE
			_otherclass = _args[1]::regclass;
			_attr = _args[2];
            _new_name = format('_cm3_card_enforce_fk_%s_%s',_cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_otherclass)), _cm3_utils_shrink_name(_attr));
			RAISE NOTICE 'upgrade _cm_trigger_restrict for class = % with name = % args = %, new name = %', _class, _trigger, _args, _new_name;
			EXECUTE format('CREATE TRIGGER %I BEFORE UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target(%L,%L)', _new_name, _class, _otherclass, _attr);
		END IF;
	END LOOP;
	FOR _class, _trigger_names, _args IN 
			WITH _triggers AS (SELECT tgrelid, tgname, _cm3_trigger_utils_tgargs_to_string_array(tgargs) args from pg_trigger join pg_proc on pg_proc.oid = pg_trigger.tgfoid where proname = '_cm_trigger_fk')
			SELECT tgrelid::regclass, array_agg(tgname), args FROM _triggers WHERE tgrelid::regclass IN (SELECT _cm3_class_list()) GROUP BY tgrelid, args LOOP
		_otherclass = _args[2]::regclass;
		_attr = _args[1];
        _new_name = format('_cm3_card_enforce_fk_%s', _cm3_utils_shrink_name_lon(_attr));
		RAISE NOTICE 'upgrade _cm_trigger_fk for class = % with names = % args = %, new name = %', _class, _trigger_names, _args, _new_name;
		FOREACH _trigger IN ARRAY _trigger_names LOOP EXECUTE format('DROP TRIGGER "%s" ON %s', _trigger, _class); END LOOP;
        IF _args[3] ILIKE 'simple' THEN
    		EXECUTE format('CREATE TRIGGER %I BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_enforce_foreign_key_for_source(%L,%L)', _new_name, _class, _attr, _otherclass);
        ELSE
            EXECUTE format('CREATE TRIGGER %I BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_source(%L,%L)', _new_name, _class, _attr, _otherclass);
        END IF;
	END LOOP;
	FOR _class, _trigger_names, _args IN 
			WITH _triggers AS (SELECT tgrelid, tgname, _cm3_trigger_utils_tgargs_to_string_array(tgargs) args from pg_trigger join pg_proc on pg_proc.oid = pg_trigger.tgfoid where proname = '_cm_trigger_update_relation')
			SELECT tgrelid::regclass, array_agg(tgname), args FROM _triggers WHERE tgrelid::regclass IN (SELECT _cm3_class_list()) GROUP BY tgrelid, args LOOP
		_otherclass = _args[2]::regclass;
		_attr = _args[1];
        _new_name = format('_cm3_card_update_rels_%s', _cm3_utils_shrink_name_lon(_attr));
		RAISE NOTICE 'upgrade _cm_trigger_update_relation for class = % with names = % args = %, new name = %', _class, _trigger_names, _args, _new_name;
		FOREACH _trigger IN ARRAY _trigger_names LOOP EXECUTE format('DROP TRIGGER "%s" ON %s', _trigger, _class); END LOOP;
		EXECUTE format('CREATE TRIGGER %I AFTER INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_update_relations(%L,%L,%L)', _new_name, _class, _attr, _otherclass, CASE WHEN _args[3] = 'IdObj1' THEN 'DIRECT' ELSE 'INVERSE' END);
	END LOOP;
	FOR _class, _trigger_names, _args IN 
			WITH _triggers AS (SELECT tgrelid, tgname, _cm3_trigger_utils_tgargs_to_string_array(tgargs) args from pg_trigger join pg_proc on pg_proc.oid = pg_trigger.tgfoid where proname = '_cm_trigger_update_reference')
			SELECT tgrelid::regclass, array_agg(tgname), args FROM _triggers WHERE tgrelid::regclass IN (SELECT _cm3_domain_list()) GROUP BY tgrelid, args LOOP
		_otherclass = _args[2]::regclass;
		_attr = _args[1];
        _new_name = format('_cm3_rel_update_refs_%s_%s', _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_otherclass)), _cm3_utils_shrink_name(_attr));
		RAISE NOTICE 'upgrade _cm_trigger_update_reference for relation = % with names = % args = %, new name = %', _class, _trigger_names, _args, _new_name;
		FOREACH _trigger IN ARRAY _trigger_names LOOP EXECUTE format('DROP TRIGGER "%s" ON %s', _trigger, _class); END LOOP;
		EXECUTE format('CREATE TRIGGER %I AFTER INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_relation_update_references(%L,%L,%L)',
			_new_name, _class, _otherclass, _attr, CASE WHEN _args[3] = 'IdObj1' THEN 'direct' ELSE 'inverse' END);
	END LOOP;
END $$ LANGUAGE PLPGSQL;

