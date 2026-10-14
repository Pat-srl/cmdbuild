-- add barcode meta

DO $$ DECLARE
    _record record;
BEGIN
    FOR _record IN SELECT * FROM "_AttributeMetadata" WHERE "Status" = 'A' AND ("Metadata"->>'system.type.barcode.mapping')::boolean = TRUE LOOP
        PERFORM _cm3_class_metadata_set(_cm3_utils_name_to_regclass(_record."Owner"), 'cm_barcode_search_attr', _record."Code");
    END LOOP;
END $$;

UPDATE "_AttributeMetadata" SET "Metadata" = "Metadata" || jsonb_build_object('cm_mobileEditor','coordinates') WHERE ("Metadata"->>'system.type.mobile.coordinates')::boolean = TRUE AND "Status" = 'A';
