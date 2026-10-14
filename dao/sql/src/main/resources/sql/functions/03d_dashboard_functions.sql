-- dashboard functions
-- REQUIRE PATCH 3.0.0-03a_system_functions

CREATE OR REPLACE FUNCTION _cm3_dashboard_disk_usage_actual() RETURNS TABLE (item varchar, type varchar, status varchar, count bigint, size bigint, size_pretty varchar) AS $$ BEGIN
    RETURN QUERY WITH _sizes AS (SELECT CASE WHEN _cm3_class_is_history(x.item) THEN _cm3_class_parent_get(x.item) ELSE x.item END _item, x.total_size _size FROM _cm3_utils_disk_usage_detailed() x),
        _tables AS ( SELECT x.item, x.type, (SELECT SUM(_size) FROM _sizes s WHERE s._item = x.item) _tsize FROM _cm3_utils_disk_usage_detailed() x WHERE x.type <> 'history'),
        _records AS (
            SELECT "IdClass" _type, COUNT(*) _count, "Status" _status FROM "Class" GROUP BY "IdClass", "Status"
            UNION ALL SELECT "IdClass" _type, COUNT(*) _count, "Status" _status FROM "SimpleClass" GROUP BY "IdClass", "Status"
            UNION ALL SELECT "IdDomain" _type, COUNT(*) _count, "Status" _status FROM "Map" GROUP BY "IdDomain", "Status"),
        _items AS (SELECT t.*, s._status FROM _tables t CROSS JOIN (SELECT unnest(ARRAY['A','U','N']) _status) s),
        _q AS (SELECT i.*, COALESCE(r._count, 0) _count FROM _items i LEFT JOIN _records r ON i.item = r._type AND i._status = r._status),
        _qs AS (SELECT *, CASE _count WHEN 0 THEN 0 ELSE q._tsize * _count / (SELECT SUM(_count) FROM _q i WHERE i.item = q.item) END _size FROM _q q)
        SELECT
            _cm3_utils_regclass_to_name(q.item),
            CASE WHEN q.type ~ 'class|simpleclass' AND _cm3_class_features_get(q.item, 'MODE') NOT IN ('all', 'default') THEN 'system' ELSE q.type END,
            q._status::varchar status,
            q._count::bigint "count",
            q._size::bigint size,
            pg_size_pretty(_size)::varchar size_pretty FROM _qs q ORDER BY _size DESC;
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_dashboard_utils_count_get(_class regclass, _end timestamptz) RETURNS bigint AS $$
DECLARE
	_c regclass;
	_count bigint = 0;
	_index bigint;
	_querycondition text = '( "Status" = ''U'' AND "BeginDate" < %2$L::timestamptz AND "EndDate" > %2$L::timestamptz )';
	_select text = 'SELECT true';
	_cond boolean;
	_attribute varchar = 'IdClass';
BEGIN
	IF (_class = '"Class"'::regclass OR _class = '"SimpleClass"'::regclass) THEN
		_select = 'SELECT _cm3_class_features_get(%1$L, ''MODE'') IN (''default'', ''all'') AND NOT _cm3_class_is_process(%1$L) AND NOT _cm3_class_is_dmsmodel(%1$L)';
	END IF;

	IF EXISTS (select 1 FROM _cm3_class_list_ancestors_and_self(_class) _cl WHERE _cl = '"Map"'::regclass) THEN
		_attribute = 'IdDomain';
	END IF;

	IF EXISTS (select 1 FROM _cm3_class_list_ancestors_and_self(_class) _cl WHERE _cl = '"SimpleClass"'::regclass) THEN
		_querycondition = '( "Status" = ''U'' AND "BeginDate" < %2$L::timestamptz)';
	END IF;
	FOR _c IN EXECUTE(format('SELECT DISTINCT %I FROM %s', _attribute, _class)) LOOP
		EXECUTE format(_select, _c) INTO _cond;
		IF _cond THEN
			EXECUTE format('SELECT count(*) FROM %1$s WHERE ("Status" = ''A'' AND "BeginDate" < %2$L::timestamptz ) OR %3$s', _c, _end, format(_querycondition, _c, _end)) INTO _index;
			_count = _count + _index;
		END IF;
	END LOOP;
	RETURN _count;
END $$ LANGUAGE PLPGSQL;

-- COMMENT TYPE: function|cached: true
CREATE OR REPLACE FUNCTION _cm3_dashboard_disk_usage() RETURNS TABLE (item varchar, type varchar, status varchar, count bigint, size bigint, size_pretty varchar) AS $$ BEGIN
    RETURN QUERY SELECT * from _cm3_cached_records_get('_cm3_dashboard_disk_usage_actual'::regproc) AS (item varchar, type varchar, status varchar, count bigint, size bigint, size_pretty varchar);
END $$ LANGUAGE PLPGSQL;

-- COMMENT TYPE: function
CREATE OR REPLACE FUNCTION _cm3_dashboard_model_stats() RETURNS TABLE (type varchar, count bigint) AS $$ BEGIN
    RETURN QUERY SELECT x."type"::varchar, x."count" FROM (
        SELECT 'class' "type", (SELECT COALESCE(COUNT(*),0) count FROM _cm3_class_list() c WHERE NOT _cm3_class_is_process(c) AND _cm3_class_features_get(c, 'MODE') IN ('default', 'all'))
        UNION ALL SELECT 'processclass' "type", (SELECT COALESCE(COUNT(*),0) count FROM _cm3_class_list() c WHERE _cm3_class_is_process(c) AND _cm3_class_is_superclass(c) = false AND (_cm3_class_features_get(c, 'MODE') = 'default' OR _cm3_class_features_get(c, 'MODE') = 'all'))
        UNION ALL SELECT 'domain' "type", (SELECT COALESCE(COUNT(*),0) count FROM _cm3_domain_list() d)
        UNION ALL SELECT 'report' "type", (SELECT COALESCE(COUNT(*),0) count FROM "_Report" WHERE "Status" = 'A')
        UNION ALL SELECT 'dashboard' "type", (SELECT COALESCE(COUNT(*),0) count FROM "_Dashboard" WHERE "Status" = 'A')
        UNION ALL SELECT 'view' "type", (SELECT COALESCE(COUNT(*),0) count FROM "_View" WHERE "Status" = 'A' AND "Shared"=true)
        UNION ALL SELECT 'custompage' "type", (SELECT COALESCE(COUNT(*),0) count FROM "_UiComponent" WHERE "Status" = 'A' AND "Type" = 'custompage')
        UNION ALL SELECT 'busdescriptor' "Type", (SELECT COALESCE(COUNT(*), 0) FROM "_EtlConfig" WHERE "Status" = 'A' AND "Config" -> 'tag' is null AND "Config" -> 'items' @? '$ ? (exists (@."gate"))')
        ) x ORDER BY x.count DESC;
END $$ LANGUAGE PLPGSQL;

-- COMMENT TYPE: function
CREATE OR REPLACE FUNCTION _cm3_dashboard_user_group_session() RETURNS TABLE (type varchar, count bigint) AS $$ BEGIN
    RETURN QUERY SELECT 'user_standard_active'::varchar "type", COUNT(*) "count" FROM "User" WHERE "Status" = 'A' AND "Service" = FALSE AND "Active" = TRUE
        UNION ALL SELECT 'user_standard_nonactive'::varchar "type", COUNT(*) "count" FROM "User" WHERE "Status" = 'A' AND "Service" = FALSE AND "Active" = FALSE
        UNION ALL SELECT 'user_service_active'::varchar "type", COUNT(*) "count" FROM "User" WHERE "Status" = 'A' AND "Service" = TRUE AND "Active" = TRUE
        UNION ALL SELECT 'user_service_nonactive'::varchar "type", COUNT(*) "count" FROM "User" WHERE "Status" = 'A' AND "Service" = TRUE AND "Active" = FALSE
        UNION ALL SELECT 'group_active'::varchar "type", COUNT(*) "count" FROM "Role" WHERE "Status" = 'A' AND "Active" = TRUE
        UNION ALL SELECT 'group_nonactive'::varchar "type", COUNT(*) "count" FROM "Role" WHERE "Status" = 'A' AND "Active" = FALSE
        UNION ALL SELECT 'session'::varchar "type", COUNT(*) "count" FROM "_Session";
END $$ LANGUAGE PLPGSQL;

CREATE OR REPLACE FUNCTION _cm3_dashboard_records_history_actual() RETURNS TABLE(type character varying, date date, count bigint) AS $$ BEGIN
    RETURN QUERY
	WITH _enddate AS ( SELECT unnest(ARRAY[now(),now()-'1 year'::interval,now()-'2 year'::interval,now()-'3 year'::interval,now()-'4 year'::interval]) _end ),
        _types AS (SELECT 'card' _type, '"Class"'::regclass _cl
        UNION SELECT 'card' _type, '"SimpleClass"'::regclass _cl
        UNION SELECT 'process' _type, '"Activity"'::regclass _cl
        UNION SELECT 'document' _type, '"DmsModel"'::regclass _cl
        UNION SELECT 'relation' _type, '"Map"'::regclass _cl),
        _rows AS (SELECT * FROM _types, _enddate),
        _records AS (SELECT _type, _end::date _date, _cm3_dashboard_utils_count_get(_cl, e._end) c FROM _rows e)
	SELECT _type::varchar, _date, sum(c)::bigint
	FROM _records
	GROUP BY _type, _date
	ORDER BY _type, _date;
END $$ LANGUAGE PLPGSQL;

-- COMMENT TYPE: function|cached: true
CREATE OR REPLACE FUNCTION _cm3_dashboard_records_history() RETURNS TABLE (type varchar, date date, count bigint) AS $$ BEGIN
    RETURN QUERY SELECT * from _cm3_cached_records_get('_cm3_dashboard_records_history_actual'::regproc) as (type varchar, date date, count bigint);
END $$ LANGUAGE PLPGSQL;

-- COMMENT TYPE: function|scheduled: 0 0 ? * SUN *
CREATE OR REPLACE FUNCTION _cm3_dashboard_functions_load() RETURNS VOID AS $$ BEGIN --TODO schedule this at startup!
    PERFORM _cm3_cached_records_renew('_cm3_dashboard_records_history_actual'::regproc);
    PERFORM _cm3_cached_records_renew('_cm3_dashboard_disk_usage_actual'::regproc);
END $$ LANGUAGE PLPGSQL;

