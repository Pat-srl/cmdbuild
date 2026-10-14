--multitenant function prototype; it is meant to be replaced by custom user function
CREATE OR REPLACE FUNCTION _cm3_multitenant_get(_user_id bigint) RETURNS SETOF bigint AS $$ BEGIN
	IF _user_id = -1 THEN
-- 		RETURN QUERY SELECT "Tenant"."Id" FROM "Tenant" WHERE "Tenant"."Status" = 'A';
		RETURN; -- all tenants
	ELSE
-- 		RETURN QUERY SELECT "Tenant"."Id" FROM "Tenant" JOIN "Map_UserTenant" ON "Tenant"."Id" = "Map_UserTenant"."IdObj2" WHERE "Map_UserTenant"."IdObj1" = _user_id  AND "Tenant"."Status" = 'A' AND "Map_UserTenant"."Status" = 'A';
		RETURN; -- user tenants
	END IF;
END $$ LANGUAGE PLPGSQL;
