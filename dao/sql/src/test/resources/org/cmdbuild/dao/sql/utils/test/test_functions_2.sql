
--- SYSTEM UTILS ---
-- REQUIRE PATCH 3.0.0-01



-- REQUIRE PATCH 3.0.0-03

CREATE OR REPLACE FUNCTION _cm3_system_message_send(_type varchar, _data jsonb) RETURNS VOID AS $$
	LISTEN cminfo;
	SELECT pg_notify('cmevents', jsonb_set(_data,ARRAY['type'],to_jsonb(_type))::varchar);
$$ LANGUAGE SQL;

CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar, _data jsonb) RETURNS VOID AS $$
	SELECT _cm3_system_message_send('command', jsonb_set(_data, ARRAY['action'], to_jsonb(_action)));
$$ LANGUAGE SQL;


-- REQUIRE PATCH 3.0.0-12
CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar) RETURNS VOID AS $$
	SELECT 1; --some difference
	SELECT _cm3_system_command_send(_action, '{}'::jsonb);
$$ LANGUAGE SQL;

-- REQUIRE PATCH 3.0.0-03

-- COMMENT: TYPE: function
CREATE OR REPLACE FUNCTION _cm3_system_reload() RETURNS VOID AS $$
	SELECT _cm3_system_command_send('reload');
$$ LANGUAGE SQL;

--some comment
CREATE OR REPLACE FUNCTION _cm3_system_command_send(_action varchar, VARIADIC _args varchar[]) RETURNS VOID AS $$
	SELECT _cm3_system_command_send(_action, jsonb_set('{}'::jsonb, ARRAY['args'], to_jsonb(_args)));
$$ LANGUAGE SQL;



CREATE OR REPLACE FUNCTION _cm3_system_login() RETURNS VOID AS $$ BEGIN
	SET SESSION cmdbuild.operation_user = 'postgres';
	SET SESSION cmdbuild.user_tenants = '{}';
	SET SESSION cmdbuild.ignore_tenant_policies = 'true';
END $$ LANGUAGE PLPGSQL;
