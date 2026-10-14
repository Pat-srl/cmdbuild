-- multitenant
-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_multitenant_mode_set(_class regclass, _mode varchar) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_multitenant_mode_change(_class, _cm3_class_comment_get(_class, 'MTMODE'), _mode);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_mode_change(_class regclass, _previous_mode varchar, _mode varchar) RETURNS VOID AS $$ DECLARE
    _has_data boolean;
BEGIN	
    _mode = _cm3_utils_first_not_blank(_mode, 'never');
    _previous_mode = _cm3_utils_first_not_blank(_previous_mode, 'never');
    IF _mode NOT IN ('never','mixed','always','readonly') THEN
        RAISE EXCEPTION 'CM: error configuring multitenant for class = % : unsupported multitenant mode = %', _class, _mode;
    END IF;
    IF _mode <> _previous_mode THEN
        RAISE NOTICE 'set multitenant mode for class = %, cur mode = %, new mode = %', _class, _previous_mode, _mode;
        IF _cm3_class_is_superclass(_class) THEN
            RAISE 'CM: unable to set multitenant mode on superclass % : operation not allowed', _cm3_utils_regclass_to_name(_class);
        ELSE
            IF _mode = 'never' THEN
                IF _cm3_class_is_simple(_class) THEN
                    EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE "IdTenant" IS NOT NULL AND "IdTenant" <> -1)', _class) INTO _has_data;
                ELSE
                    EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE "IdTenant" IS NOT NULL AND "IdTenant" <> -1 AND "Status" = ''A'')', _class) INTO _has_data;
                END IF;
                IF _has_data THEN
                    RAISE 'CM: unable to set multitenant mode to "never" for class % : class contains some cards with non-null IdTenant (suggestion: set mode to "mixed" and set all card tenants to NULL, then set mode to "never")', _class;
                END IF;
            END IF;
            IF _previous_mode IN ('mixed','always','readonly') THEN
                EXECUTE format('DROP POLICY IF EXISTS "%s_policy" ON %s', _cm3_utils_regclass_to_name(_class), _class);
                EXECUTE format('ALTER TABLE %s DISABLE ROW LEVEL SECURITY', _class);
            END IF;
            IF _mode IN ('mixed','always','readonly') THEN
                EXECUTE format('CREATE INDEX IF NOT EXISTS "%s_idtenant" ON %s ("IdTenant")', _cm3_utils_regclass_to_name(_class), _class);
                EXECUTE format('ALTER TABLE %s ENABLE ROW LEVEL SECURITY, FORCE ROW LEVEL SECURITY', _class);
                EXECUTE format('CREATE POLICY "%s_policy" ON %s USING (current_setting(''cmdbuild.ignore_tenant_policies'') = ''true'' OR "IdTenant" IS NULL OR "IdTenant" = ANY (current_setting(''cmdbuild.user_tenants'')::bigint[]))',
                _cm3_utils_regclass_to_name(_class), _class);
                PERFORM _cm3_class_triggers_disable(_class);
                IF _mode = 'always' THEN
                    EXECUTE format('UPDATE %s SET "IdTenant" = -1 WHERE "IdTenant" IS NULL', _class);
                ELSEIF _mode IN('mixed','readonly') THEN
                    EXECUTE format('UPDATE %s SET "IdTenant" = NULL WHERE "IdTenant" = -1', _class);
                END IF;
                PERFORM _cm3_class_triggers_enable(_class);
                PERFORM _cm3_class_comment_set(_class, 'MTMODE', _mode);
            ELSEIF _mode = 'never' THEN
                IF _previous_mode = 'always' THEN
                    EXECUTE format('ALTER TABLE %s DISABLE TRIGGER USER', _class);
                    EXECUTE format('UPDATE %s SET "IdTenant" = NULL WHERE "IdTenant" = -1', _class);
                    EXECUTE format('ALTER TABLE %s ENABLE TRIGGER USER', _class);
                END IF;
                EXECUTE format('DROP INDEX IF EXISTS "%s_idtenant"', _cm3_utils_regclass_to_name(_class), _class);
                PERFORM _cm3_class_comment_delete(_class, 'MTMODE');
            END IF;
            PERFORM _cm3_multitenant_superclass_policy_update(c) FROM _cm3_class_list_ancestors(_class) c WHERE _cm3_class_is_superclass(c);
        END IF;
        ELSE
            RAISE DEBUG 'no need to set multitenant mode for class = %, cur mode = %, new mode = %', _class, _previous_mode, _mode;
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_superclass_policy_update(_class regclass) RETURNS VOID AS $$ DECLARE
    _has_policy boolean;
    _should_set_policy boolean;
BEGIN
    IF NOT _cm3_class_is_superclass(_class) THEN
        RAISE 'CM: unable to set multitenant superclassclass policy on class % : operation not allowed', _cm3_utils_regclass_to_name(_class);
    END IF;
    _has_policy = ( SELECT EXISTS ( SELECT * FROM pg_catalog.pg_policies WHERE schemaname = 'public' AND tablename = _cm3_utils_regclass_to_name(_class) AND policyname = format('%s_policy', _cm3_utils_regclass_to_name(_class)) ) );
    _should_set_policy = ( SELECT EXISTS ( SELECT * FROM _cm3_class_list_descendant_classes(_class) c WHERE _cm3_class_comment_get(c, 'MTMODE') IN ('mixed','always','readonly') ) );
    IF _should_set_policy <> _has_policy THEN
        IF _should_set_policy THEN
            EXECUTE format('ALTER TABLE %s ENABLE ROW LEVEL SECURITY, FORCE ROW LEVEL SECURITY', _class);
            EXECUTE format('CREATE POLICY "%s_policy" ON %s USING (current_setting(''cmdbuild.ignore_tenant_policies'') = ''true'' OR "IdTenant" IS NULL OR "IdTenant" = ANY (current_setting(''cmdbuild.user_tenants'')::bigint[]))', _cm3_utils_regclass_to_name(_class), _class);
        ELSE
            EXECUTE format('ALTER TABLE %s DISABLE ROW LEVEL SECURITY', _class);
            EXECUTE format('DROP POLICY "%s_policy" ON %s', _cm3_utils_regclass_to_name(_class), _class);
        END IF;
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_tenant_class_helper_trigger() RETURNS trigger AS $$ DECLARE
	_class_mode varchar;
BEGIN
    _class_mode = _cm3_utils_first_not_blank(_cm3_class_comment_get(TG_RELID::regclass, 'MTMODE'), 'never');
    IF _class_mode = 'always' AND NEW."IdTenant" IS NULL THEN
        NEW."IdTenant" = NEW."Id";
    END IF;
    RETURN NEW;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_tenant_class_trigger_install(_class regclass) RETURNS VOID AS $$ BEGIN
	EXECUTE format('CREATE TRIGGER "_cm3_multitenant_tenant_class_helper" BEFORE INSERT OR UPDATE ON %s FOR EACH ROW EXECUTE PROCEDURE _cm3_multitenant_tenant_class_helper_trigger()', _class);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_tenants_get() RETURNS bigint[] AS $$ BEGIN
	RETURN current_setting('cmdbuild.user_tenants')::bigint[];
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_tenant_admin_get() RETURNS boolean AS $$ BEGIN
	RETURN current_setting('cmdbuild.ignore_tenant_policies') = 'true';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_multitenant_disable() RETURNS VOID AS $$ DECLARE
    _class regclass;
    _oldmode varchar;
BEGIN
    FOR _class IN SELECT c FROM _cm3_class_list() c LOOP
        _oldmode = _cm3_utils_first_not_blank(_cm3_class_comment_get(_class, 'MTMODE'), 'never');
        IF(_oldmode IN ('mixed','always','readonly')) THEN
            EXECUTE format('UPDATE %s c SET "IdTenant"=null where c."IdTenant" IS NOT NULL and c."Status"=''A''', _class);
            PERFORM _cm3_multitenant_mode_set(_class, 'never');
            EXECUTE format('DROP TRIGGER IF EXISTS "_cm3_multitenant_tenant_class_helper" ON %s', _class);
        END IF;
    END LOOP;
    PERFORM _cm3_system_config_set('org.cmdbuild.multitenant.mode', 'DISABLED');
END $$ LANGUAGE PLPGSQL;

