-- add report config field with params

SELECT _cm3_class_triggers_disable('"_EmailTemplate"'::regclass);

ALTER TABLE "_EmailTemplate" ADD COLUMN "ReportConfigs" jsonb DEFAULT '[]'::jsonb;

DO $$ DECLARE
    _record record;
    _code varchar;
    _reportConfigs jsonb[];
    n int := 0;
BEGIN
    FOR _record IN SELECT * FROM "_EmailTemplate" LOOP
                _reportConfigs := '{}'::jsonb;
                RAISE NOTICE '>> _EmailTemplate.Reports - original array:%', _record."Reports";
                FOR _code IN SELECT unnest(_record."Reports") LOOP
                   RAISE NOTICE '_EmailTemplate.Reports - code: %', _code;
                   RAISE NOTICE '_EmailTemplate - adding json reportConfig: %', jsonb_build_object('code', _code);
               n := n + 1;
           _reportConfigs[n] := jsonb_build_object('code', _code);
            END LOOP;
            UPDATE "_EmailTemplate" SET "ReportConfigs" = array_to_json(_reportConfigs)::jsonb WHERE "Id" = _record."Id";
    END LOOP;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_class_triggers_enable('"_EmailTemplate"'::regclass);