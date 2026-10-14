-- add new access values
-- PARAMS: FORCE_IF_NOT_EXISTS=true

DO $$ DECLARE
    _record record;
    _config jsonb;
    _key varchar;
BEGIN
    FOR _record IN SELECT * FROM "Role" WHERE "Status" = 'A' AND "Permissions" <> '{}' LOOP
        _config = _record."Permissions";
        FOREACH _key IN ARRAY (ARRAY['card_tab_attachment','card_tab_detail','card_tab_email','card_tab_history','card_tab_note','card_tab_relation','card_tab_schedule','flow_tab_attachment','flow_tab_detail','flow_tab_email','flow_tab_history','flow_tab_note','flow_tab_relation']) LOOP
            IF _config->format('%s_access', _key) IS NOT NULL THEN
                _config = ( _config || jsonb_build_object(format('%s_access_read', _key), _config->format('%s_access', _key), format('%s_access_write', _key), _config->format('%s_access', _key)) ) - format('%s_access', _key);
            END IF;
            IF _config <> _record."Permissions" THEN
                UPDATE "Role" SET "Permissions" = _config WHERE "Id" = _record."Id";
            END IF;
        END LOOP;
    END LOOP;
    FOR _record IN SELECT * FROM "_Grant" WHERE "Status" = 'A' AND "Type" IN ('class','process') AND "OtherPrivileges" <> '{}' LOOP
        _config = _record."OtherPrivileges";
        FOREACH _key IN ARRAY (ARRAY['attachment','detail','email','history','note','relation']) LOOP
            IF _config->_key IS NOT NULL THEN
                _config = ( _config || jsonb_build_object(format('%s_read', _key), _config->_key, format('%s_write', _key), _config->_key) ) - _key;
            END IF;
            IF _config <> _record."OtherPrivileges" THEN
                UPDATE "_Grant" SET "OtherPrivileges" = _config WHERE "Id" = _record."Id";
            END IF;
        END LOOP;
    END LOOP;
END; $$ LANGUAGE PLPGSQL;
