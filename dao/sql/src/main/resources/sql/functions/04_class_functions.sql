-- system functions for class edit
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- MISC UTILS ---

CREATE OR REPLACE FUNCTION _cm3_class_triggers_disable(_class regclass) RETURNS void AS $$ DECLARE
    _trigger varchar;
    _context jsonb;
	_subclass regclass;
BEGIN
	IF _cm3_class_is_superclass(_class) THEN
		FOR _subclass IN SELECT _cm3_class_list_descendant_classes_and_self_not_superclass(_class) LOOP
			PERFORM _cm3_class_triggers_disable(_subclass);
		END LOOP;
	ELSE
        BEGIN
            _context = _cm3_utils_first_not_blank(current_setting('cmdbuild.utils.triggers.disabled'), '{}')::jsonb;
        EXCEPTION WHEN undefined_object THEN
            _context = '{}';
        END;
        FOR _trigger IN SELECT tgname FROM pg_trigger WHERE tgrelid = _class AND tgisinternal = FALSE AND tgenabled <> 'D' LOOP
            EXECUTE format('ALTER TABLE %s DISABLE TRIGGER %I', _class, _trigger);
           _context = _context || jsonb_build_object(_class::varchar, (COALESCE(_context->_class::varchar, '[]'::jsonb)) || to_jsonb(_trigger));
        END LOOP;
        PERFORM set_config('cmdbuild.utils.triggers.disabled', _context::varchar, FALSE);
		IF _cm3_class_has_history(_class) THEN
            PERFORM _cm3_class_triggers_disable(_cm3_utils_regclass_to_history(_class));
		END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_triggers_enable(_class regclass) RETURNS void AS $$ DECLARE
    _trigger varchar;
	_subclass regclass;
BEGIN
	IF _cm3_class_is_superclass(_class) THEN
		FOR _subclass IN SELECT _cm3_class_list_descendant_classes_and_self_not_superclass(_class) LOOP
			PERFORM _cm3_class_triggers_enable(_subclass);
		END LOOP;
	ELSE
        FOR _trigger IN SELECT * FROM jsonb_array_elements_text(current_setting('cmdbuild.utils.triggers.disabled')::jsonb->_class::varchar) LOOP
            EXECUTE format('ALTER TABLE %s ENABLE TRIGGER %I', _class, _trigger);
        END LOOP;
        PERFORM set_config('cmdbuild.utils.triggers.disabled', (current_setting('cmdbuild.utils.triggers.disabled')::jsonb - _class::varchar)::varchar, FALSE);
		IF _cm3_class_has_history(_class) THEN
            PERFORM _cm3_class_triggers_enable(_cm3_utils_regclass_to_history(_class));
		END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;


--- CLASS UTILS FOR MODIFICATION ---

CREATE OR REPLACE FUNCTION _cm3_class_utils_copy_superclass_attribute_features(_class regclass,_parent_class regclass) RETURNS void AS $$ DECLARE
	_attr varchar;
BEGIN
	FOR _attr IN SELECT * FROM _cm3_attribute_list(_parent_class) LOOP
		PERFORM _cm3_attribute_features_set(_class, _attr, _cm3_attribute_features_get(_parent_class, _attr));
	END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_class_trigger_copy(_source_class regclass, _target_class regclass) RETURNS void AS $$ DECLARE
	_trigger record;
BEGIN
	FOR _trigger IN SELECT * FROM _cm3_class_triggers_list_detailed(_source_class)
		WHERE trigger_function IN ('_cm3_trigger_card_enforce_foreign_key_for_target', '_cm3_trigger_card_enforce_foreign_key_for_source', '_cm3_trigger_card_update_relations')
	LOOP
		RAISE NOTICE 'copy trigger = % from class = % to class = %', _trigger.trigger_name, _source_class, _target_class;
		EXECUTE format('CREATE TRIGGER %I %s ON %s FOR EACH %s EXECUTE PROCEDURE %I(%s)',
			_trigger.trigger_name, _trigger.trigger_when, _target_class, _trigger.trigger_for_each, _trigger.trigger_function, (SELECT string_agg(quote_ident(x),',') FROM unnest(_trigger.trigger_params) x));
	END LOOP;
END $$ LANGUAGE PLPGSQL;


--- CLASS ATTRIBUTE GROUPS ---

CREATE OR REPLACE FUNCTION _cm3_class_utils_copy_superclass_attribute_groups(_class regclass, _parent_class regclass) RETURNS void AS $$ BEGIN --TODO improve this (multiple function version)
    -- attr group not handled in this version
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_utils_attribute_groups_fix(_class regclass, _attr varchar) RETURNS void AS $$ BEGIN --TODO improve this (multiple function version)
    -- attr group not handled in this version
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.1.0-01_attribute_groups

CREATE OR REPLACE FUNCTION _cm3_class_utils_copy_superclass_attribute_groups(_class regclass, _parent_class regclass) RETURNS void AS $$ DECLARE
    _record record;
BEGIN
    RAISE NOTICE 'copy attribute groups from class = % to class = %', _parent_class, _class;
    FOR _record IN SELECT "Code", "Description", "Index" FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _parent_class LOOP
        RAISE NOTICE 'copy attribute group = % from class = % to class = %', _record."Code", _parent_class, _class;
        INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner") VALUES (_record."Code", _record."Description", _record."Index", _class);
    END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_utils_attribute_groups_fix(_class regclass, _attr varchar) RETURNS void AS $$ DECLARE --TODO improve this (multiple function version)
    _group varchar;
    _super_class regclass;
BEGIN
    _group = _cm3_attribute_features_get(_class, _attr, 'GROUP');
    IF _group <> '' AND NOT EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Code" = _group AND "Owner" = _class AND "Status" = 'A') THEN
        FOR _super_class IN WITH q AS (SELECT c,row_number() over() rn FROM _cm3_class_list_ancestors(_class) c) SELECT c FROM q ORDER BY rn DESC LOOP
            IF EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Code" = _group AND "Owner" = _super_class AND "Status" = 'A') THEN
                RAISE NOTICE 'copy attribute group = % from class = % to class = %', _group, _super_class, _class;
                INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner")
                    SELECT "Code", "Description", COALESCE((SELECT MAX("Index")+1 FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _class), 1), _class
                    FROM "_AttributeGroup" WHERE "Code" = _group AND "Owner" = _super_class AND "Status" = 'A';
                RETURN;
            END IF;
        END LOOP;
        RAISE NOTICE 'auto create attribute group = % for class = %', _group, _class;
        INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner") SELECT _group, _group, COALESCE((SELECT MAX("Index")+1 FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _class), 1), _class;
    END IF;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-29_class_structure
CREATE OR REPLACE FUNCTION _cm3_class_utils_copy_superclass_attribute_groups(_class regclass, _parent_class regclass) RETURNS void AS $$ DECLARE
    _record record;
BEGIN
    RAISE NOTICE 'copy attribute groups from class = % to class = %', _parent_class, _class;
    FOR _record IN SELECT "Code", "Description", "Index" FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _cm3_utils_regclass_to_name(_parent_class) LOOP
        RAISE NOTICE 'copy attribute group = % from class = % to class = %', _record."Code", _parent_class, _class;
        INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner") VALUES (_record."Code", _record."Description", _record."Index", _cm3_utils_regclass_to_name(_class));
    END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_utils_attribute_groups_fix(_class regclass, _attr varchar) RETURNS void AS $$ DECLARE --TODO improve this (multiple function version)
    _group varchar;
    _super_class regclass;
BEGIN
    _group = _cm3_attribute_features_get(_class, _attr, 'GROUP');
    IF _group <> '' AND NOT EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Code" = _group AND "Owner" = _cm3_utils_regclass_to_name(_class) AND "Status" = 'A') THEN
        FOR _super_class IN WITH q AS (SELECT c,row_number() over() rn FROM _cm3_class_list_ancestors(_class) c) SELECT c FROM q ORDER BY rn DESC LOOP
            IF EXISTS (SELECT * FROM "_AttributeGroup" WHERE "Code" = _group AND "Owner" = _cm3_utils_regclass_to_name(_super_class) AND "Status" = 'A') THEN
                RAISE NOTICE 'copy attribute group = % from class = % to class = %', _group, _super_class, _class;
                INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner")
                    SELECT "Code", "Description", COALESCE((SELECT MAX("Index")+1 FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _cm3_utils_regclass_to_name(_class)), 1), _cm3_utils_regclass_to_name(_class)
                    FROM "_AttributeGroup" WHERE "Code" = _group AND "Owner" = _cm3_utils_regclass_to_name(_super_class) AND "Status" = 'A';
                RETURN;
            END IF;
        END LOOP;
        RAISE NOTICE 'auto create attribute group = % for class = %', _group, _class;
        INSERT INTO "_AttributeGroup" ("Code", "Description", "Index", "Owner") SELECT _group, _group, COALESCE((SELECT MAX("Index")+1 FROM "_AttributeGroup" WHERE "Status" = 'A' AND "Owner" = _cm3_utils_regclass_to_name(_class)), 1), _cm3_utils_regclass_to_name(_class);
    END IF;
END $$ LANGUAGE PLPGSQL;
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- CLASS ATTRIBUTE UTILS FOR MODIFICATION ---

CREATE OR REPLACE FUNCTION _cm3_attribute_unique_set(_class regclass, _attr varchar, _unique boolean) RETURNS VOID AS $$ DECLARE
	c1 int;
	c2 int;
BEGIN
	IF _cm3_attribute_unique_get(_class, _attr) <> _unique THEN
		IF _unique THEN
			IF _cm3_class_is_simple(_class) THEN
				EXECUTE format('SELECT COUNT(DISTINCT %I) FROM %s', _attr, _class) INTO c1;
				EXECUTE format('SELECT COUNT(%I) FROM %s', _attr, _class) INTO c2;
			ELSEIF _cm3_class_is_superclass(_class) THEN
                c1 = 0;
                c2 = 0;
            ELSE
				EXECUTE format('SELECT COUNT(DISTINCT %I) FROM %s WHERE "Status" = ''A''', _attr, _class) INTO c1;
				EXECUTE format('SELECT COUNT(%I) FROM %s WHERE "Status" = ''A''', _attr, _class) INTO c2;
			END IF;
			IF c1 <> c2 THEN
				RAISE EXCEPTION 'CMO 200: error setting unique flag on attribute %.%: there are active cards with non unique values for this attribute', _class, _attr;
			END IF;
			IF _cm3_class_is_simple(_class) THEN
                PERFORM _cm3_attribute_index_delete(_class, _attr);
				EXECUTE format('ALTER TABLE %s ADD UNIQUE (%I)', _class, _attr);
			ELSE
				PERFORM _cm3_attribute_index_unique_create(_class, _attr);
			END IF;
		ELSE
            PERFORM _cm3_attribute_index_delete(_class, _attr);
			IF _cm3_class_is_simple(_class) THEN
				EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "%s_%s_key"', _class, _cm3_utils_regclass_to_name(_class), _attr);
			END IF;
		END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_unique_set(_class regclass, _attr varchar) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_attribute_unique_set(_class, _attr, TRUE);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_notnull_set(_class regclass, _attr varchar, _notnull boolean) RETURNS VOID AS $$ DECLARE
	_hasnull_any boolean;
	_hasnull_active boolean;
    _default varchar;
BEGIN
	IF _notnull <> _cm3_attribute_notnull_get(_class, _attr) THEN
		IF _notnull THEN
            _default = _cm3_attribute_default_get(_class, _attr);
            IF _default IS NOT NULL THEN
                IF _cm3_class_is_simple(_class) THEN
                    EXECUTE format('UPDATE %s SET %I = %s WHERE %I IS NULL', _class, _attr, _default, _attr);
                ELSE
                    EXECUTE format('UPDATE %s SET %I = %s WHERE "Status" = ''A'' AND %I IS NULL', _class, _attr, _default, _attr);
                END IF;
            END IF;
            IF _cm3_class_is_superclass(_class) THEN
                _hasnull_any = false;
            ELSE
                EXECUTE format('SELECT EXISTS(SELECT * FROM %s WHERE %I IS NULL)', _class, _attr) INTO _hasnull_any;
                IF _cm3_class_is_simple(_class) THEN
                    _hasnull_active = _hasnull_any;
                ELSE
                    EXECUTE format('SELECT EXISTS(SELECT * FROM %s WHERE %I IS NULL AND "Status" = ''A'')', _class, _attr) INTO _hasnull_active;
                END IF;
                IF _hasnull_active THEN
                    RAISE EXCEPTION 'CMO 200: error setting notnull flag on attribute %.%: there are active cards with null values for this attribute', _class, _attr;
                END IF;
			END IF;
 			IF _cm3_class_is_simple(_class) OR NOT _hasnull_any THEN
				EXECUTE format('ALTER TABLE %s ALTER COLUMN %I SET NOT NULL', _class, _attr);
			ELSE
				EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_%s_notnull" CHECK ("Status"<>''A'' OR %I IS NOT NULL)', _class, _attr, _attr);
			END IF;
		ELSE
			EXECUTE format('ALTER TABLE %s ALTER COLUMN %I DROP NOT NULL', _class, _attr);
			EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "_cm3_%s_notnull"', _class, _attr);
		END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-49b_domain_attribute_notnull_update
CREATE OR REPLACE FUNCTION _cm3_attribute_notnull_set(_class regclass, _attr varchar, _notnull boolean) RETURNS VOID AS $$ DECLARE
	_hasnull_any boolean;
	_hasnull_active boolean;
    _default varchar;
BEGIN
	IF _notnull <> _cm3_attribute_notnull_get(_class, _attr) THEN
            IF _notnull THEN
                _default = _cm3_attribute_default_get(_class, _attr);
                IF _default IS NOT NULL THEN
                    IF _cm3_class_is_simple(_class) THEN
                        EXECUTE format('UPDATE %s SET %I = %s WHERE %I IS NULL', _class, _attr, _default, _attr);
                    ELSE
                        EXECUTE format('UPDATE %s SET %I = %s WHERE "Status" = ''A'' AND %I IS NULL', _class, _attr, _default, _attr);
                    END IF;
                END IF;
            IF _cm3_class_is_superclass(_class) THEN
                _hasnull_any = false;
            ELSE
                EXECUTE format('SELECT EXISTS(SELECT * FROM %s WHERE %I IS NULL)', _class, _attr) INTO _hasnull_any;
                IF _cm3_class_is_simple(_class) THEN
                    _hasnull_active = _hasnull_any;
                ELSE
                    EXECUTE format('SELECT EXISTS(SELECT * FROM %s WHERE %I IS NULL AND "Status" = ''A'')', _class, _attr) INTO _hasnull_active;
                END IF;
                IF _hasnull_active THEN
                    RAISE EXCEPTION 'CMO 200: error setting notnull flag on attribute %.%: there are active cards with null values for this attribute', _class, _attr;
                END IF;
            END IF;
            IF _cm3_class_is_domain(_class) THEN
                EXECUTE format('DROP TRIGGER IF EXISTS _cm3_%s_notnull_trigger ON %s', _attr, _class);
                EXECUTE format('CREATE TRIGGER _cm3_%s_notnull_trigger
					AFTER INSERT OR UPDATE
					ON %s
					FOR EACH ROW
					EXECUTE PROCEDURE _cm3_domain_attribute_notnull(''%s'')', _attr, _class, _attr);
            ELSE
                IF _cm3_class_is_simple(_class) THEN
                    EXECUTE format('ALTER TABLE %s ALTER COLUMN %I SET NOT NULL', _class, _attr);
                ELSE
                    EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_%s_notnull" CHECK ("Status"<>''A'' OR %I IS NOT NULL)', _class, _attr, _attr);
                END IF;
            END IF;
            ELSE
                EXECUTE format('ALTER TABLE %s ALTER COLUMN %I DROP NOT NULL', _class, _attr);
                EXECUTE format('ALTER TABLE %s DROP CONSTRAINT IF EXISTS "_cm3_%s_notnull"', _class, _attr);
                EXECUTE format('DROP TRIGGER IF EXISTS _cm3_%s_notnull_trigger ON %s', _attr, _class);
            END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.0.0-03a_system_functions
CREATE OR REPLACE FUNCTION _cm3_attribute_notnull_set(_class regclass, _attr varchar) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_attribute_notnull_set(_class, _attr, TRUE);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_default_set( _class regclass,	_attr varchar, _value varchar, _update_records boolean) RETURNS void AS $$ DECLARE
	_current_value varchar;
	_attr_sqltype varchar;
	_trigger_enabled boolean;
BEGIN
	_value = NULLIF(TRIM(_value), '');
	_attr_sqltype = LOWER(_cm3_attribute_sqltype_get(_class, _attr));
	IF _value IS NOT NULL AND TRIM(_value) !~ '[0-9]+|''.*''|.+[(].*[)]|.*::[a-zA-Z0-9]+' THEN
		_value = quote_literal(_value);
	END IF;
	_current_value = _cm3_attribute_default_get(_class, _attr);
    IF _value IS DISTINCT FROM _current_value THEN
    	IF _value IS NULL THEN
			EXECUTE format('ALTER TABLE %s ALTER COLUMN	%I DROP DEFAULT', _class, _attr);
	    ELSE
	        EXECUTE format('ALTER TABLE %s ALTER COLUMN %I SET DEFAULT %s', _class, _attr, _value);
			IF _update_records THEN
				_trigger_enabled = _cm3_class_triggers_are_enabled(_class);
				IF _trigger_enabled THEN
					PERFORM _cm3_class_triggers_disable(_class);
				END IF;
	        	EXECUTE format('UPDATE %s SET %I = %s', _class, _attr, _value);
				IF _trigger_enabled THEN
					PERFORM _cm3_class_triggers_enable(_class);
				END IF;
	        END IF;
		END IF;
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_default_set( _class regclass,	_attr varchar, _value varchar) RETURNS void AS $$ BEGIN
    PERFORM _cm3_attribute_default_set(_class, _attr, _value, FALSE);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_validate_features_and_type(_comment jsonb, _type text) RETURNS VOID AS $$ BEGIN
	IF (SELECT EXISTS (SELECT * FROM jsonb_each_text(_comment) WHERE key IN ('REFERENCEDOM','LOOKUP','FKTARGETCLASS') AND coalesce(value,'') <> '' )) AND _type NOT IN ('int8','_int8','bigint','varchar','int8[]','bigint[]','varchar[]') THEN
		RAISE EXCEPTION 'CM: attribute type error: this attribute should have sql type int8/bigint, has type % instead', _type;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_index_create(_class regclass, _unique boolean, VARIADIC _attrs varchar[]) RETURNS void AS $$ DECLARE
	_indexexpr varchar;
	_indexname varchar;
	_whereexpr varchar;
BEGIN
	_indexname = format('_cm3_%s_%s',
		_cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)),
		_cm3_utils_shrink_name((WITH _attr_names AS (SELECT unnest(_attrs) AS _attr_name ORDER BY _attr_name) SELECT string_agg(_attr_name, '_') from _attr_names)));
	_indexexpr = CASE WHEN _unique THEN 'UNIQUE INDEX' ELSE 'INDEX' END;
	_whereexpr = CASE WHEN _cm3_class_has_history(_class) THEN ' WHERE "Status" = ''A''' ELSE '' END;
-- 	IF _unique THEN
-- 		IF _cm3_class_is_domain(_class) THEN
-- 			IF _cm3_class_comment_get(_class, 'CARDIN') = '1:N' THEN
-- 				_attrs = _attrs || ARRAY['IdClass2', 'IdObj2'];
-- 			ELSEIF _cm3_class_comment_get(_class, 'CARDIN') = 'N:1' THEN
-- 				_attrs = _attrs || ARRAY['IdClass1', 'IdObj1'];
-- 			ELSE
-- 				RAISE EXCEPTION 'cannot create unique index on attributes = % for domain = % with cardinality = %', _attrs, _class, _cm3_class_comment_get(_class,'CARDIN');
-- 			END IF;
-- 		END IF;
-- 	END IF;
	IF array_length(_attrs, 1) = 0 THEN
		RAISE EXCEPTION 'missing attrs value';
	ELSEIF array_length(_attrs, 1) = 1 THEN
		EXECUTE format('DROP INDEX IF EXISTS %I.%I', (SELECT nspname FROM pg_catalog.pg_class AS c JOIN pg_catalog.pg_namespace AS ns ON c.relnamespace = ns.oid WHERE c.oid = _class::oid), _indexname);
		EXECUTE format('CREATE %s %I ON %s (%I)%s', _indexexpr, _indexname, _class, _attrs[1], _whereexpr);
	ELSE
		EXECUTE format('DROP INDEX IF EXISTS %I.%I', (SELECT nspname FROM pg_catalog.pg_class AS c JOIN pg_catalog.pg_namespace AS ns ON c.relnamespace = ns.oid WHERE c.oid = _class::oid), _indexname);
		EXECUTE format('CREATE %s %I ON %s (%s)%s', _indexexpr, _indexname, _class, (SELECT string_agg(format('%I', x), ',') from unnest(_attrs) x), _whereexpr);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_index_delete(_class regclass, VARIADIC _attrs varchar[]) RETURNS void AS $$ DECLARE
	_indexname varchar;
BEGIN
	_indexname = format('_cm3_%s_%s',
		_cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)),
		_cm3_utils_shrink_name((WITH _attr_names AS (SELECT unnest(_attrs) AS _attr_name ORDER BY _attr_name) SELECT string_agg(_attr_name, '_') from _attr_names)));
    EXECUTE format('DROP INDEX IF EXISTS %I', _indexname);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_index_create(_class regclass, VARIADIC _attrs varchar[]) RETURNS void AS $$
	SELECT _cm3_attribute_index_create(_class, FALSE, VARIADIC _attrs);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_index_unique_create(_class regclass, VARIADIC _attrs varchar[]) RETURNS void AS $$
	SELECT _cm3_attribute_index_create(_class, TRUE, VARIADIC _attrs);
$$ LANGUAGE SQL;


--- CLASS MODIFY ---

-- REQUIRE PATCH 3.2.0-51_simpleclass_improvements

CREATE OR REPLACE FUNCTION _cm3_class_create(_class_name varchar, _class_parent regclass, _features jsonb) RETURNS regclass AS $$ DECLARE
	_class_type varchar;
	_class_is_simple boolean;
	_class_is_standard boolean;
	_class_is_superclass boolean;
	_class regclass;
	_history regclass;
BEGIN

	_class_type = coalesce(_features->>'TYPE', 'class');
	_features = _features || jsonb_build_object('TYPE', _class_type);

	IF _class_type !~ '^(simpleclass|class)$' THEN RAISE EXCEPTION 'CM: invalid class type = "%"', _class_type; END IF;

	_class_is_simple = _class_type = 'simpleclass';
	_class_is_standard = _class_type = 'class';

	IF _class_is_simple THEN
		IF _class_parent IS NOT NULL THEN RAISE EXCEPTION 'CM: cannot create simple class with a parent'; END IF;
		_class_parent = '"SimpleClass"'::regclass;
	END IF;

	IF _class_is_standard THEN
		_class_parent = coalesce(_class_parent, '"Class"'::regclass);
		IF NOT _cm3_class_is_superclass(_class_parent) THEN RAISE EXCEPTION 'CM: cannot extend class = % (it is not a superclass)', _class_parent; END IF;
	END IF;

    PERFORM _cm3_event_post('class_before_create', _class_name);

	EXECUTE format('CREATE TABLE %s (CONSTRAINT "%s_pkey" PRIMARY KEY ("Id")) INHERITS (%s)', _cm3_utils_name_escape(_class_name), _cm3_utils_name_to_basename(_class_name), _class_parent);

	_class = _cm3_utils_name_to_regclass(_class_name);

    PERFORM _cm3_class_features_set(_class, _features);
	EXECUTE format('COMMENT ON COLUMN %s."Id" IS ''MODE: reserved''', _class);

	_class_is_superclass = _cm3_class_is_superclass(_class);

	RAISE NOTICE 'create class = % parent = % features = % ', _class, _class_parent, _features;

	PERFORM _cm3_class_utils_copy_superclass_attribute_groups(_class, _class_parent);

	PERFORM _cm3_class_utils_copy_superclass_attribute_features(_class, _class_parent);

	IF _class_is_simple THEN
		EXECUTE format('CREATE TRIGGER "_cm3_card_prepare_record" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_prepare_record()', _class);
		EXECUTE format('CREATE TRIGGER "_cm3_card_cascade_delete_on_relations" AFTER DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_cascade_delete_on_relations()', _class);
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_Status_check" CHECK ("Status" = ''A'')', _class);
		PERFORM _cm3_attribute_index_create(_class, 'BeginDate');
	END IF;

	IF _class_is_standard THEN
		PERFORM _cm3_utils_class_trigger_copy(_class_parent, _class);
	END IF;

	IF _class_is_simple OR ( _class_is_standard AND NOT _class_is_superclass ) THEN
        EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdClass" SET DEFAULT %L', _class, _class);
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_IdClass_check" CHECK ( "IdClass" = %L::regclass )', _class, _class);
	END IF;

	IF _class_is_standard AND NOT _class_is_superclass THEN
		EXECUTE format('CREATE TRIGGER "_cm3_card_prepare_record" BEFORE INSERT OR UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_prepare_record()', _class);
		EXECUTE format('CREATE TRIGGER "_cm3_card_create_history" AFTER UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_create_history()', _class);
		EXECUTE format('CREATE TRIGGER "_cm3_card_cascade_delete_on_relations" AFTER UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_cascade_delete_on_relations()', _class);
		PERFORM _cm3_attribute_index_create(_class, 'Code');
		PERFORM _cm3_attribute_index_create(_class, 'Description');

		EXECUTE format('CREATE TABLE "%s_history" ( PRIMARY KEY ("Id"), CONSTRAINT "_cm3_CurrentId_foreign_key" FOREIGN KEY ("CurrentId") REFERENCES %s ("Id"), CONSTRAINT "_cm3_Status_check" CHECK ("Status" = ''U'') ) INHERITS (%s)',
			_class_name, _class, _class);
		_history = _cm3_utils_regclass_to_history(_class);
		PERFORM _cm3_attribute_index_create(_history, 'CurrentId');

        EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdClass" SET DEFAULT %L', _cm3_utils_regclass_to_history(_class), _class);
	END IF;

	IF _class_is_superclass THEN
		EXECUTE format('CREATE TRIGGER "_cm3_superclass_forbid_operations" BEFORE INSERT OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_superclass_forbid_operations()', _class);
	END IF;

	PERFORM _cm3_multitenant_mode_change(_class, NULL, _features->>'MTMODE');

    PERFORM _cm3_event_post('class_after_create', _class_name);

	RETURN _class;

END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_class_create(_class_name varchar, _class_parent regclass) RETURNS regclass AS $$ BEGIN
	RETURN _cm3_class_create(_class_name, _class_parent, '{}');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_create(_class_name varchar, _class_parent regclass, _comment varchar) RETURNS regclass AS $$ BEGIN
	RETURN _cm3_class_create(_class_name, _class_parent, _cm3_comment_to_jsonb(_comment));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_create(_class_name varchar, _features jsonb) RETURNS regclass AS $$ BEGIN
	RETURN _cm3_class_create(_class_name, _cm3_utils_name_to_regclass(_features->>'PARENT'), _features - 'PARENT');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_create(_class_name varchar, _features varchar) RETURNS regclass AS $$ BEGIN
	RETURN _cm3_class_create(_class_name, _cm3_comment_to_jsonb(_features));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_create(_class_name varchar) RETURNS regclass AS $$ BEGIN
    IF _class_name ~ '.*NAME:.*' THEN
        RETURN _cm3_class_create(_cm3_comment_to_jsonb(_class_name)->>'NAME', _cm3_comment_to_jsonb(_class_name) - 'NAME');
    ELSE
        RETURN _cm3_class_create(_class_name, '{}'::jsonb);
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_modify(_class regclass, _features varchar) RETURNS void AS $$ BEGIN
	PERFORM _cm3_class_modify(_class, _cm3_comment_to_jsonb(_features));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_modify(_class regclass, _features jsonb) RETURNS void AS $$ DECLARE
	_current_features jsonb;
BEGIN

    PERFORM _cm3_event_post('class_before_modify', _cm3_utils_regclass_to_name(_class));

	_current_features = _cm3_class_features_get(_class);

	IF _current_features->>'SUPERCLASS' <> _features->>'SUPERCLASS' THEN
		RAISE EXCEPTION 'CM: unable to change SUPERCLASS status of existing class: operation not allowed';
	END IF;

	IF _current_features->>'TYPE' <> _features->>'TYPE' THEN
		RAISE EXCEPTION 'CM: unable to change TYPE of existing class: operation not allowed';
	END IF;

	PERFORM _cm3_class_features_set(_class, _features);
	PERFORM _cm3_multitenant_mode_change(_class, _current_features->>'MTMODE', _features->>'MTMODE');

    PERFORM _cm3_event_post('class_after_modify', _cm3_utils_regclass_to_name(_class));

END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_delete(_class regclass) RETURNS void AS $$ DECLARE
    _record record;
    _found boolean;
    _class_name varchar = _cm3_utils_regclass_to_name(_class);
BEGIN
	IF EXISTS (SELECT d FROM _cm3_domain_list() d WHERE _cm3_class_comment_get(d,'CLASS1') = _cm3_utils_regclass_to_name(_class) OR  _cm3_class_comment_get(d,'CLASS2') = _cm3_utils_regclass_to_name(_class)) THEN
		RAISE EXCEPTION 'CM: cannot delete class = %: class has domains', _class;
	ELSEIF _cm3_class_has_descendants(_class) THEN
		RAISE EXCEPTION 'CM: cannot delete class = %: class has descendants', _class;
	ELSEIF _cm3_class_has_records(_class) THEN
		RAISE EXCEPTION 'CM: cannot delete class = %: class has cards', _class;
    ELSEIF EXISTS ( SELECT * FROM _cm3_attribute_list_detailed() a WHERE a.owner <> _class AND a.comment->>'FKTARGETCLASS' = _cm3_utils_regclass_to_name(_class) ) THEN
        RAISE EXCEPTION 'CM: cannot delete class = %: class is foreignkey target', _class;
	END IF;
    PERFORM _cm3_event_post('class_before_delete', _class_name);
	PERFORM _cm3_attribute_delete_all(_class);
    UPDATE "_AttributeGroup" SET "Status" = 'N' WHERE "Owner" = _class AND "Status" = 'A';
	UPDATE "_AttributeMetadata" SET "Status" = 'N' WHERE "Owner" = _class AND "Status" = 'A';
	UPDATE "_ClassMetadata" SET "Status" = 'N' WHERE "Owner" = _class AND "Status" = 'A';
	UPDATE "_Grant" SET "Status" = 'N' WHERE "ObjectClass" = _class AND "Status" = 'A';
    --TODO cascade gis delete (??)
    FOR _record IN SELECT * FROM _cm3_attribute_list_detailed() a WHERE a.owner <> _class AND a.sql_type = 'regclass' AND a.name <> 'IdClass' LOOP
        IF _cm3_class_has_history(_record.owner) THEN
            EXECUTE format('SELECT EXISTS ( SELECT * FROM %s WHERE %I = %L::regclass AND "Status" = ''A'' )', _record.owner, _record.name, _class) INTO _found;
        ELSE
            EXECUTE format('SELECT EXISTS ( SELECT * FROM %s WHERE %I = %L::regclass )', _record.owner, _record.name, _class) INTO _found;
        END IF;
        IF _found THEN
            RAISE EXCEPTION 'CM: cannot delete class = %: class oid is referenced from record[s] of class = % attr = %', _class, _record.owner, _record.name;
        END IF;
    END LOOP;
    FOR _record IN WITH q AS (SELECT * FROM _cm3_attribute_list_detailed() a WHERE a.owner <> _class AND a.sql_type = 'jsonb' AND _cm3_utils_is_not_blank(a.comment->>'CLASSREFPATHS')) SELECT owner, name, regexp_split_to_table(comment->>'CLASSREFPATHS',',') path FROM q LOOP
        EXECUTE format('SELECT EXISTS ( SELECT * FROM %s WHERE %I->>%L = %L AND "Status" = ''A'' )', _record.owner, _record.name, _record.path, _cm3_utils_regclass_to_name(_class)) INTO _found;
        IF _found THEN
            RAISE EXCEPTION 'CM: cannot delete class = %: class oid is referenced from record[s] of class = % attr = %', _class, _record.owner, _record.name;
        END IF;
    END LOOP;
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" IN ('process','class') AND "Status" = 'A' AND "ObjectClass" = _class LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    UPDATE "_Menu" SET "Data" = _cm3_utils_menu_from_list((SELECT jsonb_agg(x) FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x WHERE NOT (x->>'type' IN ('class','process') AND x->>'target' = _class_name))) WHERE "Status" = 'A' AND EXISTS (SELECT x FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
        WHERE x->>'type' IN ('class','process') AND x->>'target' = _class_name);
	IF _cm3_class_has_history(_class) THEN
		EXECUTE format('DROP TABLE %s', _cm3_utils_regclass_to_history(_class));
	END IF;
	EXECUTE format('DROP TABLE %s', _class);
    PERFORM _cm3_event_post('class_after_delete', _class_name);
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-29_class_structure
CREATE OR REPLACE FUNCTION _cm3_class_delete(_class regclass) RETURNS void AS $$ DECLARE
    _record record;
    _found boolean;
    _class_name varchar = _cm3_utils_regclass_to_name(_class);
BEGIN
	IF EXISTS (SELECT d FROM _cm3_domain_list() d WHERE _cm3_class_comment_get(d,'CLASS1') = _cm3_utils_regclass_to_name(_class) OR  _cm3_class_comment_get(d,'CLASS2') = _cm3_utils_regclass_to_name(_class)) THEN
		RAISE EXCEPTION 'CM: cannot delete class = %: class has domains', _class;
	ELSEIF _cm3_class_has_descendants(_class) THEN
		RAISE EXCEPTION 'CM: cannot delete class = %: class has descendants', _class;
	ELSEIF _cm3_class_has_records(_class) THEN
		RAISE EXCEPTION 'CM: cannot delete class = %: class has cards', _class;
    ELSEIF EXISTS ( SELECT * FROM _cm3_attribute_list_detailed() a WHERE a.owner <> _class AND a.comment->>'FKTARGETCLASS' = _cm3_utils_regclass_to_name(_class) ) THEN
        RAISE EXCEPTION 'CM: cannot delete class = %: class is foreignkey target', _class;
	END IF;
    PERFORM _cm3_event_post('class_before_delete', _class_name);
	PERFORM _cm3_attribute_delete_all(_class);
    UPDATE "_AttributeGroup" SET "Status" = 'N' WHERE "Owner" = _class_name AND "Status" = 'A';
	UPDATE "_AttributeMetadata" SET "Status" = 'N' WHERE "Owner" = _class_name AND "Status" = 'A';
	UPDATE "_ClassMetadata" SET "Status" = 'N' WHERE "Code" = _class_name AND "Status" = 'A';
	UPDATE "_Grant" SET "Status" = 'N' WHERE "ObjectClass" = _class AND "Status" = 'A';
	UPDATE "_Filter" SET "Status" = 'N' WHERE "OwnerType" = 'class' AND "Owner" = _cm3_utils_regclass_to_name(_class) AND "Status" = 'A'; --TODO check this ??
    --TODO cascade gis delete (??)
    FOR _record IN SELECT * FROM _cm3_attribute_list_detailed() a WHERE a.owner <> _class AND a.sql_type = 'regclass' AND a.name <> 'IdClass' LOOP
        IF _cm3_class_has_history(_record.owner) THEN
            EXECUTE format('SELECT EXISTS ( SELECT * FROM %s WHERE %I = %L::regclass AND "Status" = ''A'' )', _record.owner, _record.name, _class) INTO _found;
        ELSE
            EXECUTE format('SELECT EXISTS ( SELECT * FROM %s WHERE %I = %L::regclass )', _record.owner, _record.name, _class) INTO _found;
        END IF;
        IF _found THEN
            RAISE EXCEPTION 'CM: cannot delete class = %: class oid is referenced from record[s] of class = % attr = %', _class, _record.owner, _record.name;
        END IF;
    END LOOP;
    FOR _record IN WITH q AS (SELECT * FROM _cm3_attribute_list_detailed() a WHERE a.owner <> _class AND a.sql_type = 'jsonb' AND _cm3_utils_is_not_blank(a.comment->>'CLASSREFPATHS')) SELECT owner, name, regexp_split_to_table(comment->>'CLASSREFPATHS',',') path FROM q LOOP
        EXECUTE format('SELECT EXISTS ( SELECT * FROM %s WHERE %I->>%L = %L AND "Status" = ''A'' )', _record.owner, _record.name, _record.path, _cm3_utils_regclass_to_name(_class)) INTO _found;
        IF _found THEN
            RAISE EXCEPTION 'CM: cannot delete class = %: class oid is referenced from record[s] of class = % attr = %', _class, _record.owner, _record.name;
        END IF;
    END LOOP;
    FOR _record IN SELECT * FROM "_Grant" WHERE "Type" IN ('process','class') AND "Status" = 'A' AND "ObjectClass" = _class LOOP
        RAISE NOTICE 'cascade drop grant record = %', _record;
        EXECUTE format('UPDATE "_Grant" SET "Status" = ''N'' WHERE "Id" = %s', _record."Id"); --TODO cache drop ??
    END LOOP;
    UPDATE "_Menu" SET "Data" = _cm3_utils_menu_from_list((SELECT jsonb_agg(x) FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x WHERE NOT (x->>'type' IN ('class','process') AND x->>'target' = _class_name))) WHERE "Status" = 'A' AND EXISTS (SELECT x FROM jsonb_array_elements(_cm3_utils_menu_to_list("Data")) x
        WHERE x->>'type' IN ('class','process') AND x->>'target' = _class_name);
	IF _cm3_class_has_history(_class) THEN
		EXECUTE format('DROP TABLE %s', _cm3_utils_regclass_to_history(_class));
	END IF;
	EXECUTE format('DROP TABLE %s', _class);
    PERFORM _cm3_event_post('class_after_delete', _class_name);
END $$ LANGUAGE PLPGSQL;
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- ATTRIBUTE MODIFY ---

CREATE OR REPLACE FUNCTION _cm3_attribute_create(_features varchar) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_create(_cm3_comment_to_jsonb(_features));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_create(_class regclass, _attr varchar, _type varchar, _features varchar) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_create(_class, _attr, _type, _cm3_comment_to_jsonb(_features));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_create(_class regclass, _attr varchar, _features varchar) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_create(_class, _attr, _cm3_comment_to_jsonb(_features));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_create(_features jsonb) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_create(_cm3_utils_name_to_regclass(_features->>'OWNER'), _features->>'NAME', _features - 'OWNER' - 'NAME');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_create(_class regclass, _attr varchar, _features jsonb) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_create(_class, _attr, _features->>'TYPE', _features - 'TYPE');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_create(_class regclass,_attr varchar,_type varchar,_features jsonb) RETURNS void AS $$  DECLARE
	_not_null boolean;
	_unique boolean;
	_default varchar;
	_restrict_values varchar;
	_fk_target regclass;
	_sub_class regclass;
	_domain regclass;
	_direction varchar;
	_cardin varchar;
	_attr_features jsonb;
BEGIN
    RAISE NOTICE 'creating attribute for class = % name = % type = %', _cm3_utils_regclass_to_name(_class), _attr, _type;

    FOR _sub_class IN SELECT _cm3_class_list_descendant_classes(_class) LOOP
        IF _cm3_attribute_exists(_sub_class, _attr) THEN
            RAISE 'CM: cannot create attribute with name = %: this attribute already exists on subclass %', _attr, _cm3_utils_regclass_to_name(_sub_class);
        END IF;
    END LOOP;

    IF _type = 'timestamp' THEN
        _type = 'timestamp with time zone';
    END IF;

    _features = _cm3_utils_strip_null_or_empty(_features);
    -- 	IF _cm3_class_is_simple(_class) AND _attr IN (SELECT _cm3_attribute_list('"Class"')) THEN
    -- 		RAISE 'CM: cannot create attribute with name = %s: reserved name', _attr;
    -- 	END IF;

    _not_null = coalesce(_features->>'NOTNULL','false')::boolean;
    _unique = coalesce(_features->>'UNIQUE','false')::boolean;
    _default = _features->>'DEFAULT';
    _restrict_values = _features->>'VALUES';

    _attr_features = _features - 'NOTNULL' - 'UNIQUE' - 'DEFAULT' - 'VALUES';

    PERFORM _cm3_attribute_validate_features_and_type(_attr_features, _type);

    -- 	IF _cm_is_geometry_type(_type) THEN  -- TODO
    -- 		PERFORM _cm_add_spherical_mercator();
    -- 		PERFORM AddGeometryColumn(_cm_cmschema(_class), _cm_cmtable(_class), _attr, 900913, _type, 2);
    -- 	ELSE
    EXECUTE format('ALTER TABLE %s ADD COLUMN %I %s', _class, _attr, _type);
    -- 	END IF;

    PERFORM _cm3_attribute_default_set(_class, _attr, _default, TRUE);
    PERFORM _cm3_attribute_features_set(_class, _attr, _attr_features);
    PERFORM _cm3_class_utils_attribute_groups_fix(_class, _attr);
    FOR _sub_class IN SELECT _cm3_class_list_descendant_classes(_class) LOOP
        PERFORM _cm3_attribute_features_set(_sub_class, _attr, _attr_features);
        PERFORM _cm3_class_utils_attribute_groups_fix(_sub_class, _attr);
    END LOOP;
    PERFORM _cm3_attribute_notnull_set(_class, _attr, _not_null);
    PERFORM _cm3_attribute_unique_set(_class, _attr, _unique);

    IF _cm3_utils_is_not_blank(_restrict_values) THEN
        EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_%s_check" CHECK ( %I IN (%s) )', _class, _attr, _attr, (SELECT string_agg(format('%L', trim(x)),',') FROM regexp_split_to_table((_restrict_values),',') x));
    END IF;

    IF _cm3_attribute_is_foreignkey(_class, _attr) OR _cm3_attribute_is_reference(_class, _attr) THEN
        RAISE NOTICE 'attr is foreignkey or reference: activating fk triggers';
        _fk_target = _cm3_attribute_foreignkey_or_reference_target_class_get(_class, _attr);
        FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
            IF _cm3_class_is_simple(_sub_class) THEN
                EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_simplecard_enforce_foreign_key_for_source(%L,%L,%L)',
                    _cm3_utils_shrink_name_lon(_attr), _sub_class, _attr, _fk_target, _type);
            ELSE
                EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_source(%L,%L,%L)',
                    _cm3_utils_shrink_name_lon(_attr), _sub_class, _attr, _fk_target, _type);
            END IF;
        END LOOP;
    END IF;
    IF _cm3_attribute_is_foreignkey(_class, _attr) THEN
        _fk_target = _cm3_attribute_foreignkey_or_reference_target_class_get(_class, _attr);
        FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_fk_target) LOOP
            EXECUTE format('CREATE TRIGGER "_cm3_card_enforce_fk_%s_%s" BEFORE UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_enforce_foreign_key_for_target(%L,%L,%L)',
                    _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name(_attr), _sub_class, _class, _attr, _type);
        END LOOP;
    END IF;

    IF _cm3_attribute_is_reference(_class, _attr) THEN
        RAISE NOTICE 'attr is reference: activating reference triggers';
        _domain = _cm3_attribute_reference_domain_get(_class, _attr);
        _direction = _cm3_attribute_reference_direction_get(_class, _attr);
        _cardin = _cm3_class_comment_get(_domain, 'CARDIN');
        IF ( _direction = 'direct' AND _cardin NOT LIKE '%:1' ) OR ( _direction = 'inverse' AND _cardin NOT LIKE '1:%' ) THEN
            RAISE 'CM: invalid domain = % and direction = % for reference attr of class = % (domain cardin = %)', _cm3_utils_regclass_to_domain_name(_domain), _direction, _cm3_utils_regclass_to_name(_class), _cardin;
        END IF;
        IF NOT _class IN (SELECT _cm3_domain_source_classes_get(_domain, _direction)) THEN
        RAISE 'CM: invalid domain = % and direction = % for reference attr of class = % (source class is not valid for this domain/direction)', _cm3_utils_regclass_to_domain_name(_domain), _direction, _cm3_utils_regclass_to_name(_class);
        END IF;

        IF _direction = 'direct' THEN
            EXECUTE format('UPDATE %s c SET %I = d."IdObj2" FROM %s d WHERE c."Status" = ''A'' AND d."Status" = ''A'' AND c."Id" = d."IdObj1"', _class, _attr, _domain);
        ELSE
            EXECUTE format('UPDATE %s c SET %I = d."IdObj1" FROM %s d WHERE c."Status" = ''A'' AND d."Status" = ''A'' AND c."Id" = d."IdObj2"', _class, _attr, _domain);
        END IF;

        FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
            EXECUTE format('CREATE TRIGGER "_cm3_card_update_rels_%s" AFTER INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_update_relations(%L,%L,%L)',
                _cm3_utils_shrink_name_lon(_attr), _sub_class, _attr, _domain, _direction);
        END LOOP;
        EXECUTE format('CREATE TRIGGER "_cm3_rel_update_refs_%s_%s" AFTER INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_relation_update_references(%L,%L,%L)',
            _cm3_utils_shrink_name(_cm3_utils_regclass_to_name(_class)), _cm3_utils_shrink_name(_attr), _domain, _class, _attr, _direction);
    END IF;

    IF _cm3_utils_is_not_blank(_features->>'ITEMS') THEN
        IF _attr <> 'Items' THEN
            RAISE 'unsupported items attr name = %', _attr; -- unfortunately custom item attrs are not supported yet
        END IF;
        FOR _sub_class IN SELECT _cm3_class_list_descendant_classes_and_self(_class) LOOP
            EXECUTE format('CREATE TRIGGER "_cm3_card_items_%s" BEFORE INSERT OR UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_items(%L)', _cm3_utils_shrink_name_lon(_attr), _sub_class, _attr);
        END LOOP;
    END IF;

    IF _cm3_class_is_domain(_class) THEN
        PERFORM _cm3_domain_composite_index_rebuild(_class);
    END IF;

END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_modify(_features varchar) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_modify(_cm3_comment_to_jsonb(_features));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_modify(_features jsonb) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_modify(_cm3_utils_name_to_regclass(_features->>'OWNER'), _features->>'NAME', _features->>'TYPE', _features - 'OWNER' - 'NAME' - 'TYPE');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_modify(_class regclass, _attr varchar, _type varchar, _comment varchar) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_modify(_class, _attr, _type, _cm3_comment_to_jsonb(_comment));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_modify(_class regclass, _attr varchar, _type varchar, _features jsonb) RETURNS void AS $$ DECLARE
	_not_null boolean;
	_unique boolean;
	_default varchar;
	_current_features jsonb;
	_attr_features jsonb;
	_changes_in_features jsonb;
	_sub_class regclass;
BEGIN

	_not_null = coalesce(_features->>'NOTNULL', 'false')::boolean;
	_unique = coalesce(_features->>'UNIQUE', 'false')::boolean;
	_default = _features->>'DEFAULT';

	_attr_features = _features - 'NOTNULL' - 'UNIQUE' - 'DEFAULT';

	_current_features = _cm3_attribute_features_get(_class, _attr);


	WITH compare AS ( SELECT c.k comment_key, coalesce(_current_features->>c.k, '') current_value, coalesce(_attr_features->>c.k, '') new_value FROM (SELECT jsonb_object_keys(_attr_features) k UNION SELECT jsonb_object_keys(_current_features) k ) c )
	SELECT INTO _changes_in_features coalesce(jsonb_object_agg(comment_key, new_value), '{}'::jsonb) FROM compare WHERE current_value <> new_value;

--  TODO check
-- 	IF COALESCE(_cm_read_reference_domain_features(OldComment), '') IS DISTINCT FROM COALESCE(_cm_read_reference_domain_features(NewComment), '')
-- 		OR  _cm_read_reference_type_features(OldComment) IS DISTINCT FROM _cm_read_reference_type_features(NewComment)
-- 		OR  COALESCE(_cm_get_fk_target_features(OldComment), '') IS DISTINCT FROM COALESCE(_cm_get_fk_target_features(NewComment), '')
-- 	THEN
-- 		RAISE EXCEPTION 'CM_FORBIDDEN_OPERATION';
-- 	END IF;

	PERFORM _cm3_attribute_validate_features_and_type(_attr_features, _type);

	IF _cm3_attribute_sqltype_get(_class, _attr) <> trim(_type) THEN
		IF _cm3_attribute_is_inherited(_class, _attr) THEN
			RAISE 'CM: cannot alter type of attr %.%: attribute is inherited from parent class', _class, _attr;
		ELSE
			EXECUTE format('ALTER TABLE %s ALTER COLUMN %I TYPE %s', _class, _attr, _type);
		END IF;
	END IF;

    PERFORM _cm3_attribute_default_set(_class, _attr, _default, FALSE);
    PERFORM _cm3_attribute_features_set(_class, _attr, _attr_features);
    PERFORM _cm3_class_utils_attribute_groups_fix(_class, _attr);
	FOR _sub_class IN SELECT _cm3_class_list_descendant_classes(_class) LOOP
		PERFORM _cm3_attribute_features_update(_sub_class, _attr, _changes_in_features);
        PERFORM _cm3_class_utils_attribute_groups_fix(_sub_class, _attr);
	END LOOP;
	PERFORM _cm3_attribute_notnull_set(_class, _attr, _not_null);
	PERFORM _cm3_attribute_unique_set(_class, _attr, _unique);

    IF _cm3_class_is_domain(_class) THEN
        PERFORM _cm3_domain_composite_index_rebuild(_class);
    END IF;

END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-29_class_structure
CREATE OR REPLACE FUNCTION _cm3_attribute_delete(_class regclass, _attr varchar) RETURNS VOID AS $$ DECLARE
-- 	GeoType text := _cm_get_geometry_type(_class, _attr); TODO
	_fk_target regclass;
	_sub_class regclass;
	_domain regclass;
BEGIN
	IF _cm3_attribute_is_inherited(_class, _attr) THEN
		RAISE EXCEPTION 'CMO 200: cannot remove attribute %.%: attribute is inherited', _class, _attr;
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
    EXECUTE format('UPDATE "_AttributeMetadata" SET "Status"=''N'' WHERE "Owner"=''%s'' AND "Code"=''%s'' AND "Status"=''A''', _cm3_utils_regclass_to_name(_class), _attr);
-- 	END IF;
    IF _cm3_class_is_domain(_class) THEN
        PERFORM _cm3_domain_composite_index_rebuild(_class);
    END IF;
END $$ LANGUAGE PLPGSQL;
-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_attribute_delete_all(_class regclass) RETURNS void AS $$ DECLARE
	_attr varchar;
BEGIN
	FOR _attr IN SELECT a FROM _cm3_attribute_list(_class) a WHERE NOT _cm3_attribute_is_inherited(_class, a) LOOP
		PERFORM _cm3_attribute_delete(_class, _attr);
	END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_copy_all_comments(_source regclass, _target regclass) RETURNS void AS $$ DECLARE
	_attr varchar;
BEGIN
	FOR _attr IN SELECT * FROM _cm3_attribute_list(_source) LOOP
		PERFORM _cm3_attribute_comment_set(_target, _attr, _cm3_attribute_comment_get(_source,_attr));
	END LOOP;
END $$ LANGUAGE PLPGSQL;


--- CARD MODIFY ---

CREATE OR REPLACE FUNCTION _cm3_card_delete(_class regclass, _card_id bigint) RETURNS void AS $$ BEGIN
	IF _cm3_class_is_simple(_class) THEN
		RAISE DEBUG 'deleting card from simple class = % with id = %', _class, _card_id;
		EXECUTE format('DELETE FROM %s WHERE "Id" = %L', _class, _card_id);
	ELSE
		RAISE DEBUG 'deleting card from standard class = % with id = %', _class, _card_id;
		EXECUTE format('UPDATE %s SET "Status" = ''N'' WHERE "Id" = %L AND "Status" = ''A''', _class, _card_id);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_cards_delete(_class regclass, _where_expr varchar) RETURNS void AS $$ BEGIN
	IF _cm3_class_is_simple(_class) THEN
		RAISE DEBUG 'deleting cards from simple class = % with expr = %', _class, _where_expr;
		EXECUTE format('DELETE FROM %s WHERE %s', _class, _where_expr);
	ELSE
		RAISE DEBUG 'deleting cards from standard class = % with expr = %', _class, _where_expr;
		EXECUTE format('UPDATE %s SET "Status" = ''N'' WHERE %s AND "Status" = ''A''', _class, _where_expr);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_cards_update(_class regclass, _update_expr varchar, _where_expr varchar) RETURNS void AS $$ BEGIN
	IF _cm3_class_is_simple(_class) THEN
		RAISE DEBUG 'update cards from simple class = % with expr = % WHERE %', _class, _update_expr, _where_expr;
		EXECUTE format('UPDATE %s SET %s WHERE %s', _class, _update_expr, _where_expr);
	ELSE
		RAISE DEBUG 'update cards from standard class = % with expr = % WHERE %', _class, _update_expr, _where_expr;
		EXECUTE format('UPDATE %s SET %s WHERE %s AND "Status" = ''A''', _class, _update_expr, _where_expr);
	END IF;
END $$ LANGUAGE PLPGSQL;


--- STRUCTURE CACHE ---

CREATE OR REPLACE FUNCTION _cm3_structure_cache_refresh() RETURNS void AS $$ BEGIN
    REFRESH MATERIALIZED VIEW _cm3_attribute_list_detailed;
    REFRESH MATERIALIZED VIEW _cm3_class_list_detailed;
END $$ LANGUAGE PLPGSQL;
