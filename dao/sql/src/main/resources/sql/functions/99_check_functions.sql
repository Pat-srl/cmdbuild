-- check functions
-- REQUIRE PATCH 3.1.0-16_email_table_add_status_skipped

CREATE OR REPLACE FUNCTION _cm3_grant_attribute_priviledges_check(attribute_priviledge jsonb) RETURNS boolean AS $$ DECLARE
    element record;
BEGIN
	FOR element IN (SELECT value FROM jsonb_each(attribute_priviledge)) LOOP
		IF (element.value NOT IN ('"write"', '"read"', '"none"')) THEN
			RAISE WARNING 'GrantAttributePrivilege % is not valid', element.value;
			RETURN false;
		END IF;
	END LOOP;
	RETURN true;
END $$ LANGUAGE PLPGSQL IMMUTABLE SET search_path = public;

CREATE OR REPLACE FUNCTION _cm3_dashboard_config_check(_config jsonb) RETURNS boolean AS $$ DECLARE
    _val varchar;
    _res boolean;
BEGIN
    _res = TRUE;
    IF _config IS NULL OR _config->'charts' IS NULL OR _config->'layout' IS NULL THEN
        RAISE WARNING 'invalid dashboard config structure';
        RETURN false;
    END IF;
    FOR _val IN WITH 
            _chart_ids AS (SELECT _chart->>'_id' _id FROM jsonb_array_elements(_config->'charts') _chart),
            _rows AS (SELECT r _row FROM jsonb_array_elements(_config->'layout'->'rows') r),
            _cols AS (SELECT jsonb_array_elements(_row->'columns') _col FROM _rows),
            _layout_chart_ids AS (SELECT jsonb_array_elements_text(_col->'charts') _id FROM _cols)
        SELECT _id FROM _chart_ids WHERE _id NOT IN (SELECT _id FROM _layout_chart_ids) UNION SELECT _id FROM _layout_chart_ids WHERE _id NOT IN (SELECT _id FROM _chart_ids) LOOP
        RAISE WARNING 'found mismatching dashboard chart id = %', _val;
        _res = FALSE;        
    END LOOP;
	RETURN _res;
END $$ LANGUAGE PLPGSQL IMMUTABLE SET search_path = public;

CREATE OR REPLACE FUNCTION _cm3_participants_check(_participants varchar[]) RETURNS boolean AS $$ DECLARE
    _element varchar;
BEGIN
    FOREACH _element IN ARRAY (_participants) LOOP
        IF _element !~ '^(user|group)[.][1-9][0-9]*$' THEN
            RAISE WARNING 'CM: invalid participant element =< % >', _element;
            RETURN false;
        END IF;
	END LOOP;
	RETURN true;
END $$ LANGUAGE PLPGSQL IMMUTABLE SET search_path = public;

CREATE OR REPLACE FUNCTION _cm3_calendar_event_config_check(_config jsonb) RETURNS boolean AS $$ DECLARE
    _val varchar;
    _res boolean;
BEGIN
    _res = true;
    IF _config->>'onCardDeleteAction' NOT IN ('clear','delete') THEN
        RAISE WARNING 'invalid calendar event config for key onCardDeleteAction =< % >', _config->>'onCardDeleteAction';
        _res = false;
    END IF;
    RETURN _res;
END $$ LANGUAGE PLPGSQL IMMUTABLE SET search_path = public;

CREATE OR REPLACE FUNCTION _cm3_notifications_check(_notifications jsonb) RETURNS boolean AS $$ DECLARE
    _element jsonb;
    _key varchar;
BEGIN
    IF _notifications IS NULL THEN
        RAISE WARNING 'notification is null';
        RETURN false;
    END IF;
    IF jsonb_typeof(_notifications) <> 'array' THEN
        RAISE WARNING 'notification is not an array';
        RETURN false;
    END IF;
    FOR _element IN SELECT jsonb_array_elements(_notifications) LOOP
        IF _cm3_utils_is_blank(_element->>'template') THEN
            RAISE WARNING 'invalid notification config, missing `template` value';
            RETURN false;
        END IF;
        IF _cm3_utils_is_blank(_element->>'id') THEN
            RAISE WARNING 'invalid notification config, missing `id` value';
            RETURN false;
        END IF;
--         FOR _key IN SELECT k FROM jsonb_object_keys(_element) k WHERE k NOT IN ('template','delay','content','report','id') LOOP
--             RAISE WARNING 'invalid notification config, invalid key =< % >', _key;
--             RETURN false;
--         END LOOP;
	END LOOP;
	RETURN true;
END $$ LANGUAGE PLPGSQL IMMUTABLE SET search_path = public;

CREATE OR REPLACE FUNCTION _cm3_etl_handlers_check(_handlers jsonb) RETURNS boolean AS $$ DECLARE
    _handler jsonb;
BEGIN
    IF _handlers IS NULL OR jsonb_typeof(_handlers) <> 'array' THEN
        RETURN false;
    END IF;
    FOR _handler IN SELECT jsonb_array_elements(_handlers) LOOP
        IF _cm3_utils_is_blank(_handler->>'type') OR _handler->>'type' NOT IN ('template','script','ifc','cad','gate','filereader') THEN
            RAISE WARNING 'invalid etl handler type =< % >', _handler->>'type';
            RETURN false;
        END IF;
	END LOOP;
	RETURN true;
END $$ LANGUAGE PLPGSQL IMMUTABLE SET search_path = public;
