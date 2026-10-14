-- fix preselectIfUnique attr metadata

UPDATE "_AttributeMetadata" 
    SET "Metadata" = "Metadata" - 'system.type.reference.preselectIfUnique' || jsonb_build_object('cm_preselect_if_unique', "Metadata"->>'system.type.reference.preselectIfUnique') 
    WHERE "Status" = 'A' AND coalesce("Metadata"->>'system.type.reference.preselectIfUnique','') <> '';

