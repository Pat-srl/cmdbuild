-- gate embedded items

SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Items|TYPE: jsonb|NOTNULL: true|DEFAULT: []|ITEMS: any');

CREATE TABLE _aux AS SELECT "Id" _id, "Handlers" _handlers FROM "_EtlGate";

ALTER TABLE "_EtlGate" DROP COLUMN "Handlers";

SELECT _cm3_attribute_create('OWNER: _EtlGate|NAME: Handlers|TYPE: bigint[]|NOTNULL: true|DEFAULT: ARRAY[]::bigint[]'); --TODO |FKTARGETCLASS: _EtlGateHandler

DO $$ DECLARE
    _card bigint;
    _handler jsonb;
BEGIN
    PERFORM _cm3_class_triggers_disable('"_EtlGate"');
    FOR _card, _handler IN SELECT _id, jsonb_array_elements(_handlers) FROM _aux LOOP
        _handler = _cm3_item_prepare_record(_handler || jsonb_build_object('IdClass', '_EtlGateHandler'), '"_EtlGate"'::regclass, 'Items', _card);
        UPDATE "_EtlGate" SET "Items" = "Items" || _handler WHERE "Id" = _card;
    END LOOP;
    UPDATE "_EtlGate" SET "Handlers" = (SELECT array_agg((i->>'Id')::bigint) FROM jsonb_array_elements("Items") i);
    FOR _card, _handler IN SELECT "Id", jsonb_array_elements("Items") FROM "_EtlGate" WHERE cardinality("Handlers") = 1 AND "Items"->0->>'type' IN ('cad', 'ifc') LOOP
        --TODO cad, ifc migration
    END LOOP;
    --TODO wizard job migration (?)
    PERFORM _cm3_class_triggers_enable('"_EtlGate"');
END $$ LANGUAGE PLPGSQL;

DROP TABLE _aux;

