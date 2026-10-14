-- adding WidgetId in widget data where missing

DO $$ DECLARE
    _widget record;
BEGIN
    FOR _widget IN SELECT * FROM "_Widget" WHERE "Status"='A' LOOP 
	IF  (_widget."Data"->'WidgetId') IS NULL THEN
            RAISE NOTICE 'widget with id % has no WidgetId', _widget."Id";
            UPDATE "_Widget" SET "Data" = _widget."Data" || jsonb_build_object('WidgetId', quote_literal(_widget."Code")) WHERE "Id" = _widget."Id" AND "Status" = 'A';
	END IF;
    END LOOP;
END $$ LANGUAGE PLPGSQL;