-- etl gate tables upgrade

SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Handlers|TYPE: jsonb|DEFAULT: ''[]''::jsonb');

DO $$ DECLARE
    _record record;
    _handlers jsonb;
    _templates varchar;
BEGIN    
    PERFORM _cm3_class_triggers_disable('"_EtlGate"');
    FOR _record IN SELECT * FROM "_EtlGate" LOOP 
        _handlers = '[]'::jsonb;
        SELECT string_agg(_template, ',') FROM unnest(_record."Templates") _template INTO _templates;
        IF _cm3_utils_is_not_blank(_record."Script") THEN
            _handlers = _handlers || jsonb_build_object('type', 'script', 'script', _record."Script", 'templates', _templates);
        END IF;
        IF  _record."Handler" = 'default' THEN
            IF _cm3_utils_is_not_blank(_templates) THEN
                _handlers = _handlers || jsonb_build_object('type', 'template', 'templates', _templates);
            END IF;
        ELSE
            _handlers = _handlers || jsonb_build_object('type', _record."Handler", 'templates', _templates);
        END IF;
        UPDATE "_EtlGate" SET "Handlers" = _handlers WHERE "Id" = _record."Id";
    END LOOP;
    PERFORM _cm3_class_triggers_enable('"_EtlGate"');
END $$ LANGUAGE PLPGSQL;

ALTER TABLE "_EtlGate" DROP COLUMN "Script";
ALTER TABLE "_EtlGate" DROP COLUMN "Templates";
ALTER TABLE "_EtlGate" DROP COLUMN "Handler";

ALTER TABLE "_EtlGate" ADD CONSTRAINT "_cm3_Handlers_check" CHECK ( "Status" <> 'A' OR _cm3_etl_handlers_check("Handlers") );
