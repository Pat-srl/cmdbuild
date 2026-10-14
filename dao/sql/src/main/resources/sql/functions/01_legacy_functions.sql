-- mixed legacy functions
-- REQUIRE PATCH 3.0.0-03a_system_functions

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
		PERFORM _cm3_attribute_index_create(_class, 'BeginDate');
	END IF;

	IF _class_is_standard THEN 
		PERFORM _cm3_utils_class_trigger_copy(_class_parent, _class);
	END IF;

	IF _class_is_standard AND NOT _class_is_superclass THEN
		EXECUTE format('CREATE TRIGGER "_cm3_card_prepare_record" BEFORE INSERT OR UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_prepare_record()', _class);
		EXECUTE format('CREATE TRIGGER "_cm3_card_create_history" AFTER UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_create_history()', _class);
		EXECUTE format('CREATE TRIGGER "_cm3_card_cascade_delete_on_relations" AFTER UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_cascade_delete_on_relations()', _class);
		PERFORM _cm3_attribute_index_create(_class, 'Code');
		PERFORM _cm3_attribute_index_create(_class, 'Description');
		PERFORM _cm3_attribute_index_create(_class, 'IdClass');

		EXECUTE format('CREATE TABLE "%s_history" ( PRIMARY KEY ("Id"), CONSTRAINT "_cm3_CurrentId_foreign_key" FOREIGN KEY ("CurrentId") REFERENCES %s ("Id"), CONSTRAINT "_cm3_Status_check" CHECK ("Status" = ''U'') ) INHERITS (%s)', 
			_class_name, _class, _class);
		_history = _cm3_utils_regclass_to_history(_class);
		PERFORM _cm3_attribute_index_create(_history, 'CurrentId');
	END IF;

	IF _class_is_superclass THEN
		EXECUTE format('CREATE TRIGGER "_cm3_superclass_forbid_operations" BEFORE INSERT OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_superclass_forbid_operations()', _class);		
	END IF;

	PERFORM _cm3_multitenant_mode_change(_class, NULL, _features->>'MTMODE');

    PERFORM _cm3_event_post('class_after_create', _class_name);

	RETURN _class;

END $$ LANGUAGE PLPGSQL;
