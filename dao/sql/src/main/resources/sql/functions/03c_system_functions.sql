-- system functions (read access and utils)
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- SYSTEM UTILS ---

CREATE OR REPLACE FUNCTION _cm3_utils_random_id() RETURNS varchar AS $$
    SELECT md5(random()::text);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_is_not_blank(_value varchar) RETURNS boolean AS $$
    SELECT _value IS NOT NULL AND _value !~ '^[\s]*$';
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_is_blank(_value varchar) RETURNS boolean AS $$
    SELECT _value IS NULL OR _value ~ '^[\s]*$';
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_hash(_value varchar, _size int) RETURNS varchar AS $$ BEGIN
    _value = md5(_value);
    WHILE length(_value) < _size LOOP
        _value = _value || md5(_value);
    END LOOP;
    RETURN substring(_value, 1, _size);
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_anonymize(_value varchar) RETURNS varchar AS $$ BEGIN
    IF _cm3_utils_is_blank(_value) OR length(_value) < 4 THEN
        RETURN _value;
    ELSE
        RETURN substring(_value,1,1) || _cm3_utils_hash(_value, length(_value)-1);
    END IF;
END $$ LANGUAGE PLPGSQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_utils_first_not_blank(_value varchar, _default varchar) RETURNS varchar AS $$
    SELECT CASE _cm3_utils_is_not_blank(_value) WHEN TRUE THEN _value ELSE _default END;
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_check_not_blank(_value varchar) RETURNS varchar AS $$ BEGIN
    IF _cm3_utils_is_blank(_value) THEN
        RAISE 'expected nonblank value, but value is blank';
    END IF;
    RETURN _value;
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_bytea_to_varchar(_value bytea) RETURNS varchar AS $$
    SELECT convert_from(_value, 'UTF8');
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_varchar_to_bytea(_value varchar) RETURNS bytea AS $$
    SELECT convert_to(_value, 'UTF8');
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_array_reverse(anyarray) RETURNS anyarray AS $$
    SELECT ARRAY(
        SELECT $1[i]
        FROM generate_subscripts($1,1) AS s(i)
        ORDER BY i DESC
    );
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_system_message_send(_data jsonb) RETURNS VOID AS $$
	LISTEN cminfo;
	SELECT pg_notify('cmevents', _data::varchar);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_message_send(_type varchar, _data jsonb) RETURNS void AS $$
	SELECT _cm3_system_message_send(_data || jsonb_build_object('type', _type, 'id', (SELECT substring(_cm3_utils_random_id() from 0 for 8))));
$$ LANGUAGE SQL;

-- TODO
-- CREATE OR REPLACE FUNCTION _cm3_system_message_send(_type varchar, _data jsonb) RETURNS varchar AS $$ DECLARE
--     _id varchar;
-- BEGIN
--     _id = (SELECT substring(_cm3_utils_random_id() from 0 for 8));
-- 	PERFORM _cm3_system_message_send(_data || jsonb_build_object('type', _type, 'id', _id)));
--     RETURN _id;
-- END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar, _data jsonb) RETURNS VOID AS $$
	SELECT _cm3_system_message_send('command', _data || jsonb_build_object('action', _action));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar) RETURNS VOID AS $$
	SELECT _cm3_system_command_send(_action, '{}'::jsonb);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar, VARIADIC _args varchar[]) RETURNS VOID AS $$
	SELECT _cm3_system_command_send(_action, jsonb_object(_args));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_eval(_script varchar, VARIADIC _args varchar[]) RETURNS VOID AS $$
	SELECT _cm3_system_command_send('eval', jsonb_build_object('script', _script) || jsonb_object(_args));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_eval(_script varchar) RETURNS VOID AS $$
	SELECT "_cm3_system_command_eval"(_script, VARIADIC ARRAY[]::varchar[]);
$$ LANGUAGE SQL;

--  TODO fix this (?)
-- CREATE OR REPLACE FUNCTION _cm3_system_command_invoke(_action varchar, VARIADIC _args varchar[]) RETURNS jsonb AS $$ DECLARE
--     _id varchar;
-- BEGIN
-- 	_id = _cm3_system_message_send('command', jsonb_object(_args) || jsonb_build_object('action', _action));
-- --     COMMIT;
--     RAISE NOTICE 'wait for command response with id = %', _id;
--     WHILE NOT EXISTS (SELECT * FROM "_Temp" WHERE "Info"->>'type' = 'pg_commad_response' AND "Info"->>'command_id' = _id) LOOP
--         PERFORM pg_sleep(0.1);
--     END LOOP;
--     RETURN (SELECT convert_from("Data",'UTF8')::jsonb FROM "_Temp" WHERE "Info"->>'type' = 'pg_commad_response' AND "Info"->>'command_id' = _id);
-- END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_event_send(_event varchar) RETURNS VOID AS $$
	SELECT _cm3_system_message_send('event', jsonb_build_object('event', _event));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_event_send(_event varchar, VARIADIC _args varchar[]) RETURNS VOID AS $$ BEGIN
	SELECT _cm3_system_message_send('event', jsonb_object(_args) || jsonb_build_object('event', _event));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_reload() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('reload');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_restart() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('restart');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_shutdown() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('shutdown');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_email_send() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('email_queue_trigger', jsonb_build_object('cluster_mode', 'run_on_single_node'));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_email_send(_id bigint) RETURNS VOID AS $$
	SELECT _cm3_system_command_send('email_queue_send_single', jsonb_build_object('cluster_mode', 'run_on_single_node', '_email_id', _id));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_login() RETURNS VOID AS $$ BEGIN
	SET SESSION cmdbuild.operation_user = 'postgres';
	SET SESSION cmdbuild.operation_scope = 'default';
	SET SESSION cmdbuild.user_tenants = '{}';
	SET SESSION cmdbuild.ignore_tenant_policies = 'true';
    PERFORM set_config('cmdbuild.operation_session', format('dummy_%s', _cm3_utils_random_id()), FALSE); --TODO check this
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_build_sqltype_string(_sqltype oid, _typemod integer) RETURNS varchar AS $$
	SELECT pg_type.typname::text || CASE
		WHEN _typemod IS NULL THEN ''
		WHEN pg_type.typname IN ('varchar','bpchar') AND _typemod < 0 THEN ''
		WHEN pg_type.typname IN ('varchar','bpchar') THEN '(' || _typemod - 4 || ')'
		WHEN pg_type.typname = 'numeric' THEN '(' || _typemod / 65536 || ',' || _typemod - _typemod / 65536 * 65536 - 4|| ')'
		ELSE ''
	END FROM pg_type WHERE pg_type.oid = _sqltype;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_user_get() RETURNS varchar AS $$ BEGIN
	RETURN current_setting('cmdbuild.operation_user');
EXCEPTION WHEN undefined_object THEN
	RETURN 'postgres';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_lang_get() RETURNS varchar AS $$ BEGIN
	RETURN current_setting('cmdbuild.lang');
EXCEPTION WHEN undefined_object THEN
	RETURN 'default';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_role_get() RETURNS varchar AS $$ BEGIN
	RETURN current_setting('cmdbuild.operation_role');
EXCEPTION WHEN undefined_object THEN
	RETURN '';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_session_get() RETURNS varchar AS $$ BEGIN
	RETURN current_setting('cmdbuild.operation_session');
EXCEPTION WHEN undefined_object THEN
	RETURN '';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_scope_get() RETURNS varchar AS $$ BEGIN
	RETURN current_setting('cmdbuild.operation_scope');
EXCEPTION WHEN undefined_object THEN
	RETURN 'default';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_context_get() RETURNS varchar AS $$ DECLARE
    _user varchar = _cm3_utils_operation_user_get();
    _scope varchar = _cm3_utils_operation_scope_get();
BEGIN
    IF _scope ~ '^(|default)$' THEN
        RETURN _user;
    ELSE
        RETURN format('%s / %s', _scope, _user);
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_operation_language_get() RETURNS varchar AS $$ DECLARE
    _username varchar = _cm3_utils_operation_user_get();
BEGIN --TODO improve this, get actual request context language (?)
	RETURN coalesce((SELECT uc."Data"->>'cm_user_language' FROM "_UserConfig" uc JOIN "User" u on uc."Owner" = u."Id" WHERE uc."Status" = 'A' AND u."Status" = 'A' AND u."Username" = _username), _cm3_system_config_get('org.cmdbuild.core.language'), 'en');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_new_card_id() RETURNS bigint AS $$
	SELECT nextval('class_seq')::bigint;
$$ LANGUAGE SQL VOLATILE;

CREATE OR REPLACE FUNCTION _cm3_utils_name_escape(_class_name varchar) RETURNS varchar AS $$ BEGIN
	IF _class_name ~ '^".*"$' THEN
		RETURN _class_name;
	ELSE
		IF _class_name ~ '^[^.]+[.][^.]+$' THEN
			RETURN regexp_replace(_class_name, '^([^.]+)[.]([^.]+)$', '"\1"."\2"');
		ELSE
			RETURN format('"%s"', _class_name);
		END IF;
	END IF;
END $$ LANGUAGE PLPGSQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_utils_name_to_basename(_class_name varchar) RETURNS varchar AS $$ BEGIN
	return regexp_replace(regexp_replace(_class_name,'"','','g'),'^([^.]+)[.]','');
END $$ LANGUAGE PLPGSQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_utils_name_to_regclass(_class_name varchar) RETURNS regclass AS $$ BEGIN
	_class_name = _cm3_utils_name_escape(_class_name);
	IF (SELECT pg_get_function_arguments(oid) FROM pg_proc WHERE proname = 'to_regclass') = 'cstring' THEN
		RETURN to_regclass(_class_name::cstring);
	ELSE
		RETURN to_regclass(_class_name::text);
	END IF;
END $$ LANGUAGE PLPGSQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_utils_name_to_regclass_not_null(_class_name varchar) RETURNS regclass AS $$ DECLARE
    _class varchar = _cm3_utils_name_to_regclass(_class_name);
BEGIN
    IF _class IS NULL THEN
        RAISE 'class not found for name = < % >', _class_name;
    ELSE
        RETURN _class;
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_regclass_to_name(_class regclass) RETURNS varchar AS $$ BEGIN
	RETURN _cm3_utils_name_to_basename(_class::varchar);
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_regclass_to_domain_name(_class regclass) RETURNS varchar AS $$ BEGIN
	RETURN regexp_replace(_cm3_utils_regclass_to_name(_class), '^Map_', '');
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_domain_name_to_regclass(_name varchar) RETURNS regclass AS $$ BEGIN
	RETURN _cm3_utils_name_to_regclass(format('Map_%s', _name));
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_utils_regclass_to_history(_class regclass) RETURNS regclass AS $$
	SELECT format('"%s_history"',_cm3_utils_regclass_to_name(_class))::regclass;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_shrink_name(_name varchar, _maxlength int) RETURNS varchar AS $$ BEGIN
	IF length(_name) <= _maxlength THEN
		RETURN _name;
	ELSE
		RETURN substring( _name from 1 for ( _maxlength / 3 ) )
			|| substring( md5(_name) from 1 for ( _maxlength / 3 + _maxlength % 3 ) )
			|| substring( _name from ( length(_name) - _maxlength / 3 + 1 ) for ( _maxlength / 3 ) );
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_shrink_name(_name varchar) RETURNS varchar AS $$
	SELECT _cm3_utils_shrink_name(_name, 20);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_shrink_name_lon(_name varchar) RETURNS varchar AS $$
	SELECT _cm3_utils_shrink_name(_name, 40);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_strip_null_or_empty(_data jsonb) RETURNS jsonb AS $$
	SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each(_data) WHERE coalesce(_data->>key,'') <> '';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_request_performance_stats() RETURNS TABLE(instant timestamptz, request_count bigint, slow_request_count bigint, quick_request_count bigint, average_response_time real, average_slow_response_time real, node varchar, load numeric, performance int) AS $$ DECLARE
    _window_size interval = '10 minutes';
    _slow_response_threshold_millis int = 2000;
    _quick_response_threshold_millis int = 100;
BEGIN
    RETURN QUERY
        WITH
            times AS (SELECT DISTINCT date_trunc('minute', "Timestamp") "Timestamp", "NodeId" FROM "_Request" ORDER BY "Timestamp", "NodeId")
        SELECT *, (CASE WHEN q.request_count = q.quick_request_count THEN 100 ELSE 100 - q.slow_request_count * 100 / ( q.request_count - q.quick_request_count ) END)::int performance  FROM (SELECT
            "Timestamp" instant,
            (SELECT count(*) FROM "_Request" r2  WHERE r2."NodeId" = r1."NodeId" AND r2."Timestamp" BETWEEN r1."Timestamp" AND r1."Timestamp" + _window_size) request_count,
            (SELECT count(*) FROM "_Request" r2  WHERE r2."NodeId" = r1."NodeId" AND r2."Timestamp" BETWEEN r1."Timestamp" AND r1."Timestamp" + _window_size AND "ElapsedTime" > _slow_response_threshold_millis) slow_request_count,
            (SELECT count(*) FROM "_Request" r2  WHERE r2."NodeId" = r1."NodeId" AND r2."Timestamp" BETWEEN r1."Timestamp" AND r1."Timestamp" + _window_size AND "ElapsedTime" < _quick_response_threshold_millis) quick_request_count,
            (SELECT avg("ElapsedTime") FROM "_Request" r2  WHERE r2."NodeId" = r1."NodeId" AND r2."Timestamp" BETWEEN r1."Timestamp" AND r1."Timestamp" + _window_size)::real average_response_time,
            (SELECT coalesce(avg("ElapsedTime"), 0) FROM "_Request" r2  WHERE r2."NodeId" = r1."NodeId" AND r2."Timestamp" BETWEEN r1."Timestamp" AND r1."Timestamp" + _window_size AND "ElapsedTime" > _slow_response_threshold_millis)::real average_slow_response_time,
            "NodeId" node,
            (SELECT "LoadAvg" FROM "_SystemStatusLog" l WHERE l."BeginDate" > r1."Timestamp" AND l."NodeId" = r1."NodeId" ORDER BY "BeginDate" ASC LIMIT 1) AS load
        FROM times r1) q;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_disk_usage_detailed() RETURNS TABLE (schema varchar, item regclass, type varchar, row_estimate real, total_size bigint, total_size_pretty varchar) AS $$ BEGIN
	RETURN QUERY WITH q AS (SELECT a.schema, oid::regclass item,
			(CASE WHEN _cm3_class_is_simple(oid) THEN 'simpleclass'
				WHEN _cm3_class_is_standard(oid) THEN 'class'
				WHEN _cm3_class_is_domain(oid) THEN 'map'
				WHEN _cm3_class_is_history(oid) THEN 'history'
				ELSE 'other' END)::varchar AS type,
			a.row_estimate,
			total_bytes,
			pg_size_pretty(total_bytes)::varchar AS total
		FROM (
			SELECT
				c.oid oid,
				nspname::varchar AS schema,
				c.reltuples AS row_estimate,
				pg_total_relation_size(c.oid) AS total_bytes,
				pg_indexes_size(c.oid) AS index_bytes,
				COALESCE(pg_total_relation_size(reltoastrelid),0) AS toast_bytes
			FROM pg_class c LEFT JOIN pg_namespace n ON n.oid = c.relnamespace WHERE relkind = 'r' AND nspname IN ('public','gis')
		) a
  ) SELECT * FROM q ORDER BY schema, type, q.item::varchar;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_table_size(_table regclass) RETURNS TABLE (schema varchar, item regclass, type varchar, row_estimate real, total_size bigint, total_size_pretty varchar) AS $$ BEGIN
    RETURN QUERY SELECT * FROM _cm3_utils_disk_usage_detailed() x WHERE x.item = _table OR _cm3_utils_regclass_to_name(x.item) = _cm3_utils_regclass_to_name(_table) || '_history';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_connection_usage_detailed() RETURNS TABLE (database varchar, username varchar, client varchar, addr varchar, hostname varchar, port int, state varchar, start timestamptz, age interval, elapsed interval, query varchar) AS $$
    SELECT
        datname::varchar,
        usename::varchar,
        application_name,
        client_addr::varchar,
        client_hostname,
        client_port,
        state,
        query_start,
        NOW() - state_change,
        state_change - query_start,
        query
    FROM pg_stat_activity
    WHERE query !~ 'pg_stat_activity|_cm3_utils_connection_usage_detailed'
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_utils_domain_reserved_attributes() RETURNS SETOF VARCHAR AS $$
    SELECT UNNEST(ARRAY['IdDomain', 'IdClass1', 'IdObj1', 'IdClass2', 'IdObj2', 'BeginDate', 'Id', 'CurrentId']);
$$ LANGUAGE SQL;


--- SYSTEM CONFIG UTILS ---

CREATE OR REPLACE FUNCTION _cm3_system_config_get(_key varchar) RETURNS varchar AS $$ BEGIN
    RETURN (SELECT coalesce("Value",'') FROM "_SystemConfig" WHERE "Code" = _key AND "Status" = 'A');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_config_get(_key varchar, _default varchar) RETURNS varchar AS $$ BEGIN
    RETURN _cm3_utils_first_not_blank(_cm3_system_config_get(_key), _default);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_config_get() RETURNS jsonb AS $$ BEGIN
    RETURN (SELECT coalesce(jsonb_object_agg("Code", "Value"), '{}'::jsonb) FROM "_SystemConfig" WHERE "Status" = 'A');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_config_set(_key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
    RAISE NOTICE 'update system config set % = %', _key, _value;
    INSERT INTO "_SystemConfig" ("Code", "Value") VALUES (_key, _value) ON CONFLICT ("Code") WHERE "Status" = 'A' DO UPDATE SET "Value" = EXCLUDED."Value";
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_config_delete(_key varchar) RETURNS VOID AS $$ BEGIN
    UPDATE "_SystemConfig" SET "Status" = 'N' WHERE "Code" = _key AND "Status" = 'A';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_email_utils_field_contains_value(_field_value varchar, _email varchar) RETURNS boolean AS $$ DECLARE
    _res boolean;
BEGIN
    _email = regexp_replace(_email, '^ *(.*<)?(.*?)>? *$', '\2');
    IF _field_value IS NULL THEN
        RETURN FALSE;
    ELSE
        SELECT INTO _res EXISTS (WITH q AS (SELECT regexp_replace(x, '^ *(.*<)?(.*?)>? *$', '\2') e FROM regexp_split_to_table(_field_value, ',') x) SELECT * FROM q WHERE e ILIKE _email);
        RETURN _res;
    END IF;
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_system_service_enabled(_service varchar) RETURNS BOOLEAN AS $$
	SELECT COALESCE(_cm3_system_config_get(format('org.cmdbuild.%s.enabled', _service)), 'false')::boolean;
$$ LANGUAGE SQL;


--- COMMENT UTILS ---

CREATE OR REPLACE FUNCTION _cm3_comment_to_jsonb(_comment varchar) RETURNS jsonb AS $$  DECLARE
	_map jsonb;
	_part varchar[];
BEGIN
	_map = '{}'::jsonb;
	FOR _part IN SELECT regexp_matches(unnest(string_to_array(_comment,'|')),'^ *([^:]+) *: *(.*)$') LOOP
		_map = jsonb_set(_map,ARRAY[(_part[1])],to_jsonb(replace(_part[2], '&vert;', '|')));
	END LOOP;
	RETURN _map;
END $$ LANGUAGE PLPGSQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_comment_get_part(_comment varchar,_key varchar) RETURNS varchar AS $$
	SELECT _cm3_comment_to_jsonb(_comment)->>_key;
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_comment_from_jsonb(_map jsonb) RETURNS varchar AS $$
	SELECT COALESCE((SELECT string_agg(format('%s: %s',key,replace(value,'|','&vert;')),'|') from jsonb_each_text(_map)), '');
$$ LANGUAGE SQL IMMUTABLE;


--- TRIGGER LIST ---

CREATE OR REPLACE FUNCTION _cm3_trigger_utils_tgargs_to_string_array(_args bytea) RETURNS varchar[] AS $$
    SELECT array(SELECT param FROM(SELECT regexp_split_to_table(encode(_args, 'escape'), E'\\\\000') AS param) AS q WHERE q.param <> '')::varchar[]
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_class_triggers_list_detailed(_class regclass) RETURNS TABLE (trigger_name varchar, trigger_when varchar, trigger_for_each varchar, trigger_function varchar, trigger_params varchar[]) AS $$ BEGIN
	RETURN QUERY SELECT
			t.tgname::varchar AS trigger_name,
			(CASE t.tgtype::int2 & cast(2 as int2) WHEN 0 THEN 'AFTER' ELSE 'BEFORE' END || ' ' || CASE t.tgtype::int2 & cast(28 as int2)
				WHEN 16 THEN 'UPDATE'
				WHEN  8 THEN 'DELETE'
				WHEN  4 THEN 'INSERT'
				WHEN 20 THEN 'INSERT OR UPDATE'
				WHEN 28 THEN 'INSERT OR UPDATE OR DELETE'
				WHEN 24 THEN 'UPDATE OR DELETE'
				WHEN 12 THEN 'INSERT OR DELETE'
			END)::varchar AS trigger_when,
				(CASE t.tgtype::int2 & cast(1 as int2)
				WHEN 0 THEN 'STATEMENT'
				ELSE 'ROW'
			END)::varchar AS trigger_for_each,
			p.proname::varchar AS trigger_function,
			_cm3_trigger_utils_tgargs_to_string_array(tgargs) AS trigger_params
		FROM pg_trigger t, pg_proc p
		WHERE tgrelid = _class AND t.tgfoid = p.oid AND tgisinternal = false
		ORDER BY t.tgname;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_triggers_list_detailed() RETURNS TABLE (owner regclass, trigger_name varchar, trigger_when varchar, trigger_for_each varchar, trigger_function varchar, trigger_params varchar[]) AS $$ BEGIN
	RETURN QUERY EXECUTE (SELECT string_agg(format('SELECT %L::regclass AS owner,* FROM _cm3_class_triggers_list_detailed(%L)',c.x,c.x),' UNION ALL ')  FROM (SELECT x FROM _cm3_class_list() x UNION SELECT x FROM _cm3_domain_list() x) c);--TODO improve this
END $$ LANGUAGE PLPGSQL;


--- TRIGGER CHECK ---

CREATE OR REPLACE FUNCTION _cm3_class_triggers_are_enabled(_class regclass) RETURNS BOOLEAN AS $$
	SELECT (SELECT DISTINCT tgenabled FROM pg_trigger where tgrelid = _class AND tgisinternal = false) <> 'D'; --TODO check only cm triggers (??)
$$ LANGUAGE SQL;


--- CLASS COMMENT ---

CREATE OR REPLACE FUNCTION _cm3_class_comment_keys() RETURNS SETOF varchar AS $$
	SELECT DISTINCT unnest(ARRAY[
            'LABEL','CLASS1','CLASS2','TYPE','DESCRDIR','DESCRINV','CARDIN','MASTERDETAIL','MDLABEL','MDFILTER','DISABLED1','DISABLED2','INDEX1','INDEX2','ACTIVE','MODE','CASCADEDIRECT','CASCADEINVERSE', -- domain
            'DESCR','SUPERCLASS','TYPE','MTMODE','USERSTOPPABLE','WFSTATUSATTR','WFSAVE','ACTIVE','MODE', 'FASTDEL' -- class
        ]);
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_class_comment_get(_class regclass) RETURNS varchar AS $$
	SELECT description FROM pg_description WHERE objoid = _class AND objsubid = 0;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_comment_get_jsonb(_class regclass) RETURNS jsonb AS $$
	SELECT _cm3_comment_to_jsonb(_cm3_class_comment_get(_class));
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_comment_get(_class regclass, _key varchar) RETURNS varchar AS $$
	SELECT coalesce(_cm3_class_comment_get_jsonb(_class)->>_key, '');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_comment_set(_class regclass, _comment jsonb) RETURNS void AS $$ DECLARE
	_key varchar;
BEGIN
	FOR _key IN SELECT x FROM jsonb_object_keys(_comment) x WHERE CASE WHEN _cm3_utils_regclass_to_name(_class) LIKE 'Map_%'
		THEN x NOT IN ('LABEL','CLASS1','CLASS2','TYPE','DESCRDIR','DESCRINV','CARDIN','MASTERDETAIL','MDLABEL','MDFILTER','DISABLED1','DISABLED2','INDEX1','INDEX2','ACTIVE','MODE','CASCADEDIRECT','CASCADEINVERSE')
		ELSE x NOT IN ('DESCR','SUPERCLASS','TYPE','MTMODE','USERSTOPPABLE','WFSTATUSATTR','WFSAVE','ACTIVE','MODE', 'FASTDEL') END
	LOOP
		RAISE WARNING 'CM: invalid comment for class = %: invalid comment key = %', _class, _key;
	END LOOP;
	RAISE NOTICE 'set class comment % = %', _class, _comment;
	EXECUTE format('COMMENT ON TABLE %s IS %L', _class, _cm3_comment_from_jsonb(_comment));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_comment_set(_class regclass, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_class_comment_set(_class, _cm3_class_comment_get_jsonb(_class) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_comment_delete(_class regclass, _key varchar) RETURNS void AS $$
	SELECT _cm3_class_comment_set(_class, _cm3_class_comment_get_jsonb(_class) - _key);
$$ LANGUAGE SQL;


--- CLASS FEATURE ---

CREATE OR REPLACE FUNCTION _cm3_class_features_get(_classe regclass) RETURNS jsonb AS $$ BEGIN
	RETURN _cm3_class_metadata_get(_classe) || _cm3_class_comment_get_jsonb(_classe);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_get(_classe regclass, _key varchar) RETURNS varchar AS $$ BEGIN
	RETURN coalesce(_cm3_class_features_get(_classe)->>_key, '');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_set(_classe regclass, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_comment_set(_classe, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key IN (SELECT _cm3_class_comment_keys())));
	PERFORM _cm3_class_metadata_set(_classe, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key NOT IN (SELECT _cm3_class_comment_keys())));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_set(_classe regclass, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_features_set(_classe, _cm3_class_features_get(_classe) || jsonb_build_object(_key, _value));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_delete(_classe regclass, _key varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_features_set(_classe, _cm3_class_features_get(_classe) - _key);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_features_update(_classe regclass, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_class_features_set(_classe, _cm3_class_features_get(_classe) || _features);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_description_get(_class regclass) RETURNS VARCHAR AS $$ BEGIN
    RETURN _cm3_utils_first_not_blank(_cm3_class_features_get(_class)->>'DESCR',_cm3_utils_regclass_to_name(_class));
END $$ LANGUAGE PLPGSQL STABLE;


--- CLASS METADATA ---

CREATE OR REPLACE FUNCTION _cm3_class_metadata_get(_classe regclass) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Metadata" FROM "_ClassMetadata" WHERE "Owner" = _classe AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_metadata_get(_classe regclass,_key varchar) RETURNS varchar AS $$ BEGIN
	RETURN jsonb_extract_path_text(_cm3_class_metadata_get(_classe),_key::text);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_metadata_set(_classe regclass, _metadata jsonb) RETURNS VOID AS $$ BEGIN
    IF NOT EXISTS ( SELECT 1 FROM pg_class c WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND c.oid::regclass = _classe ) THEN
        RAISE WARNING 'skip class metadata set for class = % : class not found in `public` namespace', _classe;
    ELSE
        _metadata = _cm3_utils_strip_null_or_empty(_metadata);
        IF EXISTS (SELECT 1 FROM "_ClassMetadata" WHERE "Owner" = _classe AND "Status" = 'A') THEN
            IF _metadata = '{}'::jsonb THEN
                UPDATE "_ClassMetadata" SET "Status" = 'N' WHERE "Owner" = _classe AND "Status" = 'A';
            ELSE
                UPDATE "_ClassMetadata" SET "Metadata" = _metadata WHERE "Owner" = _classe AND "Status" = 'A';
            END IF;
        ELSE
            IF _metadata <> '{}'::jsonb THEN
                INSERT INTO "_ClassMetadata" ("Owner","Metadata") VALUES (_classe,_metadata);
            END IF;
        END IF;
	END IF;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-29_class_structure
CREATE OR REPLACE FUNCTION _cm3_class_metadata_get(_class regclass) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Metadata" FROM "_ClassMetadata" WHERE "Code" = _cm3_utils_regclass_to_name(_class) AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_metadata_set(_class regclass, _metadata jsonb) RETURNS VOID AS $$ BEGIN
    _metadata = _cm3_utils_strip_null_or_empty(_metadata);
    IF EXISTS (SELECT 1 FROM "_ClassMetadata" WHERE "Code" = _cm3_utils_regclass_to_name(_class) AND "Status" = 'A') THEN
        IF _metadata = '{}'::jsonb THEN
            UPDATE "_ClassMetadata" SET "Status" = 'N' WHERE "Code" = _cm3_utils_regclass_to_name(_class) AND "Status" = 'A';
        ELSE
            UPDATE "_ClassMetadata" SET "Metadata" = _metadata WHERE "Code" = _cm3_utils_regclass_to_name(_class) AND "Status" = 'A';
        END IF;
    ELSE
        IF _metadata <> '{}'::jsonb THEN
            INSERT INTO "_ClassMetadata" ("Code","Metadata") VALUES (_cm3_utils_regclass_to_name(_class), _metadata);
        END IF;
    END IF;
END $$ LANGUAGE PLPGSQL;
-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_class_metadata_set(_classe regclass, _key varchar, _value varchar) RETURNS VOID AS $$ DECLARE
	_metadata jsonb;
BEGIN
	_metadata = _cm3_class_metadata_get(_classe);
	_metadata = jsonb_set(_metadata,ARRAY[_key::text],to_jsonb(_value));
	PERFORM _cm3_class_metadata_set(_classe,_metadata);
END $$ LANGUAGE PLPGSQL;


--- CLASS MISC ---

CREATE OR REPLACE FUNCTION _cm3_class_parent_get(_classe regclass) RETURNS regclass AS $$ BEGIN
	RETURN  (SELECT inhparent::regclass FROM pg_inherits WHERE inhrelid = _classe) ;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_utils_class_for_card(_superclass regclass, _card_id bigint) RETURNS regclass AS $$ DECLARE
	_res regclass;
BEGIN
	EXECUTE format('SELECT tableoid FROM %s WHERE "Id"= $1 LIMIT 1', _superclass) USING _card_id INTO _res;
	RETURN _res;
END $$ LANGUAGE PLPGSQL STABLE RETURNS NULL ON NULL INPUT;


--- CLASS CHECK ---

CREATE OR REPLACE FUNCTION _cm3_class_list_ancestors(_class regclass) RETURNS SETOF regclass AS $$ DECLARE
    _parent regclass;
BEGIN
    _parent = _cm3_class_parent_get(_class);
    IF _parent IS NOT NULL THEN
        RETURN QUERY SELECT _cm3_class_list_ancestors(_parent);
        RETURN NEXT _parent;
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_list_ancestors_and_self(_class regclass) RETURNS SETOF regclass AS $$ BEGIN
    RETURN QUERY SELECT _cm3_class_list_ancestors(_class);
    RETURN NEXT _class;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_is_process(_class regclass) RETURNS BOOLEAN AS $$
	SELECT '"Activity"'::regclass IN (SELECT _cm3_class_list_ancestors_and_self(_class));
$$ LANGUAGE SQL;

-- REQUIRE PATCH 3.3.0-17_doc_model_classes

CREATE OR REPLACE FUNCTION _cm3_class_is_dmsmodel(_class regclass) RETURNS BOOLEAN AS $$
	SELECT '"DmsModel"'::regclass IN (SELECT _cm3_class_list_ancestors_and_self(_class));
$$ LANGUAGE SQL;

-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_class_type_get(_class regclass) RETURNS varchar AS $$
    SELECT _cm3_class_type_get(_cm3_class_comment_get(_class,'TYPE'))
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_type_get(_type_val varchar) RETURNS varchar AS $$
    SELECT CASE _type_val
        WHEN 'domain' THEN 'domain'
        WHEN 'class' THEN 'class'
        WHEN 'simpleclass' THEN 'class'
        WHEN 'function' THEN 'function'
        ELSE 'unknown' END;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_simple(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'TYPE') = 'simpleclass';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_domain(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'TYPE') = 'domain';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_standard(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'TYPE') = 'class';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_history(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_utils_regclass_to_name(_class) LIKE '%_history';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_superclass(_class regclass) RETURNS boolean AS $$
	SELECT _cm3_class_comment_get(_class,'SUPERCLASS') = 'true';
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_has_history(_class regclass) RETURNS boolean AS $$
	SELECT ( _cm3_class_is_standard(_class) OR _cm3_class_is_domain(_class) ) AND NOT _cm3_class_is_superclass(_class);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_is_simple_or_standard(_class regclass) RETURNS boolean AS $$
	SELECT  _cm3_class_comment_get(_class,'TYPE') IN ('class','simpleclass');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_exists(_class regclass) RETURNS BOOLEAN AS $$
	SELECT NULLIF(_cm3_class_comment_get(_class, 'TYPE'), '') IS NOT NULL;--TODO check this
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_exists(_class_name varchar) RETURNS BOOLEAN AS $$
	SELECT _cm3_utils_name_to_regclass(_class_name) IS NOT NULL;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_exists(_class_name text) RETURNS BOOLEAN AS $$
	SELECT _cm3_class_exists(_class_name::varchar);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_has_records(_class regclass) RETURNS boolean AS $$ DECLARE
	_has_records boolean;
BEGIN
	EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s)', _class) INTO _has_records;
	RETURN _has_records;
END $$ LANGUAGE PLPGSQL;


--- CARD MISC ---

CREATE OR REPLACE FUNCTION _cm3_card_info_get(_class regclass, _card_id bigint, _include_deleted boolean, OUT "IdClass" regclass, OUT "Description" varchar, OUT "Code" varchar) RETURNS record AS $$ DECLARE
	_ignore_tenant_policies VARCHAR;
BEGIN
	IF _card_id IS NULL THEN
		RETURN;
	ELSE
		_ignore_tenant_policies = coalesce(current_setting('cmdbuild.ignore_tenant_policies', 'true'), 'true');
		SET SESSION cmdbuild.ignore_tenant_policies = 'true';
		IF _cm3_class_is_simple(_class) THEN --TODO handle simple class with code or description attrs
			EXECUTE format('SELECT "IdClass", NULL::varchar "Code", NULL::varchar "Description" FROM %s WHERE "Id" = %L', _class, _card_id) INTO "IdClass", "Code", "Description";
		ELSEIF _include_deleted THEN
			EXECUTE format('SELECT "IdClass", "Code", "Description" FROM %s WHERE "Id" = %L', _class, _card_id) INTO "IdClass", "Code", "Description";
		ELSE
			EXECUTE format('SELECT "IdClass", "Code", "Description" FROM %s WHERE "Id" = %L AND "Status" = ''A''', _class, _card_id)  INTO "IdClass", "Code", "Description";
		END IF;
		PERFORM set_config('cmdbuild.ignore_tenant_policies', _ignore_tenant_policies, FALSE);
		RETURN;
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_info_get(_class regclass, _card_id bigint, OUT "IdClass" regclass, OUT "Description" varchar, OUT "Code" varchar) RETURNS record AS $$ BEGIN
    SELECT x."IdClass", x."Code", x."Description" FROM _cm3_card_info_get(_class, _card_id, FALSE) x INTO "IdClass", "Code", "Description";
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_description_get(_class regclass, _card_id bigint) RETURNS varchar AS $$ BEGIN
    RETURN (SELECT "Description" FROM _cm3_card_info_get(_class, _card_id));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_code_get(_class regclass, _card_id bigint) RETURNS varchar AS $$ BEGIN
    RETURN (SELECT "Code" FROM _cm3_card_info_get(_class, _card_id));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_type_get(_class regclass, _card_id bigint) RETURNS regclass AS $$ BEGIN
    RETURN (SELECT "IdClass" FROM _cm3_card_info_get(_class, _card_id));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_description_get(_class regclass, _card_id bigint, _include_deleted boolean) RETURNS varchar AS $$ BEGIN
    RETURN (SELECT "Description" FROM _cm3_card_info_get(_class, _card_id, _include_deleted));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_code_get(_class regclass, _card_id bigint, _include_deleted boolean) RETURNS varchar AS $$ BEGIN
    RETURN (SELECT "Code" FROM _cm3_card_info_get(_class, _card_id, _include_deleted));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_type_get(_class regclass, _card_id bigint, _include_deleted boolean) RETURNS regclass AS $$ BEGIN
    RETURN (SELECT "IdClass" FROM _cm3_card_info_get(_class, _card_id, _include_deleted));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_list_at_date(_any anyelement, _date date) RETURNS setof anyelement AS $$ BEGIN
    RETURN QUERY EXECUTE format('SELECT * FROM %s WHERE "BeginDate" < (%L::date+1)::timestamptz AND ("Status" = ''A'' OR ("Status" = ''U'' AND "EndDate" >= (%L::date+1)::timestamptz))', pg_typeof(_any)::varchar::regclass, _date, _date);
END $$ LANGUAGE PLPGSQL;


--- CARD CHECK ---

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_value(_class regclass,_attr varchar,_value bigint) RETURNS BOOLEAN AS $$ BEGIN
	RETURN _cm3_card_exists_with_value(_class,_attr,_value,FALSE);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_value(_class regclass,_attr varchar,_value varchar) RETURNS BOOLEAN AS $$ BEGIN
	RETURN _cm3_card_exists_with_value(_class,_attr,_value,FALSE);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_value(_class regclass,_attr varchar,_value bigint,_include_deleted boolean) RETURNS BOOLEAN AS $$ DECLARE
    _res boolean;
BEGIN
	IF _value IS NULL THEN
		RETURN FALSE;
	ELSEIF _cm3_class_is_standard(_class) AND NOT _include_deleted THEN
        EXECUTE format('SELECT EXISTS ( SELECT "Id" FROM %s WHERE %I = %L AND "Status"=''A'' )', _class, _attr, _value) INTO _res;
    ELSE
        EXECUTE format('SELECT EXISTS ( SELECT "Id" FROM %s WHERE %I = %L )', _class, _attr, _value) INTO _res;
    END IF;
    RETURN _res;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_value(_class regclass,_attr varchar,_value varchar,_include_deleted boolean) RETURNS BOOLEAN AS $$ DECLARE
    _res boolean;
BEGIN
	IF _value IS NULL THEN
		RETURN FALSE;
	ELSEIF _cm3_class_is_standard(_class) AND NOT _include_deleted THEN
        EXECUTE format('SELECT EXISTS ( SELECT "Id" FROM %s WHERE %I = %L AND "Status"=''A'' )', _class, _attr, _value) INTO _res;
    ELSE
        EXECUTE format('SELECT EXISTS ( SELECT "Id" FROM %s WHERE %I = %L )', _class, _attr, _value) INTO _res;
    END IF;
    RETURN _res;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_id(_class regclass, _card_id bigint, _include_deleted boolean) RETURNS BOOLEAN AS $$
    SELECT "IdClass" IS NOT NULL FROM _cm3_card_info_get(_class, _card_id, _include_deleted);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_card_exists_with_code(_class regclass, _code varchar, _include_deleted boolean) RETURNS BOOLEAN AS $$
	SELECT _cm3_card_exists_with_value(_class, 'Code', _code, _include_deleted);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_card_creationdate_get(_class regclass, _card bigint) RETURNS timestamptz AS $$ DECLARE
    _res timestamptz;
BEGIN
	EXECUTE format('SELECT MIN("BeginDate") FROM %s WHERE "CurrentId" = %s', _class, _card) INTO _res;
    RETURN _res;
END $$ LANGUAGE PLPGSQL;


--- CLASS LIST ---

CREATE OR REPLACE FUNCTION _cm3_class_list() RETURNS SETOF regclass AS $$
	SELECT c.oid::regclass FROM pg_class c
        JOIN pg_description d ON d.objoid = c.oid AND objsubid = 0
        WHERE _cm3_comment_to_jsonb(description)->>'TYPE' IN ('class','simpleclass')
            AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public')
            AND c.oid::regclass <> '"SimpleClass"'::regclass
        ORDER BY c.oid::regclass::varchar;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_gis() RETURNS SETOF regclass AS $$
	SELECT c.oid::regclass FROM pg_class c
        JOIN pg_description d ON d.objoid = c.oid AND objsubid = 0
        WHERE _cm3_comment_to_jsonb(description)->>'TYPE' IN ('class','simpleclass')
            AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='gis')
            AND c.oid::regclass <> '"SimpleClass"'::regclass
        ORDER BY c.oid::regclass::varchar;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_simple() RETURNS SETOF regclass AS $$
	SELECT c.oid::regclass FROM pg_class c
        JOIN pg_description d ON d.objoid = c.oid AND objsubid = 0
        WHERE _cm3_comment_to_jsonb(description)->>'TYPE' = 'simpleclass'
            AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public')
            AND c.oid::regclass <> '"SimpleClass"'::regclass
        ORDER BY c.oid::regclass::varchar;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_standard() RETURNS SETOF regclass AS $$
	SELECT c.oid::regclass FROM pg_class c
        JOIN pg_description d ON d.objoid = c.oid AND objsubid = 0
        WHERE _cm3_comment_to_jsonb(description)->>'TYPE' = 'class'
            AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public')
            AND c.oid::regclass <> '"SimpleClass"'::regclass
        ORDER BY c.oid::regclass::varchar;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_hierarchy(_class regclass) RETURNS SETOF regclass AS $$ BEGIN
    RETURN QUERY SELECT * FROM _cm3_class_list_ancestors_and_self(_class) UNION SELECT * FROM _cm3_class_list_descendants_and_self(_class);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_class_list_descendants_and_self(_class regclass) RETURNS SETOF regclass AS $$
	SELECT _class
		UNION
	SELECT i.inhrelid::regclass FROM pg_catalog.pg_inherits i WHERE i.inhparent = _class
		UNION
	SELECT _cm3_class_list_descendants_and_self(i.inhrelid) FROM pg_catalog.pg_inherits i WHERE i.inhparent = _class;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_list_descendant_classes_and_self(_class regclass) RETURNS SETOF regclass AS $$
	SELECT c FROM _cm3_class_list_descendants_and_self(_class) c WHERE _cm3_class_is_standard(c) OR _cm3_class_is_simple(c) OR _cm3_class_is_domain(c);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_list_descendant_classes(_class regclass) RETURNS SETOF regclass AS $$
	SELECT c FROM _cm3_class_list_descendant_classes_and_self(_class) c WHERE c <> _class;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_list_descendant_classes_and_self_not_superclass(_class regclass) RETURNS SETOF regclass AS $$
	SELECT c FROM _cm3_class_list_descendant_classes_and_self(_class) c WHERE NOT _cm3_class_is_superclass(c);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_class_has_descendants(_class regclass) RETURNS boolean AS $$
	SELECT EXISTS(SELECT c FROM _cm3_class_list_descendant_classes_and_self(_class) c WHERE c <> _class);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_process_list() RETURNS SETOF regclass AS $$
	SELECT _cm3_class_list_descendant_classes_and_self_not_superclass('"Activity"'::regclass);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_class_list_detailed() RETURNS TABLE (table_id int, table_name varchar, features jsonb, is_process boolean, parent_id int, parent_name varchar, ancestor_ids int[], ancestor_names varchar[]) AS $$
	WITH _classes AS (SELECT
                c,
                _cm3_class_parent_get(c) AS p,
                _cm3_class_features_get(c) AS features,
                (SELECT array_agg(a) FROM _cm3_class_list_ancestors(c) a) ancestors
             FROM _cm3_class_list() c)
        SELECT
            c::oid::int,
            _cm3_utils_regclass_to_name(c),
            features,
            (SELECT '"Activity"'::regclass = ANY (ancestors) OR '"Activity"'::regclass = c),
            p::oid::int,
            _cm3_utils_regclass_to_name(p),
            (SELECT array_agg(a::oid::int) FROM unnest(ancestors) a),
            (SELECT array_agg(_cm3_utils_regclass_to_name(a)) FROM unnest(ancestors) a)
        FROM _classes;
$$ LANGUAGE SQL STABLE;

-- REQUIRE PATCH 3.3.0-17_doc_model_classes
CREATE OR REPLACE FUNCTION _cm3_class_list_detailed() RETURNS TABLE (table_id int, table_name varchar, features jsonb, parent_id int, parent_name varchar, ancestor_ids int[], ancestor_names varchar[]) AS $$
	WITH _classes AS (SELECT
                c,
                _cm3_class_parent_get(c) AS p,
                _cm3_class_features_get(c) AS features,
                (SELECT array_agg(a) FROM _cm3_class_list_ancestors(c) a) ancestors
             FROM _cm3_class_list() c)
        SELECT
            c::oid::int,
            _cm3_utils_regclass_to_name(c),
            features || jsonb_build_object('cm_class_speciality', CASE
                WHEN (SELECT '"Activity"'::regclass = ANY (ancestors) OR '"Activity"'::regclass = c) THEN 'process'
                WHEN (SELECT '"DmsModel"'::regclass = ANY (ancestors) OR '"DmsModel"'::regclass = c) THEN 'dmsmodel'
                ELSE 'default' END),
            p::oid::int,
            _cm3_utils_regclass_to_name(p),
            (SELECT array_agg(a::oid::int) FROM unnest(ancestors) a),
            (SELECT array_agg(_cm3_utils_regclass_to_name(a)) FROM unnest(ancestors) a)
        FROM _classes;
$$ LANGUAGE SQL STABLE;
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- DOMAIN LIST ---

CREATE OR REPLACE FUNCTION _cm3_domain_list() RETURNS SETOF regclass AS $$
	SELECT c.oid::regclass FROM pg_class c
        JOIN pg_description d ON d.objoid = c.oid AND objsubid = 0
        WHERE _cm3_comment_to_jsonb(description)->>'TYPE' = 'domain'
            AND relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public')
            AND c.oid::regclass <> '"Map"'::regclass
        ORDER BY c.oid::regclass::varchar;
$$ LANGUAGE SQL STABLE;


--- ATTRIBUTE COMMENT ---

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_keys() RETURNS SETOF varchar AS $$
	SELECT unnest(ARRAY['DESCR','BASEDSP','CLASSORDER','EDITORTYPE','GROUP','INDEX','LOOKUP','REFERENCEDIR','REFERENCEDOM','FKTARGETCLASS','CASCADE','FILTER','IP_TYPE','ACTIVE','MODE','DOMAINKEY','GISATTR','CLASSREFPATHS','ITEMS']);
$$ LANGUAGE SQL IMMUTABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_get_raw(_class regclass, _attr varchar) RETURNS varchar AS $$
	SELECT description FROM pg_description
		JOIN pg_attribute ON pg_description.objoid = pg_attribute.attrelid AND pg_description.objsubid = pg_attribute.attnum
		WHERE attrelid = _class and attname = _attr;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_get(_class regclass, _attr varchar) RETURNS jsonb AS $$
	SELECT _cm3_comment_to_jsonb(_cm3_attribute_comment_get_raw(_class, _attr));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_set(_class regclass, _attr varchar, _comment jsonb) RETURNS void AS $$ DECLARE
	_ref_attr int;
	_key varchar;
BEGIN
	_comment = _cm3_utils_strip_null_or_empty(_comment);
	FOR _key IN SELECT x FROM jsonb_object_keys(_comment) x WHERE x NOT IN (SELECT _cm3_attribute_comment_keys()) LOOP
		RAISE WARNING 'CM: invalid comment for class = % attr = %: invalid comment key = %', _class, _attr, _key;
	END LOOP;
	_ref_attr = (SELECT count(x) FROM jsonb_object_keys(_comment) x WHERE x IN ('REFERENCEDOM','LOOKUP','FKTARGETCLASS') AND NULLIF(_comment->>x, '') IS NOT NULL );
	IF _ref_attr > 1 THEN
		RAISE EXCEPTION 'CM: invalid comment for class = % attr = %, comment = %: attribute type error (too many attribute type specified)', _class, _attr, _comment;
	END IF;
	RAISE DEBUG 'update class attribute comment %.% = %', _class, _attr, _comment;
	EXECUTE format('COMMENT ON COLUMN %s.%I IS %L', _class, _attr, _cm3_comment_from_jsonb(_comment));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_update(_class regclass, _attr varchar, _comment jsonb) RETURNS void AS $$ BEGIN
	PERFORM _cm3_attribute_comment_set(_class, _attr, _cm3_attribute_comment_get(_class, _attr) || _comment);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_get(_class regclass, _attr varchar, _part varchar) RETURNS varchar AS $$
	SELECT coalesce(_cm3_attribute_comment_get(_class,_attr)->>_part,'');
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_set(_class regclass, _attr varchar, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_attribute_comment_update(_class, _attr, jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_comment_delete(_class regclass, _attr varchar, _key varchar) RETURNS void AS $$
	SELECT _cm3_attribute_comment_set(_class, _attr, _cm3_attribute_comment_get(_class, _attr) - _key);
$$ LANGUAGE SQL;


--- ATTRIBUTE METADATA ---

CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_get(_classe regclass, _attr varchar) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Metadata" FROM "_AttributeMetadata" WHERE "Owner" = _classe AND "Code" = _attr AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_set(_class regclass, _attr varchar, _metadata jsonb) RETURNS void AS $$ BEGIN
    IF NOT EXISTS ( SELECT 1 FROM pg_class c WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND c.oid::regclass = _class ) THEN
        RAISE WARNING 'skip attribute metadata set for class = % : class not found in `public` namespace', _class;
    ELSE
        _metadata = _cm3_utils_strip_null_or_empty(_metadata);
        IF EXISTS (SELECT * FROM "_AttributeMetadata" WHERE "Owner" = _class AND "Code" = _attr AND "Status" = 'A') THEN
            IF _metadata = '{}'::jsonb THEN
                UPDATE "_AttributeMetadata" SET "Status" = 'N' WHERE "Owner" = _class AND "Code" = _attr AND "Status" = 'A';
            ELSE
                UPDATE "_AttributeMetadata" SET "Metadata" = _metadata WHERE "Owner" = _class AND "Code" = _attr AND "Status" = 'A';
            END IF;
        ELSE
            IF _metadata <> '{}'::jsonb THEN
                INSERT INTO "_AttributeMetadata" ("Owner","Code","Status","Metadata") VALUES (_class,_attr,'A',_metadata);
            END IF;
        END IF;
    END IF;
END $$ LANGUAGE plpgsql;

-- REQUIRE PATCH 3.3.0-29_class_structure
CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_get(_class regclass, _attr varchar) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Metadata" FROM "_AttributeMetadata" WHERE "Owner" = _cm3_utils_regclass_to_name(_class) AND "Code" = _attr AND "Status" = 'A'), '{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_set(_class regclass, _attr varchar, _metadata jsonb) RETURNS void AS $$ BEGIN
    IF NOT EXISTS ( SELECT 1 FROM pg_class c WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public') AND c.oid::regclass = _class ) THEN
        RAISE WARNING 'skip attribute metadata set for class = % : class not found in `public` namespace', _class;
    ELSE
        _metadata = _cm3_utils_strip_null_or_empty(_metadata);
        IF EXISTS (SELECT * FROM "_AttributeMetadata" WHERE "Owner" = _cm3_utils_regclass_to_name(_class) AND "Code" = _attr AND "Status" = 'A') THEN
            IF _metadata = '{}'::jsonb THEN
                UPDATE "_AttributeMetadata" SET "Status" = 'N' WHERE "Owner" = _cm3_utils_regclass_to_name(_class) AND "Code" = _attr AND "Status" = 'A';
            ELSE
                UPDATE "_AttributeMetadata" SET "Metadata" = _metadata WHERE "Owner" = _cm3_utils_regclass_to_name(_class) AND "Code" = _attr AND "Status" = 'A';
                PERFORM _cm3_attribute_notnull_set(_class, _attr, COALESCE((_metadata ->> 'NOTNULL')::boolean, FALSE));
                PERFORM _cm3_attribute_unique_set(_class, _attr, COALESCE((_metadata ->> 'UNIQUE')::boolean, FALSE));
            END IF;
        ELSE
            IF _metadata <> '{}'::jsonb THEN
                INSERT INTO "_AttributeMetadata" ("Owner", "Code", "Status", "Metadata") VALUES (_cm3_utils_regclass_to_name(_class), _attr, 'A', _metadata);
                PERFORM _cm3_attribute_notnull_set(_class, _attr, COALESCE((_metadata ->> 'NOTNULL')::boolean, FALSE));
                PERFORM _cm3_attribute_unique_set(_class, _attr, COALESCE((_metadata ->> 'UNIQUE')::boolean, FALSE));
            END IF;
        END IF;
    END IF;
END $$ LANGUAGE plpgsql;
-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_attribute_metadata_set(_class regclass, _attr varchar, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_attribute_metadata_set(_class, _attr, _cm3_attribute_metadata_get(_class, _attr) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;


--- ATTRIBUTE FEATURES ---

CREATE OR REPLACE FUNCTION _cm3_attribute_features_get(_classe regclass, _attr varchar) RETURNS jsonb AS $$ DECLARE
    _features jsonb;
BEGIN
	_features = _cm3_attribute_metadata_get(_classe, _attr) || _cm3_attribute_comment_get(_classe, _attr);
    IF _cm3_attribute_unique_get(_classe, _attr) THEN
        _features = _features || jsonb_build_object('UNIQUE', 'TRUE');
    END IF;
    IF _cm3_attribute_notnull_get(_classe, _attr) THEN
        _features = _features || jsonb_build_object('NOTNULL', 'TRUE');
    END IF;
    RETURN _features;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_get(_classe regclass, _attr varchar, _key varchar) RETURNS varchar AS $$ BEGIN
	RETURN coalesce(_cm3_attribute_features_get(_classe, _attr)->>_key, '');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_set(_classe regclass, _attr varchar, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_comment_set(_classe, _attr, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key IN (SELECT _cm3_attribute_comment_keys())));
	PERFORM _cm3_attribute_metadata_set(_classe, _attr, (SELECT coalesce(jsonb_object_agg(key, value), '{}'::jsonb) FROM jsonb_each_text(_features) WHERE key NOT IN (SELECT _cm3_attribute_comment_keys())));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_set(_classe regclass, _attr varchar, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_features_set(_classe, _attr, _cm3_attribute_features_get(_classe, _attr) || jsonb_build_object(_key, _value));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_delete(_classe regclass, _attr varchar, _key varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_features_set(_classe, _attr, _cm3_attribute_features_get(_classe, _attr) - _key);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_features_update(_classe regclass, _attr varchar, _features jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_attribute_features_set(_classe, _attr, _cm3_attribute_features_get(_classe, _attr) || _features);
END $$ LANGUAGE PLPGSQL;


--- ATTRIBUTE CHECK ---

CREATE OR REPLACE FUNCTION _cm3_attribute_unique_get(_class regclass, _attr varchar) RETURNS boolean AS $$ BEGIN
    RETURN EXISTS (SELECT * FROM pg_attribute a JOIN pg_index i ON a.attrelid = i.indrelid AND a.attnum = i.indkey[0] WHERE i.indnatts = 1 AND i.indisunique AND a.attrelid = _class AND a.attname = _attr);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_notnull_get(_class regclass, _attr varchar) RETURNS boolean AS $$ DECLARE
	_is_notnull boolean;
BEGIN
    SELECT pg_attribute.attnotnull OR c.oid IS NOT NULL
    FROM pg_attribute
    LEFT JOIN pg_constraint AS c ON c.conrelid = pg_attribute.attrelid AND c.conname::text = format('_cm3_%s_notnull', pg_attribute.attname)
    WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr INTO _is_notnull;
    RETURN _is_notnull;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.3.0-49b_domain_attribute_notnull_update
CREATE OR REPLACE FUNCTION _cm3_attribute_notnull_get(_class regclass, _attr varchar) RETURNS boolean AS $$ DECLARE
	_is_notnull boolean;
BEGIN
    IF _cm3_class_is_domain(_class) THEN
	SELECT EXISTS(
        SELECT 1
	FROM pg_trigger
	WHERE NOT tgisinternal AND tgname::text = LOWER(format('_cm3_%s_notnull_trigger', _attr))
	AND tgrelid = _class INTO _is_notnull);
    ELSE
	SELECT pg_attribute.attnotnull OR c.oid IS NOT NULL
	FROM pg_attribute
	LEFT JOIN pg_constraint AS c ON c.conrelid = pg_attribute.attrelid AND c.conname::text = format('_cm3_%s_notnull', pg_attribute.attname)
	WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr INTO _is_notnull;
    END IF;
    RETURN _is_notnull;
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.0.0-03a_system_functions
CREATE OR REPLACE FUNCTION _cm3_attribute_is_inherited(_class regclass, _attr varchar) RETURNS boolean AS $$
	SELECT pg_attribute.attinhcount <> 0 FROM pg_attribute WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr;
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_has_data(_class regclass, _attr varchar) RETURNS boolean AS $$ DECLARE
	_has_data boolean;
BEGIN
	IF _cm3_class_is_simple(_class) THEN
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I IS NOT NULL AND %I::text <> '''')', _class, _attr, _attr) INTO _has_data;
	ELSE
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I IS NOT NULL AND %I::text <> '''' AND "Status" = ''A'')', _class, _attr, _attr) INTO _has_data;
	END IF;
	RETURN _has_data;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_has_value(_class regclass, _attr varchar, _value bigint) RETURNS boolean AS $$ DECLARE
	_has_value boolean;
BEGIN
	IF _cm3_class_is_simple(_class) THEN
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I = %L)', _class, _attr, _value) INTO _has_value;
	ELSE
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I = %L AND "Status" = ''A'')', _class, _attr, _value) INTO _has_value;
	END IF;
	RETURN _has_value;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_has_value(_class regclass, _attr varchar, _value varchar) RETURNS boolean AS $$ DECLARE
	_has_value boolean;
BEGIN
	IF _cm3_class_is_simple(_class) THEN
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I = %L)', _class, _attr, _value) INTO _has_value;
	ELSE
		EXECUTE format('SELECT EXISTS (SELECT 1 FROM %s WHERE %I = %L AND "Status" = ''A'')', _class, _attr, _value) INTO _has_value;
	END IF;
	RETURN _has_value;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_sqltype_get(_class regclass, _attr varchar) RETURNS varchar AS $$
	SELECT _cm3_utils_build_sqltype_string(pg_attribute.atttypid, pg_attribute.atttypmod) FROM pg_attribute WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_default_get(_class regclass, _attr varchar) RETURNS varchar AS $$
	SELECT pg_get_expr(adbin,adrelid) FROM pg_attribute JOIN pg_attrdef ON pg_attrdef.adrelid = pg_attribute.attrelid AND pg_attrdef.adnum = pg_attribute.attnum WHERE pg_attribute.attrelid = _class AND pg_attribute.attname = _attr;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_is_reference(_class regclass, _attr varchar) RETURNS BOOLEAN AS $$
	SELECT _cm3_attribute_comment_get(_class, _attr, 'REFERENCEDOM') <> '';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_domain_get(_class regclass, _attr varchar) RETURNS regclass AS $$
	SELECT _cm3_utils_name_to_regclass_not_null('Map_' || _cm3_attribute_comment_get(_class, _attr, 'REFERENCEDOM'))
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_direction_get(_class regclass, _attr varchar) RETURNS varchar AS $$ DECLARE
	_direction varchar;
BEGIN
	_direction = _cm3_attribute_comment_get(_class, _attr, 'REFERENCEDIR');
	IF NOT _direction IN ('direct','inverse') THEN
		RAISE EXCEPTION 'invalid reference dir for class = % attr = % value = %', _class, _attr, _direction;
	END IF;
	RETURN _direction;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_cascade_get(_class regclass, _attr varchar) RETURNS varchar AS $$ BEGIN
	RETURN _cm3_domain_cascade_get(_cm3_attribute_reference_domain_get(_class, _attr), _cm3_attribute_reference_direction_get(_class, _attr));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_cascade_get(_domain regclass, _direction varchar) RETURNS varchar AS $$ DECLARE
	_cascade varchar;
BEGIN
    _cascade = _cm3_utils_first_not_blank(_cm3_class_features_get(_domain, (CASE _direction WHEN 'direct' THEN 'CASCADEDIRECT' ELSE 'CASCADEINVERSE' END)), 'AUTO');
    IF _cascade ILIKE 'auto' THEN
        _cascade = 'setnull';
        IF EXISTS (SELECT * FROM _cm3_attribute_list_detailed() WHERE comment->>'REFERENCEDOM' = _cm3_utils_regclass_to_domain_name(_domain) AND comment->>'REFERENCEDIR' = _cm3_utils_direction_inverse(_direction)) THEN
            _cascade = 'restrict';
        END IF;
    END IF;
    IF NOT _cascade IN ('restrict', 'setnull', 'delete') THEN
		RAISE EXCEPTION 'invalid reference cascade for domain = % attr = % value = %', _class, _attr, _cascade;
	END IF;
	RETURN _cascade;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_domain_cardin_get(_domain regclass) RETURNS varchar AS $$ DECLARE
    _cardin varchar;
BEGIN
    _cardin = _cm3_utils_first_not_blank(_cm3_class_features_get(_domain, 'CARDIN'), 'N:N');
    IF NOT _cardin IN ('1:N', 'N:N', 'N:1', '1:1') THEN
		RAISE EXCEPTION 'invalid cardin for domain = % value = %', _class, _cardin;
	END IF;
	RETURN _cardin;
END $$ LANGUAGE PLPGSQL;


CREATE OR REPLACE FUNCTION _cm3_utils_direction_inverse(_direction varchar) RETURNS varchar AS $$ BEGIN
    RETURN CASE _direction WHEN 'direct' THEN 'inverse' ELSE 'direct' END;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_is_direct(_class regclass, _attr varchar) RETURNS boolean AS $$ BEGIN
	RETURN _cm3_attribute_reference_direction_get(_class, _attr) = 'direct';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_is_lookup(_class regclass, _attr varchar) RETURNS BOOLEAN AS $$
	SELECT _cm3_attribute_comment_get(_class, _attr, 'LOOKUP') <> '';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_is_foreignkey(_class regclass, _attr varchar) RETURNS BOOLEAN AS $$
	SELECT _cm3_attribute_comment_get(_class, _attr, 'FKTARGETCLASS') <> '';
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_foreignkey_target_get(_class regclass, _attr varchar) RETURNS regclass AS $$
	SELECT _cm3_utils_name_to_regclass_not_null(_cm3_attribute_comment_get(_class, _attr, 'FKTARGETCLASS'))
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_reference_target_class_get(_class regclass, _attr varchar) RETURNS regclass AS $$ BEGIN
	RETURN _cm3_domain_target_class_get(_cm3_attribute_reference_domain_get(_class, _attr), _cm3_attribute_reference_is_direct(_class, _attr));
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_foreignkey_or_reference_target_class_get(_class regclass, _attr varchar) RETURNS regclass AS $$ BEGIN
	IF _cm3_attribute_is_foreignkey(_class, _attr) THEN
		RETURN _cm3_attribute_foreignkey_target_get(_class, _attr);
	ELSEIF _cm3_attribute_is_reference(_class, _attr) THEN
		RETURN  _cm3_attribute_reference_target_class_get(_class, _attr);
	ELSE
		RAISE EXCEPTION 'attribute is not reference nor foreignkey for class = % attr = %', _class, _attr;
	END IF;
END $$ LANGUAGE PLPGSQL STABLE;


--- ATTRIBUTE LIST ---

CREATE OR REPLACE FUNCTION _cm3_attribute_exists(_class regclass, _attr varchar) RETURNS boolean AS $$
	SELECT EXISTS (SELECT * FROM pg_attribute WHERE attrelid = _class AND attnum > 0 AND atttypid > 0 AND attname::varchar = _attr);
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_list(_class regclass) RETURNS SETOF varchar AS $$
	SELECT attname::varchar FROM pg_attribute WHERE attrelid = _class AND attnum > 0 AND atttypid > 0 ORDER BY attnum;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_list() RETURNS TABLE(owner regclass, name varchar) AS $$
    WITH q AS ( SELECT c, attname::varchar, attnum FROM pg_attribute JOIN (SELECT c FROM _cm3_class_list() c UNION SELECT d FROM _cm3_domain_list() d ) c ON attrelid = c WHERE attnum > 0 AND atttypid > 0 ) SELECT c, attname FROM q ORDER BY attnum;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_attribute_list_detailed(_class regclass) RETURNS TABLE(
	owner regclass,
	owner_type varchar,
	name varchar,
	comment jsonb,
	not_null_constraint boolean,
	unique_constraint boolean,
	sql_type varchar,
	inherited boolean,
	default_value varchar,
	metadata jsonb
) AS $$ BEGIN
    RETURN QUERY WITH cq1 AS (
        SELECT c.oid::regclass classe, _cm3_comment_to_jsonb(description)->>'TYPE' type_val FROM pg_class c
            JOIN pg_description d ON d.objoid = c.oid AND d.objsubid = 0
            WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public')
                AND c.oid::regclass NOT IN ('"SimpleClass"'::regclass,'"Map"'::regclass)
                AND ( _class IS NULL OR _class = c.oid )
    ), cq2 AS (
        SELECT *, _cm3_class_type_get(type_val::varchar) class_type FROM cq1 WHERE type_val IN ('class','simpleclass','domain')
    ), q1 AS (
        SELECT
            c.classe AS owner,
            c.class_type AS owner_type,
            a.attname::varchar AS name,
            _cm3_comment_to_jsonb(d.description) AS comment,
            ( a.attnotnull OR notnull_constraint.oid IS NOT NULL ) AS not_null_constraint,
            EXISTS (SELECT 1 FROM pg_index unique_index WHERE a.attrelid = unique_index.indrelid AND a.attnum = unique_index.indkey[0] AND unique_index.indnatts = 1 AND unique_index.indisunique) AS unique_constraint,
            _cm3_utils_build_sqltype_string(a.atttypid, a.atttypmod) AS sql_type,
            a.attinhcount <> 0 AS inherited,
            pg_attrdef.adsrc::varchar AS default_value,
            COALESCE(m."Metadata",'{}'::jsonb) AS metadata
        FROM pg_attribute a
        JOIN cq2 c ON a.attrelid = c.classe
        LEFT JOIN pg_description d ON d.objoid = a.attrelid AND d.objsubid = a.attnum
        LEFT JOIN pg_constraint notnull_constraint ON notnull_constraint.conrelid = a.attrelid AND notnull_constraint.conname::text = format('_cm3_%s_notnull', a.attname)
        LEFT JOIN pg_attrdef ON pg_attrdef.adrelid = a.attrelid AND pg_attrdef.adnum = a.attnum
        LEFT JOIN "_AttributeMetadata" m ON m."Owner" = a.attrelid AND m."Code" = a.attname AND m."Status" = 'A'
        WHERE a.attnum > 0 AND a.atttypid > 0
    ), q2 AS ( SELECT q1.*, _cm3_utils_first_not_blank(q1.comment->>'INDEX', '2147483647')::int AS _index FROM q1 )
    SELECT q2.owner, q2.owner_type, q2.name, q2.comment, q2.not_null_constraint, q2.unique_constraint, q2.sql_type, q2.inherited, q2.default_value, q2.metadata FROM q2 ORDER BY q2.owner::varchar, _index;
END $$ LANGUAGE PLPGSQL STABLE;

-- REQUIRE PATCH 3.3.0-29_class_structure
CREATE OR REPLACE FUNCTION _cm3_attribute_list_detailed(_class regclass) RETURNS TABLE(
	owner regclass,
	owner_type varchar,
	name varchar,
	comment jsonb,
	not_null_constraint boolean,
	unique_constraint boolean,
	sql_type varchar,
	inherited boolean,
	default_value varchar,
	metadata jsonb
) AS $$ BEGIN
    RETURN QUERY WITH cq1 AS (
        SELECT c.oid::regclass classe, _cm3_comment_to_jsonb(description)->>'TYPE' type_val FROM pg_class c
            JOIN pg_description d ON d.objoid = c.oid AND d.objsubid = 0
            WHERE relnamespace = (SELECT oid FROM pg_namespace WHERE nspname='public')
                AND c.oid::regclass NOT IN ('"SimpleClass"'::regclass,'"Map"'::regclass)
                AND ( _class IS NULL OR _class = c.oid )
    ), cq2 AS (
        SELECT *, _cm3_class_type_get(type_val::varchar) class_type FROM cq1 WHERE type_val IN ('class','simpleclass','domain')
    ), q1 AS (
        SELECT
            c.classe AS owner,
            c.class_type AS owner_type,
            a.attname::varchar AS name,
            _cm3_comment_to_jsonb(d.description) AS comment,
            (CASE
		WHEN a.attname::varchar IN (SELECT _cm3_utils_domain_reserved_attributes()) THEN TRUE
		ELSE _cm3_attribute_notnull_get(c.classe, a.attname::varchar)
	    END) AS not_null_constraint,
            EXISTS (SELECT 1 FROM pg_index unique_index WHERE a.attrelid = unique_index.indrelid AND a.attnum = unique_index.indkey[0] AND unique_index.indnatts = 1 AND unique_index.indisunique) AS unique_constraint,
            _cm3_utils_build_sqltype_string(a.atttypid, a.atttypmod) AS sql_type,
            a.attinhcount <> 0 AS inherited,
            pg_get_expr(pg_attrdef.adbin, pg_attrdef.adrelid)::varchar AS default_value,
            COALESCE(m."Metadata",'{}'::jsonb) AS metadata
        FROM pg_attribute a
        JOIN cq2 c ON a.attrelid = c.classe
        LEFT JOIN pg_description d ON d.objoid = a.attrelid AND d.objsubid = a.attnum
        LEFT JOIN pg_constraint notnull_constraint ON notnull_constraint.conrelid = a.attrelid AND notnull_constraint.conname::text = format('_cm3_%s_notnull', a.attname)
        LEFT JOIN pg_attrdef ON pg_attrdef.adrelid = a.attrelid AND pg_attrdef.adnum = a.attnum
        LEFT JOIN "_AttributeMetadata" m ON m."Owner" = _cm3_utils_regclass_to_name(a.attrelid::regclass) AND m."Code" = a.attname AND m."Status" = 'A'
        WHERE a.attnum > 0 AND a.atttypid > 0
    ), q2 AS ( SELECT q1.*, _cm3_utils_first_not_blank(q1.comment->>'INDEX', '2147483647')::int AS _index FROM q1 )
    SELECT q2.owner, q2.owner_type, q2.name, q2.comment, q2.not_null_constraint, q2.unique_constraint, q2.sql_type, q2.inherited, q2.default_value, q2.metadata FROM q2 ORDER BY q2.owner::varchar, _index;
END $$ LANGUAGE PLPGSQL STABLE;
-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_attribute_list_detailed() RETURNS TABLE(
	owner regclass,
	owner_type varchar,
	name varchar,
	comment jsonb,
	not_null_constraint boolean,
	unique_constraint boolean,
	sql_type varchar,
	inherited boolean,
	default_value varchar,
	metadata jsonb
) AS $$
    SELECT * FROM _cm3_attribute_list_detailed(NULL)
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_index_list() RETURNS TABLE (owner regclass, index_name varchar, is_unique boolean, definition varchar) AS $$
    WITH q AS (select c.relname::varchar index_name,o.relname,o.oid::regclass AS owner,i.*,ix.* from pg_class c join pg_index i on c.oid = i.indexrelid join pg_class o on o.oid = i.indrelid join pg_indexes ix on c.relname = ix.indexname and ix.schemaname='public' where c.relname like format('_cm3_%s_%%', o.relname) and c.relnamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public'))
    SELECT owner, index_name, indisunique is_unique, indexdef definition FROM q ORDER BY owner::varchar, index_name; --TODO attrs, def
$$ LANGUAGE SQL STABLE;


--- DOMAIN CHECK ---

CREATE OR REPLACE FUNCTION _cm3_domain_target_class_get(_domain regclass, _direct boolean) RETURNS regclass AS $$ BEGIN
	RETURN CASE
		WHEN _direct
		THEN _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, 'CLASS2'))
		ELSE _cm3_utils_name_to_regclass(_cm3_class_comment_get(_domain, 'CLASS1'))
	END;
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_domain_target_classes_get(_domain regclass) RETURNS SETOF regclass AS $$ BEGIN
    RETURN QUERY SELECT _cm3_domain_target_classes_get(_domain, true);
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_domain_source_classes_get(_domain regclass) RETURNS SETOF regclass AS $$ BEGIN
    RETURN QUERY SELECT _cm3_domain_source_classes_get(_domain, true);
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_domain_source_classes_get(_domain regclass, _direction varchar) RETURNS SETOF regclass AS $$ BEGIN
    RETURN QUERY SELECT _cm3_domain_target_classes_get(_domain, _cm3_utils_direction_inverse(_direction));
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_domain_target_classes_get(_domain regclass, _direction varchar) RETURNS SETOF regclass AS $$ BEGIN
    RETURN QUERY SELECT _cm3_domain_target_classes_get(_domain, CASE _direction WHEN 'direct' THEN true ELSE false END);
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_domain_source_classes_get(_domain regclass, _direct boolean) RETURNS SETOF regclass AS $$ BEGIN
    RETURN QUERY SELECT _cm3_domain_target_classes_get(_domain, NOT _direct);
END $$ LANGUAGE PLPGSQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_domain_target_classes_get(_domain regclass, _direct boolean) RETURNS SETOF regclass AS $$ DECLARE
    _target_class regclass;
    _disabled_classes_attr varchar;
    _disabled_classes regclass[];
BEGIN
    _target_class = _cm3_domain_target_class_get(_domain, _direct);
    _disabled_classes_attr = CASE WHEN _direct
		THEN _cm3_class_comment_get(_domain, 'DISABLED2')
		ELSE _cm3_class_comment_get(_domain, 'DISABLED1') END;
    IF _cm3_utils_is_blank(_disabled_classes_attr) THEN
        _disabled_classes = ARRAY[]::regclass[];
    ELSE
        RAISE DEBUG 'disabled classes attr =< % >', _disabled_classes_attr;
        SELECT INTO _disabled_classes coalesce(array_agg(c), ARRAY[]::regclass[]) FROM (SELECT _cm3_utils_name_to_regclass(s) c FROM regexp_split_to_table(_disabled_classes_attr, ' *, *') s) x WHERE c IS NOT NULL;
        RAISE DEBUG 'disabled classes =< % >', _disabled_classes;
    END IF;
    RETURN QUERY SELECT c FROM _cm3_class_list_descendant_classes_and_self(_target_class) c WHERE c <> ALL (_disabled_classes);
END $$ LANGUAGE PLPGSQL STABLE;


--- CARD METADATA ---

CREATE OR REPLACE FUNCTION _cm3_card_metadata_set(_classe regclass,_card bigint,_metadata jsonb) RETURNS VOID AS $$ BEGIN
	IF NOT EXISTS (SELECT * FROM "_CardMetadata" WHERE "OwnerClass" = _classe AND "OwnerCard" = _card AND "Status" = 'A') THEN
		INSERT INTO "_CardMetadata" ("OwnerClass","OwnerCard","Data") VALUES (_classe,_card,_metadata);
	ELSE
		UPDATE "_CardMetadata" SET "Data" = _metadata WHERE "OwnerClass" = _classe AND "OwnerCard" = _card AND "Status" = 'A';
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_metadata_get(_classe regclass,_card bigint) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Data" FROM "_CardMetadata" WHERE "OwnerClass" = _classe AND "OwnerCard" = _card AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_card_metadata_set(_classe regclass, _card bigint, _key varchar, _value varchar) RETURNS VOID AS $$
	SELECT _cm3_card_metadata_set( _classe, _card, _cm3_card_metadata_get(_classe, _card) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;


--- FUNCTION COMMENT ---

CREATE OR REPLACE FUNCTION _cm3_function_comment_get(_function oid) RETURNS varchar AS $$
	SELECT description FROM pg_description WHERE objoid = _function;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_function_comment_get_jsonb(_function oid) RETURNS jsonb AS $$
	SELECT _cm3_comment_to_jsonb(_cm3_function_comment_get(_function));
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_function_comment_get(_function oid, _key varchar) RETURNS varchar AS $$
	SELECT _cm3_function_comment_get_jsonb(_function)->>_key;
$$ LANGUAGE SQL STABLE;

CREATE OR REPLACE FUNCTION _cm3_function_definition_get(_fun oid) RETURNS varchar AS $$
	SELECT format('%I(%s)', (SELECT proname FROM pg_proc WHERE oid = _fun), pg_get_function_identity_arguments(_fun));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_function_comment_set(_fun oid, _comment jsonb) RETURNS void AS $$ DECLARE
	_ref_attr int;
	_key varchar;
BEGIN
	FOR _key IN SELECT x FROM jsonb_object_keys(_comment) x
		WHERE x NOT IN ('CATEGORIES','MASTERTABLE','TAGS','ACTIVE','MODE','TYPE')
	LOOP
		RAISE WARNING 'CM: invalid comment for function = %: invalid comment key = %', _fun, _key;
	END LOOP;
	EXECUTE format('COMMENT ON FUNCTION %s IS %L', _cm3_function_definition_get(_fun), _cm3_comment_from_jsonb(_comment));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_function_comment_set(_fun oid, _key varchar, _value varchar) RETURNS void AS $$
	SELECT _cm3_function_comment_set(_fun, _cm3_function_comment_get_jsonb(_fun) || jsonb_build_object(_key, _value));
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_function_comment_delete(_fun oid, _key varchar) RETURNS void AS $$
	SELECT _cm3_function_comment_set(_fun, _cm3_function_comment_get_jsonb(_fun) - _key);
$$ LANGUAGE SQL;


--- FUNCTION LIST ---

CREATE OR REPLACE FUNCTION _cm3_function_list() RETURNS SETOF oid AS $$
	SELECT oid FROM pg_proc WHERE LOWER(_cm3_function_comment_get_jsonb(oid)->>'TYPE') = 'function' AND pronamespace = (SELECT oid FROM pg_namespace WHERE nspname = 'public');
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_function_list_detailed() RETURNS TABLE(
		function_name varchar,
		function_id oid,
		arg_io varchar[],
		arg_names varchar[],
		arg_types varchar[],
		returns_set boolean,
		features jsonb
	) AS $$ DECLARE
	_arg_io varchar[];
	_arg_names varchar[];
	_arg_types varchar[];
	_record record;
	_index integer;
BEGIN
	FOR _record IN SELECT *,oid FROM pg_proc WHERE oid IN (SELECT _cm3_function_list()) LOOP
		IF _record.proargmodes IS NULL THEN
			_arg_io = '{}'::varchar[];
			_arg_types = '{}'::varchar[];
			_arg_names = '{}'::varchar[];
			FOR _index IN SELECT generate_series(1, array_upper(_record.proargtypes,1)) LOOP
				_arg_io = _arg_io || '{i}';
				_arg_types = _arg_types || _cm3_utils_build_sqltype_string(_record.proargtypes[_index], NULL);
				_arg_names = _arg_names || COALESCE(_record.proargnames[_index]::varchar,('$'||_index)::varchar);
			END LOOP;
			_arg_io = _arg_io || '{o}';
			_arg_types = _arg_types || _cm3_utils_build_sqltype_string(_record.prorettype, NULL);
			_arg_names = _arg_names || _record.proname::varchar;
		ELSE
			_arg_io = _record.proargmodes;
			_arg_types = '{}'::varchar[];
			_arg_names = _record.proargnames;
			FOR _index IN SELECT generate_series(1, array_upper(_arg_io,1)) LOOP
				IF _arg_io[_index] = 't' THEN
					_arg_io[_index] = 'o';
				ELSEIF _arg_io[_index] = 'b' THEN
					_arg_io[_index] = 'io';
				ELSEIF _arg_io[_index] NOT IN ('i','o', 'io') THEN
					RAISE 'unsupported arg io value = % for function = %', _arg_io[_index], _record.proname;
				END IF;
				_arg_types = _arg_types || _cm3_utils_build_sqltype_string(_record.proallargtypes[_index], NULL);
				IF _arg_names[_index] = '' THEN
					IF _arg_io[_index] = 'i' THEN
						_arg_names[_index] = '$'||_index;
					ELSE
						_arg_names[_index] = 'column'||_index;
					END IF;
				END IF;
			END LOOP;
		END IF;
		RETURN QUERY SELECT
			_record.proname::varchar,
			_record.oid,
			_arg_io,
			_arg_names,
			_arg_types,
			_record.proretset,
			_cm3_function_comment_get_jsonb(_record.oid) || _cm3_function_metadata_get(_record.oid);
	END LOOP;
END $$ LANGUAGE PLPGSQL STABLE;


--- FUNCTION METADATA ---

CREATE OR REPLACE FUNCTION _cm3_function_metadata_get(_function oid, _key varchar) RETURNS varchar AS $$ BEGIN
	RETURN jsonb_extract_path_text(_cm3_function_metadata_get(_function), _key::text);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_function_metadata_set(_function oid, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_function_metadata_set(_function, _cm3_function_metadata_get(_function) || jsonb_build_object(_key, _value));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_function_metadata_set(_function varchar, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_function_metadata_set(_function::regproc, _key, _value);
END $$ LANGUAGE PLPGSQL;

-- REQUIRE PATCH 3.2.0-36_improve_function_metadata_table
CREATE OR REPLACE FUNCTION _cm3_function_metadata_get(_function oid) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Data" FROM "_FunctionMetadata" WHERE "Owner" = ( _function::regproc::varchar ) AND "Status" = 'A'), '{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_function_metadata_set(_function oid, _metadata jsonb) RETURNS VOID AS $$ BEGIN
	IF EXISTS (SELECT 1 FROM "_FunctionMetadata" WHERE "Owner" = ( _function::regproc::varchar ) AND "Status" = 'A') THEN
		UPDATE "_FunctionMetadata" SET "Data" = _metadata WHERE "Owner" = ( _function::regproc::varchar ) AND "Status" = 'A';
	ELSE
		INSERT INTO "_FunctionMetadata" ("Owner","Data") VALUES (_function::regproc::varchar, _metadata);
	END IF;
END $$ LANGUAGE PLPGSQL;
-- REQUIRE PATCH 3.0.0-03a_system_functions


--- FUNCTION CACHE ---

CREATE OR REPLACE FUNCTION _cm3_cached_records_load(_function regproc) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_cached_records_load(_function, 43200);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_cached_records_renew(_function regproc) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_cached_records_renew(_function, 43200, 43200 / 2);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_cached_records_renew(_function regproc, _ttl bigint, _if_older_than bigint) RETURNS VOID AS $$ DECLARE
    _code varchar = format('CM_FUNCTION%s', _function::regproc::varchar);
BEGIN
    IF NOT EXISTS (SELECT * FROM "_Cache" WHERE "Code" = _code AND "BeginDate" + format('%s seconds', _if_older_than)::interval > now() ) THEN
        PERFORM _cm3_cached_records_load(_function, _ttl);
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_cached_records_load(_function regproc, _ttl bigint) RETURNS VOID AS $$ DECLARE
    _code varchar = format('CM_FUNCTION%s', _function::regproc::varchar);
BEGIN
    EXECUTE format($query$INSERT INTO "_Cache" ("Code", "TimeToLive", "Data") VALUES (%L, %L, (SELECT to_json(array_agg(x))::varchar FROM %I() x)) ON CONFLICT ("Code") DO UPDATE SET "TimeToLive" = EXCLUDED."TimeToLive", "Data" = EXCLUDED."Data"$query$, _code, _ttl, _function::regproc::varchar);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_cached_records_get(_function regproc) RETURNS SETOF RECORD AS $$ DECLARE
    _code varchar = format('CM_FUNCTION%s', _function::regproc::varchar);
    _query varchar = format('WITH q AS (SELECT jsonb_array_elements("Data"::jsonb) x FROM "_Cache" WHERE "Code" = %L) SELECT %s FROM q', _code, (WITH q AS (SELECT unnest(proargmodes) _argmode, unnest(proargnames) _argname, unnest(proallargtypes) _argtype FROM pg_proc WHERE oid = _function) SELECT string_agg(format('(x->>''%s'')::%s AS %I', _argname, _argtype::regtype, _argname), ', ') FROM q WHERE _argmode = 't'));
BEGIN
    IF NOT EXISTS (SELECT * FROM "_Cache" WHERE "Code" = _code AND "BeginDate" + format('%s seconds', "TimeToLive")::interval > now() ) THEN
        PERFORM pg_advisory_xact_lock(1314, _function::oid::int);
        IF NOT EXISTS (SELECT * FROM "_Cache" WHERE "Code" = _code AND "BeginDate" + format('%s seconds', "TimeToLive")::interval > now() ) THEN
            PERFORM _cm3_cached_records_load(_function);
        END IF;
    END IF;
    RAISE DEBUG 'execute query = %', _query;
    RETURN QUERY EXECUTE _query;
END $$ LANGUAGE PLPGSQL;


--- USER CONFIG ---

CREATE OR REPLACE FUNCTION _cm3_user_config_get(_userid bigint) RETURNS jsonb AS $$ BEGIN
	RETURN COALESCE((SELECT "Data" FROM "_UserConfig" WHERE "Owner" = _userid AND "Status" = 'A'),'{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_set(_userid bigint,_data jsonb) RETURNS VOID AS $$ BEGIN
 	IF EXISTS (SELECT 1 FROM "_UserConfig" WHERE "Owner" = _userid AND "Status" = 'A') THEN
		UPDATE "_UserConfig" SET "Data" = _data WHERE "Owner" = _userid AND "Status" = 'A';
	ELSE
		INSERT INTO "_UserConfig" ("Owner","Data") VALUES (_userid,_data);
	END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_get(_username varchar) RETURNS jsonb AS $$ BEGIN
	RETURN (SELECT _cm3_user_config_get((SELECT "Id" FROM "User" WHERE "Username" = _username AND "Status" = 'A')));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_set(_username varchar, _data jsonb) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_user_config_set((SELECT "Id" FROM "User" WHERE "Username" = _username AND "Status" = 'A'),_data);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_config_set(_username varchar, _key varchar, _value varchar) RETURNS VOID AS $$ BEGIN
	PERFORM _cm3_user_config_set(_username, _cm3_user_config_get(_username) || jsonb_build_object(_key, _value));
END $$ LANGUAGE PLPGSQL;


-- UTILITY PATCH FUNCTIONS

CREATE OR REPLACE FUNCTION _cm3_utils_store_and_drop_dependant_views(_table regclass) RETURNS VOID AS $$ DECLARE
	_view regclass;
	_sub_table regclass;
BEGIN
    IF to_regclass('_utils_dependant_views_aux') IS NULL THEN
        CREATE TEMPORARY TABLE _utils_dependant_views_aux(index int, viewname varchar, viewdef varchar);
    END IF;
	FOR _view IN
		select distinct(r.ev_class::regclass) as views
			from pg_depend d join pg_rewrite r on r.oid = d.objid
			where refclassid = 'pg_class'::regclass
				and refobjid = _table
				and classid = 'pg_rewrite'::regclass
				and ev_class != _table
	LOOP
		PERFORM _cm3_utils_store_and_drop_dependant_views(_view);
		IF _view::varchar <> _view::int::varchar THEN --TODO check this
            INSERT INTO _utils_dependant_views_aux (index, viewname, viewdef) VALUES ( (SELECT COALESCE(MAX(index)+1,0) FROM _utils_dependant_views_aux), _view::varchar, pg_get_viewdef(_view, true) );
			RAISE NOTICE 'store and drop view %', _view;
			EXECUTE format('DROP VIEW %s', _view);
		END IF;
	END LOOP;
	FOR _sub_table IN SELECT i.inhrelid::regclass FROM pg_catalog.pg_inherits i WHERE i.inhparent = _table LOOP
		PERFORM _cm3_utils_store_and_drop_dependant_views(_sub_table);
	END LOOP;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_restore_dependant_views() RETURNS VOID AS $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM _utils_dependant_views_aux ORDER BY index DESC LOOP
        RAISE NOTICE 'restore view %', _record.viewname;
        EXECUTE format('CREATE VIEW %s AS %s', _record.viewname, _record.viewdef);
    END LOOP;
    DROP TABLE _utils_dependant_views_aux;
END $$ LANGUAGE PLPGSQL;


-- UTILITY FUNCTIONS

CREATE OR REPLACE FUNCTION _cm3_system_housekeeping() RETURNS VOID AS $$ DECLARE
	_record RECORD;
    _invalid_record RECORD;
BEGIN
    PERFORM _cm3_system_lock_aquire('housekeeping');
    FOR _record IN SELECT * FROM "_Grant" WHERE "Status" = 'A' AND ((
                            "Type" = 'class' AND NOT _cm3_class_exists("ObjectClass")
                    ) OR (
                            "Type" = 'report' AND NOT EXISTS (SELECT * FROM "_Report" WHERE "Id" = "_Grant"."ObjectId" AND "Status" = 'A')
                    ) OR (
                            "Type" = 'dashboard' AND NOT EXISTS (SELECT * FROM "_Dashboard" WHERE "Id" = "_Grant"."ObjectId" AND "Status" = 'A')
                    ))
    LOOP
        RAISE WARNING 'removing invalid grant record = %', _record;
        UPDATE "_Grant" SET "Status" = 'N' WHERE "Id" = _record."Id" AND "Status" = 'A';
    END LOOP;
    FOR _record IN SELECT owner class_name, name attribute_name, comment->>'LOOKUP' _lookupType FROM _cm3_attribute_list_detailed() AS  x WHERE NULLIF(comment ->> 'LOOKUP', '') IS NOT NULL AND sql_type = 'int8' ORDER BY owner, name LOOP
        FOR _invalid_record IN EXECUTE format('SELECT "Id" id, %I attributeId FROM %s WHERE %I IS NOT NULL AND %I NOT IN (SELECT "Id" FROM "LookUp" WHERE "Type" = %L AND "Status" = ''A'')' || CASE WHEN _cm3_class_is_simple(_record.class_name) THEN '' ELSE ' AND "Status" = ''A''' END,
                _record.attribute_name, _record.class_name::regclass, _record.attribute_name, _record.attribute_name, _record._lookupType)
        LOOP
            RAISE WARNING 'found invalid LookUp value for attr = %.% card = %, invalid value = %: will set value to NULL', _cm3_utils_regclass_to_name(_record.class_name), _record.attribute_name, _invalid_record.id, _invalid_record.attributeId;
            BEGIN
                EXECUTE format('UPDATE %s SET "%s" = NULL WHERE "Id" = %s', _record.class_name, _record.attribute_name, _invalid_record.id);
            EXCEPTION WHEN others THEN
                RAISE WARNING 'unable to clear invalid value for attr = %.% card = %: %', _cm3_utils_regclass_to_name(_record.class_name), _record.attribute_name, _invalid_record.id, SQLERRM;
            END;
        END LOOP;
    END LOOP;
    FOR _record IN SELECT owner class_name, name attribute_name, comment->>'LOOKUP' _lookupType FROM _cm3_attribute_list_detailed() AS  x WHERE NULLIF(comment ->> 'LOOKUP', '') IS NOT NULL AND sql_type = '_int8' ORDER BY owner, name LOOP
        FOR _invalid_record IN EXECUTE format('WITH _lookup_housekeeping AS (SELECT "Id", unnest(%I) AS %I, "Status" FROM %s' || CASE WHEN _cm3_class_is_simple(_record.class_name) THEN '' ELSE ' WHERE "Status" = ''A''' END || ') SELECT "Id" id, %I attributeId FROM _lookup_housekeeping WHERE %I IS NOT NULL AND %I NOT IN (SELECT "Id" FROM "LookUp" WHERE "Type" = %L AND "Status" = ''A'')',
            _record.attribute_name, _record.attribute_name, _record.class_name::regclass, _record.attribute_name, _record.attribute_name, _record.attribute_name, _record._lookupType)
        LOOP
            RAISE WARNING 'found invalid LookUp value for attr = %.% card = %, invalid value = %: will set value to NULL', _cm3_utils_regclass_to_name(_record.class_name), _record.attribute_name, _invalid_record.id, _invalid_record.attributeId;
            BEGIN
                EXECUTE format('UPDATE %s SET "%s" = NULL WHERE "Id" = %s', _record.class_name, _record.attribute_name, _invalid_record.id);
            EXCEPTION WHEN others THEN
                RAISE WARNING 'unable to clear invalid value for attr = %.% card = %: %', _cm3_utils_regclass_to_name(_record.class_name), _record.attribute_name, _invalid_record.id, SQLERRM;
            END;
        END LOOP;
    END LOOP;
    FOR _record IN SELECT * FROM "Class" WHERE "IdClass" NOT IN ('"LookUp"'::regclass,'"_LookupValue"'::regclass,'"_LookupType"'::regclass) AND "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdClass"::varchar LOOP
        RAISE WARNING 'found duplicate card id = % for record = %', _record."Id", _record;
        END LOOP;
    FOR _record IN SELECT * FROM "Map" WHERE "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdDomain"::varchar LOOP
        RAISE WARNING 'found duplicate map id = % for record = %', _record."Id", _record;
    END LOOP;
    FOR _record IN SELECT * FROM "SimpleClass" WHERE "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdClass"::varchar LOOP
        RAISE WARNING 'found duplicate simple class id = % for record = %', _record."Id", _record;
    END LOOP;
    PERFORM _cm3_system_lock_release('housekeeping');	FOR _record IN SELECT * FROM "Class" WHERE "IdClass" NOT IN ('"LookUp"'::regclass,'"_LookupValue"'::regclass,'"_LookupType"'::regclass) AND "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdClass"::varchar LOOP
        RAISE WARNING 'found duplicate card id = % for record = %', _record."Id", _record;
    END LOOP;
    FOR _record IN SELECT * FROM "Map" WHERE "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdDomain"::varchar LOOP
        RAISE WARNING 'found duplicate map id = % for record = %', _record."Id", _record;
    END LOOP;
    FOR _record IN SELECT * FROM "SimpleClass" WHERE "Id" IN (WITH _records AS (SELECT "Id",COUNT(*) "Count" FROM (SELECT "Id" FROM "Class" UNION ALL SELECT "Id" FROM "Map" UNION ALL SELECT "Id" FROM "SimpleClass") x GROUP BY "Id") SELECT "Id" FROM _records WHERE "Count" > 1) ORDER BY "Id","IdClass"::varchar LOOP        RAISE WARNING 'found duplicate simple class id = % for record = %', _record."Id", _record;
    END LOOP;
    PERFORM _cm3_system_lock_release('housekeeping');
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_attribute_list_agg(_class regclass) RETURNS varchar AS $$ BEGIN
	RETURN (SELECT string_agg(x.x,',') from (select quote_ident(x) x from _cm3_attribute_list(_class) x) x);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_rollback(_timestamp timestamptz) RETURNS VOID AS $$ DECLARE
    _class regclass;
    _count bigint;
    _record record;
BEGIN
    SET session_replication_role = 'replica';
    FOR _class IN SELECT c FROM _cm3_class_list_standard() c WHERE NOT _cm3_class_is_superclass(c) UNION SELECT d FROM _cm3_domain_list() d LOOP
        EXECUTE format('SELECT COUNT(*) FROM %s WHERE "BeginDate" > %L', _class, _timestamp) INTO _count;
        IF _count > 0 THEN
            RAISE NOTICE 'rollback % records for class/domain = %', _count, _class;
            EXECUTE format('DELETE FROM %s WHERE "BeginDate" > %L', _class, _timestamp);
            FOR _record IN EXECUTE format('SELECT * FROM %s WHERE "BeginDate" <= %L AND "EndDate" > %L AND "Status" = ''U''', _class, _timestamp, _timestamp) LOOP
                EXECUTE format('DELETE FROM %s WHERE "Id" = %s', _class, _record."Id");
                _record."Status" = 'A';
                _record."Id" = _record."CurrentId";
                _record."EndDate" = NULL;
                EXECUTE format('INSERT INTO %s (%s) VALUES ( (%L::%s).* )', _class, _cm3_attribute_list_agg(_class), _record, _class);
            END LOOP;
        END IF;
    END LOOP;
    FOR _class IN SELECT c FROM _cm3_class_list_simple() c LOOP
        EXECUTE format('SELECT COUNT(*) FROM %s WHERE "BeginDate" > %L', _class, _timestamp) INTO _count;
        IF _count > 0 THEN
            RAISE NOTICE 'delete % records for simple class = %', _count, _class;
            EXECUTE format('DELETE FROM %s WHERE "BeginDate" > %L', _class, _timestamp);
        END IF;
    END LOOP;
    SET session_replication_role = 'origin';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_anonymize() RETURNS VOID AS $$ DECLARE
    _class regclass;
    _count bigint;
    _record record;
BEGIN
    SET session_replication_role = 'replica';
    FOR _class IN SELECT c FROM _cm3_class_list_standard() c WHERE NOT _cm3_class_is_superclass(c) UNION SELECT d FROM _cm3_domain_list() d UNION SELECT c FROM _cm3_class_list_simple() c LOOP
        IF (_cm3_class_features_get(_class, 'MODE') IN ('default','all') OR _cm3_utils_regclass_to_name(_class) IN ('User', '_EmailAccount')) AND EXISTS (SELECT * FROM _cm3_attribute_list_detailed(_class) WHERE sql_type ~ '^(varchar|text)' AND ("metadata" ->> 'cm_anonymizable')::boolean) THEN
            RAISE NOTICE 'processing class = %', _class;
            EXECUTE format('UPDATE %s SET %s', _class, (SELECT string_agg(format('%I = _cm3_utils_anonymize(%I)', name, name), ', ') FROM _cm3_attribute_list_detailed(_class) WHERE sql_type ~ '^(varchar|text)' AND ("metadata" ->> 'cm_anonymizable')::boolean));
        END IF;
    END LOOP;
    SET session_replication_role = 'origin';
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_system_anonymize_class(_class regclass) RETURNS VOID AS $$ DECLARE
    _count bigint;
    _record record;
BEGIN
    SET session_replication_role = 'replica';
        IF (_cm3_class_features_get(_class, 'MODE') IN ('default','all') OR _cm3_utils_regclass_to_name(_class) IN ('User', '_EmailAccount')) AND EXISTS (SELECT * FROM _cm3_attribute_list_detailed(_class) WHERE sql_type ~ '^(varchar|text)' AND ("metadata" ->> 'cm_anonymizable')::boolean) THEN
            RAISE NOTICE 'processing class = %', _class;
            EXECUTE format('UPDATE %s SET %s', _class, (SELECT string_agg(format('%I = _cm3_utils_anonymize(%I)', name, name), ', ') FROM _cm3_attribute_list_detailed(_class) WHERE sql_type ~ '^(varchar|text)' AND ("metadata" ->> 'cm_anonymizable')::boolean));
        END IF;
    SET session_replication_role = 'origin';
END $$ LANGUAGE PLPGSQL;

-- USER UTILS --

CREATE OR REPLACE FUNCTION _cm3_user_create(_username varchar, variadic _groups varchar[]) RETURNS bigint AS $$ BEGIN
    RETURN _cm3_user_create(_username, NULL, variadic _groups);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_user_create(_username varchar, _password varchar, variadic _groups varchar[]) RETURNS bigint AS $$ DECLARE
    _userid bigint;
    _role varchar;
    _roleid bigint;
BEGIN
    RAISE NOTICE 'create user %', _username;
    INSERT INTO "User" ("Username","Password") VALUES (_username, _password) RETURNING "Id" INTO _userid;
    FOREACH _role IN ARRAY _groups LOOP
        RAISE NOTICE 'add group % to user %', _role, _username;
        _roleid = (SELECT "Id" FROM "Role" WHERE "Code" = _role AND "Status"='A');
        IF _roleid IS NULL THEN
            RAISE 'group not found for code =< % >', _role;
        END IF;
        INSERT INTO "Map_UserRole" ("IdObj1","IdObj2") VALUES (_userid, _roleid);
    END LOOP;
    RETURN _userid;
END $$ LANGUAGE PLPGSQL;


-- LOOKUP UTILS --

CREATE OR REPLACE FUNCTION _cm3_lookup(_type varchar, _code varchar) RETURNS bigint AS $$ DECLARE
    _id bigint;
BEGIN
    SELECT "Id" INTO _id FROM "LookUp" WHERE "Type" = _type AND "Code" = _code AND "Status" = 'A';
    IF _id IS NULL THEN
        RAISE 'CM: lookup not found for type =< % > and code =< % >', _type, _code;
    ELSE
        RETURN _id;
    END IF;
END $$ LANGUAGE PLPGSQL STABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_lookup(_val varchar) RETURNS bigint AS $$
    SELECT _cm3_lookup(split_part(_val,'.',1), split_part(_val,'.',2));
$$ LANGUAGE SQL STABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_lookup_code(_id bigint) RETURNS varchar AS $$
    SELECT "Code" FROM "LookUp" WHERE "Id" = _id AND "Status" = 'A';
$$ LANGUAGE SQL STABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_lookup_type(_id bigint) RETURNS varchar AS $$
    SELECT "Type" FROM "LookUp" WHERE "Id" = _id AND "Status" = 'A';
$$ LANGUAGE SQL STABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_lookup(_id bigint) RETURNS varchar AS $$
    SELECT format('%s.%s', _cm3_lookup_type(_id), _cm3_lookup_code(_id));
$$ LANGUAGE SQL IMMUTABLE RETURNS NULL ON NULL INPUT;

CREATE OR REPLACE FUNCTION _cm3_lookup_info(_id bigint[]) RETURNS jsonb AS $$ DECLARE
	jsonObject jsonb;
BEGIN
    SELECT to_jsonb(array_agg(x)) FROM (SELECT "Code", "Description" FROM "LookUp" WHERE "Id" = ANY (_id) AND "Status" = 'A' ORDER BY array_position(_id, "Id"::bigint)) x INTO jsonObject;
    RETURN jsonObject;
END $$ LANGUAGE PLPGSQL STABLE RETURNS NULL ON NULL INPUT;


-- EVENT UTILS --

CREATE OR REPLACE FUNCTION _cm3_event_post(_event varchar, _subject varchar) RETURNS VOID AS $$ DECLARE
    _function record;
BEGIN
    FOR _function IN WITH f AS ( SELECT
                pg_proc.*,
                pg_proc.oid _function_id,
                (pg_proc.oid)::regproc::varchar _function_name,
                _cm3_comment_to_jsonb(pg_description.description) _comment,
                cardinality(proargnames) _arg_len
            FROM pg_proc JOIN pg_description ON pg_proc.oid = pg_description.objoid )
        SELECT * FROM f WHERE _comment->>'TYPE' = 'trigger' AND _comment->>'EVENT' = _event ORDER BY _function_name LOOP
            IF _function._arg_len = 0 THEN
                EXECUTE format('SELECT %I()', _function._function_name);
            ELSEIF _function._arg_len = 1 THEN
                IF ( _function.proargtypes[0]::regtype::varchar ) = 'regclass' THEN
                    _subject = _cm3_utils_name_to_regclass(_subject)::varchar;
                END IF;
                EXECUTE format('SELECT %I(%L::%s)', _function._function_name, _subject, _function.proargtypes[0]::regtype::varchar);
            ELSE
                RAISE WARNING 'found invalid trigger function = % %', _function._function_id, _function._function_id::regprocedure::varchar;
            END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;


-- PROCESS UTILS --

CREATE OR REPLACE FUNCTION _cm3_process_start(_process regclass, _activity varchar, _group varchar, VARIADIC _args varchar[]) RETURNS BIGINT AS $$ BEGIN
    RETURN _cm3_process_start(_process, _activity, _group, jsonb_object(_args));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_process_start(_process regclass, _activity varchar, _group varchar, _data jsonb) RETURNS BIGINT AS $$ DECLARE
    _id bigint;
    _columns varchar = '';
    _values varchar = '';
    _plan varchar;
    _key varchar;
    _value varchar;
BEGIN
    _plan = _cm3_utils_check_not_blank((SELECT "Code" FROM "_Plan" WHERE "ClassId" = _cm3_utils_regclass_to_name(_process) AND "Status" = 'A' ORDER BY "BeginDate" DESC LIMIT 1));
    FOR _key, _value IN SELECT key, value FROM jsonb_each_text(_data) LOOP
       _columns = format('%s, %I', _columns, _key);
       _values = format('%s, %L', _values, _value);
    END LOOP;
    EXECUTE format('INSERT INTO %s ("FlowStatus", "ActivityDefinitionId", "ProcessCode", "NextExecutor", "ActivityInstanceId", "UniqueProcessDefinition", "FlowData"%s)
        VALUES (%L, ARRAY[%L], _cm3_utils_random_id(), ARRAY[%L], ARRAY[_cm3_utils_random_id()], %L, %L%s) RETURNING "Id"', _process, _columns,
                (SELECT "Id" FROM "LookUp" WHERE "Code" = 'open.running' AND "Type" = 'FlowStatus' AND "Status" = 'A'),
                _activity, _group, format('river#0#%s', _plan), jsonb_build_object('RiverFlowStatus', 'RUNNING'), _values) INTO _id;
    RAISE INFO 'start workflow = %[%] with data = %', _cm3_utils_regclass_to_name(_process), _id, _data;
    RETURN _id;
END $$ LANGUAGE PLPGSQL;

-- MENU UTILS --

CREATE OR REPLACE FUNCTION _cm3_utils_menu_to_list(_menu jsonb) RETURNS jsonb AS $$ DECLARE
    _code varchar;
    _child jsonb;
    _list jsonb = '[]'::jsonb;
BEGIN
    _code = COALESCE(_menu->>'code', '_ROOT');
    IF _menu->'children' IS NOT NULL AND jsonb_typeof(_menu->'children') = 'array' THEN
        FOR _child IN SELECT jsonb_array_elements(_menu->'children') LOOP
            _list = _list || ( _child - 'children' || jsonb_build_object('_parent', _code) );
            _list = _list || _cm3_utils_menu_to_list(_child);
        END LOOP;
    END IF;
    RETURN _list;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_utils_menu_from_list(_list jsonb) RETURNS jsonb AS $$ DECLARE
    _parent varchar;
    _leaf jsonb;
    _branch jsonb;
    _leaves jsonb;
    _branches jsonb;
    _element jsonb;
    _elements jsonb;
    _root_elements jsonb = '[]'::jsonb;
BEGIN
    WHILE TRUE LOOP
        IF _list IS NULL OR jsonb_array_length(_list) = 0 THEN
            _root_elements = (WITH q AS (SELECT x-'_index' r, (x->>'_index')::bigint i FROM  jsonb_array_elements(_root_elements) x) SELECT coalesce(jsonb_agg(r ORDER BY i),'[]') FROM q);
            RETURN jsonb_build_object('version', 3, 'children', _root_elements);
        END IF;
        _list = (WITH q AS (SELECT e, ROW_NUMBER() OVER () i FROM jsonb_array_elements(_list) e) SELECT jsonb_agg(e || jsonb_build_object('_index', COALESCE((e->>'_index')::bigint, i))) FROM q);
        _leaves = (WITH q AS (SELECT x FROM jsonb_array_elements(_list) x) SELECT jsonb_agg(e1.x) FROM q e1 WHERE NOT EXISTS (SELECT 1 FROM q e2 WHERE e2.x->>'_parent' = e1.x->>'code'));
        _branches = (WITH q AS (SELECT x FROM jsonb_array_elements(_list) x) SELECT jsonb_agg(e1.x) FROM q e1 WHERE EXISTS (SELECT 1 FROM q e2 WHERE e2.x->>'_parent' = e1.x->>'code'));
        _leaves = (SELECT jsonb_agg(l || jsonb_build_object('children', (WITH q AS (SELECT x-'_index' c, (x->>'_index')::int i FROM  jsonb_array_elements(l->'children') x) SELECT jsonb_agg(c ORDER BY i) FROM q))) FROM jsonb_array_elements(_leaves) l);
        _elements = '{}'::jsonb;
        FOR _element IN SELECT jsonb_array_elements(_branches) LOOP
            _elements = _elements || jsonb_build_object(_element->>'code', _element || jsonb_build_object('children', COALESCE(_element->'children', '[]'::jsonb)));
        END LOOP;
        FOR _leaf IN SELECT jsonb_array_elements(_leaves) LOOP
            IF _leaf->>'_parent' = '_ROOT' THEN
                _root_elements = _root_elements || ( _leaf - '_parent' );
            ELSE
                _branch = _elements->(_leaf->>'_parent');
                _branch = _branch || jsonb_build_object('children', (_branch->'children') || (_leaf - '_parent'));
                _elements = _elements || jsonb_build_object(_branch->>'code', _branch);
            END IF;
        END LOOP;
        _list = (SELECT jsonb_agg(_elements->(x->>'code')) FROM jsonb_array_elements(_branches) x);
    END LOOP;
END $$ LANGUAGE PLPGSQL;

-- EVENT LOG UTILS --

-- REQUIRE PATCH 3.2.0-55_application_event_log

CREATE OR REPLACE FUNCTION _cm3_event_log(_code varchar, _card bigint, _data jsonb) RETURNS VOID AS $$ BEGIN
    INSERT INTO "_EventLog" ("Code", "Timestamp", "SessionId", "SessionUser", "Card", "Data") VALUES ( --TODO handle request id
        _code, now(), _cm3_utils_operation_session_get(), _cm3_utils_operation_user_get(), _card, COALESCE(_data, '{}'::jsonb));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_event_log(_code varchar, _card bigint) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_event_log(_code, _card, NULL);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_event_log(_code varchar, _data jsonb) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_event_log(_code, NULL, _data);
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_event_log(_code varchar, VARIADIC _args varchar[]) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_event_log(_code, jsonb_object(_args));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_event_log(_code varchar, _card bigint, VARIADIC _args varchar[]) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_event_log(_code, _card, jsonb_object(_args));
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_event_log(_code varchar) RETURNS VOID AS $$ BEGIN
    PERFORM _cm3_event_log(_code, NULL, '{}'::jsonb);
END $$ LANGUAGE PLPGSQL;

-- ITEM FUNCTIONS

CREATE OR REPLACE FUNCTION _cm3_item_get(_id bigint) RETURNS jsonb AS $$ DECLARE
    _item jsonb;
    _info record;
BEGIN
    SELECT * FROM "_Items" WHERE "Id" = _id INTO _info;
    IF _info IS NULL THEN
        RAISE 'CM: item not found for id = %', _id;
    ELSE
        EXECUTE format('WITH q AS (SELECT jsonb_array_elements(%I) i FROM %s WHERE "Id" = %L AND "Status" = ''A'') SELECT i FROM q WHERE i->>''Id'' = %L', _info."Attr", _info."OwnerClass", _info."Card", _id) INTO _item;
        RETURN _item;
    END IF;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_item_list() RETURNS TABLE (owner_class regclass, owner_attr varchar, owner_card bigint, item_id bigint, item_type varchar, item jsonb) AS $$ DECLARE
    _info record;
BEGIN
    FOR _info IN SELECT * FROM "_Items" LOOP
        owner_class = _info."OwnerClass";
        owner_attr = _info."Attr";
        owner_card = _info."Card";
        item_id = _info."Id";
        item = _cm3_item_get(item_id);
        item_type = item->>'IdClass';
        RETURN NEXT;
    END LOOP;
    RETURN;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_item_list(_type varchar) RETURNS TABLE (owner_class regclass, owner_attr varchar, owner_card bigint, item_id bigint, item_type varchar, item jsonb) AS $$ BEGIN
    RETURN QUERY SELECT * FROM _cm3_item_list() x WHERE x.item_type = _type;
END $$ LANGUAGE PLPGSQL;

-- TEST FUNCTIONS

-- COMMENT TYPE: function
CREATE OR REPLACE FUNCTION _cm3_function_test() RETURNS TABLE (_message varchar) AS $$ BEGIN
    RETURN QUERY SELECT 'test OK'::varchar _message;
END $$ LANGUAGE PLPGSQL;

-- COMMENT TYPE: function
CREATE OR REPLACE FUNCTION _cm3_function_test_sleep(_delay_seconds float) RETURNS TABLE (_message varchar) AS $$ BEGIN
    PERFORM pg_sleep(_delay_seconds);
    RETURN QUERY SELECT format('test OK, slept for %s seconds', _delay_seconds)::varchar _message;
END $$ LANGUAGE PLPGSQL;

-- NAVTREE FUNCTIONS

CREATE OR REPLACE FUNCTION _cm3_navtree_element_fix(_navtree jsonb) RETURNS jsonb AS $$ DECLARE
_target varchar;
BEGIN
    IF _navtree->>'direction' = 'direct' THEN
        _navtree = _navtree || jsonb_build_object('direction', 'inverse');
    ELSE
        IF _navtree->>'direction' = 'inverse' THEN
            _navtree = _navtree || jsonb_build_object('direction', 'direct');
        END IF;
    END IF;
    _navtree = _navtree || jsonb_build_object('nodes', (SELECT coalesce(jsonb_agg(fixed_node),'[]'::jsonb) FROM ( SELECT _cm3_navtree_element_fix(x) fixed_node FROM jsonb_array_elements(_navtree->'nodes') x WHERE jsonb_typeof(x) <> 'null') fixed_nodes));
    RETURN _navtree;
END $$ LANGUAGE PLPGSQL;

-- COMMENT TYPE: function
CREATE OR REPLACE FUNCTION _cm3_navtree_element_directions_invert(navcode varchar) RETURNS TABLE (_test varchar) AS $$ BEGIN
    IF _cm3_utils_operation_role_get() = 'SuperUser' THEN
        UPDATE "_NavTree" SET "Data" = _cm3_navtree_element_fix("Data") WHERE "Code" = navcode AND "Status" = 'A';
    ELSE
        RAISE EXCEPTION 'CM: user not allowed to execute this function';
    END IF;
END $$ LANGUAGE PLPGSQL;
