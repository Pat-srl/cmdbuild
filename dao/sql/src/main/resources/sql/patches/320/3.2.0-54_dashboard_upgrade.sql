-- upgrade dashboard format

DO $$ DECLARE
    _record record;
    _value jsonb;
    _charts jsonb;
    _layout jsonb;
BEGIN
    FOR _record IN SELECT * FROM "_Dashboard" WHERE "Status" = 'A' LOOP
        _value = _record."Config";
        _charts = (SELECT COALESCE(jsonb_agg(_chart), '[]'::jsonb) FROM (SELECT value || jsonb_build_object('_id', key) _chart FROM jsonb_each(COALESCE(_value->'charts', '{}'::jsonb))) x);
        _layout = jsonb_build_object('rows', jsonb_build_array(jsonb_build_object('columns', COALESCE(_value->'columns', '[]'::jsonb ))));
        UPDATE "_Dashboard" SET "Config" = jsonb_build_object('charts', _charts, 'layout', _layout) WHERE "Id" = _record."Id";
    END LOOP;
    FOR _record IN SELECT * FROM "_Dashboard" WHERE "Status" = 'A' AND NOT _cm3_dashboard_config_check("Config") LOOP
        RAISE WARNING 'disable invalid dashboard config = %', _record."Id";
        UPDATE "_Dashboard" SET "Status" = 'N' WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_Dashboard" ADD CONSTRAINT "_cm3_Config_check" CHECK ( "Status" <> 'A' OR _cm3_dashboard_config_check("Config") );
