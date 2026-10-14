-- bim tables fix

DO $$ DECLARE
    _record record;
    _navtree jsonb;
BEGIN
    IF EXISTS (SELECT * FROM "_BimLayer" WHERE "Active" = TRUE AND "Root" = TRUE) THEN        
        _navtree = jsonb_build_object(
                '_id', _cm3_utils_random_id(),
                'targetClass', (SELECT _cm3_utils_regclass_to_name("OwnerClassId") FROM "_BimLayer" WHERE "Active" = TRUE AND "Root" = TRUE),
                'nodes', '[]'::jsonb
            );
        FOR _record IN SELECT * FROM "_BimLayer" WHERE "Active" = TRUE AND "Root" = FALSE LOOP
            _navtree = _navtree || jsonb_build_object('nodes', _navtree->'nodes' || jsonb_build_object(
                '_id', _cm3_utils_random_id(),
                'targetClass', _cm3_utils_regclass_to_name(_record."OwnerClassId"),
                'domain', _cm3_utils_regclass_to_domain_name(_cm3_attribute_reference_domain_get(_record."OwnerClassId", _record."RootReference")),
                'direction', _cm3_utils_direction_inverse(_cm3_attribute_reference_direction_get(_record."OwnerClassId", _record."RootReference"))
            ));
        END LOOP;
        INSERT INTO "_NavTree" ("Code", "Data") VALUES ('bimnavigation', _navtree);
    END IF;
    DROP TABLE "_BimLayer" CASCADE;
END $$ LANGUAGE PLPGSQL;

SELECT _cm3_attribute_create('"_BimProject"'::regclass, 'ParentId', 'bigint', 'FKTARGETCLASS: _BimProject|MODE: immutable');
