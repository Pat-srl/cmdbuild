-- system functions for domain edit
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- DOMAIN MODIFY ---

CREATE OR REPLACE FUNCTION _cm3_domain_create(_features varchar) RETURNS int AS $$ BEGIN
    RETURN _cm3_domain_create(_cm3_comment_to_jsonb(_features));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_create(_features jsonb) RETURNS int AS $$ BEGIN
    RETURN _cm3_domain_create(_features->>'NAME', _features-'NAME');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_create(_name varchar, _features jsonb) RETURNS int AS $$ DECLARE 
	_attr varchar;
	_domain regclass;
	_cardinality varchar;
	_history regclass; 
BEGIN

	IF NOT _name LIKE 'Map_%' THEN 
		_name = format('Map_%s', _name);
	END IF;

	_features = _features || jsonb_build_object('TYPE', 'domain');

	IF NOT _cm3_class_exists(_features->>'CLASS1') THEN
		RAISE 'invalid domain definition: class not found for name = %', _features->>'CLASS1';
	END IF;
	IF NOT _cm3_class_exists(_features->>'CLASS2') THEN
		RAISE 'invalid domain definition: class not found for name = %', _features->>'CLASS2';
	END IF;

	EXECUTE format('CREATE TABLE "%s" (PRIMARY KEY ("Id")) INHERITS ("Map")', _name, _name);

	_domain = _cm3_utils_name_to_regclass(_name);

    PERFORM _cm3_class_features_set(_domain, _features);
	
	PERFORM _cm3_attribute_copy_all_comments('"Map"'::regclass, _domain);

	EXECUTE format('CREATE TABLE "%s_history" (PRIMARY KEY ("Id")) INHERITS (%s)', _name, _domain);
	_history = _cm3_utils_regclass_to_history(_domain);
	EXECUTE format('ALTER TABLE %s ALTER COLUMN "EndDate" SET DEFAULT now()', _history);
	EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_CurrentId_fkey" FOREIGN KEY ("CurrentId") REFERENCES %s("Id")', _history, _domain);
	EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_Status_check" CHECK ("Status" = ''U'')', _history);
	PERFORM _cm3_attribute_index_create(_history, 'CurrentId');

	_cardinality = _cm3_class_comment_get(_domain, 'CARDIN');
    IF _cardinality !~ '^[1N]:[1N]$' THEN
        RAISE 'invalid domain CARDIN value = %', _cardinality;
    END IF;
	
	PERFORM _cm3_attribute_index_create(_domain, 'IdDomain');
	PERFORM _cm3_attribute_index_create(_domain, 'IdObj1');
	PERFORM _cm3_attribute_index_create(_domain, 'IdObj2');

    PERFORM _cm3_domain_composite_index_rebuild(_domain);

	IF _cardinality LIKE '%:1' THEN
		EXECUTE format('CREATE UNIQUE INDEX "_cm3_%s_source" ON %s ("IdClass1", "IdObj1") WHERE "Status" = ''A''', _cm3_utils_shrink_name_lon(_name), _domain);
	END IF;
	IF _cardinality LIKE '1:%' THEN
		EXECUTE format('CREATE UNIQUE INDEX "_cm3_%s_target" ON %s ("IdClass2", "IdObj2") WHERE "Status" = ''A''', _cm3_utils_shrink_name_lon(_name), _domain);
	END IF;

	EXECUTE format('CREATE TRIGGER "_cm3_card_prepare_record" BEFORE INSERT OR UPDATE OR DELETE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_prepare_record()', _domain);
	EXECUTE format('CREATE TRIGGER "_cm3_card_create_history" AFTER UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_trigger_card_create_history()', _domain);

    EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdDomain" SET DEFAULT %L', _domain, _domain);
    EXECUTE format('ALTER TABLE %s ADD CONSTRAINT "_cm3_IdDomain_check" CHECK ( "IdDomain" = %L::regclass )', _domain, _domain);
    EXECUTE format('ALTER TABLE %s ALTER COLUMN "IdDomain" SET DEFAULT %L', _cm3_utils_regclass_to_history(_domain), _domain);

	RETURN _domain::oid::int;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_composite_index_rebuild(_domain regclass) RETURNS VOID AS $$ DECLARE
    _index_name varchar = format('_cm3_%s_relations', _cm3_utils_shrink_name_lon(_cm3_utils_regclass_to_name(_domain)));
BEGIN
    EXECUTE format('DROP INDEX IF EXISTS %I', _index_name);
    IF _cm3_domain_cardin_get(_domain) = 'N:N' AND (SELECT EXISTS (SELECT * FROM _cm3_attribute_list_detailed(_domain) WHERE comment->>'DOMAINKEY' = 'true')) THEN
        EXECUTE format('CREATE UNIQUE INDEX %I ON %s ("IdDomain","IdClass1","IdObj1","IdClass2","IdObj2"%s) WHERE "Status" = ''A''', _index_name, _domain,(
            WITH q AS (SELECT string_agg(quote_ident(name),',') a FROM _cm3_attribute_list_detailed(_domain) WHERE comment->>'DOMAINKEY' = 'true') SELECT CASE a WHEN NULL THEN '' ELSE ','||a END FROM q));
    ELSE
        EXECUTE format('CREATE UNIQUE INDEX %I ON %s ("IdDomain","IdClass1","IdObj1","IdClass2","IdObj2") WHERE "Status" = ''A''', _index_name, _domain);
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_create(_name varchar, _comment varchar) RETURNS int AS $$ 
	SELECT _cm3_domain_create(_name, _cm3_comment_to_jsonb(_comment)) 
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_domain_modify(_domain regclass, _features jsonb) RETURNS void AS $$ DECLARE
	_current_features jsonb;
BEGIN
	_current_features = _cm3_class_features_get(_domain);
	IF _current_features->'CARDIN' <> _features->'CARDIN' 
		OR _current_features->'CLASS1' <> _features->'CLASS1'
		OR _current_features->'CLASS2' <> _features->'CLASS2'
		OR _current_features->'TYPE' <> _features->'TYPE'
	THEN
		RAISE EXCEPTION 'CMO 200: invalid comment features on domain = % current features = "%" new features = "%"', _domain, _cm3_comment_from_jsonb(_current_features), _cm3_comment_from_jsonb(_features);
	END IF;

	PERFORM _cm3_class_features_set(_domain, _features);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_delete(_domain regclass) RETURNS void AS $$ DECLARE
	_class regclass;
	_attr varchar;
BEGIN
	IF _cm3_class_has_records(_domain) THEN
		RAISE EXCEPTION 'CM_CONTAINS_DATA: cannot delete domain %: table is not empty', _domain;
	END IF;
	FOR _class, _attr IN SELECT owner, name FROM _cm3_attribute_list() WHERE _cm3_attribute_features_get(owner, name, 'REFERENCEDOM') = _cm3_utils_regclass_to_domain_name(_domain) LOOP
		RAISE EXCEPTION 'CM: cannot delete domain %: found reference in class = % attr = %', _domain, _class, _attr;
	END LOOP;
	PERFORM _cm3_attribute_delete_all(_domain);
	EXECUTE format('DROP TABLE %s CASCADE', _domain);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_source_check(_domain regclass, _source_class regclass) RETURNS void AS $$ BEGIN
    IF _source_class IS NULL OR NOT EXISTS (SELECT c FROM _cm3_class_list_ancestors_and_self(_source_class) c where c = _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, 'CLASS1'))) THEN --TODO check also disabled classes
        RAISE EXCEPTION 'CM: invalid source class = % for domain = %', _cm3_utils_regclass_to_name(_source_class), _cm3_utils_regclass_to_domain_name(_domain);
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_target_check(_domain regclass, _target_class regclass) RETURNS void AS $$ BEGIN
    IF _target_class IS NULL OR NOT EXISTS (SELECT c FROM _cm3_class_list_ancestors_and_self(_target_class) c where c = _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, 'CLASS2'))) THEN --TODO check also disabled classes
        RAISE EXCEPTION 'CM: invalid target class = % for domain = %', _cm3_utils_regclass_to_name(_target_class), _cm3_utils_regclass_to_domain_name(_domain);
    END IF;    
END $$ LANGUAGE PLPGSQL;
