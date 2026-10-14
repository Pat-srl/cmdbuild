-- restoring lost parameters for punctual gis style rules

DO $$ DECLARE
    _stylerule record;
    _analysis varchar;
    _classattr varchar;
    _params varchar;
    _item jsonb;
BEGIN
FOR _stylerule IN SELECT * FROM "_GisStyleRules" WHERE "Status" = 'A' AND "Function" IS NULL AND "Params" = '{}' LOOP
	FOR _item IN SELECT jsonb_array_elements(_stylerule."Rules") LOOP
		IF _item->'condition'->'attribute'->'simple'->>'operator' IS NOT NULL AND _item->'condition'->'attribute'->'simple'->>'attribute' IS NOT NULL 
			AND _analysis IS NULL AND _classattr IS NULL THEN
			IF _item->'condition'->'attribute'->'simple'->>'operator' = 'equal' THEN
				_analysis = 'punctual';
			END IF;
			IF _item->'condition'->'attribute'->'simple'->>'operator' = 'between' THEN
				_analysis = 'intervals';
			END IF;
			_classattr = _item->'condition'->'attribute'->'simple'->>'attribute';
			_params = format('{"segments": null, "analysisType": "%s", "classAttribute": "%s"}', _analysis, _classattr);
			UPDATE "_GisStyleRules" SET "Params" = _params::jsonb WHERE "Id"=_stylerule."Id" AND "Status"='A';
		END IF;
		_analysis = null;
		_classattr = null;
        END LOOP;
END LOOP;
END $$ LANGUAGE PLPGSQL;